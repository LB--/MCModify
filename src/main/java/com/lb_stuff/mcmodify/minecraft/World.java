package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.nbt.FormatException;

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

	/**
	 * Returns the directory of this world.
	 * @return The directory of this world.
	 */
	public File getDirectory()
	{
		return dir;
	}

	/**
	 * Returns the "level.dat" file for this world.
	 * @return The "level.dat" file for this world.
	 */
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
	 * Locks this world, causing any other application with a lock to
	 * give up their lock. The act of locking a nonexistent world will
	 * create the directory if it does not exist, but will not create
	 * the "level.dat" file.
	 * @throws IOException if the locking process fails.
	 */
	public void lock() throws IOException
	{
		File f = getLockFile();
		f.getParentFile().mkdirs();
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
	/**
	 * Returns whether this application and instance still own the lock.
	 * @return Whether this application and instance still own the lock.
	 */
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

	/**
	 * Get a {@link FileRegion} instance tied to this world.
	 * @param d The dimension of the chunk/region.
	 * @param chunkX The X chunk coordinate of the chunk in the world.
	 * @param chunkZ The Z chunk coordinate of the chunk in the world.
	 * @return a {@link FileRegion} instance tied to this world.
	 * @throws IOException if thrown by the FileRegion constructor.
	 */
	public FileRegion getFileRegion(Dimension d, int chunkX, int chunkZ) throws IOException
	{
		d.getFolder(this).mkdirs();
		int rx = (int)Math.floor(chunkX/32.0);
		int rz = (int)Math.floor(chunkZ/32.0);
		return new FileRegion(new File(d.getFolder(this), "r."+rx+"."+rz+".mca"))
		{
			@Override
			public Chunk getChunk(int x, int z) throws FormatException, IOException
			{
				throwIfNotLocked();
				return super.getChunk(x, z);
			}
			@Override
			public int getTimestamp(int x, int z) throws IOException
			{
				throwIfNotLocked();
				return super.getTimestamp(x, z);
			}
			@Override
			public void setChunk(int x, int z, Chunk c) throws IOException
			{
				throwIfNotLocked();
				super.setChunk(x, z, c);
			}
			@Override
			public void setTimestamp(int x, int z, int timestamp) throws IOException
			{
				throwIfNotLocked();
				super.setTimestamp(x, z, timestamp);
			}
		};
	}
}
