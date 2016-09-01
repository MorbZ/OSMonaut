// This software is released into the Public Domain.  See copying.txt for details.

package net.morbz.osmonaut.binary.pbf;

/**
 * Represents a single piece of raw blob data extracted from the PBF stream. It
 * has not yet been decoded into a PBF blob object.
 * 
 * @author Brett Henderson
 * @author Merten Peetz
 */
public class PbfRawBlob {
	private String type;
	private byte[] data;
	private long fileOffset;

	/**
	 * Creates a new instance.
	 * 
	 * @param type
	 *            The type of data represented by this blob. This corresponds to
	 *            the type field in the blob header.
	 * @param data
	 *            The raw contents of the blob in binary undecoded form.
	 * @param fileOffset        
	 *            The position from the beginning of the PBF file in bytes 
	 *            where the blob starts
	 */
	public PbfRawBlob(String type, byte[] data, long fileOffset) {
		this.type = type;
		this.data = data;
		this.fileOffset = fileOffset;
	}

	/**
	 * Gets the type of data represented by this blob. This corresponds to the
	 * type field in the blob header.
	 * 
	 * @return The blob type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets the raw contents of the blob in binary undecoded form.
	 * 
	 * @return The raw blob data.
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @return The position from the beginning of the PBF file in bytes where 
	 * this blob starts  
	 */
	public long getFileOffset() {
		return fileOffset;
	}
}
