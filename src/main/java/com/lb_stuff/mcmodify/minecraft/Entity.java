package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.nbt.FormatException;
import com.lb_stuff.mcmodify.nbt.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * The class extended by the various entity classes in Minecraft.
 */
public abstract class Entity
{
	/**
	 * The position of the entity.
	 */
	private double posx, posy, posz;
	/**
	 * The motion of the entity.
	 */
	private double motionx, motiony, motionz;
	/**
	 * The rotation of the entity.
	 */
	private float rotationyaw, rotationpitch;
	/**
	 * The fall distance of the entity.
	 */
	private float falldistance;
	/**
	 * The number of ticks before this entity's fire is extinguished.
	 */
	private short fire;
	/**
	 * The number of ticks before this entity begins to drown.
	 * <p>
	 * Why this isn't in the Mob class is unknown; entities that aren't mobs don't drown.
	 */
	private short air;
	/**
	 * Whether or not the entity is on the ground.
	 */
	private boolean onground;

	/**
	 * Additional Entity Classes; if you write your own class that extends this class, you should call <code>RegisterClassForID</code> to register it.
	 */
	private static Map<String, Class<? extends Entity>> AEC = new HashMap<>();
	/**
	 * Registers a yet-to-be-known entity class for recognition when loading. Useful for mods/plugins.
	 * @param EntityID The Entity ID string used to identify the entity.
	 * @param clazz	The class to register with the given Entity ID.
	 * @throws IllegalArgumentException if the given class has no constructor that takes only a Tag.Compound parameter to construct it from.
	 */
	protected static void RegisterClassForID(String EntityID, Class<? extends Entity> clazz) throws IllegalArgumentException
	{
		try
		{
			clazz.getDeclaredConstructor(Tag.Compound.class);
		}
		catch(NoSuchMethodException e)
		{
			throw new IllegalArgumentException("Entity classes must at least be constructible from a single Tag.Compound parameter", e);
		}
		AEC.put(EntityID, clazz);
	}
	/**
	 * Given an Entity's tag, returns the Class object for the class that represents that Entity ID.
	 * @param entity The tag that represents the entity.
	 * @return The Class object for the class that represents the Entity ID.
	 * @throws FormatException if the given Tile Entity ID is unknown.
	 */
	public static Class<? extends Entity> ClassFromID(Tag.Compound entity) throws FormatException
	{
		String ID = ((Tag.String)entity.find(Tag.Type.STRING, "id")).v;
		try
		{
			return Mob.ClassFromID(ID);
		}
		catch(FormatException e)
		{
		}
		try
		{
			return Projectile.ClassFromID(ID);
		}
		catch(FormatException e)
		{
		}
		switch(ID)
		{
		case "Item":			return Item.class;
		case "Painting":		return Painting.class;
		case "XPOrb":			return XPOrb.class;
		case "Minecart":		return Minecart.ClassFromType(((Tag.Int)entity.find(Tag.Type.INT, "Type")).v);
		case "Boat":			return Boat.class;
		case "PrimedTnt":		return PrimedTnt.class;
		case "FallingSand":		return FallingSand.class;
		}
		if(AEC.containsKey(ID))
		{
			return AEC.get(ID);
		}
		throw new FormatException("Unknown Entity ID: \""+ID+"\"");
	}

	/**
	 * The constructor required to instantiate any class that extends entity.
	 * @param entity The entity's tag. This tag will not be affected by the entity class object at any time.
	 * @throws FormatException if there is something wrong with the format of the entity's tag.
	 */
	public Entity(Tag.Compound entity) throws FormatException
	{
		if(!(this instanceof Level.Player))
		{
			String id = ((Tag.String)entity.find(Tag.Type.STRING, "id")).v;
			if(!id.equals(getClass().getSimpleName()))
			{
				throw new FormatException("Attempted to instantiate a "+getClass().getSimpleName()+" Entity from a tag with id \""+id+"\"");
			}
		}

		Tag.List pos = (Tag.List)entity.find(Tag.Type.LIST, "Pos");
		if(pos.getSize() != 3 || pos.getContainedType() != Tag.Type.DOUBLE)
		{
			throw new FormatException("Invalid Pos list");
		}
		posx = ((Tag.Double)pos.get(0)).v;
		posy = ((Tag.Double)pos.get(1)).v;
		posz = ((Tag.Double)pos.get(2)).v;

		Tag.List motion = (Tag.List)entity.find(Tag.Type.LIST, "Motion");
		if(motion.getSize() != 3 || motion.getContainedType() != Tag.Type.DOUBLE)
		{
			throw new FormatException("Invalid Motion list");
		}
		motionx = ((Tag.Double)motion.get(0)).v;
		motiony = ((Tag.Double)motion.get(1)).v;
		motionz = ((Tag.Double)motion.get(2)).v;

		Tag.List rotation = (Tag.List)entity.find(Tag.Type.LIST, "Rotation");
		if(rotation.getSize() != 2 || rotation.getContainedType() != Tag.Type.FLOAT)
		{
			throw new FormatException("Invalid Rotation list");
		}
		rotationyaw = ((Tag.Float)rotation.get(0)).v;
		rotationpitch = ((Tag.Float)rotation.get(1)).v;

		falldistance = ((Tag.Float)entity.find(Tag.Type.FLOAT, "FallDistance")).v;

		fire = ((Tag.Short)entity.find(Tag.Type.SHORT, "Fire")).v;

		air = ((Tag.Short)entity.find(Tag.Type.SHORT, "Air")).v;

		onground = ((Tag.Byte)entity.find(Tag.Type.BYTE, "OnGround")).v != 0 ? true : false;
	}
	/**
	 * Constructs an entity from a position.
	 * @param X The X position.
	 * @param Y The Y position.
	 * @param Z The Z position.
	 */
	public Entity(double X, double Y, double Z)
	{
		posx = X;
		posy = Y;
		posz = Z;
		motionx = motiony = motionz = 0.0;
		rotationyaw = rotationpitch = 0.0f;
		falldistance = 0.0f;
		fire = air = 0;
		onground = false;
	}

