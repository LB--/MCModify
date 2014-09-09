package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.nbt.FormatException;
import com.lb_stuff.mcmodify.nbt.Tag;

/**
 * The Tile Entity class.
 */
public abstract class TileEntity
{
	/**
	 * The coordinates of this tile entity.
	 */
	private int x, y, z;

	/**
	 * Given a Tile Entity ID, returns the Class object for the class that represents that Tile Entity ID.
	 * @param ID The Tile Entity ID.
	 * @return The Class object for the class that represents the Tile Entity ID.
	 * @throws FormatException if the given Tike Entity ID is unknown.
	 */
	public static Class<? extends TileEntity> ClassFromID(String ID) throws FormatException
	{
		switch(ID)
		{
		case "Furnace":		return Furnace.class;
		case "Sign":		return Sign.class;
		case "MobSpawner":	return MobSpawner.class;
		case "Chest":		return Chest.class;
		case "Music":		return Music.class;
		case "Trap":		return Trap.class;
		case "RecordPlayer":return RecordPlayer.class;
		case "Piston":		return Piston.class;
		case "Cauldron":	return Cauldron.class;
		case "EnchantTable":return EnchantTable.class;
		case "Airportal":	return Airportal.class;
		}
		throw new FormatException("Unkown Tile Entity ID: \""+ID+"\"");
	}

	/**
	 * Constructs a tile entity from a compound tag.
	 * @param tileentity The tag from which to instantiate this tile entity.
	 * @throws FormatException if the given tag is invalid.
	 */
	public TileEntity(Tag.Compound tileentity) throws FormatException
	{
		String id = ((Tag.String)tileentity.find(Tag.Type.STRING, "id")).v;
		if(!getClass().getSimpleName().equals(id))
		{
			throw new FormatException("Tried to instantiate a "+getClass().getSimpleName()+" Tile Entity from a tag with id \""+id+"\"");
		}
		x = ((Tag.Int)tileentity.find(Tag.Type.INT, "x")).v;
		y = ((Tag.Int)tileentity.find(Tag.Type.INT, "y")).v;
		z = ((Tag.Int)tileentity.find(Tag.Type.INT, "z")).v;
	}

	/**
	 * Returns the tag for this tile entity.
	 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
	 * @return The tag for this tile entity.
	 */
	public Tag.Compound ToNBT(String name)
	{
		return new Tag.Compound(name, new Tag.String("id", getClass().getSimpleName()),
									  new Tag.Int("x", x),
									  new Tag.Int("y", y),
									  new Tag.Int("z", z));
	}

	/**
	 * The tile entity for furnaces to store their items and smelting state.
	 */
	public static class Furnace extends TileEntity
	{
		/**
		 * Number of ticks left before another fuel source must be consumed.
		 */
		private short burntime;
		/**
		 * Number of ticks for which the item has been smelted.
		 */
		private short cooktime;
		/**
		 * The item being smelted.
		 */
		private Inventory.Item smelting;
		/**
		 * The item to be used as fuel.
		 */
		private Inventory.Item fuel;
		/**
		 * The item that results from the smelting process.
		 */
		private Inventory.Item result;

		/**
		 * Constructs a Furnace tile entity from a tile entity tag.
		 * @param furnace The tag from which to construct this Furnace tile entity.
		 * @throws FormatException if the tag is invalid.
		 */
		public Furnace(Tag.Compound furnace) throws FormatException
		{
			super(furnace);

			burntime = ((Tag.Short)furnace.find(Tag.Type.SHORT, "BurnTime")).v;
			cooktime = ((Tag.Short)furnace.find(Tag.Type.SHORT, "CookTime")).v;

			Tag.List items = (Tag.List)furnace.find(Tag.Type.LIST, "Items");
			if(items.getContainedType() != Tag.Type.COMPOUND)
			{
				throw new FormatException("Invalid Items list; expected list of Compound, got list of "+items.getContainedType());
			}
			smelting = new Inventory.Item((Tag.Compound)items.get(0));
			fuel = new Inventory.Item((Tag.Compound)items.get(1));
			result = new Inventory.Item((Tag.Compound)items.get(2));
		}

		/**
		 * Returns the item being smelted.
		 * @return The item being smelted.
		 */
		public Inventory.Item Smelting()
		{
			return smelting;
		}
		/**
		 * Sets the item being smelted.
		 * @param item The item being smelted.
		 */
		public void Smelting(Inventory.Item item)
		{
			smelting = item;
		}

