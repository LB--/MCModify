package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.location.LocChunkInRegion;
import com.lb_stuff.mcmodify.nbt.Tag;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Loads an entire region file into memory and allows you to save it later.
 * @see <a href="http://minecraft.gamepedia.com/Region_file_format">Region file format</a> on the Minecraft Wiki
 */
public class MemoryRegion extends Region
{
	private final CompressionScheme compression;
	private final int[] timestamps = new int[MAX_CHUNKS];
	private final byte[][] chunks = new byte[MAX_CHUNKS][];
	public MemoryRegion(File mca, CompressionScheme preferred) throws IOException
	{
		compression = preferred;
		try(RandomAccessFile region = new RandomAccessFile(mca, "r"))
		{
			for(int i = 0; i < MAX_CHUNKS; ++i)
			{
				region.seek(LOCATIONS_SECTOR_START + i*4);
				final LocationPair loc = new LocationPair(region);

				region.seek(TIMESTAMPS_SECTOR_START + i*4);
				timestamps[i] = region.readInt();

				if(loc.offset > 0 && loc.size > 0)
				{
					region.seek(loc.offset);
					final int length = region.readInt();
					final CompressionScheme compressed = CompressionScheme.fromId(region.readByte());
					chunks[i] = new byte[length-1];
					region.readFully(chunks[i]);
					if(compressed != null && compressed != compression)
					{
						try(InputStream is = compressed.getInputStream(new ByteArrayInputStream(chunks[i])))
						{
							try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
							{
								try(OutputStream os = compression.getOutputStream(baos))
								{
									os.write(IOUtils.toByteArray(is));
								}
								chunks[i] = baos.toByteArray();
							}
						}
					}
				}
			}
		}
	}

	public void saveToFile(File mca, CompressionScheme preferred) throws IOException
	{
		if(preferred == CompressionScheme.None)
		{
			throw new IllegalArgumentException("Minecraft does not support uncompressed region files");
		}
		try(RandomAccessFile region = new RandomAccessFile(mca, "rw"))
		{
			long offset = CHUNK_SECTORS_START;
			for(int i = 0; i < MAX_CHUNKS; ++i)
			{
				region.seek(LOCATIONS_SECTOR_START + i*4);
				new LocationPair(0, 0).serialize(region);

				region.seek(TIMESTAMPS_SECTOR_START + i*4);
				region.writeInt(timestamps[i]);

				if(chunks[i] != null)
				{
					final byte[] chunk;
					try(InputStream is = compression.getInputStream(new ByteArrayInputStream(chunks[i])))
					{
						try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
						{
							try(OutputStream os = preferred.getOutputStream(baos))
							{
								os.write(IOUtils.toByteArray(is));
							}
							chunk = baos.toByteArray();
						}
					}
					region.seek(offset);
					region.writeInt(chunk.length+1);
					region.writeByte(preferred.getId());
					region.write(chunk);

					final LocationPair loc = new LocationPair(offset, region.getFilePointer()-offset);
					offset = nextSector(region.getFilePointer());
					region.seek(LOCATIONS_SECTOR_START + i*4);
					loc.serialize(region);
				}
			}
		}
	}

	@Override
	public Chunk getChunk(LocChunkInRegion pos) throws IOException
	{
		final int index = chunkIndex(pos);
		if(chunks[index] != null)
		{
			return new Chunk((Tag.Compound)Tag.deserialize(compression.getInputStream(new ByteArrayInputStream(chunks[index]))));
		}
		return null;
	}
	@Override
	public int getTimestamp(LocChunkInRegion pos)
	{
		return timestamps[chunkIndex(pos)];
	}

	@Override
	public void setChunk(LocChunkInRegion pos, Chunk c) throws IOException
	{
		final int index = chunkIndex(pos);
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			try(OutputStream os = compression.getOutputStream(baos))
			{
				c.ToNBT("").serialize(os);
			}
			chunks[index] = baos.toByteArray();
		}
	}
	@Override
	public void setTimestamp(LocChunkInRegion pos, int timestamp)
	{
		timestamps[chunkIndex(pos)] = timestamp;
	}
}