	/**
	 * Returns the position of this entity.
	 * @return The position of this entity.
	 */
	public Tag.List Pos()
	{
		return new Tag.List("Pos", Tag.Type.DOUBLE, new Tag.Double(null, posx),
													new Tag.Double(null, posy),
													new Tag.Double(null, posz));
	}
	/**
	 * Returns the X position of this entity.
	 * @return The X position of this entity.
	 */
	public double PosX()
	{
		return posx;
	}
	/**
	 * Returns the Y position of this entity.
	 * @return The Y position of this entity.
	 */
	public double PosY()
	{
		return posy;
	}
	/**
	 * Returns the Z position of this entity.
	 * @return The Z position of this entity.
	 */
	public double PosZ()
	{
		return posz;
	}
	/**
	 * Sets the position of this entity.
	 * @param pos The new position of this entity.
	 * @throws FormatException if the given position is invalid.
	 */
	public void Pos(Tag.List pos) throws FormatException
	{
		if(pos.getSize() != 3 || pos.getContainedType() != Tag.Type.DOUBLE)
		{
			throw new FormatException("Invalid Pos list");
		}
		posx = ((Tag.Double)pos.get(0)).v;
		posy = ((Tag.Double)pos.get(1)).v;
		posz = ((Tag.Double)pos.get(2)).v;
	}
	/**
	 * Sets the X position of this entity.
	 * @param x The new X position of this entity.
	 */
	public void PosX(double x)
	{
		posx = x;
	}
	/**
	 * Sets the Y position of this entity.
	 * @param y The new Y position of this entity.
	 */
	public void PosY(double y)
	{
		posy = y;
	}
	/**
	 * Sets the Z position of this entity.
	 * @param z The new Z position of this entity.
	 */
	public void PosZ(double z)
	{
		posz = z;
	}

	/**
	 * Returns the motion of this entity.
	 * @return The motion of this entity.
	 */
	public Tag.List Motion()
	{
		return new Tag.List("Motion", Tag.Type.DOUBLE, new Tag.Double(null, motionx),
													   new Tag.Double(null, motiony),
													   new Tag.Double(null, motionz));
	}
	/**
	 * Returns the X motion of this entity.
	 * @return The X motion of this entity.
	 */
	public double MotionX()
	{
		return motionx;
	}
	/**
	 * Returns the Y motion of this entity.
	 * @return The Y motion of this entity.
	 */
	public double MotionY()
	{
		return motiony;
	}
	/**
	 * Returns the Z motion of this entity.
	 * @return The Z motion of this entity.
	 */
	public double MotionZ()
	{
		return motionz;
	}
	/**
	 * Sets the motion of this entity.
	 * @param motion The new motion of this entity.
	 * @throws FormatException if the given motion is invalid.
	 */
	public void Motion(Tag.List motion) throws FormatException
	{
		if(motion.getSize() != 3 || motion.getContainedType() != Tag.Type.DOUBLE)
		{
			throw new FormatException("Invalid Motion list");
		}
		motionx = ((Tag.Double)motion.get(0)).v;
		motiony = ((Tag.Double)motion.get(1)).v;
		motionz = ((Tag.Double)motion.get(2)).v;
	}
	/**
	 * Sets the X motion of this entity.
	 * @param x The new X motion of this entity.
	 */
	public void MotionX(double x)
	{
		motionx = x;
	}
	/**
	 * Sets the Y motion of this entity.
	 * @param y The new Y motion of this entity.
	 */
	public void MotionY(double y)
	{
		motiony = y;
	}
	/**
	 * Sets the Z motion of this entity.
	 * @param z The new Z motion of this entity.
	 */
	public void MotionZ(double z)
	{
		motionz = z;
	}

	/**
	 * Returns the rotation of this entity.
	 * @return The rotation of this entity.
	 */
	public Tag.List Rotation()
	{
		return new Tag.List("Rotation", Tag.Type.FLOAT, new Tag.Float(null, rotationyaw),
														new Tag.Float(null, rotationpitch));
	}
	/**
	 * Returns the Yaw of this entity.
	 * @return The Yaw of this entity.
	 */
	public float Yaw()
	{
		return rotationyaw;
	}
	/**
	 * Returns the Pitch of this entity.
	 * @return The Pitch of this entity.
	 */
	public float Pitch()
	{
		return rotationpitch;
	}
	/**
	 * Sets the rotation of this entity.
	 * @param rotation The rotation of this entity.
	 * @throws FormatException if the given rotation is invalid.
	 */
	public void Rotation(Tag.List rotation) throws FormatException
	{
		if(rotation.getSize() != 2 || rotation.getContainedType() != Tag.Type.FLOAT)
		{
			throw new FormatException("Invalid Rotation list");
		}
		rotationyaw = ((Tag.Float)rotation.get(0)).v;
		rotationpitch = ((Tag.Float)rotation.get(1)).v;
	}
	/**
	 * Sets the yaw for this entity.
	 * @param yaw The yaw for this entity.
	 */
	public void Yaw(float yaw)
	{
		rotationyaw = yaw;
	}
	/**
	 * Sets the pitch for this entity.
	 * @param pitch The pitch for this entity.
	 */
	public void Pitch(float pitch)
	{
		rotationpitch = pitch;
	}

