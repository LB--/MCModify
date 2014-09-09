package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.location.LocChunkInRegion;
import com.lb_stuff.mcmodify.nbt.Tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Region file reader/writer
 * @see <a href="http://minecraft.gamepedia.com/Region_file_format">Region file format</a> on the Minecraft Wiki
 */
public class FileRegion extends Region
{
	/**
	 * The Region File.
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
				region.write(new byte[(int)CHUNK_SECTORS_START]);
			}
		}
	}

	@Override
	public Chunk getChunk(LocChunkInRegion pos) throws IOException
	{
		try(RandomAccessFile region = new RandomAccessFile(rf, "r"))
		{
			region.seek(LOCATIONS_SECTOR_START + chunkIndex(pos)*4);
			LocationPair loc = new LocationPair(region);
			if(loc.offset > 0 && loc.size > 0)
			{
				region.seek(loc.offset);
				int length = region.readInt();
				CompressionScheme compressed = CompressionScheme.fromId(region.readByte());
				byte[] chunk = new byte[length-1];
				region.readFully(chunk);
				try(InputStream is = compressed.getInputStream(new ByteArrayInputStream(chunk)))
				{
					return new Chunk((Tag.Compound)Tag.deserialize(is));
				}
			}
		}
		return null;
	}
	@Override
	public int getTimestamp(LocChunkInRegion pos) throws IOException
	{
		try(RandomAccessFile region = new RandomAccessFile(rf, "r"))
		{
			region.seek(TIMESTAMPS_SECTOR_START + chunkIndex(pos)*4);
			return region.readInt();
		}
	}

	@Override
	public void setChunk(LocChunkInRegion pos, Chunk c) throws IOException
	{
		try(RandomAccessFile region = new RandomAccessFile(rf, "rw"))
		{
			final int index = chunkIndex(pos);
			if(c == null)
			{
				region.seek(LOCATIONS_SECTOR_START + index*4);
				new LocationPair(0, 0).serialize(region);
				return;
			}
			final byte[] chunkdata;
			try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				baos.write(CompressionScheme.GZip.getId());
				try(OutputStream os = CompressionScheme.GZip.getOutputStream(baos))
				{
					c.ToNBT("").serialize(os);
				}
				chunkdata = baos.toByteArray();
			}
			final long newsize = 4+chunkdata.length;

			region.seek(LOCATIONS_SECTOR_START + index*4);
			LocationPair loc = new LocationPair(region);
			if((loc.offset == 0 && loc.size == 0) || loc.size < newsize)
			{
				final long offset = nextSector(region.length());
				region.seek(offset);
				region.writeInt(chunkdata.length);
				region.write(chunkdata);
				loc = new LocationPair(offset, region.getFilePointer()-offset);
				region.seek(LOCATIONS_SECTOR_START + index*4);
				loc.serialize(region);
			}
			else
			{
				region.seek(loc.offset);
				region.writeInt(chunkdata.length);
				region.write(chunkdata);
			}
		}
	}
	@Override
	public void setTimestamp(LocChunkInRegion pos, int timestamp) throws IOException
	{
		try(RandomAccessFile region = new RandomAccessFile(rf, "rw"))
		{
			region.seek(TIMESTAMPS_SECTOR_START + chunkIndex(pos)*4);
			region.writeInt(timestamp);
		}
	}
}