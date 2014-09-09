package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.nbt.FormatException;
import com.lb_stuff.mcmodify.nbt.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @see <a href="http://minecraft.gamepedia.com/Chunk_format">Chunk format</a> on the Minecraft Wiki
 */
public class Chunk
{
	/**
	 * The chunk coordinates.
	 */
	private int xpos, zpos;
	/**
	 * The tick when this chunk was last updated.
	 */
	private long lastupdate;
	/**
	 * Whether special map features such as ores and structures have been generated.
	 */
	private boolean terrainpopulated;
	/**
	 * Total number of ticks this chunk has been inhabited by players.
	 */
	private long inhabitedtime;
	/**
	 * Biome data, 16x16.
	 */
	private byte[] biomes;
	/**
	 * Used for sky light calculation, 16x16.
	 */
	private int[] heightmap = new int[256];
	/**
	 * Section
	 */
	private static class Section
	{
		/**
		 * The blocks in this section.
		 */
		byte[] blocks, add, data;
		/**
		 * The light in this section.
		 */
		byte[] blocklight, skylight;

		/**
		 * Constructs a Section from the given tag.
		 * @param section The tag fromm which to construct this Section.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Section(Tag.Compound section) throws FormatException
		{
			blocks = ((Tag.ByteArray)section.find(Tag.Type.BYTEARRAY, "Blocks")).v;
			try
			{
				add = ((Tag.ByteArray)section.find(Tag.Type.BYTEARRAY, "Add")).v;
			}
			catch(FormatException e)
			{
				add = new byte[2048];
			}
			data = ((Tag.ByteArray)section.find(Tag.Type.BYTEARRAY, "Data")).v;
			blocklight = ((Tag.ByteArray)section.find(Tag.Type.BYTEARRAY, "BlockLight")).v;
			skylight = ((Tag.ByteArray)section.find(Tag.Type.BYTEARRAY, "SkyLight")).v;
		}
		/**
		 * Constructs an empty Section.
		 */
		public Section()
		{
			blocks = new byte[4096];
			add = new byte[2048];
			data = new byte[2048];
			blocklight = new byte[2048];
			skylight = new byte[2048];
			for(int i = 0; i < 2048; ++i)
			{
				skylight[i] = 15; //full exposure to sky
			}
		}

		/**
		 * Returns whether this section has only air blocks.
		 * @return Whether this section has only air blocks.
		 */
		public boolean Empty()
		{
			for(byte block : blocks)
			{
				if(block != 0)
				{
					return false;
				}
			}
			for(byte block : add)
			{
				if(block != 0)
				{
					return false;
				}
			}
			return true;
		}