	/**
	 * Returns the fall distance for this entity.
	 * @return The fall distance for this entity.
	 */
	public float FallDistance()
	{
		return falldistance;
	}
	/**
	 * Sets the fall distance for this entity.
	 * @param distance The fall distance for this entity.
	 */
	public void FallDistance(float distance)
	{
		falldistance = distance;
	}

	/**
	 * Returns the number of ticks before this entity's fire is extinguished.
	 * @return The number of ticks before this entity's fire is extinguished.
	 */
	public short FireTicks()
	{
		return fire;
	}
	/**
	 * Sets she number of ticks before this entity's fire is extinguished.
	 * @param ticks The number of ticks before this entity's fire is extinguished.
	 */
	public void FireTicks(short ticks)
	{
		fire = ticks;
	}

	/**
	 * Returns the number of ticks before this entity begins to drown.
	 * @return The number of ticks before this entity begins to drown.
	 */
	public short AirTicks()
	{
		return air;
	}
	/**
	 * Sets the number of ticks before this entity begins to drown.
	 * @param ticks The number of ticks before this entity begins to drown.
	 */
	public void AirTicks(short ticks)
	{
		air = ticks;
	}

	/**
	 * Returns whether or not this entity is on the ground.
	 * @return Whether or not this entity is on the ground.
	 */
	public boolean OnGround()
	{
		return onground;
	}
	/**
	 * Sets whether or not this entity is on the ground.
	 * @param onGround Whether or not this entity is on the ground.
	 */
	public void OnGround(boolean onGround)
	{
		onground = onGround;
	}

	/**
	 * Converts this entity to its tag.
	 * <p>
	 * Should be overridden and called by classes that extend this class.
	 * @param name The name for the compound tag, or null if the compound tag should not have a name.
	 * @return The tag for this entity.
	 */
	public Tag.Compound ToNBT(String name)
	{
		return new Tag.Compound(name, new Tag.String("id", getClass().getSimpleName()),
									  Pos(),
									  Motion(),
									  Rotation(),
									  new Tag.Float("FallDistance", falldistance),
									  new Tag.Short("Fire", fire),
									  new Tag.Short("Air", air),
									  new Tag.Byte("OnGround", (byte)(onground?1:0)));
	}

	/**
	 * Boat entity
	 */
	public static class Boat extends Entity
	{
		/**
		 * Constructs a Boat entity from the given tag.
		 * @param boat The tag from which to construct this Boat entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Boat(Tag.Compound boat) throws FormatException
		{
			super(boat);
		}
		/**
		 * Constructs a Boat from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Boat(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Minecart entities
	 */
	public static class Minecart extends Entity
	{
		/**
		 * Gives the class object for the correct Minecart class to instantiate given the type.
		 * @param type The type {0, 1, 2} of Minecart.
		 * @return The class for the correct type of Minecart.
		 * @throws FormatException if the given type is invalid.
		 */
		public static Class<? extends Minecart> ClassFromType(int type) throws FormatException
		{
			switch(type)
			{
			case 0: return Minecart.class;
			case 1: return Chest.class;
			case 2: return Furnace.class;
			}
			throw new FormatException("Invalid Minecart Type: "+type);
		}

		/**
		 * Constructs a Minecart entity from the given tag.
		 * @param minecart The tag from which to construct this Minecart entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Minecart(Tag.Compound minecart) throws FormatException
		{
			super(minecart);

			int type = ((Tag.Int)minecart.find(Tag.Type.INT, "Type")).v;
			if("Minecart".equals(getClass().getSimpleName()) && type != 0)
			{
				throw new FormatException("Tried to instantiate a regular Minecart from a minecart of type "+type);
			}
			else if("Chest".equals(getClass().getSimpleName()) && type != 1)
			{
				throw new FormatException("Tried to instantiate a Storage Minecart from a minecart of type "+type);
			}
			else if("Furnace".equals(getClass().getSimpleName()) && type != 2)
			{
				throw new FormatException("Tried to instantiate a Powered Minecart from a minecart of type "+type);
			}
		}
		/**
		 * Constructs a Minecart from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Minecart(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}

		/**
		 * Returns the tag for this Minecart entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Minecart entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			switch(getClass().getSimpleName())
			{
			case "Minecart":t.add(new Tag.Int("Type", 0));
			case "Chest":	t.add(new Tag.Int("Type", 1));
			case "Furnace":	t.add(new Tag.Int("Type", 2));
			}
			return t;
		}

		/**
		 * Storage Minecart
		 */
		public static class Chest extends Minecart
		{
			private Inventory items;
			/**
			 * Constructs a Storage Minecart entity from the given tag.
			 * @param mcwchest The tag from which to construct this Storage Minecart entity.
			 * @throws FormatException if the give tag is invalid.
			 */
			public Chest(Tag.Compound mcwchest) throws FormatException
			{
				super(mcwchest);

				items = new Inventory((Tag.List)mcwchest.find(Tag.Type.LIST, "Items"));
			}
			/**
			 * Constructs a Storage Minecart from a position.
			 * @param X The X position.
			 * @param Y The Y position.
			 * @param Z The Z position.
			 */
			public Chest(double X, double Y, double Z)
			{
				super(X, Y, Z);
				items = new Inventory();
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
			 * Returns the tag for this Storage Minecart entity.
			 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
			 * @return The tag for this Storage Minecart entity.
			 */
			@Override public Tag.Compound ToNBT(String name)
			{
				Tag.Compound t = super.ToNBT(name);
				t.add(items.ToNBT("Items"));
				return t;
			}
		}
		/**
		 * Powered Minecart
		 */
		public static class Furnace extends Minecart
		{
			/**
			 * Unknown.
			 */
			private double pushx, pushz;
			/**
			 * The number of ticks before the Powered Minecart runs out of fuel.
			 */
			private short fuel;

