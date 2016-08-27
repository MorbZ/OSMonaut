// This software is released into the Public Domain.  See copying.txt for details.

package net.morbz.osmonaut.binary.pbf;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import net.morbz.osmonaut.osm.Node;
import net.morbz.osmonaut.osm.Relation;
import net.morbz.osmonaut.osm.Way;

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
	private EntityFilter filter;
	private InputStream inputStream;
	private PbfStreamSplitter streamSplitter;
	private ExecutorService executorService;

	/**
	 * Creates a new instance.
	 * 
	 * @param file
	 *            The file to read.
	 * @param workers
	 *            The number of worker threads for decoding PBF blocks.
	 * @param sink
	 *            The sink to send all decoded entities to.
	 */
	public PbfDecoder(final File file, int workers, OsmonautSink sink) {
		this.workers = workers;
		this.maxPendingBlobs = workers + 1;
		this.sink = sink;
		this.filter = sink.getEntityFilter();

		// Open PBF file
		try {
			inputStream = new FileInputStream(file);
		} catch (IOException e) {
			throw new RuntimeException("Unable to read PBF file " + file + ".", e);
		}

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
				switch(entity.getEntityType()) {
				case NODE:
					sink.foundNode((Node)entity);
					break;
				case WAY:
					sink.foundWay((Way)entity);
					break;
				case RELATION:
					sink.foundRelation((Relation)entity);
					break;
				}
			}
			lock.lock();
		}
	}

	private void processBlobs() {
		// Process until the PBF stream is exhausted.
		while (streamSplitter.hasNext()) {
			// Obtain the next raw blob from the PBF stream.
			PbfRawBlob rawBlob = streamSplitter.next();

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
				public void complete(List<Entity> decodedEntities) {
					lock.lock();
					try {
						blobResult.storeSuccessResult(decodedEntities);
						signalUpdate();
					} finally {
						lock.unlock();
					}
				}
			};

			// Create the blob decoder itself and execute it on a worker thread.
			PbfBlobDecoder blobDecoder = new PbfBlobDecoder(rawBlob.getType(), rawBlob.getData(), decoderListener, filter);
			executorService.execute(blobDecoder);

			// If the number of pending blobs has reached capacity we must begin
			// sending results to the sink. This method will block until blob
			// decoding is complete.
			sendResultsToSink(maxPendingBlobs - 1);
		}

		// There are no more entities available in the PBF stream, so send all remaining data to the sink.
		sendResultsToSink(0);
	}

	public void scan() {
		executorService = Executors.newFixedThreadPool(workers);

		try {
			// Create a stream splitter to break the PBF stream into blobs.
			streamSplitter = new PbfStreamSplitter(new DataInputStream(inputStream));

			// Process all blobs of data in the stream using threads from the
			// executor service. We allow the decoder to issue an extra blob
			// than there are workers to ensure there is another blob
			// immediately ready for processing when a worker thread completes.
			// The main thread is responsible for splitting blobs from the
			// request stream, and sending decoded entities to the sink.
			lock.lock();
			try {
				processBlobs();
			} finally {
				lock.unlock();
			}
		} finally {
			executorService.shutdownNow();

			if (streamSplitter != null) {
				streamSplitter.close();
			}
		}
	}
}