		/**
		 * Returns the item to be used as fuel.
		 * @return The item to be used as fuel.
		 */
		public Inventory.Item Fuel()
		{
			return fuel;
		}
		/**
		 * Sets the item to be used as fuel.
		 * @param item The item to be used as fuel.
		 */
		public void Fuel(Inventory.Item item)
		{
			fuel = item;
		}

		/**
		 * Returns the result item.
		 * @return The result item.
		 */
		public Inventory.Item Result()
		{
			return result;
		}
		/**
		 * Sets the result item.
		 * @param item The result item.
		 */
		public void Result(Inventory.Item item)
		{
			result = item;
		}

		/**
		 * Returns the tag for this Furnace tile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Furnace tile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Short("BurnTime", burntime),
				  new Tag.Short("CookTime", cooktime),
				  new Tag.List("Items", Tag.Type.COMPOUND, smelting.ToNBT(null),
														   fuel.ToNBT(null),
														   result.ToNBT(null)));
			return t;
		}
	}
	/**
	 * The tile entity for signs to store their text.
	 */
	public static class Sign extends TileEntity
	{
		/**
		 * The text on the sign, one string per line.
		 */
		private String text1, text2, text3, text4;

		/**
		 * Constructs a Sign tile entity from the given tag.
		 * @param sign The tag from which to construct this Sign tile entity.
		 * @throws FormatException if the tag is invalid.
		 */
		public Sign(Tag.Compound sign) throws FormatException
		{
			super(sign);

			text1 = ((Tag.String)sign.find(Tag.Type.STRING, "Text1")).v;
			text2 = ((Tag.String)sign.find(Tag.Type.STRING, "Text2")).v;
			text3 = ((Tag.String)sign.find(Tag.Type.STRING, "Text3")).v;
			text4 = ((Tag.String)sign.find(Tag.Type.STRING, "Text4")).v;
		}

		/**
		 * Retrieves a line of text from the sign.
		 * @param line The line number to get, ranged 0 to 3.
		 * @return The text on that line.
		 * @throws IllegalArgumentException if the given line number is not within the range 0 to 3.
		 */
		public String Line(int line) throws IllegalArgumentException
		{
			switch(line)
			{
			case 0:	return text1;
			case 1: return text2;
			case 2: return text3;
			case 3: return text4;
			}
			throw new IllegalArgumentException("Line numbers are 0 to 3; given "+line);
		}
		/**
		 * Sets the text of a line on the sign.
		 * @param line The line number to set, ranged 0 to 3.
		 * @param text The text to set the line to.
		 * @throws IllegalArgumentException if the given line number is not within the range 0 to 3.
		 */
		public void Line(int line, String text) throws IllegalArgumentException
		{
			switch(line)
			{
			case 0:	text1 = text;
			case 1: text2 = text;
			case 2: text3 = text;
			case 3: text4 = text;
			}
			throw new IllegalArgumentException("Line numbers are 0 to 3; given "+line);
		}

		/**
		 * Returns the tag for this Sign tile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Sign tile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.String("Text1", text1),
				  new Tag.String("Text2", text2),
				  new Tag.String("Text3", text3),
				  new Tag.String("Text4", text4));
			return t;
		}
	}
	/**
	 * The tile entity used by Monster Spawners to store which mob they spawn and how many ticks there are until the next spawn.
	 */
	public static class MobSpawner extends TileEntity
	{
		/**
		 * The mob class to spawn.
		 */
		Class<? extends Mob> entityid;
		/**
		 * Data to spawn the mob with.
		 */
		Tag.Compound spawndata;
		/**
		 * The delay in ticks until the next spawn.
		 */
		private short delay;
		/**
		 * The range for the next random delay.
		 */
		private short minspawndelay, maxspawndelay;
		/**
		 * How many mobs to spawn when delay reaches 0.
		 */
		private short spawncount;

		/**
		 * Constructs a Mob Spawner tile entity from the given tag.
		 * @param mobspawner The tag from which to construct this Mob Spawner.
		 * @throws FormatException if the given tag is invalid.
		 */
		public MobSpawner(Tag.Compound mobspawner) throws FormatException
		{
			super(mobspawner);

			String eid = ((Tag.String)mobspawner.find(Tag.Type.STRING, "EntityId")).v;
			entityid = Mob.ClassFromID(eid);
			spawndata = new Tag.Compound(null);
			try
			{
				spawndata.addAll((Tag.Compound)mobspawner.find(Tag.Type.COMPOUND, "SpawnData"));
			}
			catch(FormatException e)
			{
			}
			delay = ((Tag.Short)mobspawner.find(Tag.Type.SHORT, "Delay")).v;
			minspawndelay = ((Tag.Short)mobspawner.find(Tag.Type.SHORT, "MinSpawnDelay")).v;
			maxspawndelay = ((Tag.Short)mobspawner.find(Tag.Type.SHORT, "MaxSpawnDelay")).v;
			spawncount = ((Tag.Short)mobspawner.find(Tag.Type.SHORT, "SpawnCount")).v;
		}