			/**
			 * Constructs a Powered Minecart entity from the given tag.
			 * @param mcwfurnace The tag from which to construct this Powered Minecart entity.
			 * @throws FormatException if the given tag is invalid.
			 */
			public Furnace(Tag.Compound mcwfurnace) throws FormatException
			{
				super(mcwfurnace);

				pushx = ((Tag.Double)mcwfurnace.find(Tag.Type.DOUBLE, "PushX")).v;
				pushz = ((Tag.Double)mcwfurnace.find(Tag.Type.DOUBLE, "PushZ")).v;

				fuel = ((Tag.Short)mcwfurnace.find(Tag.Type.SHORT, "Fuel")).v;
			}
			/**
			 * Constructs a Powered Minecart from a position.
			 * @param X The X position.
			 * @param Y The Y position.
			 * @param Z The Z position.
			 */
			public Furnace(double X, double Y, double Z)
			{
				super(X, Y, Z);
				pushx = pushz = 0.0;
				fuel = 0;
			}

			/**
			 * Returns the X Push factor.
			 * @return The X Push factor.
			 */
			public double PushX()
			{
				return pushx;
			}
			/**
			 * Returns the Z Push factor.
			 * @return The Z Push factor.
			 */
			public Double PushZ()
			{
				return pushz;
			}
			/**
			 * Sets the X Push factor.
			 * @param push The X Push factor.
			 */
			public void PushX(double push)
			{
				pushx = push;
			}
			/**
			 * Sets the Z Push factor.
			 * @param push The Z Push factor.
			 */
			public void PushZ(double push)
			{
				pushz = push;
			}
			/**
			 * Sets the X and Z Push factors.
			 * @param x The X Push factor.
			 * @param z The Z Push factor.
			 */
			public void Push(double x, double z)
			{
				pushx = x;
				pushz = z;
			}

			/**
			 * Returns the number of ticks before this Powered Minecart runs out of fuel.
			 * @return The number of ticks before this Powered Minecart runs out of fuel.
			 */
			public short Fuel()
			{
				return fuel;
			}
			/**
			 * Sets the number of ticks before this Powered Minecart runs out of fuel.
			 * @param ticks The number of ticks before this Powered Minecart runs out of fuel.
			 */
			public void Fuel(short ticks)
			{
				fuel = ticks;
			}

			/**
			 * Returns the tag for this Powered Minecart entity.
			 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
			 * @return The tag for this Powered Minecart entity.
			 */
			@Override public Tag.Compound ToNBT(String name)
			{
				Tag.Compound t = super.ToNBT(name);
				t.add(new Tag.Double("PushX", pushx),
					  new Tag.Double("PushZ", pushz),
					  new Tag.Short("Fuel", fuel));
				return t;
			}
		}
	}
	/**
	 * TNT dynamic tile entity.
	 */
	public static class PrimedTnt extends Entity
	{
		/**
		 * The number of ticks left before the TNT explodes.
		 */
		private byte fuse;

		/**
		 * Constructs a TNT dynamic tile entity from the given tag.
		 * @param primedtnt The tag from which to construct this TNT dynamic tile entity.
		 * @throws FormatException If the given tag is invalid.
		 */
		public PrimedTnt(Tag.Compound primedtnt) throws FormatException
		{
			super(primedtnt);

			fuse = ((Tag.Byte)primedtnt.find(Tag.Type.BYTE, "Fuse")).v;
		}
		/**
		 * Constructs a TNT dynamic tile from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public PrimedTnt(double X, double Y, double Z)
		{
			super(X, Y, Z);
			fuse = 0;
		}

		/**
		 * Returns the number of ticks before the TNT explodes.
		 * @return The number of ticks before the TNT explodes.
		 */
		public byte Fuse()
		{
			return fuse;
		}
		/**
		 * Sets the number of ticks before the TNT explodes.
		 * @param ticks The number of ticks before the TNT explodes.
		 */
		public void Fuse(byte ticks)
		{
			fuse = ticks;
		}

		/**
		 * Returns the tag for this TNT dynamic tile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this TNT dynamic tile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Byte("Fuse", fuse));
			return t;
		}
	}
	/**
	 * Falling block dynamic tile entity.
	 */
	public static class FallingSand extends Entity
	{
		/**
		 * The Block ID of the falling block.
		 */
		private byte tile;

		/**
		 * Constructs a Falling Block dynamic tile entity from the given tag.
		 * @param fallingsand The tag from which to construct this Falling Block dynamic tile entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public FallingSand(Tag.Compound fallingsand) throws FormatException
		{
			super(fallingsand);

			tile = ((Tag.Byte)fallingsand.find(Tag.Type.BYTE, "Tile")).v;
		}
		/**
		 * Constructs a Falling Block dynamic tile from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public FallingSand(double X, double Y, double Z)
		{
			super(X, Y, Z);
			tile = 12;
		}

		/**
		 * Returns the Block ID of this falling block.
		 * @return The Block ID of this falling block.
		 */
		public byte BlockID()
		{
			return tile;
		}
		/**
		 * Sets the Block ID of this falling block.
		 * @param id The Block ID of this falling block.
		 */
		public void BlockID(byte id)
		{
			tile = id;
		}

