package com.lb_stuff.mcmodify.test.minecraft;

import com.lb_stuff.mcmodify.location.LocChunkInRegion;
import com.lb_stuff.mcmodify.minecraft.Chunk;
import com.lb_stuff.mcmodify.minecraft.FileRegion;
import com.lb_stuff.mcmodify.minecraft.Mob;
import com.lb_stuff.mcmodify.test.TestingUtils;

import org.junit.Test;


public class RegionTest
{
	@Test
	public void modifyEveryChunk() throws Throwable
	{
		FileRegion region = new FileRegion(TestingUtils.getInputFile("r.0.0.mca"));
		FileRegion newregion = new FileRegion(TestingUtils.getOutputFile("r.0.0.mca"));
		for(int x = 0; x < 31; ++x)
		{
			for(int z = 0; z < 31; ++z)
			{
				Chunk chunk = region.getChunk(new LocChunkInRegion(x, z));
				if(chunk != null)
				{
					chunk.Entities().add(new Mob.EnderDragon(x*16+8, 96, z*16+8));
				}
				newregion.setChunk(new LocChunkInRegion(x, z), chunk);
			}
		}
	}
}
