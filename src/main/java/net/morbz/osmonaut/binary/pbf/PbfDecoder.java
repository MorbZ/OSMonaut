// This software is released into the Public Domain.  See copying.txt for details.

package net.morbz.osmonaut.binary.pbf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.morbz.osmonaut.EntityFilter;
import net.morbz.osmonaut.binary.OsmonautSink;
import net.morbz.osmonaut.osm.Entity;
import net.morbz.osmonaut.osm.EntityType;

/**
 * Decodes all blocks from a PBF stream using worker threads, and passes the
 * results to the downstream sink.
 * 
 * @author Brett Henderson
 * @author Merten Peetz
 */
public class PbfDecoder {
	private int maxPendingBlobs;
	private Lock lock;
	private Condition dataWaitCondition;
	private Queue<PbfBlobResult> blobResults;
	private int workers;
	private OsmonautSink sink;
	private RandomAccessFile inputStream;
	private ExecutorService executorService;
	private RawBlobIndexer nodeIndexer, wayIndexer, relationIndexer;
	private boolean firstScan = true;

	/**
	 * Creates a new instance.
	 * 
	 * @param file
	 *            The file to read.
	 * @param workers
	 *            The number of worker threads for decoding PBF blocks.
	 */
	public PbfDecoder(final File file, int workers) {
		this.workers = workers;
		this.maxPendingBlobs = workers + 1;

		// Open PBF file
		try {
			inputStream = new RandomAccessFile(file, "r");
		} catch (IOException e) {
			throw new RuntimeException("Unable to read PBF file " + file + ".", e);
		}

		// Create indexes
		nodeIndexer = new RawBlobIndexer(inputStream);
		wayIndexer = new RawBlobIndexer(inputStream);
		relationIndexer = new RawBlobIndexer(inputStream);

		// Create the thread synchronisation primitives.
		lock = new ReentrantLock();
		dataWaitCondition = lock.newCondition();

		// Create the queue of blobs being decoded.
		blobResults = new LinkedList<PbfBlobResult>();
	}

	/**
	 * Any thread can call this method when they wish to wait until an update
	 * has been performed by another thread.
	 */
	private void waitForUpdate() {
		try {
			dataWaitCondition.await();
		} catch (InterruptedException e) {
			throw new RuntimeException("Thread was interrupted.", e);
		}
	}

	/**
	 * Any thread can call this method when they wish to signal another thread
	 * that an update has occurred.
	 */
	private void signalUpdate() {
		dataWaitCondition.signal();
	}

	private void sendResultsToSink(int targetQueueSize) {
		while (blobResults.size() > targetQueueSize) {
			// Get the next result from the queue and wait for it to complete.
			PbfBlobResult blobResult = blobResults.remove();
			while (!blobResult.isComplete()) {
				// The thread hasn't finished processing yet so wait for an
				// update from another thread before checking again.
				waitForUpdate();
			}

			if (!blobResult.isSuccess()) {
				throw new RuntimeException("A PBF decoding worker thread failed, aborting.");
			}

			// Send the processed entities to the sink. We can release the lock
			// for the duration of processing to allow worker threads to post
			// their results.
			lock.unlock();
			for (Entity entity : blobResult.getEntities()) {
				sink.foundEntity(entity);
			}
			lock.lock();
		}
	}

	private void processBlobs(EntityType type) {
		// During the first file scan we index the file position for every blob
		// and the entity types it contains. So that in every other run we just
		// have to read the blobs for which we know they contain the 
		// entities we need. This greatly improves the lookup speed for 
		// relations. Also we can be sure that we get the entity types in the 
		// right order, no matter how they are ordered in the file.
		RawBlobProvider provider = null;
		if(firstScan) {
			provider = new RawBlobReader(inputStream);
		} else {
			switch(type) {
			case NODE:
				provider = nodeIndexer;
				break;
			case WAY:
				provider = wayIndexer;
				break;
			case RELATION:
				provider = relationIndexer;
				break;
			}
		}

		// Process until the PBF stream is exhausted.
		while (provider.hasNext()) {
			// Obtain the next raw blob from the PBF stream.
			final PbfRawBlob rawBlob = provider.next();

			// Create the result object to capture the results of the decoded
			// blob and add it to the blob results queue.
			final PbfBlobResult blobResult = new PbfBlobResult();
			blobResults.add(blobResult);

			// Create the listener object that will update the blob results
			// based on an event fired by the blob decoder.
			PbfBlobDecoderListener decoderListener = new PbfBlobDecoderListener() {
				@Override
				public void error() {
					lock.lock();
					try {
						blobResult.storeFailureResult();
						signalUpdate();
					} finally {
						lock.unlock();
					}
				}

				@Override
				public void complete(List<Entity> decodedEntities, EntityFilter containedTypes) {
					lock.lock();
					try {
						blobResult.storeSuccessResult(decodedEntities);
						indexBlob(rawBlob, containedTypes);
						signalUpdate();
					} finally {
						lock.unlock();
					}
				}
			};

			// Create the blob decoder itself and execute it on a worker thread.
			PbfBlobDecoder blobDecoder = new PbfBlobDecoder(rawBlob.getType(), rawBlob.getData(), decoderListener, type);
			executorService.execute(blobDecoder);

			// If the number of pending blobs has reached capacity we must begin
			// sending results to the sink. This method will block until blob
			// decoding is complete.
			sendResultsToSink(maxPendingBlobs - 1);
		}

		// There are no more entities available in the PBF stream, so send all remaining data to the sink.
		sendResultsToSink(0);

		firstScan = false;
		provider.resetIterator();
	}

	private void indexBlob(PbfRawBlob rawBlob, EntityFilter containedTypes) {
		if(!firstScan) {
			return;
		}

		long fileOffset = rawBlob.getFileOffset();
		int blobSize = rawBlob.getData().length;

		// Each blob may contain entities of different types, so we can't just
		// use an enum array.
		if(containedTypes.getEntityEnabled(EntityType.NODE)) {
			nodeIndexer.indexBlob(fileOffset, blobSize);
		}
		if(containedTypes.getEntityEnabled(EntityType.WAY)) {
			wayIndexer.indexBlob(fileOffset, blobSize);
		}
		if(containedTypes.getEntityEnabled(EntityType.RELATION)) {
			relationIndexer.indexBlob(fileOffset, blobSize);
		}
	}

	/**
	 * Scans the PBF file for entities of the given type and sends them to the
	 * sink.
	 * @param type The entity type to scan for. Only entities of this type will
	 * be returned.
	 * @param sink The sink to send all decoded entities to
	 */
	public void scan(EntityType type, OsmonautSink sink) {
		this.sink = sink;

		executorService = Executors.newFixedThreadPool(workers);

		try {
			// Process all blobs of data in the stream using threads from the
			// executor service. We allow the decoder to issue an extra blob
			// than there are workers to ensure there is another blob
			// immediately ready for processing when a worker thread completes.
			// The main thread is responsible for splitting blobs from the
			// request stream, and sending decoded entities to the sink.
			lock.lock();
			try {
				processBlobs(type);
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		} finally {
			executorService.shutdownNow();
		}
	}

	/**
	 * Closes the PBF file.
	 */
	public void close() {
		if(inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
			}
		}
	}
}