		/**
		 * Returns the tag for this Falling Block dynamic tile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Falling Block dynamic tile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Byte("Tile", tile));
			return t;
		}
	}
	/**
	 * Ender Crystal
	 */
	public static class EnderCrystal extends Entity
	{
		/**
		 * Constructs an Ender Crystal entity from the given tag.
		 * @param endercrystal The tag from which to construct this Ender Crystal.
		 * @throws FormatException if the given tag is invalid.
		 */
		public EnderCrystal(Tag.Compound endercrystal) throws FormatException
		{
			super(endercrystal);
		}
		/**
		 * Constructs an Ender Crystal from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public EnderCrystal(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Thrown Eye of Ender
	 */
	public static class EyeOfEnderSignal extends Entity
	{
		/**
		 * Constructs a Thrown Eye of Ender from the given tag.
		 * @param eyeofendersignal The tag from which to construct this Thrown Eye of Ender.
		 * @throws FormatException if the given tag is invalid.
		 */
		public EyeOfEnderSignal(Tag.Compound eyeofendersignal) throws FormatException
		{
			super(eyeofendersignal);
		}
		/**
		 * Constructs a Thrown Eye of Ender from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public EyeOfEnderSignal(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Dropped Item entity
	 * <p>
	 * For items in inventories, see {@link Inventory.Item}.
	 */
	public static class Item extends Entity
	{
		/**
		 * The hit points of this Dropped Item has.
		 */
		private short health;
		/**
		 * The number of ticks this Dropped Item has been left alone for.
		 */
		private short age;
		/**
		 * The item that has been dropped.
		 */
		private Inventory.Item item;

		/**
		 * Constructs a Dropped Item entity from the given tag.
		 * @param item The tag from which to construct this Dropped Item entity.
		 * @throws FormatException if there is something wrong with the format of the item's tag.
		 */
		public Item(Tag.Compound item) throws FormatException
		{
			super(item);

			health = ((Tag.Short)item.find(Tag.Type.SHORT, "Health")).v;
			age = ((Tag.Short)item.find(Tag.Type.SHORT, "Age")).v;
			this.item = new Inventory.Item((Tag.Compound)item.find(Tag.Type.COMPOUND, "Item"));
		}
		/**
		 * Constructs a Dropped Item from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Item(double X, double Y, double Z)
		{
			super(X, Y, Z);
			health = 5;
			age = 0;
			item = new Inventory.Item(7, 0, 1);
		}

		/**
		 * Returns the hit points this Dropped Item has.
		 * @return The hit points this Dropped Item has.
		 */
		public short Health()
		{
			return health;
		}
		/**
		 * Sets the hit points this Dropped Item has.
		 * @param hp The hit points this Dropped Item has.
		 */
		public void Health(short hp)
		{
			health = hp;
		}

		/**
		 * Returns the number of ticks this Dropped Item has been left alone.
		 * @return The number of ticks this Dropped Item has been left alone.
		 */
		public short Age()
		{
			return age;
		}
		/**
		 * Sets the number of ticks this Dropped Item has been left alone.
		 * @param ticks The number of ticks this Dropped Item has been left alone.
		 */
		public void Age(short ticks)
		{
			age = ticks;
		}

		/**
		 * Returns the item that was dropped.
		 * @return The item that was dropped.
		 */
		public Inventory.Item Item()
		{
			return item;
		}
		/**
		 * Sets the item that was dropped.
		 * @param dropped The item that was dropped.
		 */
		public void Item(Inventory.Item dropped)
		{
			item = dropped;
		}

		/**
		 * Returns the tag for this Dropped Item entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Dropped Item entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Short("Health", health),
				  new Tag.Short("Age", age),
				  item.ToNBT("Item"));
			return t;
		}
	}
	/**
	 * Painting entity
	 */
	public static class Painting extends Entity
	{
		/**
		 * Represents the direction the Painting is facing.
		 */
		public static enum Direction
		{
			East, North, West, South;

			/**
			 * Returns the Direction constant that corresponds to the given ordinal.
			 * @param ordinal The direction value of the Painting.
			 * @return The Direction constant that corresponds to the given ordinal.
			 * @throws FormatException if the ordinal is invalid.
			 */
			public static Direction FromOrdinal(byte ordinal) throws FormatException
			{
				switch(ordinal)
				{
				case 0: return East;
				case 1: return North;
				case 2: return West;
				case 3: return South;
				}
				throw new FormatException("Invalid Painting Direction: "+ordinal);
			}
		}
		/**
		 * The direction this Painting is facing.
		 */
		private Direction dir;
		/**
		 * The name of this Painting's art.
		 */
		private String motive;
		/**
		 * The coordinates of the block this Painting is hanging on.
		 */
		private int tilex, tiley, tilez;

