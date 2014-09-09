package com.lb_stuff.mcmodify.location;

import com.lb_stuff.mcmodify.minecraft.Region;

/**
 * Location of a Region in a Dimension.
 */
public class LocRegionInDimension
{
	public final int x;
	public final int z;
	public LocRegionInDimension(int cx, int cz)
	{
		x = cx;
		z = cz;
	}

	/**
	 * Returns whether this region contains the given chunk.
	 * @param loc The chunk being tested.
	 * @return Whether this region contains the given chunk.
	 */
	public boolean containsChunk(LocChunkInDimension loc)
	{
		return
			(x+0)*Region.CHUNK_X_SIZE <= loc.x &&
			(x+1)*Region.CHUNK_X_SIZE >  loc.x &&
			(z+0)*Region.CHUNK_Z_SIZE <= loc.z &&
			(z+1)*Region.CHUNK_Z_SIZE >  loc.z;
	}
}