		/**
		 * Returns the Entity ID of the mob that this spawner spawns.
		 * @return The Entity ID of the mob that this spawner spawns.
		 */
		public Class<? extends Mob> MobID()
		{
			return entityid;
		}
		/**
		 * Sets the Entity ID of the mob that this spawner spawns.
		 * @param mobid The Entity ID of the mob that this spawner spawns.
		 */
		public void MobID(Class<? extends Mob> mobid)
		{
			entityid = mobid;
		}

		/**
		 * Returns the additional data used for spawning the mob. Changes made to this tag will reflect on the internally stored tag.
		 * @return The additional data used for spawning the mob.
		 */
		Tag.Compound SpawnData()
		{
			return spawndata;
		}

		/**
		 * Returns the number of ticks before the next attempt to spawn mobs.
		 * @return The number of ticks before the next attempt to spawn mobs.
		 */
		public short Delay()
		{
			return delay;
		}
		/**
		 * Sets the number of ticks before the next attempt to spawn mobs.
		 * @param ticks The number of ticks before the next attempt to spawn mobs.
		 */
		public void Delay(short ticks)
		{
			delay = ticks >= 0 ? ticks : 0;
		}

		/**
		 * Returns the minimum number of ticks to be randomly chosen for the next spawn delay.
		 * @return The minimum number of ticks to be randomly chosen for the next spawn delay.
		 */
		public short MinDelay()
		{
			return minspawndelay;
		}
		/**
		 * Returns the maximum number of ticks to be randomly chosen for the next spawn delay.
		 * @return The maximum number of ticks to be randomly chosen for the next spawn delay.
		 */
		public short MaxDelay()
		{
			return maxspawndelay;
		}
		/**
		 * Sets the minimum number of ticks to be randomly chosen for the next spawn delay.
		 * @param ticks The minimum number of ticks to be randomly chosen for the next spawn delay.
		 */
		public void MinDelay(short ticks)
		{
			minspawndelay = ticks >= 0 ? ticks : 0;
			if(maxspawndelay < minspawndelay)
			{
				maxspawndelay = minspawndelay;
			}
		}
		/**
		 * Sets the maximum number of ticks to be randomly chosen for the next spawn delay.
		 * @param ticks The maximum number of ticks to be randomly chosen for the next spawn delay.
		 */
		public void MaxDelay(short ticks)
		{
			maxspawndelay = ticks > 0 ? ticks : 1;
			if(minspawndelay > maxspawndelay)
			{
				minspawndelay = maxspawndelay;
			}
		}

		/**
		 * Returns the number of mobs to attempt to spawn when delay reaches 0.
		 * @return The number of mobs to attempt to spawn when delay reaches 0.
		 */
		public short SpawnCount()
		{
			return spawncount;
		}
		/**
		 * Sets the number of mobs to attempt to spawn when delay reaches 0.
		 * @param mobs The number of mobs to attempt to spawn when delay reaches 0.
		 */
		public void SpawnCount(short mobs)
		{
			spawncount = mobs > 0 ? mobs : 1;
		}

		/**
		 * Returns the tag for this Mob Spawner tile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Mob Spawner tile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name), sd;
			t.add(new Tag.String("EntityId", entityid.getSimpleName()),
				  sd = new Tag.Compound("SpawnData"),
				  new Tag.Short("Delay", delay),
				  new Tag.Short("MinSpawnDelay", minspawndelay),
				  new Tag.Short("MaxSpawnDelay", maxspawndelay),
				  new Tag.Short("SpawnCount", spawncount));
			sd.addAll(spawndata);
			return t;
		}
	}
	/**
	 * The tile entity used by Chests to store their items.
	 */
	public static class Chest extends TileEntity
	{
		/**
		 * The items in this chest.
		 * <p>
		 * Slots are numbered 0 to 26.
		 */
		private Inventory items;

