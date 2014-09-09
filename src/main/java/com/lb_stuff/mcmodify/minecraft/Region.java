package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.location.LocChunkInRegion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @see <a href="http://minecraft.gamepedia.com/Region_file_format">Region file format</a> on the Minecraft Wiki
 */
public abstract class Region
{
	/**
	 * The number of bytes in a sector.
	 */
	protected static final long SECTOR_BYTES = 4096;
	/**
	 * The number of chunks along the X axis.
	 */
	public static final int CHUNK_X_SIZE = 32;
	/**
	 * The number of chunks along the Z axis.
	 */
	public static final int CHUNK_Z_SIZE = 32;
	/**
	 * The maximum number of chunks in a single region.
	 */
	public static final int MAX_CHUNKS = CHUNK_X_SIZE*CHUNK_Z_SIZE;
	/**
	 * The index of the first byte of the locations sector.
	 */
	protected static final long LOCATIONS_SECTOR_START = 0;
	/**
	 * The index of the first byte of the timestamps sector.
	 */
	protected static final long TIMESTAMPS_SECTOR_START = SECTOR_BYTES;
	/**
	 * The index of the first byte of the chunk data sectors.
	 */
	protected static final long CHUNK_SECTORS_START = TIMESTAMPS_SECTOR_START+SECTOR_BYTES;

	protected static final class LocationPair
	{
		/**
		 * Offset in bytes.
		 */
		public final long offset;
		/**
		 * Length in bytes.
		 */
		public final long size;
		public LocationPair(long off, long c)
		{
			if(off > 0b11111111_11111111_11111111L*SECTOR_BYTES)
			{
				throw new IllegalArgumentException("Unserializable offset: "+off);
			}
			offset = off;
			if(c > Byte.MAX_VALUE*SECTOR_BYTES)
			{
				throw new IllegalArgumentException("Unserializable count: "+c);
			}
			size = c;
		}
		public LocationPair(DataInput in) throws IOException
		{
			final byte[] temp = new byte[4];
			in.readFully(temp);
			try(DataInputStream dis = new DataInputStream(new ByteArrayInputStream(new byte[]{0, temp[0], temp[1], temp[2]})))
			{
				offset = dis.readInt()*SECTOR_BYTES;
			}
			size = temp[3]*SECTOR_BYTES;
		}
		public void serialize(DataOutput out) throws IOException
		{
			try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				try(DataOutputStream dos = new DataOutputStream(baos))
				{
					if(offset % SECTOR_BYTES == 0)
					{
						dos.writeInt((int)(offset/SECTOR_BYTES));
					}
					else
					{
						dos.writeInt((int)((offset/SECTOR_BYTES) + 1));
					}
				}
				final byte[] temp = baos.toByteArray();
				if(size % SECTOR_BYTES == 0)
				{
					temp[3] = (byte)(size/SECTOR_BYTES);
				}
				else
				{
					temp[3] = (byte)((size/SECTOR_BYTES) + 1);
				}
				out.write(temp);
			}
		}
	}

	protected static int chunkIndex(LocChunkInRegion pos)
	{
		return (pos.x%32) + (pos.z%32)*32;
	}
	protected static long nextSector(long offset)
	{
		if(offset % SECTOR_BYTES == 0)
		{
			return offset + SECTOR_BYTES;
		}
		return ((offset/SECTOR_BYTES) + 1)*SECTOR_BYTES;
	}

	/**
	 * Loads the requested chunk from the region.
	 * @param pos The chunk to load.
	 * @return The freshly-constructed requested chunk.
	 * @throws IOException If there is a problem with loading the chunk.
	 */
	public abstract Chunk getChunk(LocChunkInRegion pos) throws IOException;
	/**
	 * Loads the timestamp of the requested chunk from the region.
	 * @param pos The chunk whose timestamp is desired.
	 * @return The timestamp of the requested chunk.
	 * @throws IOException If there is a problem with loading the timestamp.
	 */
	public abstract int getTimestamp(LocChunkInRegion pos) throws IOException;
	/**
	 * Saves the given chunk to the region.
	 * @param pos The chunk coordinates.
	 * @param c The chunk to save.
	 * @throws IOException If there is a problem with saving the chunk.
	 */
	public abstract void setChunk(LocChunkInRegion pos, Chunk c) throws IOException;
	/**
	 * Saves the timestamp of the indicated chunk to the region.
	 * @param pos The chunk whose timestamp should be altered.
	 * @param timestamp The new timestamp value.
	 * @throws IOException If there is a problem with saving the timestamp.
	 */
	public abstract void setTimestamp(LocChunkInRegion pos, int timestamp) throws IOException;
}
