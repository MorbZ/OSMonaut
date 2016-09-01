// This software is released into the Public Domain.  See copying.txt for details.

package net.morbz.osmonaut.binary.pbf;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.morbz.osmonaut.binary.pbf.proto.Fileformat;
import net.morbz.osmonaut.binary.pbf.proto.Fileformat.BlobHeader;

/**
 * Parses a PBF data stream and extracts the raw data of each blob in sequence
 * until the end of the stream is reached.
 * 
 * @author Brett Henderson
 * @author Merten Peetz
 */
public class RawBlobReader extends RawBlobProvider {
	private boolean eof;
	private PbfRawBlob nextBlob;

	/**
	 * Creates a new instance.
	 * 
	 * @param file
	 *            The PBF file to be parsed.
	 */
	public RawBlobReader(RandomAccessFile file) {
		super(file);
		eof = false;
	}

	private BlobHeader readHeader(int headerLength) throws IOException {
		byte[] headerBuffer = new byte[headerLength];
		file.readFully(headerBuffer);

		BlobHeader blobHeader = Fileformat.BlobHeader.parseFrom(headerBuffer);
		return blobHeader;
	}

	private void getNextBlob() {
		try {
			// Read the length of the next header block. This is the only time
			// we should expect to encounter an EOF exception. In all other
			// cases it indicates a corrupt or truncated file.
			int headerLength;
			try {
				headerLength = file.readInt();
			} catch (EOFException e) {
				eof = true;
				return;
			}

			BlobHeader blobHeader = readHeader(headerLength);
			long fileOffset = file.getFilePointer();
			byte[] blobData = readRawBlob(blobHeader.getDatasize());
			nextBlob = new PbfRawBlob(blobHeader.getType(), blobData, fileOffset);
		} catch (IOException e) {
			throw new RuntimeException("Unable to get next blob from PBF stream.", e);
		}
	}

	@Override
	public boolean hasNext() {
		if (nextBlob == null && !eof) {
			getNextBlob();
		}

		return nextBlob != null;
	}

	@Override
	public PbfRawBlob next() {
		PbfRawBlob result = nextBlob;
		nextBlob = null;

		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetIterator() {
		// No need to reset as the file is indexed after the first read.
	}
}
