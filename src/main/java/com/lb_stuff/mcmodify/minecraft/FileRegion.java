package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.nbt.FormatException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.zip.InflaterInputStream;

/**
 * Region file reader/writer
 * @see <a href="http://minecraft.gamepedia.com/Region_file_format">Region file format</a> on the Minecraft Wiki
 */
public class FileRegion extends Region
{
	@Deprecated
	private static final int NUMBER_OF_HEADER_SECTORS = 2;

	/**
	 * The FileRegion File.
	 */
	private final File rf;

	/**
	 * Constructs this region from a Region File. If the file does not exist it is created with no chunks in it.
	 * @param mca The Region File.
	 * @throws IOException if an error occurs while reading the region file.
	 */
	public FileRegion(File mca) throws IOException
	{
		rf = mca;
		if(!rf.exists())
		{
			rf.createNewFile();
			try(FileOutputStream region = new FileOutputStream(rf))
			{
				region.write(new byte[8192]);
			}
		}
	}

	/**
	 * Returns the offset and sector count for a chunk.
	 * @param region The RandomAccessFile to read the data from.
	 * @param index The chunk index, pre-calculated.
	 * @return The offset and sector count for a chunk. The offset is the key and the sector count is the value.
	 * @throws IOException if the input operation throws an exception.
	 */
	@Deprecated
	private static Entry<Integer, Integer> sectorOffset(RandomAccessFile region, int index) throws IOException
	{
		region.getChannel().position(4*index);
		byte[] buf = new byte[4];
		region.readFully(buf);
		int offset;
		try(DataInputStream dis = new DataInputStream(new ByteArrayInputStream(new byte[]{0, buf[0], buf[1], buf[2]})))
		{
			offset = dis.readInt()-NUMBER_OF_HEADER_SECTORS;
		}
		int sectors = buf[3];
		return new SimpleEntry<>(offset, sectors);
	}
	/**
	 * Writes the offset and sector count for a chunk.
	 * @param region The RandomAccessFile to write the data to.
	 * @param index The chunk index, pre-computed.
	 * @param offset The offset of the chunk.
	 * @param sectors The sector count of the chunk.
	 * @throws IOException if the output operation throws an exception.
	 */
	@Deprecated
	private static void sectorOffset(RandomAccessFile region, int index, int offset, int sectors) throws IOException
	{
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream(4))
		{
			try(DataOutputStream dos = new DataOutputStream(baos))
			{
				dos.writeInt(offset+NUMBER_OF_HEADER_SECTORS);
			}
			byte[] temp = baos.toByteArray();
			ByteBuffer buf = ByteBuffer.allocate(4);
			buf.put(new byte[]{temp[1], temp[2], temp[3], (byte)sectors});
			region.getChannel().position(4*index);
			region.write(buf.array());
		}
	}

	/**
	 * Reads a chunk from the region file.
	 * @param x The X chunk coordinate of the chunk.
	 * @param z The Z chunk coordinate of the chunk.
	 * @return The read chunk, or null if the chunk does not exist.
	 * @throws FormatException if the read chunk is invalid.
	 * @throws IOException if an input operation throws an exception.
	 */
	@Override
	public Chunk getChunk(int x, int z) throws FormatException, IOException
	{
		try(RandomAccessFile region = new RandomAccessFile(rf, "r"))
		{
			Entry<Integer, Integer> pair = sectorOffset(region, ((x%32) + (z%32)*32));
			int offset = pair.getKey();
			int sectors = pair.getValue();
			if(offset != -NUMBER_OF_HEADER_SECTORS && sectors != 0)
			{
				region.seek(CHUNK_SECTORS_START+offset*SECTOR_BYTES);
				int length;
				int compression;
				byte[] chunk;
				length = region.readInt()-1;
				compression = region.readByte();
				chunk = new byte[length];
				region.readFully(chunk);
				try(ByteArrayInputStream chunkin = new ByteArrayInputStream(chunk))
				{
					if(compression == GZip_Compression)
					{
						return new Chunk(com.lb_stuff.mcmodify.nbt.IO.Read(chunkin));
					}
					else if(compression == Zlib_Compression)
					{
						try(InflaterInputStream ci = new InflaterInputStream(chunkin))
						{
							return new Chunk(com.lb_stuff.mcmodify.nbt.IO.ReadUncompressed(ci));
						}
					}
				}
			}
		}
		return null;
	}
	/**
	 * Reads a chunk timestamp from the region file.
	 * @param x The X chunk coordinate of the chunk.
	 * @param z The Z chunk coordinate of the chunk.
	 * @return The read chunk timestamp.
	 * @throws IOException if an input operation throws an exception.
	 */
	@Override
	public int getTimestamp(int x, int z) throws IOException
	{
		try(FileInputStream region = new FileInputStream(rf))
		{
			region.getChannel().position(TIMESTAMPS_SECTOR_START+4*((x%32) + (z%32)*32));
			try(DataInputStream dis = new DataInputStream(region))
			{
				return dis.readInt();
			}
		}
	}

	/**
	 * Writes the given chunk to the region file in a lazy fashion. If the chunk exists in the region file and the given chunk can fit, it will be placed there and the sector size will be updated. Otherwise the chunk will be placed at the end of the region file without removing the old chunk, and the offset and sector size will be updated.
	 * @param x The X chunk coordinate of the chunk.
	 * @param z The Z chunk coordinate of the chunk.
	 * @param c The chunk to write.
	 * @throws IOException if an input or output operation throws an exception.
	 */
	@Override
	public void setChunk(int x, int z, Chunk c) throws IOException
	{
		try(RandomAccessFile region = new RandomAccessFile(rf, "rw"))
		{
			final int index = ((x%32) + (z%32)*32);
			if(c == null)
			{
				sectorOffset(region, index, -NUMBER_OF_HEADER_SECTORS, 0);
				return;
			}
			int chunksize, newsectors;
			ByteBuffer chunkbytes;
			try(ByteArrayOutputStream baos = new ByteArrayOutputStream(0))
			{
				com.lb_stuff.mcmodify.nbt.IO.Write(c.ToNBT(""), baos);
				chunksize = baos.size();
				newsectors = (int)((chunksize+5)/SECTOR_BYTES+1); //TODO: remove cast
				chunkbytes = ByteBuffer.allocate((int)(newsectors*SECTOR_BYTES)); //TODO: remove cast
				chunkbytes.putInt(chunksize+1);
				chunkbytes.put(GZip_Compression);
				chunkbytes.put(baos.toByteArray());
			}

			Entry<Integer, Integer> pair = sectorOffset(region, index);
			int offset = pair.getKey();
			int sectors = pair.getValue();

			if((offset == -NUMBER_OF_HEADER_SECTORS && sectors == 0) || sectors < newsectors)
			{
				int newoffset = (int)((region.length()-CHUNK_SECTORS_START)/SECTOR_BYTES)+1;
				newoffset = newoffset < 0 ? 0 : newoffset;
				region.seek(CHUNK_SECTORS_START+SECTOR_BYTES*newoffset);
				region.write(chunkbytes.array());
				sectorOffset(region, index, newoffset, newsectors);
			}
			else if(sectors >= newsectors)
			{
				region.seek(CHUNK_SECTORS_START+SECTOR_BYTES*offset);
				region.write(chunkbytes.array());
				sectorOffset(region, index, offset, newsectors);
			}
		}
	}
	/**
	 * Writes a chunk timestamp to the region file.
	 * @param x The X chunk coordinate of the chunk.
	 * @param z The Z chunk coordinate of the chunk.
	 * @param timestamp The new timestamp.
	 * @throws IOException if the output operation throws an exception.
	 */
	@Override
	public void setTimestamp(int x, int z, int timestamp) throws IOException
	{
		try(FileOutputStream region = new FileOutputStream(rf))
		{
			region.getChannel().position(TIMESTAMPS_SECTOR_START+4*((x%32) + (z%32)*32));
			try(DataOutputStream dos = new DataOutputStream(region))
			{
				dos.writeInt(timestamp);
			}
		}
	}
}