		/**
		 * Constructs a Painting entity from the given tag.
		 * @param painting The tag from which this Painting entity is constructed.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Painting(Tag.Compound painting) throws FormatException
		{
			super(painting);

			dir = Direction.FromOrdinal(((Tag.Byte)painting.find(Tag.Type.BYTE, "Dir")).v);
			motive = ((Tag.String)painting.find(Tag.Type.STRING, "Motive")).v;
			tilex = ((Tag.Int)painting.find(Tag.Type.INT, "TileX")).v;
			tiley = ((Tag.Int)painting.find(Tag.Type.INT, "TileY")).v;
			tilez = ((Tag.Int)painting.find(Tag.Type.INT, "TileZ")).v;
		}
		/**
		 * Constructs a Painting from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Painting(double X, double Y, double Z)
		{
			super(X, Y, Z);
			dir = Direction.East;
			motive = "Creebet";
			tilex = (int)X;
			tiley = (int)Y;
			tilez = (int)Z;
		}

		/**
		 * Returns the direction that this Painting is facing.
		 * @return The direction that this Painting is facing.
		 */
		public Direction Direction()
		{
			return dir;
		}
		/**
		 * Sets the direction that this Painting is facing.
		 * @param direction The direction that this Painting is facing.
		 */
		public void Direction(Direction direction)
		{
			dir = direction;
		}

		/**
		 * Returns the name of the art on this painting.
		 * @return The name of the art on this painting.
		 */
		public String Art()
		{
			return motive;
		}
		/**
		 * Sets the name of the art on this painting.
		 * @param name The name of the art on this painting.
		 */
		public void Art(String name)
		{
			motive = name;
		}

		/**
		 * Returns the X coordinate of the Tile that this painting is on.
		 * @return The X coordinate of the Tile that this painting is on.
		 */
		public int TileX()
		{
			return tilex;
		}
		/**
		 * Returns the Y coordinate of the Tile that this painting is on.
		 * @return The Y coordinate of the Tile that this painting is on.
		 */
		public int TileY()
		{
			return tiley;
		}
		/**
		 * Returns the Z coordinate of the Tile that this painting is on.
		 * @return The Z coordinate of the Tile that this painting is on.
		 */
		public int TileZ()
		{
			return tilez;
		}
		/**
		 * Sets the X coordinate of the Tile that this painting is on.
		 * @param x The X coordinate of the Tile that this painting is on.
		 */
		public void TileX(int x)
		{
			tilex = x;
		}
		/**
		 * Sets the Y coordinate of the Tile that this painting is on.
		 * @param y The Y coordinate of the Tile that this painting is on.
		 */
		public void TileY(int y)
		{
			tiley = y;
		}
		/**
		 * Sets the Z coordinate of the Tile that this painting is on.
		 * @param z The Z coordinate of the Tile that this painting is on.
		 */
		public void TileZ(int z)
		{
			tilez = z;
		}
		/**
		 * Sets the coordinates of the Tile that this painting is on.
		 * @param x The X coordinate of the Tile that this painting is on.
		 * @param y The Y coordinate of the Tile that this painting is on.
		 * @param z The Z coordinate of the Tile that this painting is on.
		 */
		public void Tile(int x, int y, int z)
		{
			tilex = x;
			tiley = y;
			tilez = z;
		}

		/**
		 * Returns the tag for this Painting entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Painting entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Byte("Dir", (byte)dir.ordinal()),
				  new Tag.String("Motive", motive),
				  new Tag.Int("TileX", tilex),
				  new Tag.Int("TileY", tiley),
				  new Tag.Int("TileZ", tilez));
			return t;
		}
	}
	/**
	 * XP Orb entity
	 */
	public static class XPOrb extends Entity
	{
		/**
		 * The hit points of this XP Orb has.
		 */
		private short health;
		/**
		 * The number of ticks this XP Orb has been left alone for.
		 */
		private short age;
		/**
		 * The amount of experience this XP Orb gives when picked up.
		 */
		private short value;

		/**
		 * Constructs an XP Orb entity from the given tag.
		 * @param xporb The tag from which to construct this XP Orb entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public XPOrb(Tag.Compound xporb) throws FormatException
		{
			super(xporb);

			health = ((Tag.Short)xporb.find(Tag.Type.SHORT, "Health")).v;
			age = ((Tag.Short)xporb.find(Tag.Type.SHORT, "Age")).v;
			value = ((Tag.Short)xporb.find(Tag.Type.SHORT, "Value")).v;
		}
		/**
		 * Constructs an XP Orb from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public XPOrb(double X, double Y, double Z)
		{
			super(X, Y, Z);
			health = 5;
			age = 0;
			value = 1;
		}

		/**
		 * Returns the hit points this XP Orb has.
		 * @return The hit points this XP Orb has.
		 */
		public short Health()
		{
			return health;
		}
		/**
		 * Sets the hit points this XP Orb has.
		 * @param hp The hit points this XP Orb has.
		 */
		public void Health(short hp)
		{
			health = hp;
		}

		/**
		 * Returns the number of ticks this XP Orb has been left alone.
		 * @return The number of ticks this XP Orb has been left alone.
		 */
		public short Age()
		{
			return age;
		}
		/**
		 * Sets the number of ticks this XP Orb has been left alone.
		 * @param ticks The number of ticks this XP Orb has been left alone.
		 */
		public void Age(short ticks)
		{
			age = ticks;
		}

		/**
		 * Returns the amount of experience this XP Orb will give when picked up.
		 * @return The amount of experience this XP Orb will give when picked up.
		 */
		public short Value()
		{
			return value;
		}
		/**
		 * Sets the amount of experience this XP Orb will give when picked up.
		 * @param xp The amount of experience this XP Orb will give when picked up.
		 */
		public void Value(short xp)
		{
			value = xp;
		}