		/**
		 * Constructs a Chest tile entity from the given tag.
		 * @param chest The tag from which to construct the Chest tile entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Chest(Tag.Compound chest) throws FormatException
		{
			super(chest);

			items = new Inventory((Tag.List)chest.find(Tag.Type.LIST, "Items"));
		}

		/**
		 * Returns the item at the specified slot.
		 * @param slot The slot from which to get the item, range 0 to 26.
		 * @return The item at the specified slot.
		 * @throws IllegalArgumentException if the given slot number is not within the range 0 to 26.
		 */
		public Inventory.Item Item(int slot) throws IllegalArgumentException
		{
			if(slot < 0 || slot > 26)
			{
				throw new IllegalArgumentException("Slot number must be between 0 and 26 inclusive, given: "+slot);
			}
			return items.Item(slot);
		}
		/**
		 * Sets the item at the specified slot.
		 * @param slot The slot to set the new item to, range 0 to 26.
		 * @param item The item to set to the slot.
		 * @throws IllegalArgumentException if the given slot number is not within the range 0 to 26.
		 */
		public void Item(int slot, Inventory.Item item) throws IllegalArgumentException
		{
			if(slot < 0 || slot > 26)
			{
				throw new IllegalArgumentException("Slot number must be between 0 and 26 inclusive, given: "+slot);
			}
			items.Item(slot, item);
		}

		/**
		 * Returns the tag for this Chest tile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Chest tile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(items.ToNBT("Items"));
			return t;
		}
	}
	/**
	 * The tile entity used by Note Blocks to store which note they play.
	 * <p>
	 * Why this isn't simply in the block data value is a mystery.
	 */
	public static class Music extends TileEntity
	{
		/**
		 * The pitch of the note block.
		 */
		private byte note;

		/**
		 * Constructs a Music tile entity from a tag.
		 * @param music The tag from which to construct the Music tile entity.
		 * @throws FormatException if the tag is invalid.
		 */
		public Music(Tag.Compound music) throws FormatException
		{
			super(music);

			note = ((Tag.Byte)music.find(Tag.Type.BYTE, "note")).v;
		}

		/**
		 * Returns the pitch of the note block.
		 * @return The pitch of the note block.
		 */
		public byte Pitch()
		{
			return note;
		}
		/**
		 * Sets the pitch of the note block.
		 * @param pitch The pitch of the note block.
		 */
		public void Pitch(byte pitch)
		{
			note = pitch;
		}

		/**
		 * Returns the tag for this Music tile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Music tile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Byte("note", note));
			return t;
		}
	}
	/**
	 * The tile entity used by Dispensers to store the items they contain.
	 */
	public static class Trap extends TileEntity
	{
		private Inventory items;
		public Trap(Tag.Compound trap) throws FormatException
		{
			super(trap);

			items = new Inventory((Tag.List)trap.find(Tag.Type.LIST, "Items"));
		}

		/**
		 * Returns the item at the specified slot.
		 * @param slot The slot from which to get the item, range 0 to 8.
		 * @return The item at the specified slot.
		 * @throws IllegalArgumentException if the given slot number is not within the range 0 to 8.
		 */
		public Inventory.Item Item(int slot) throws IllegalArgumentException
		{
			if(slot < 0 || slot > 8)
			{
				throw new IllegalArgumentException("Slot number must be between 0 and 8 inclusive, given: "+slot);
			}
			return items.Item(slot);
		}
		/**
		 * Sets the item at the specified slot.
		 * @param slot The slot to set the new item to, range 0 to 8.
		 * @param item The item to set to the slot.
		 * @throws IllegalArgumentException if the given slot number is not within the range 0 to 8.
		 */
		public void Item(int slot, Inventory.Item item) throws IllegalArgumentException
		{
			if(slot < 0 || slot > 8)
			{
				throw new IllegalArgumentException("Slot number must be between 0 and 8 inclusive, given: "+slot);
			}
			items.Item(slot, item);
		}

		/**
		 * Returns the tag for this Trap tile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Trap tile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(items.ToNBT("Items"));
			return t;
		}
	}
	/**
	 * The tile entity used by Jukeboxes to store the record/item ID they contain.
	 * <p>
	 * Why this isn't simply in the block data value is a mystery.
	 */
	public static class RecordPlayer extends TileEntity
	{
		/**
		 * The Item ID of the record/item in the Jukebox.
		 */
		private int record;

