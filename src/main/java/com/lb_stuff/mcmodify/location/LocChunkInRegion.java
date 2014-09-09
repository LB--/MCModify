package com.lb_stuff.mcmodify.location;

import com.lb_stuff.mcmodify.minecraft.Region;

/**
 * Location of a Chunk in a Region.
 */
public class LocChunkInRegion
{
	public final int x;
	public final int z;
	public LocChunkInRegion(int cx, int cz)
	{
		if(cx < 0 || cx >= 32 || cz < 0 || cz >= 32)
		{
			throw new IllegalArgumentException("Invalid chunk location ("+cx+","+cz+") in region");
		}
		x = cx;
		z = cz;
	}

	/**
	 * Returns the location of this chunk in a dimension.
	 * @param loc The region this chunk is in.
	 * @return The location of this chunk in a dimension.
	 */
	public LocChunkInDimension getLocInDimension(LocRegionInDimension loc)
	{
		return new LocChunkInDimension(loc.x*Region.CHUNK_X_SIZE + x, loc.z*Region.CHUNK_Z_SIZE + z);
	}
}