		/**
		 * Returns the tag for this XP Orb entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this XP Orb entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Short("Health", health),
				  new Tag.Short("Age", age),
				  new Tag.Short("Value", value));
			return t;
		}
	}
	/**
	 * Projectiles
	 */
	public static abstract class Projectile extends Entity
	{
		/**
		 * The tile that the projectile is in.
		 */
		private short xtile, ytile, ztile;
		/**
		 * Whether the projectile is lodged into a tile or not.
		 */
		private boolean intile;
		/**
		 * The shake progress for arrows.
		 */
		private byte shake;
		/**
		 * Difference from <code>intile</code> is unknown. Possibly used if buried?
		 */
		private boolean inground;

		/**
		 * Returns the projectile class object given the entity ID.
		 * @param ID The ID of the projectile entity.
		 * @return The projectile class for the given entity ID.
		 * @throws FormatException if the entity ID is not recognized as a projectile.
		 */
		public static Class<? extends Projectile> ClassFromID(String ID) throws FormatException
		{
			switch(ID)
			{
			case "Arrow":			return Projectile.Arrow.class;
			case "Snowball":		return Projectile.Snowball.class;
			case "Egg":				return Projectile.Egg.class;
			case "Fireball":		return Projectile.Fireball.class;
			case "SmallFireball":	return Projectile.SmallFireball.class;
			case "ThrownEnderpearl":return Projectile.ThrownEnderpearl.class;
			}
			throw new FormatException("Unknown Projectile ID: \""+ID+"\"");
		}

		/**
		 * Constructs a projectile from the given tag.
		 * @param projectile The tag from which to construct the projectile.
		 * @throws FormatException if the tag is invalid.
		 */
		public Projectile(Tag.Compound projectile) throws FormatException
		{
			super(projectile);

			xtile = ((Tag.Short)projectile.find(Tag.Type.SHORT, "xTile")).v;
			ytile = ((Tag.Short)projectile.find(Tag.Type.SHORT, "yTile")).v;
			ztile = ((Tag.Short)projectile.find(Tag.Type.SHORT, "zTile")).v;
			intile = ((Tag.Byte)projectile.find(Tag.Type.BYTE, "inTile")).v != 0 ? true : false;
			shake = ((Tag.Byte)projectile.find(Tag.Type.BYTE, "shake")).v;
			inground = ((Tag.Byte)projectile.find(Tag.Type.BYTE, "inGround")).v != 0 ? true : false;
		}
		/**
		 * Constructs a Projectile from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Projectile(double X, double Y, double Z)
		{
			super(X, Y, Z);
			xtile = (short)X;
			ytile = (short)Y;
			ztile = (short)Z;
			intile = false;
			shake = 0;
			inground = false;
		}

		/**
		 * Returns the X coordinate of the tile this projectile is in.
		 * @return The X coordinate of the tile this projectile is in.
		 */
		public short TileX()
		{
			return xtile;
		}
		/**
		 * Returns the Y coordinate of the tile this projectile is in.
		 * @return The Y coordinate of the tile this projectile is in.
		 */
		public short TileY()
		{
			return ytile;
		}
		/**
		 * Returns the Z coordinate of the tile this projectile is in.
		 * @return The Z coordinate of the tile this projectile is in.
		 */
		public short TileZ()
		{
			return ztile;
		}
		/**
		 * Sets the X coordinate of the tile this projectile is in.
		 * @param x The X coordinate of the tile this projectile is in.
		 */
		public void TileX(short x)
		{
			xtile = x;
		}
		/**
		 * Sets the Y coordinate of the tile this projectile is in.
		 * @param y The Y coordinate of the tile this projectile is in.
		 */
		public void TileY(short y)
		{
			ytile = y;
		}
		/**
		 * Sets the Z coordinate of the tile this projectile is in.
		 * @param z The Z coordinate of the tile this projectile is in.
		 */
		public void TileZ(short z)
		{
			ztile = z;
		}
		/**
		 * Sets the coordinates of the tile this projectile is in.
		 * @param x The X coordinate of the tile this projectile is in.
		 * @param y The Y coordinate of the tile this projectile is in.
		 * @param z The Z coordinate of the tile this projectile is in.
		 */
		public void Tile(short x, short y, short z)
		{
			xtile = x;
			ytile = y;
			ztile = z;
		}

		/**
		 * Returns whether this projectile is in a tile.
		 * @return Whether this projectile is in a tile.
		 */
		public boolean InTile()
		{
			return intile;
		}
		/**
		 * Sets whether this projectile is in a tile.
		 * @param in Whether this projectile is in a tile.
		 */
		public void InTile(boolean in)
		{
			intile = in;
		}

		/**
		 * Returns the shake value of this projectile.
		 * @return The shake value of this projectile.
		 */
		public byte Shake()
		{
			return shake;
		}
		/**
		 * Sets the shake value of this projectile.
		 * @param shake The shake value of this projectile.
		 */
		public void Shake(byte shake)
		{
			this.shake = shake;
		}

		/**
		 * Returns whether this projectile is in the ground or not.
		 * @return Whether this projectile is in the ground or not.
		 */
		public boolean InGround()
		{
			return inground;
		}
		/**
		 * Sets whether this projectile is in the ground or not.
		 * @param in Whether this projectile is in the ground or not.
		 */
		public void InGround(boolean in)
		{
			inground = in;
		}

		/**
		 * Returns the tag for this Projectile entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Projectile entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Short("xTile", xtile),
				  new Tag.Short("yTile", ytile),
				  new Tag.Short("zTile", ztile),
				  new Tag.Byte("inTile", (byte)(intile?1:0)),
				  new Tag.Byte("shake", shake),
				  new Tag.Byte("inGround", (byte)(inground?1:0)));
			return t;
		}

		/**
		 * Arrow projectiles
		 */
		public static class Arrow extends Projectile
		{
			/**
			 * Unknown.
			 */
			private byte indata;
			/**
			 * Unknown, affects how the arrow can be picked up.
			 */
			private byte pickup;
			/**
			 * Unknown, affects damage dealt.
			 */
			private double damage;