		/**
		 * Constructs a Record Player tile entity from the given tag.
		 * @param recordplayer The tag from which to construct the Record Player tile entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public RecordPlayer(Tag.Compound recordplayer) throws FormatException
		{
			super(recordplayer);

			record = ((Tag.Int)recordplayer.find(Tag.Type.INT, "Record")).v;
		}

		/**
		 * Returns the Item ID of the record/item in the Jukebox.
		 * @return The Item ID of the record/item in the Jukebox.
		 */
		public int ItemID()
		{
			return record;
		}
		/**
		 * Sets the Item ID of the record/item in the Jukebox.
		 * @param item The Item ID of the record/item in the Jukebox.
		 */
		public void ItemID(int item)
		{
			record = item;
		}

		/**
		 * Returns the tag for this Record Player tile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Record Player tile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Int("Record", record));
			return t;
		}
	}
	/**
	 * The tile entity used by block 36, the block being moved by a piston.
	 */
	public static class Piston extends TileEntity
	{
		/**
		 * The ID of the block being moved.
		 */
		private int blockid;
		/**
		 * The data/damage value of the block being moved.
		 */
		private int blockdata;
		/**
		 * Represents a direction for blocks being moved by pistons.
		 */
		public static enum Facing
		{
			Down, Up,
			North, South, East, West;

			/**
			 * Converts an ordinal to the Facing it represents.
			 * @param ordinal The facing as a number.
			 * @return The Facing represented by the given ordinal.
			 * @throws FormatException If the ordinal does not represent a valid facing.
			 */
			public static Facing FromOrdinal(int ordinal) throws FormatException
			{
				switch(ordinal)
				{
				case 0:	return Down;
				case 1:	return Up;
				case 2:	return North;
				case 3:	return South;
				case 4:	return East;
				case 5:	return West;
				}
				throw new FormatException("Unkown direction: "+ordinal);
			}
		}
		/**
		 * The direction the block is being moved.
		 */
		private Facing facing;
		/**
		 * The progress that has been made in moving the block.
		 */
		private float progress;
		/**
		 * Whether the block is being pushed nor not. If not, the block is being pulled.
		 */
		private boolean extending;

		/**
		 * Constructs a Piston tile entity from the given tag.
		 * @param piston The tag from which to construct the Piston tile entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Piston(Tag.Compound piston) throws FormatException
		{
			super(piston);

			blockid = ((Tag.Int)piston.find(Tag.Type.INT, "blockId")).v;
			blockdata = ((Tag.Int)piston.find(Tag.Type.INT, "blockData")).v;
			facing = Facing.FromOrdinal(((Tag.Int)piston.find(Tag.Type.INT, "facing")).v);
			progress = ((Tag.Float)piston.find(Tag.Type.FLOAT, "progress")).v;
			extending = ((Tag.Byte)piston.find(Tag.Type.BYTE, "extending")).v != 0 ? true : false;
		}

		/**
		 * Returns the ID of the block being moved.
		 * @return The ID of the block being moved.
		 */
		public int BlockID()
		{
			return blockid;
		}
		/**
		 * Sets the ID of the block being moved.
		 * @param id The ID of the block being moved.
		 */
		public void BlockID(int id)
		{
			blockid = id;
		}

		/**
		 * Returns the data/damage value of the block being moved.
		 * @return The data/damage value of the block being moved.
		 */
		public int BlockData()
		{
			return blockdata;
		}
		/**
		 * Sets the data/damage value of the block being moved.
		 * @param data The data/damage value of the block being moved.
		 */
		public void BlockData(int data)
		{
			blockdata = data;
		}

		/**
		 * Returns the direction the block is being moved.
		 * @return The direction the block is being moved.
		 */
		public Facing Facing()
		{
			return facing;
		}
		/**
		 * Sets the direction the block is being moved.
		 * @param direction The direction the block is being moved.
		 */
		public void Facing(Facing direction)
		{
			facing = direction;
		}

		/**
		 * Returns the progress of moving the block so far.
		 * @return The progress of moving the block so far.
		 */
		public float Progress()
		{
			return progress;
		}
		/**
		 * Sets the progress of moving the block so far.
		 * @param progress The progress of moving the block so far.
		 */
		public void Progress(float progress)
		{
			this.progress = progress;
		}

		/**
		 * Returns whether or not the block is being pushed.
		 * @return Whether or not the block is being pushed.
		 */
		public boolean Extending()
		{
			return extending;
		}
		/**
		 * Sets whether or not the block is being pushed.
		 * @param notretracting Whether or not the block is being pushed.
		 */
		public void Extending(boolean notretracting)
		{
			extending = notretracting;
		}

