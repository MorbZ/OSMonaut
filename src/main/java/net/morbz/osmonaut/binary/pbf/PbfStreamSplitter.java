// This software is released into the Public Domain.  See copying.txt for details.

package net.morbz.osmonaut.binary.pbf;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;

import net.morbz.osmonaut.binary.pbf.proto.Fileformat;
import net.morbz.osmonaut.binary.pbf.proto.Fileformat.BlobHeader;

/**
 * Parses a PBF data stream and extracts the raw data of each blob in sequence
 * until the end of the stream is reached.
 * 
 * @author Brett Henderson
 */
public class PbfStreamSplitter implements Iterator<PbfRawBlob> {
	private DataInputStream dis;
	private boolean eof;
	private PbfRawBlob nextBlob;

	/**
	 * Creates a new instance.
	 * 
	 * @param pbfStream
	 *            The PBF data stream to be parsed.
	 */
	public PbfStreamSplitter(DataInputStream pbfStream) {
		dis = pbfStream;
		eof = false;
	}

	private BlobHeader readHeader(int headerLength) throws IOException {
		byte[] headerBuffer = new byte[headerLength];
		dis.readFully(headerBuffer);

		BlobHeader blobHeader = Fileformat.BlobHeader.parseFrom(headerBuffer);
		return blobHeader;
	}

	private byte[] readRawBlob(BlobHeader blobHeader) throws IOException {
		byte[] rawBlob = new byte[blobHeader.getDatasize()];

		dis.readFully(rawBlob);
		return rawBlob;
	}

	private void getNextBlob() {
		try {
			// Read the length of the next header block. This is the only time
			// we should expect to encounter an EOF exception. In all other
			// cases it indicates a corrupt or truncated file.
			int headerLength;
			try {
				headerLength = dis.readInt();
			} catch (EOFException e) {
				eof = true;
				return;
			}

			BlobHeader blobHeader = readHeader(headerLength);
			byte[] blobData = readRawBlob(blobHeader);
			nextBlob = new PbfRawBlob(blobHeader.getType(), blobData);
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

	public void close() {
		if (dis != null) {
			try {
				dis.close();
			} catch (IOException e) {
			}
		}
		dis = null;
	}
}
