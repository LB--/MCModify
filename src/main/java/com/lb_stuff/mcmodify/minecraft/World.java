package com.lb_stuff.mcmodify.minecraft;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @see <a href="http://minecraft.gamepedia.com/Level_format">Level format</a> on the Minecraft Wiki
 */
public class World
{
	private final File dir;
	public World(File level)
	{
		if(level.isDirectory())
		{
			dir = level;
			return;
		}
		else if(level.isFile() && level.getName().equalsIgnoreCase("level.dat"))
		{
			dir = level.getParentFile();
			return;
		}
		throw new IllegalArgumentException("\"level\" must be the world directory or level.dat file");
	}

	private File getLockFile()
	{
		return new File(dir, "session.lock");
	}
	private Long locktimestamp = null;
	public void lock() throws IOException
	{
		File f = getLockFile();
		if(!f.delete() || !f.createNewFile())
		{
			throw new IOException("Cannot recreate session.lock");
		}
		Long t;
		try(PrintWriter pw = new PrintWriter(f))
		{
			t = System.currentTimeMillis();
			pw.print((long)t);
		}
		locktimestamp = t;
	}
	public boolean isLocked()
	{
		if(locktimestamp == null)
		{
			return false;
		}
		try(Scanner s = new Scanner(getLockFile()))
		{
			return locktimestamp.equals(s.nextLong());
		}
		catch(IOException|NoSuchElementException e)
		{
			return false;
		}
	}
	public void throwIfNotLocked() throws IllegalStateException
	{
		if(!isLocked())
		{
			throw new IllegalStateException("World is no longer locked by this instance/program");
		}
	}

	//
}
