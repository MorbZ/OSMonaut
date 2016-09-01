package net.morbz.osmonaut.binary.pbf;

/*
* The MIT License (MIT)
* 
* Copyright (c) 2016 Merten Peetz
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Indexes the file position and size of all blobs that contain entities of the 
 * given type.
 */
public class RawBlobIndexer extends RawBlobProvider {
	private List<BlobFileIndex> blobIndexes = new ArrayList<BlobFileIndex>();
	private Iterator<BlobFileIndex> iterator;

	/**
	 * @param file The PBF file input stream
	 */
	public RawBlobIndexer(RandomAccessFile file) {
		super(file);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		if(iterator == null) {
			iterator = blobIndexes.iterator();
		}
		return iterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PbfRawBlob next() {
		try {
			BlobFileIndex blobIndex = iterator.next();

			// Read blob
			file.seek(blobIndex.getFileOffset());
			byte[] blobData = readRawBlob(blobIndex.getBlobSize());
			return new PbfRawBlob("OSMData", blobData, blobIndex.getFileOffset());
		} catch (IOException e) {
			throw new RuntimeException("Unable to get next blob from PBF stream.", e);
		}
	}

	/**
	 * Adds a blob the the index.
	 * 
	 * @param fileOffset The position from the beginning of the PBF file in 
	 * bytes where the blob starts
	 * @param blobSize The size of the blob in bytes
	 */
	public void indexBlob(long fileOffset, int blobSize) {
		BlobFileIndex blobIndex = new BlobFileIndex(fileOffset, blobSize);
		blobIndexes.add(blobIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetIterator() {
		iterator = null;
	}

	private class BlobFileIndex {
		private long fileOffset;
		private int blobSize;

		public BlobFileIndex(long fileOffset, int blobSize) {
			this.fileOffset = fileOffset;
			this.blobSize = blobSize;
		}

		public long getFileOffset() {
			return fileOffset;
		}

		public int getBlobSize() {
			return blobSize;
		}
	}
}
