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
import java.util.Iterator;

/** 
 * Provides an iterator that reads the raw blobs of the PBF file.
 */
public abstract class RawBlobProvider implements Iterator<PbfRawBlob> {
	protected RandomAccessFile file;

	/**
	 * @param file The file from which the blobs are read
	 */
	public RawBlobProvider(RandomAccessFile file) {
		this.file = file;
	}

	/**
	 * Resets the iterator so that it can be used again.
	 */
	public abstract void resetIterator();

	protected byte[] readRawBlob(int size) throws IOException {
		byte[] rawBlob = new byte[size];
		file.readFully(rawBlob);
		return rawBlob;
	}
}
