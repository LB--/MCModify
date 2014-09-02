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
public final class World
{
	private final File dir;
	/**
	 * Construct this instance from either the directory of the world
	 * or the "level.dat" file of the world. Neither has to exist.
	 * @param level
	 */
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

	public File getDirectory()
	{
		return dir;
	}

	public File getLevelFile()
	{
		return new File(getDirectory(), "level.dat");
	}
	/**
	 * Returns whether this world exists and could be seen by Minecraft.
	 * @return Whether this world exists and could be seen by Minecraft.
	 */
	public boolean exists()
	{
		return dir.exists() && getLevelFile().exists();
	}

	/**
	 * Thrown when the world indicated by {@link NotLockedException#getWorld()} was going
	 * to be used while unlocked.
	 */
	public final class NotLockedException extends IllegalStateException
	{
		private NotLockedException()
		{
			super("World is no longer locked by this instance/program");
		}

		/**
		 * Returns the {@link World} instance that would have been illegitimately used.
		 * @return The {@link World} instance that would have been illegitimately used.
		 */
		public World getWorld()
		{
			return World.this;
		}
	}

	private File getLockFile()
	{
		return new File(getDirectory(), "session.lock");
	}
	private Long locktimestamp = null;
	/**
	 * //
	 * @throws IOException
	 */
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
	/**
	 * If {@link #isLocked()} returns false, throws {@link NotLockedException}.
	 * @throws NotLockedException if {@link #isLocked()} returns false.
	 */
	public void throwIfNotLocked() throws NotLockedException
	{
		if(!isLocked())
		{
			throw new NotLockedException();
		}
	}

	//
}
