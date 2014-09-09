package com.lb_stuff.mcmodify.location;

import com.lb_stuff.mcmodify.minecraft.Region;

/**
 * Location of a Chunk in a Dimension.
 */
public class LocChunkInDimension
{
	public final int x;
	public final int z;
	public LocChunkInDimension(int cx, int cz)
	{
		x = cx;
		z = cz;
	}

	/**
	 * Returns the location of this chunk in a region.
	 * @return The location of this chunk in a region.
	 */
	public LocChunkInRegion getLocInRegion()
	{
		return new LocChunkInRegion(x - x/Region.CHUNK_X_SIZE*Region.CHUNK_X_SIZE, z - z/Region.CHUNK_Z_SIZE*Region.CHUNK_Z_SIZE);
	}
	/**
	 * Returns the region this chunk is in.
	 * @return The region this chunk is in.
	 */
	public LocRegionInDimension getRegionLoc()
	{
		return new LocRegionInDimension(x/Region.CHUNK_X_SIZE, z/Region.CHUNK_Z_SIZE);
	}
}