		/**
		 * Returns the tag for this Piston tile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Piston tile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Int("blockId", blockid),
				  new Tag.Int("blockData", blockdata),
				  new Tag.Int("facing", facing.ordinal()),
				  new Tag.Float("progress", progress),
				  new Tag.Byte("extending", (byte)(extending?1:0)));
			return t;
		}
	}
	/**
	 * The tile entity used by Brewing Stands to store their items and brewing progress.
	 */
	public static class Cauldron extends TileEntity
	{
		/**
		 * The items in the brewing stand.
		 * <p>
		 * Slot 0: ?<br />
		 * Slot 1: ?<br />
		 * Slot 2: ?<br />
		 * Slot 3: ?
		 */
		private Inventory items;
		/**
		 * The number of ticks that the potions have been brewing.
		 */
		private int brewtime;

		/**
		 * Constructs a Cauldron tile entity from the given tag.
		 * @param cauldron The tag from which to  construct the Cauldron tile entity.
		 * @throws FormatException if the tag is invalid.
		 */
		public Cauldron(Tag.Compound cauldron) throws FormatException
		{
			super(cauldron);

			items = new Inventory((Tag.List)cauldron.find(Tag.Type.LIST, "Items"));
			brewtime = ((Tag.Int)cauldron.find(Tag.Type.INT, "BrewTime")).v;
		}

		/**
		 * Returns the item in the Ingredient slot.
		 * @return The item in the Ingredient slot.
		 */
		public Inventory.Item Ingredient()
		{
			return items.Item(0);
		}
		/**
		 * Sets the item in the Ingredient slot.
		 * @param ingredient The item in the Ingredient slot.
		 */
		public void Ingredient(Inventory.Item ingredient)
		{
			items.Item(0, ingredient);
		}

		/**
		 * Returns the item in the nth potion slot in the brewing stand, numbered 0 to 2.
		 * @param slot The nth potion slot, 0 to 2.
		 * @return The item in the nth potion slot in the brewing stand, numbered 0 to 2.
		 * @throws IllegalArgumentException if the given potion slot is not in the range 0 to 2.
		 */
		public Inventory.Item Potion(int slot) throws IllegalArgumentException
		{
			if(slot < 0 || slot > 2)
			{
				throw new IllegalArgumentException("Potion slot numbers must be from 0 to 2 inclusive, given: "+slot);
			}
			return items.Item(slot+1);
		}
		/**
		 * Sets the item in the nth potion slot in the brewing stand, numbered 0 to 2.
		 * @param slot The nth potion slot, 0 to 2.
		 * @param potion The item in the nth potion slot in the brewing stand.
		 * @throws IllegalArgumentException if the given potion slot is not in the range 0 to 2.
		 */
		public void Potion(int slot, Inventory.Item potion) throws IllegalArgumentException
		{
			if(slot < 0 || slot > 2)
			{
				throw new IllegalArgumentException("Potion slot numbers must be from 0 to 2 inclusive, given: "+slot);
			}
			items.Item(slot+1, potion);
		}

		/**
		 * Returns the number of ticks the potions have been brewing for.
		 * @return The number of ticks the potions have been brewing for.
		 */
		public int BrewTime()
		{
			return brewtime;
		}
		/**
		 * Sets the number of ticks the potions have been brewing for.
		 * @param ticks The number of ticks the potions have been brewing for.
		 */
		public void BrewTime(int ticks)
		{
			brewtime = ticks;
		}

		/**
		 * Returns the tag for this Cauldron tile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Cauldron tile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(items.ToNBT("Items"),
				  new Tag.Int("BrewTime", brewtime));
			return t;
		}
	}
	/**
	 * The tile entity used by Enchantment Tables to keep track of their rotation and opening/closing, only used at runtime.
	 */
	public static class EnchantTable extends TileEntity
	{
		/**
		 * Constructs an Enchantment Table tile entity from the given tag.
		 * @param enchanttable The tag from which to construct this Enchantment Table tile entity.
		 * @throws FormatException if the tag is invalid.
		 */
		public EnchantTable(Tag.Compound enchanttable) throws FormatException
		{
			super(enchanttable);
		}
	}
	/**
	 * The tile entity used by End Portal blocks to have that cool paralaxing look, only used at runtime.
	 */
	public static class Airportal extends TileEntity
	{
		/**
		 * Constructs an End Portal tile entity from the given tag.
		 * @param airportal The tag from which to construct this End Portal tile entity.
		 * @throws FormatException if the tag is invalid.
		 */
		public Airportal(Tag.Compound airportal) throws FormatException
		{
			super(airportal);
		}
	}
}