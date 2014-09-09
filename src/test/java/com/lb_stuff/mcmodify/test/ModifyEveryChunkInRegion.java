package com.lb_stuff.mcmodify.test;

import com.lb_stuff.mcmodify.minecraft.Chunk;
import com.lb_stuff.mcmodify.minecraft.FileRegion;
import com.lb_stuff.mcmodify.minecraft.Mob;

import java.io.File;

public class ModifyEveryChunkInRegion
{
	public static void main(String[] args) throws Throwable
	{
		FileRegion region = new FileRegion(new File("r.0.0.mca.in"));
		FileRegion newregion = new FileRegion(new File("r.0.0.mca.out"));
		for(int x = 0; x < 31; ++x)
		{
			for(int z = 0; z < 31; ++z)
			{
				Chunk chunk = region.getChunk(x, z);
				if(chunk != null)
				{
					chunk.Entities().add(new Mob.EnderDragon(x*16+8, 96, z*16+8));
				}
				newregion.setChunk(x, z, chunk);
			}
		}
	}
}