		/**
		 * Returns the tag for this section.
		 * @param name The name that the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this section.
		 */
		public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = new Tag.Compound(name, new Tag.ByteArray("Blocks", blocks),
													new Tag.ByteArray("Data", data),
													new Tag.ByteArray("BlockLight", blocklight),
													new Tag.ByteArray("SkyLight", skylight));
			for(byte b : add)
			{
				if(b != 0)
				{
					t.add(new Tag.ByteArray("Add", add));
					break;
				}
			}
			return t;
		}
		/**
		 * Returns the tag for this section.
		 * @param name The name that the compound tag should have, or null if the compound tag should not have a name.
		 * @param y The Y index this section should have.
		 * @return The tag for this section.
		 */
		public Tag.Compound ToNBT(String name, byte y)
		{
			Tag.Compound t = new Tag.Compound(name, new Tag.Byte("Y", y),
													new Tag.ByteArray("Blocks", blocks),
													new Tag.ByteArray("Data", data),
													new Tag.ByteArray("BlockLight", blocklight),
													new Tag.ByteArray("SkyLight", skylight));
			for(byte b : add)
			{
				if(b != 0)
				{
					t.add(new Tag.ByteArray("Add", add));
					break;
				}
			}
			return t;
		}
	}
	/**
	 * The sections in this chunk.
	 */
	private Map<Byte/*Y*/, Section> sections = new HashMap<>();
	/**
	 * The entities in this chunk.
	 */
	private List<Entity> entities = new ArrayList<>();
	/**
	 * The tile entities in this chunk.
	 */
	private List<TileEntity> tileentities = new ArrayList<>();
	/**
	 * Tile Tick
	 */
	public static class TileTick
	{
		/**
		 * The block ID.
		 */
		private int i;
		/**
		 * The number of ticks until processing should occur.
		 */
		private int t;
		/**
		 * The position of this tile tick.
		 */
		private int x, y, z;

		/**
		 * Constructs a Tile Tick object from a tile tick compound tag.
		 * @param tiletick The tag from which to instantiate this tile tick.
		 * @throws FormatException If the given tag is invalid.
		 */
		public TileTick(Tag.Compound tiletick) throws FormatException
		{
			i = ((Tag.Int)tiletick.find(Tag.Type.INT, "i")).v;
			t = ((Tag.Int)tiletick.find(Tag.Type.INT, "t")).v;
			x = ((Tag.Int)tiletick.find(Tag.Type.INT, "x")).v;
			y = ((Tag.Int)tiletick.find(Tag.Type.INT, "y")).v;
			z = ((Tag.Int)tiletick.find(Tag.Type.INT, "z")).v;
		}

		/**
		 * Returns the block ID for this tile tick.
		 * @return The block ID for this tile tick.
		 */
		public int BlockID()
		{
			return i;
		}
		/**
		 * Sets the block ID for this tile tick.
		 * @param id The block ID for this tile tick.
		 */
		public void BlockID(int id)
		{
			i = id;
		}

		/**
		 * Returns the number of ticks before this tile tick should be processed.
		 * @return The number of ticks before this tile tick should be processed.
		 */
		public int Ticks()
		{
			return t;
		}
		/**
		 * Sets the number of ticks before this tile tick should be processed.
		 * @param ticks The number of ticks before this tile tick should be processed.
		 */
		public void Ticks(int ticks)
		{
			t = ticks;
		}

		/**
		 * Returns the X coordinate of this tile tick.
		 * @return The X coordinate of this tile tick.
		 */
		public int X()
		{
			return x;
		}
		/**
		 * Returns the Y coordinate of this tile tick.
		 * @return The Y coordinate of this tile tick.
		 */
		public int Y()
		{
			return y;
		}
		/**
		 * Returns the Z coordinate of this tile tick.
		 * @return The Z coordinate of this tile tick.
		 */
		public int Z()
		{
			return z;
		}
		/**
		 * Sets the X coordinate of this tile tick.
		 * @param x The X coordinate of this tile tick.
		 */
		public void X(int x)
		{
			this.x = x;
		}
		/**
		 * Sets the Y coordinate of this tile tick.
		 * @param y The Y coordinate of this tile tick.
		 */
		public void Y(int y)
		{
			this.y = y;
		}
		/**
		 * Sets the Z coordinate of this tile tick.
		 * @param z The Z coordinate of this tile tick.
		 */
		public void Z(int z)
		{
			this.z = z;
		}

		/**
		 * Returns the tag for this tile tick.
		 * @param name The name that the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this tile tick.
		 */
		public Tag.Compound ToNBT(String name)
		{
			return new Tag.Compound(name, new Tag.Int("i", i),
										  new Tag.Int("t", t),
										  new Tag.Int("x", x),
										  new Tag.Int("y", y),
										  new Tag.Int("z", z));
		}
	}
	/**
	 * The Tile Ticks in this chunk.
	 */
	private List<TileTick> tileticks = new ArrayList<>();

	public Chunk(Tag.Compound chunk) throws FormatException
	{
		Tag.Compound original = chunk;
		chunk = (Tag.Compound)chunk.find(Tag.Type.COMPOUND, "Level");
		xpos = ((Tag.Int)chunk.find(Tag.Type.INT, "xPos")).v;
		zpos = ((Tag.Int)chunk.find(Tag.Type.INT, "zPos")).v;
		lastupdate = ((Tag.Long)chunk.find(Tag.Type.LONG, "LastUpdate")).v;
		try
		{
			inhabitedtime = ((Tag.Long)chunk.find(Tag.Type.LONG, "InhabitedTime")).v;
		}
		catch(FormatException e)
		{
			inhabitedtime = 0;
		}
		try
		{
			terrainpopulated = ((Tag.Byte)chunk.find(Tag.Type.BYTE, "TerrainPopulated")).v != 0 ? true : false;
		}
		catch(FormatException e)
		{
			terrainpopulated = false;
		}
		try
		{
			biomes = ((Tag.ByteArray)chunk.find(Tag.Type.BYTEARRAY, "Biomes")).v;
		}
		catch(FormatException e)
		{
			biomes = new byte[256];
			for(int i = 0; i < 256; ++i)
			{
				biomes[i] = -1;
			}
		}
		if(biomes.length != 256)
		{
			throw new FormatException("Invalid Biomes Array; size was "+biomes.length+" instead of 256", original);
		}
		heightmap = ((Tag.IntArray)chunk.find(Tag.Type.INTARRAY, "HeightMap")).v;
		if(heightmap.length != 256)
		{
			throw new FormatException("Invalid Height Map Array; size was "+heightmap.length+" instead of 256", original);
		}
		Tag.List sectionlist = (Tag.List)chunk.find(Tag.Type.LIST, "Sections");
		if(sectionlist.getContainedType() != Tag.Type.COMPOUND)
		{
			throw new FormatException("Invalid Sections list; expected list of Compound, got list of "+sectionlist.getContainedType(), original);
		}
		for(Tag t : sectionlist)
		{
			Tag.Compound section = (Tag.Compound)t;
			sections.put(((Tag.Byte)section.find(Tag.Type.BYTE, "Y")).v, new Section(section));
		}
		Tag.List entitylist = (Tag.List)chunk.find(Tag.Type.LIST, "Entities");
		if(entitylist.getSize() > 0 && entitylist.getContainedType() != Tag.Type.COMPOUND)
		{
			throw new FormatException("Invalid Entities list; expected list of Compound, got list of "+entitylist.getContainedType(), original);
		}
		for(Tag t : entitylist)
		{
			Tag.Compound entity = (Tag.Compound)t;
			Constructor<? extends Entity> econs;
			try
			{
				econs = Entity.ClassFromID(entity).getDeclaredConstructor(Tag.Compound.class);
				econs.setAccessible(true);
			}
			catch(NoSuchMethodException e)
			{
				econs = null;
			}
			try
			{
				entities.add(econs.newInstance(entity));
			}
			catch(InstantiationException|IllegalAccessException|InvocationTargetException e)
			{
				throw new FormatException(e, original);
			}
		}
		Tag.List tileentitylist = (Tag.List)chunk.find(Tag.Type.LIST, "TileEntities");
		if(tileentitylist.getSize() > 0 && tileentitylist.getContainedType() != Tag.Type.COMPOUND)
		{
			throw new FormatException("Invalid Tile Entities list; expected list of Compound, got list of "+tileentitylist.getContainedType(), original);
		}
		for(Tag t : tileentitylist)
		{
			Tag.Compound tileentity = (Tag.Compound)t;
			Constructor<? extends TileEntity> tecons;
			try
			{
				tecons = TileEntity.ClassFromID(((Tag.String)tileentity.find(Tag.Type.STRING, "id")).v).getDeclaredConstructor(Tag.Compound.class);
				tecons.setAccessible(true);
			}
			catch(NoSuchMethodException e)
			{
				tecons = null;
			}
			try
			{
				tileentities.add(tecons.newInstance(tileentity));
			}
			catch(InstantiationException|IllegalAccessException|InvocationTargetException e)
			{
				throw new FormatException(e, original);
			}
		}
		Tag.List tileticklist;
		try
		{
			tileticklist = (Tag.List)chunk.find(Tag.Type.LIST, "TileTicks");
		}
		catch(FormatException e)
		{
			tileticklist = new Tag.List(null, Tag.Type.COMPOUND);
		}
		if(tileticklist.getContainedType() != Tag.Type.COMPOUND)
		{
			throw new FormatException("Invalid Tile Tick list; expected list of Compound, got list of "+tileticklist.getContainedType(), original);
		}
		for(Tag t : tileticklist)
		{
			tileticks.add(new TileTick((Tag.Compound)t));
		}
	}
	public Chunk(int x, int z) throws IllegalArgumentException
	{
		if(x < 0 || x > 31
		|| z < 0 || z > 31)
		{
			throw new IllegalArgumentException("Invalid Chunk Coordinates: ("+x+", "+z+")");
		}
		lastupdate = 0;
		terrainpopulated = false;
		biomes = new byte[256];
		for(int i = 0; i < 256; ++i)
		{
			biomes[i] = -1;
		}
	}

	/**
	 * Utility function, returns the nibble at the given index in the given 4-bit value array.
	 * @param arr The 4-bit value array, full of nibbles.
	 * @param index The index of the nibble to taste.
	 * @return The specified nibble in the array.
	 */
	private static byte Nibble4(byte[] arr, int index)
	{
		return (byte)((index&1) == 0 ? arr[index/2]&0x0F : arr[index/2]>>4);
	}
	/**
	 * Utility function, sets the nibble at the given index in the given 4-bit value array.
	 * @param arr The 4-bit value array, full of nibbles.
	 * @param index The index of the nibble to bite.
	 * @param v4 The new 4-bit value to set the nibble to.
	 */
	private static void Nibble4(byte[] arr, int index, byte v4)
	{
		byte n = arr[index/2];
		arr[index/2] = (byte)((index&1) == 0 ? ((n>>4)<<4)+(v4&0x0F) : (n&0x0F)+((v4&0x0F)<<4)); //could use optimizing...
	}

	/**
	 * Returns the ID of the block at the given coordinates, or -1 if the coordinates are invalid.
	 * @param x The X coordinate of the block.
	 * @param y The Y coordinate of the block.
	 * @param z The Z coordinate of the block.
	 * @return The ID of the block at the given coordinates, or -1 if the coordinates are invalid.
	 */
	public short BlockID(int x, int y, int z)
	{
		if(x < 0 || x > 15
		|| y < 0 || y > 255
		|| z < 0 || z > 15)
		{
			return -1;
		}
		if(!sections.containsKey((byte)(y/16)))
		{
			return 0;
		}
		Section s = sections.get((byte)(y/16));
		y %= 16;
		return (short)(s.blocks[y*16*16 + z*16 + x] + (Nibble4(s.add, y*16*16 + z*16 + x)<<8));
	}
	/**
	 * Sets the ID of the block at the given coordinates, or does nothing if the coordinates are invalid.
	 * @param x The X coordinate of the block.
	 * @param y The Y coordinate of the block.
	 * @param z The Z coordinate of the block.
	 * @param ID The ID of the block at the given coordinates.
	 */
	public void BlockID(int x, int y, int z, short ID)
	{
		if(x < 0 || x > 15
		|| y < 0 || y > 255
		|| z < 0 || z > 15)
		{
			return;
		}
		Section s;
		if(!sections.containsKey((byte)(y/16)))
		{
			sections.put((byte)(y/16), s = new Section());
		}
		else
		{
			s = sections.get((byte)(y/16));
		}
		y %= 16;
		s.blocks[y*16*16 + z*16 + x] = (byte)ID;
		Nibble4(s.add, y*16*16 + z*16 + x, (byte)(ID>>8));
	}

	/**
	 * Returns the Data of the block at the given coordinates, or -1 if the coordinates are invalid.
	 * @param x The X coordinate of the block.
	 * @param y The Y coordinate of the block.
	 * @param z The Z coordinate of the block.
	 * @return The Data of the block at the given coordinates, or -1 if the coordinates are invalid.
	 */
	public byte BlockData(int x, int y, int z)
	{
		if(x < 0 || x > 15
		|| y < 0 || y > 255
		|| z < 0 || z > 15)
		{
			return -1;
		}
		if(!sections.containsKey((byte)(y/16)))
		{
			return 0;
		}
		Section s = sections.get((byte)(y/16));
		y %= 16;
		return Nibble4(s.data, y*16*16 + z*16 + x);
	}
	/**
	 * Sets the Data of the block at the given coordinates, or does nothing if the coordinates are invalid.
	 * @param x The X coordinate of the block.
	 * @param y The Y coordinate of the block.
	 * @param z The Z coordinate of the block.
	 * @param nibble The Data of the block at the given coordinates.
	 */
	public void BlockData(int x, int y, int z, byte nibble)
	{
		if(x < 0 || x > 15
		|| y < 0 || y > 255
		|| z < 0 || z > 15)
		{
			return;
		}
		Section s;
		if(!sections.containsKey((byte)(y/16)))
		{
			sections.put((byte)(y/16), s = new Section());
		}
		else
		{
			s = sections.get((byte)(y/16));
		}
		y %= 16;
		Nibble4(s.data, y*16*16 + z*16 + x, nibble);
	}

	/**
	 * Returns the Block Light of the block at the given coordinates, or -1 if the coordinates are invalid.
	 * @param x The X coordinate of the block.
	 * @param y The Y coordinate of the block.
	 * @param z The Z coordinate of the block.
	 * @return The Block Light of the block at the given coordinates, or -1 if the coordinates are invalid.
	 */
	public byte BlockLight(int x, int y, int z)
	{
		if(x < 0 || x > 15
		|| y < 0 || y > 255
		|| z < 0 || z > 15)
		{
			return -1;
		}
		if(!sections.containsKey((byte)(y/16)))
		{
			return 0;
		}
		Section s = sections.get((byte)(y/16));
		y %= 16;
		return Nibble4(s.blocklight, y*16*16 + z*16 + x);
	}
	/**
	 * Sets the Block Light of the block at the given coordinates, or does nothing if the coordinates are invalid.
	 * @param x The X coordinate of the block.
	 * @param y The Y coordinate of the block.
	 * @param z The Z coordinate of the block.
	 * @param nibble The Block Light of the block at the given coordinates.
	 */
	public void BlockLight(int x, int y, int z, byte nibble)
	{
		if(x < 0 || x > 15
		|| y < 0 || y > 255
		|| z < 0 || z > 15)
		{
			return;
		}
		Section s;
		if(!sections.containsKey((byte)(y/16)))
		{
			sections.put((byte)(y/16), s = new Section());
		}
		else
		{
			s = sections.get((byte)(y/16));
		}
		y %= 16;
		Nibble4(s.blocklight, y*16*16 + z*16 + x, nibble);
	}

	/**
	 * Returns the Sky Light of the block at the given coordinates, or -1 if the coordinates are invalid.
	 * @param x The X coordinate of the block.
	 * @param y The Y coordinate of the block.
	 * @param z The Z coordinate of the block.
	 * @return The Sky Light of the block at the given coordinates, or -1 if the coordinates are invalid.
	 */
	public byte SkyLight(int x, int y, int z)
	{
		if(x < 0 || x > 15
		|| y < 0 || y > 255
		|| z < 0 || z > 15)
		{
			return -1;
		}
		if(!sections.containsKey((byte)(y/16)))
		{
			return 15; //full exposure to sky
		}
		Section s = sections.get((byte)(y/16));
		y %= 16;
		return Nibble4(s.skylight, y*16*16 + z*16 + x);
	}
	/**
	 * Sets the Sky Light of the block at the given coordinates, or does nothing if the coordinates are invalid.
	 * @param x The X coordinate of the block.
	 * @param y The Y coordinate of the block.
	 * @param z The Z coordinate of the block.
	 * @param nibble The Sky Light of the block at the given coordinates.
	 */
	public void SkyLight(int x, int y, int z, byte nibble)
	{
		if(x < 0 || x > 15
		|| y < 0 || y > 255
		|| z < 0 || z > 15)
		{
			return;
		}
		Section s;
		if(!sections.containsKey((byte)(y/16)))
		{
			sections.put((byte)(y/16), s = new Section());
		}
		else
		{
			s = sections.get((byte)(y/16));
		}
		y %= 16;
		Nibble4(s.skylight, y*16*16 + z*16 + x, nibble);
	}

	/**
	 * Returns the X chunk coordinate.
	 * @return The X chunk coordinate.
	 */
	public int PosX()
	{
		return xpos;
	}
	/**
	 * Returns the Z chunk coordinate.
	 * @return The Z chunk coordinate.
	 */
	public int PosZ()
	{
		return zpos;
	}
	/**
	 * Sets the chunk coordinates.
	 * @param x The X chunk coordinate.
	 * @param z The Z chunk coordinate.
	 * @throws IllegalArgumentException if the chunk coordinates are invalid.
	 */
	public void Pos(int x, int z) throws IllegalArgumentException
	{
		if(x < 0 || x > 31
		|| z < 0 || z > 31)
		{
			throw new IllegalArgumentException("Invalid Chunk Coordinates: ("+x+", "+z+")");
		}
		xpos = x;
		zpos = z;
	}

	/**
	 * Returns the tick when this chunk was last updated.
	 * @return The tick when this chunk was last updated.
	 */
	public long LastTick()
	{
		return lastupdate;
	}
	/**
	 * Sets the tick when this chunk was last updated.
	 * @param tick The tick when this chunk was last updated.
	 */
	public void LastTick(long tick)
	{
		lastupdate = tick;
	}

	/**
	 * Returns whether this chunk has been filed with special features such as ores, streams, lakes, structures, etc.
	 * @return Whether this chunk has been filed with special features such as ores, streams, lakes, structures, etc.
	 */
	public boolean Populated()
	{
		return terrainpopulated;
	}
	/**
	 * Sets whether this chunk has been filed with special features such as ores, streams, lakes, structures, etc.
	 * @param populated Whether this chunk has been filed with special features such as ores, streams, lakes, structures, etc.
	 */
	public void Populated(boolean populated)
	{
		terrainpopulated = populated;
	}

	/**
	 * Returns how many ticks the chunk has been inhabited by players.
	 * @return how many ticks the chunk has been inhabited by players.
	 */
	public long InhabitedTime()
	{
		return inhabitedtime;
	}
	/**
	 * Sets the number of ticks this chunk has been inhabited by players.
	 * @param ticks the number of ticks this chunk has been inhabited by players. May be negative.
	 */
	public void InhabitedTime(long ticks)
	{
		inhabitedtime = ticks;
	}

	/**
	 * Returns the Biome ID at the given column coordinates.
	 * <br>
	 * <br>-1	(Uncalculated)
	 * <br>0	Ocean
	 * <br>1	Plains
	 * <br>2	Desert
	 * <br>3	Extreme Hills
	 * <br>4	Forest
	 * <br>5	Taiga
	 * <br>6	Swampland
	 * <br>7	River
	 * <br>8	Hell
	 * <br>9	Sky
	 * <br>10	Frozen Ocean
	 * <br>11	Frozen River
	 * <br>12	Ice Plains
	 * <br>13	Ice Mountains
	 * <br>14	Mushroom Island
	 * <br>15	Mushroom Island Shore
	 * <br>16	Beach
	 * <br>17	Desert Hills
	 * <br>18	Forest Hills
	 * <br>19	Taiga Hills
	 * <br>20	Extreme Hills Edge
	 * <br>21	Jungle
	 * <br>22	Jungle Hills
	 * @param x The X coordinate of the column.
	 * @param z The Z coordinate of the column.
	 * @return The Biome ID at the given column coordinates.
	 * @throws IllegalArgumentException if the given coordinates are invalid.
	 */
	public byte Biome(int x, int z) throws IllegalArgumentException
	{
		if(x < 0 || x > 15
		|| z < 0 || z > 15)
		{
			throw new IllegalArgumentException("Invalid column coordinates: ("+x+", "+z+")");
		}
		return biomes[x*16 + z];
	}
	/**
	 * Sets the Biome ID at the given column coordinates.
	 * <br>
	 * <br>-1	(Uncalculated)
	 * <br>0	Ocean
	 * <br>1	Plains
	 * <br>2	Desert
	 * <br>3	Extreme Hills
	 * <br>4	Forest
	 * <br>5	Taiga
	 * <br>6	Swampland
	 * <br>7	River
	 * <br>8	Hell
	 * <br>9	Sky
	 * <br>10	Frozen Ocean
	 * <br>11	Frozen River
	 * <br>12	Ice Plains
	 * <br>13	Ice Mountains
	 * <br>14	Mushroom Island
	 * <br>15	Mushroom Island Shore
	 * <br>16	Beach
	 * <br>17	Desert Hills
	 * <br>18	Forest Hills
	 * <br>19	Taiga Hills
	 * <br>20	Extreme Hills Edge
	 * <br>21	Jungle
	 * <br>22	Jungle Hills
	 * @param x The X coordinate of the column.
	 * @param z The Z coordinate of the column.
	 * @param id The Biome ID.
	 * @throws IllegalArgumentException if the given coordinates are invalid.
	 */
	public void Biome(int x, int z, byte id) throws IllegalArgumentException
	{
		if(x < 0 || x > 15
		|| z < 0 || z > 15)
		{
			throw new IllegalArgumentException("Invalid column coordinates: ("+x+", "+z+")");
		}
		biomes[x*16 + z] = id;
	}

	/**
	 * Returns the Y coordinate of the block where the light from the sky is at full strength.
	 * @param x The X coordinate of the column.
	 * @param z The Z coordinate of the column.
	 * @return The Y coordinate of the block where the light from the sky is at full strength.
	 * @throws IllegalArgumentException if the given coordinates are invalid.
	 */
	public int HeightMap(int x, int z) throws IllegalArgumentException
	{
		if(x < 0 || x > 15
		|| z < 0 || z > 15)
		{
			throw new IllegalArgumentException("Invalid column coordinates: ("+x+", "+z+")");
		}
		return heightmap[z*16 + x];
	}
	/**
	 * Sets the Y coordinate of the block where the light from the sky is at full strength.
	 * @param x The X coordinate of the column.
	 * @param z The Z coordinate of the column.
	 * @param height The Y coordinate of the block where the light from the sky is at full strength.
	 * @throws IllegalArgumentException if the given coordinates are invalid.
	 */
	public void HeightMap(int x, int z, int height) throws IllegalArgumentException
	{
		if(x < 0 || x > 15
		|| z < 0 || z > 15)
		{
			throw new IllegalArgumentException("Invalid column coordinates: ("+x+", "+z+")");
		}
		heightmap[z*16 + x] = height;
	}

	/**
	 * Returns the free-to-modify list of Entities in this chunk.
	 * @return The free-to-modify list of Entities in this chunk.
	 */
	public List<Entity> Entities()
	{
		return entities;
	}

	/**
	 * Returns the free-to-modify list of Tile Entities in this chunk.
	 * @return The free-to-modify list of Tile Entities in this chunk.
	 */
	public List<TileEntity> TileEntities()
	{
		return tileentities;
	}

	/**
	 * Returns the free-to-modify list of Tile Ticks in this chunk.
	 * @return The free-to-modify list of Tile Ticks in this chunk.
	 */
	public List<TileTick> TileTicks()
	{
		return tileticks;
	}

	/**
	 * Returns whether this chunk contains only air and no entities, tile entities, or tile ticks.
	 * @return Whether this chunk contains only air and no entities, tile entities, or tile ticks.
	 */
	public boolean Empty()
	{
		if(tileentities.size() > 0
		|| entities.size() > 0
		|| tileticks.size() > 0)
		{
			return false;
		}
		for(Section s : sections.values())
		{
			if(!s.Empty())
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the tag for this chunk.
	 * @param name The name that the compound tag should have, or null if the compound tag should not have a name.
	 * @return The tag for this chunk.
	 */
	public Tag.Compound ToNBT(String name)
	{
		Tag.List sectionlist = new Tag.List("Sections", Tag.Type.COMPOUND),
				 entitylist = new Tag.List("Entities", Tag.Type.COMPOUND),
				 tileentitylist = new Tag.List("TileEntities", Tag.Type.COMPOUND),
				 tileticklist = new Tag.List("TileTicks", Tag.Type.COMPOUND);
		for(Map.Entry<Byte, Section> s : sections.entrySet())
		{
			Section sec = s.getValue();
			if(!sec.Empty())
			{
				sectionlist.add(sec.ToNBT(null, s.getKey()));
			}
		}
		for(Entity e : entities)
		{
			entitylist.add(e.ToNBT(null));
		}
		for(TileEntity te : tileentities)
		{
			tileentitylist.add(te.ToNBT(null));
		}
		for(TileTick tt : tileticks)
		{
			tileticklist.add(tt.ToNBT(null));
		}
		Tag.Compound t = new Tag.Compound("Level", new Tag.Int("xPos", xpos),
												   new Tag.Int("zPos", zpos),
												   new Tag.Long("LastUpdate", lastupdate),
		                                           new Tag.Long("InhabitedTime", inhabitedtime),
												   new Tag.Byte("TerrainPopulated", (byte)(terrainpopulated?1:0)),
												   new Tag.IntArray("HeightMap", heightmap),
												   sectionlist,
												   entitylist,
												   tileentitylist,
												   tileticklist);
		for(byte b : biomes)
		{
			if(b != -1)
			{
				t.add(new Tag.ByteArray("Biomes", biomes));
				break;
			}
		}
		return new Tag.Compound(name, t);
	}
}
