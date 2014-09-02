package com.lb_stuff.mcmodify.minecraft;

import java.io.File;

/**
 * @see <a href="http://minecraft.gamepedia.com/Dimension">Dimension</a> on the Minecraft Wiki
 */
public abstract class Dimension
{
	/**
	 * -1
	 */
	public static final Dimension NETHER = new Dimension()
	{
		@Override
		public int getId()
		{
			return -1;
		}

		@Override
		public File getFolder(World world)
		{
			return new File(world.getDirectory(), "DIM-1");
		}
	};
	/**
	 * 0
	 */
	public static final Dimension OVERWORLD = new Dimension()
	{
		@Override
		public int getId()
		{
			return 0;
		}

		@Override
		public File getFolder(World world)
		{
			return new File(world.getDirectory(), "region");
		}
	};
	/**
	 * 1
	 */
	public static final Dimension END = new Dimension()
	{
		@Override
		public int getId()
		{
			return 1;
		}

		@Override
		public File getFolder(World world)
		{
			return new File(world.getDirectory(), "DIM1");
		}
	};

	private Dimension()
	{
	}

	/**
	 * Returns the dimension representing the given ID.
	 * @param id The dimension ID.
	 * @return The dimension representing the given ID.
	 */
	public static Dimension fromId(final int id)
	{
		switch(id)
		{
			case -1: return NETHER;
			case  0: return OVERWORLD;
			case +1: return END;
		}
		return new Dimension()
		{
			@Override
			public int getId()
			{
				return id;
			}

			@Override
			public File getFolder(World world)
			{
				return null;
			}
		};
	}
	/**
	 * Returns the getId of this dimension constant.
	 * @return The getId of this dimension constant.
	 */
	public abstract int getId();

	/**
	 * Returns the folder containing this dimension for the given {@link World}.
	 * @param world The {@link World} for which to get the folder.
	 * @return The folder containing this dimension.
	 */
	public abstract File getFolder(World world);
}