			/**
			 * Constructs an Arrow projectile from the given tag.
			 * @param arrow The tag from which to construct this Arrow projectile.
			 * @throws FormatException if the given tag is invalid.
			 */
			public Arrow(Tag.Compound arrow) throws FormatException
			{
				super(arrow);

				indata = ((Tag.Byte)arrow.find(Tag.Type.BYTE, "inData")).v;
				pickup = ((Tag.Byte)arrow.find(Tag.Type.BYTE, "pickup")).v;
				try
				{
					damage = ((Tag.Double)arrow.find(Tag.Type.DOUBLE, "damage")).v;
				}
				catch(FormatException e)
				{
					damage = 1.0;
				}
			}
			/**
			 * Constructs an Arrow from a position.
			 * @param X The X position.
			 * @param Y The Y position.
			 * @param Z The Z position.
			 */
			public Arrow(double X, double Y, double Z)
			{
				super(X, Y, Z);
			}

			public byte InData()
			{
				return indata;
			}
			public void InData(byte datain)
			{
				indata = datain;
			}

			public byte Pickup()
			{
				return pickup;
			}
			public void Pickup(byte up)
			{
				pickup = up;
			}

			public double Damage()
			{
				return damage;
			}
			public void Damage(double d)
			{
				damage = d;
			}

			/**
			 * Returns the tag for this Arrow projectile entity.
			 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
			 * @return The tag for this Arrow projectile entity.
			 */
			@Override public Tag.Compound ToNBT(String name)
			{
				Tag.Compound t = super.ToNBT(name);
				t.add(new Tag.Byte("inData", indata),
					  new Tag.Byte("pickup", pickup),
					  new Tag.Double("damage", damage));
				return t;
			}
		}
		/**
		 * Thrown Snowball projectiles
		 */
		public static class Snowball extends Projectile
		{
			/**
			 * Constructs a Thrown Snowball projectile from the given tag.
			 * @param snowball The tag from which to construct this Thrown Snowball projectile.
			 * @throws FormatException if the given tag is invalid.
			 */
			public Snowball(Tag.Compound snowball) throws FormatException
			{
				super(snowball);
			}
			/**
			 * Constructs a Thrown Snowball from a position.
			 * @param X The X position.
			 * @param Y The Y position.
			 * @param Z The Z position.
			 */
			public Snowball(double X, double Y, double Z)
			{
				super(X, Y, Z);
			}
		}
		/**
		 * Thrown Egg projectiles
		 */
		public static class Egg extends Projectile
		{
			/**
			 * Constructs a Thrown Egg projectile from the given tag.
			 * @param egg The tag from which to construct this Thrown Egg projectile.
			 * @throws FormatException if the given tag is invalid.
			 */
			public Egg(Tag.Compound egg) throws FormatException
			{
				super(egg);
			}
			/**
			 * Constructs a Thrown Egg from a position.
			 * @param X The X position.
			 * @param Y The Y position.
			 * @param Z The Z position.
			 */
			public Egg(double X, double Y, double Z)
			{
				super(X, Y, Z);
			}
		}
		/**
		 * Ghast Fireball projectiles
		 */
		public static class Fireball extends Projectile
		{
			/**
			 * Constructs a Ghast Fireball projectile from the given tag.
			 * @param fireball The tag from which to construct this Ghast Fireball projectile.
			 * @throws FormatException if the given tag is invalid.
			 */
			public Fireball(Tag.Compound fireball) throws FormatException
			{
				super(fireball);
			}
			/**
			 * Constructs a Ghast Fireball from a position.
			 * @param X The X position.
			 * @param Y The Y position.
			 * @param Z The Z position.
			 */
			public Fireball(double X, double Y, double Z)
			{
				super(X, Y, Z);
			}
		}
		/**
		 * Blaze Fireball projectiles (also used by Fire Charges)
		 */
		public static class SmallFireball extends Projectile
		{
			/**
			 * Constructs a Blaze Fireball projectile from the given tag.
			 * @param smallfireball The tag from which to construct this Blaze Fireball projectile.
			 * @throws FormatException if the given tag is invalid.
			 */
			public SmallFireball(Tag.Compound smallfireball) throws FormatException
			{
				super(smallfireball);
			}
			/**
			 * Constructs a Blaze Fireball from a position.
			 * @param X The X position.
			 * @param Y The Y position.
			 * @param Z The Z position.
			 */
			public SmallFireball(double X, double Y, double Z)
			{
				super(X, Y, Z);
			}
		}
		/**
		 * Thrown Ender Pearl projectiles
		 */
		public static class ThrownEnderpearl extends Projectile
		{
			/**
			 * Constructs a Thrown Ender Pearl projectile from the given tag.
			 * @param thrownenderpearl The tag from which to construct this Thrown Ender Pearl projectile.
			 * @throws FormatException if the given tag is invalid.
			 */
			public ThrownEnderpearl(Tag.Compound thrownenderpearl) throws FormatException
			{
				super(thrownenderpearl);
			}
			/**
			 * Constructs a Thrown Ender Pearl from a position.
			 * @param X The X position.
			 * @param Y The Y position.
			 * @param Z The Z position.
			 */
			public ThrownEnderpearl(double X, double Y, double Z)
			{
				super(X, Y, Z);
			}
		}
	}
}