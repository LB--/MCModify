package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.nbt.FormatException;

import java.io.IOException;

/**
 * @see <a href="http://minecraft.gamepedia.com/Region_file_format">Region file format</a> on the Minecraft Wiki
 */
public abstract class Region
{
	public abstract Chunk getChunk(int x, int z) throws FormatException, IOException;
	public abstract int getTimestamp(int x, int z) throws IOException;
	public abstract void setChunk(int x, int z, Chunk c) throws IOException;
	public abstract void setTimestamp(int x, int z, int timestamp) throws IOException;
}
