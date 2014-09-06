package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.nbt.FormatException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

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
	 * The maximum number of chunks in a single region.
	 */
	public static final int MAX_CHUNKS = 32*32;
	/**
	 * The index of the first byte of the locations sector.
	 */
	public static final long LOCATIONS_SECTOR_START = 0;
	/**
	 * The index of the first byte of the timestamps sector.
	 */
	protected static final long TIMESTAMPS_SECTOR_START = SECTOR_BYTES;
	/**
	 * The index of the first byte of the chunk data sectors.
	 */
	protected static final long CHUNK_SECTORS_START = TIMESTAMPS_SECTOR_START+SECTOR_BYTES;

	public static enum ChunkCompression
	{
		None,
		GZip,
		Zlib,
		;

		public static ChunkCompression fromId(byte id)
		{
			switch(id)
			{
				case 1: return GZip;
				case 2: return Zlib;
				default: return null;
			}
		}
		public byte getId()
		{
			switch(this)
			{
				case None: throw new IllegalArgumentException("There is no Id for compression scheme "+this);
				case GZip: return 1;
				case Zlib: return 2;
				default: throw new IllegalStateException();
			}
		}

		public InputStream getInputStream(InputStream original) throws IOException
		{
			switch(this)
			{
				case None: return original;
				case GZip: return new GZIPInputStream(original);
				case Zlib: return new InflaterInputStream(original);
				default: throw new IllegalStateException();
			}
		}
		public OutputStream getOutputStream(OutputStream original) throws IOException
		{
			switch(this)
			{
				case None: return original;
				case GZip: return new GZIPOutputStream(original);
				case Zlib: return new DeflaterOutputStream(original);
				default: throw new IllegalStateException();
			}
		}
	}
	@Deprecated
	public static final byte GZip_Compression = 1;
	@Deprecated
	public static final byte Zlib_Compression = 2;

	protected static final class LocationPair
	{
		public final long offset;
		public final long count;
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
			count = c;
		}
		public LocationPair(DataInput in) throws IOException
		{
			final byte[] temp = new byte[4];
			in.readFully(temp);
			try(DataInputStream dis = new DataInputStream(new ByteArrayInputStream(new byte[]{0, temp[0], temp[1], temp[2]})))
			{
				offset = dis.readInt()*SECTOR_BYTES;
			}
			count = temp[3]*SECTOR_BYTES;
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
				if(count % SECTOR_BYTES == 0)
				{
					temp[3] = (byte)(count/SECTOR_BYTES);
				}
				else
				{
					temp[3] = (byte)((count/SECTOR_BYTES) + 1);
				}
				out.write(temp);
			}
		}

		public static long nextSector(long offset)
		{
			if(offset % SECTOR_BYTES == 0)
			{
				return offset + SECTOR_BYTES;
			}
			return ((offset/SECTOR_BYTES) + 1)*SECTOR_BYTES;
		}
	}

	protected static int chunkIndex(int x, int z)
	{
		return (x%32) + (z%32)*32;
	}

	public abstract Chunk getChunk(int x, int z) throws FormatException, IOException;
	public abstract int getTimestamp(int x, int z) throws IOException;
	public abstract void setChunk(int x, int z, Chunk c) throws IOException;
	public abstract void setTimestamp(int x, int z, int timestamp) throws IOException;
}
