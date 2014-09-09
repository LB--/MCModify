package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.nbt.FormatException;
import com.lb_stuff.mcmodify.nbt.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mob entities
 */
public abstract class Mob extends Entity
{
	/**
	 * The number of hit points the entity has, 2 per heart.
	 */
	private short health;
	/**
	 * Ticks until the invincibility shield wears off after taking damage.
	 */
	private short attacktime;
	/**
	 * Ticks until the red shading wears off after taking damage.
	 */
	private short hurttime;
	/**
	 * Number of Ticks the entity has been dead for.
	 */
	private short deathtime;
	/**
	 * Potion Effect
	 */
	public static enum Effect
	{
		Speed, Slowness,
		Haste, MiningFatigue,
		Strength, Weakness,
		InstantHealth, InstantDamage,
		JumpBoost,
		Nausea,
		Regeneration, Poison, Wither,
		Resistance, FireResistance,
		WaterBreathing,
		Invisibility,
		Blindness, NightVision,
		Hunger;

		public static Effect FromID(byte ID) throws FormatException
		{
			switch(ID)
			{
			case 1:  return Speed;
			case 2:  return Slowness;
			case 3:  return Haste;
			case 4:  return MiningFatigue;
			case 5:  return Strength;
			case 6:  return InstantHealth;
			case 7:  return InstantDamage;
			case 8:  return JumpBoost;
			case 9:  return Nausea;
			case 10: return Regeneration;
			case 11: return Resistance;
			case 12: return FireResistance;
			case 13: return WaterBreathing;
			case 14: return Invisibility;
			case 15: return Blindness;
			case 16: return NightVision;
			case 17: return Hunger;
			case 18: return Weakness;
			case 19: return Poison;
			case 20: return Wither;
			}
			throw new FormatException("Unknown Potion Effect: "+ID);
		}
		public byte ID()
		{
			switch(this)
			{
			case Speed:          return 1;
			case Slowness:       return 2;
			case Haste:          return 3;
			case MiningFatigue:  return 4;
			case Strength:       return 5;
			case Weakness:       return 18;
			case InstantHealth:  return 6;
			case InstantDamage:  return 7;
			case JumpBoost:      return 8;
			case Nausea:         return 9;
			case Regeneration:   return 10;
			case Poison:         return 19;
			case Wither:         return 20;
			case Resistance:     return 11;
			case FireResistance: return 12;
			case WaterBreathing: return 13;
			case Invisibility:   return 14;
			case Blindness:      return 15;
			case NightVision:    return 16;
			case Hunger:         return 17;
			}
			throw new IllegalArgumentException();
		}
	}
	/**
	 * Potion Effect Info
	 */
	public static class EffectInfo
	{
		/**
		 * The effect level, 0 is 1.
		 */
		private byte amplifier;
		/**
		 * The number of ticks before the effect wears out.
		 */
		private int duration;
		/**
		 * Whether or not the effect was granted by a beacon and is therefore ambient.
		 */
		boolean ambient;

		/**
		 * Constructs this portion effect info from the given level and time.
		 * <code>ambient</code> is assumed false.
		 * @param level The level of the effect. 0 is level 1.
		 * @param ticks The number of ticks before the effect wears off.
		 */
		public EffectInfo(byte level, int ticks)
		{
			amplifier = level;
			duration = ticks;
		}
		/**
		 * Constructs this portion effect info from the given level, time,
		 * and ambience.
		 * @param level The level of the effect. 0 is level 1.
		 * @param ticks The number of ticks before the effect wears off.
		 * @param beacon Whether the effect is provided by a beacon and thus ambient.
		 */
		public EffectInfo(byte level, int ticks, boolean beacon)
		{
			amplifier = level;
			duration = ticks;
			ambient = beacon;
		}

		/**
		 * Returns the level of the effect. 0 is level 1.
		 * @return The level of the effect. 0 is level 1.
		 */
		public byte Level()
		{
			return amplifier;
		}
		/**
		 * Sets the level of the effect. 0 is level 1.
		 * @param level The level of the effect. 0 is level 1.
		 */
		public void Level(byte level)
		{
			amplifier = level;
		}

		/**
		 * Returns the number of ticks before the effect wears off.
		 * @return The number of ticks before the effect wears off.
		 */
		public int Time()
		{
			return duration;
		}
		/**
		 * Sets the number of ticks before the effect wears off.
		 * @param ticks The number of ticks before the effect wears off.
		 */
		public void Time(int ticks)
		{
			duration = ticks;
		}

		/**
		 * Returns whether or not the effect was granted by a beacon and is therefore ambient.
		 * @return Whether or not the effect was granted by a beacon and is therefore ambient.
		 */
		public boolean Ambient()
		{
			return ambient;
		}
		/**
		 * Sets whether or not the effect was granted by a beacon and is therefore ambient.
		 * @param ticks Whether or not the effect was granted by a beacon and is therefore ambient.
		 */
		public void Ambient(boolean ticks)
		{
			ambient = ticks;
		}
	}
	/**
	 * The effects currently active on this mob.
	 */
	private Map<Effect, EffectInfo> activeeffects = new HashMap<>();

	/**
	 * Equip ID
	 */
	public static enum EquipmentSlot
	{
		/**
		 * The slot for the item in the mob's hand.
		 */
		Hand,
		/**
		 * The slots for the items equipped as the mob's armor.
		 */
		Boots, Leggings, Chest, Helmet;

		public static EquipmentSlot FromID(byte ID) throws FormatException
		{
			switch(ID)
			{
			case 0: return Hand;
			case 1: return Boots;
			case 2: return Leggings;
			case 3: return Chest;
			case 4: return Helmet;
			}
			throw new FormatException("Unknown Equip: "+ID);
		}
		public byte ID()
		{
			switch(this)
			{
			case Hand:     return 0;
			case Boots:    return 1;
			case Leggings: return 2;
			case Chest:    return 3;
			case Helmet:   return 4;
			}
			throw new IllegalArgumentException();
		}
	}
	/**
	 * The equipped items on this mob.
	 */
	private Map<EquipmentSlot, Inventory.Item> equipment = new HashMap<>();
	/**
	 * The chances for each equipped item on this mob to drop.
	 */
	private Map<EquipmentSlot, Float> dropchances = new HashMap<>();

	/**
	 * Whether or not this mob can pick up dropped items on the ground and equip them.
	 */
	private boolean canpickuploot;

	/**
	 * Whether or not this mob is disallowed from despawning.
	 */
	private boolean persistencerequired;

	/**
	 * The custom name for this particular mob. May be null.
	 */
	private String customname;
	/**
	 * Whether or not a nametag should display the custom name.
	 */
	private boolean customnamevisible;

	/**
	 * Health in a floating point format.
	 */
	private float healf;

	/**
	 * Represents a single leash the mob has.
	 */
	public static abstract class Leash
	{
		/**
		 * Constructs a Leash from the given tag.
		 * @param t The tag from which to construct the leash.
		 * @return The leash constructed from the given tag.
		 * @throws FormatException If a leash cannot be constructed from the given tag.
		 */
		public static Leash FromNBT(Tag.Compound t) throws FormatException
		{
			try
			{
				int x = ((Tag.Int)t.find(Tag.Type.INT, "X")).v,
				    y = ((Tag.Int)t.find(Tag.Type.INT, "Y")).v,
				    z = ((Tag.Int)t.find(Tag.Type.INT, "Z")).v;
				return new Leash.Fence(x, y, z);
			}
			catch(FormatException e)
			{
			}
			long least = ((Tag.Long)t.find(Tag.Type.LONG, "UUIDLeast")).v,
			     most  = ((Tag.Long)t.find(Tag.Type.LONG, "UUIDMost")).v;
			return new Leash.UUID(least, most);
		}
		/**
		 * Returns the tag for this leash.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this leash.
		 */
		public abstract Tag.Compound ToNBT(String name);

		/**
		 * Represents a leash that attaches a mob to a fence post.
		 */
		public static class Fence extends Leash
		{
			/**
			 * The coordinates of the fence post.
			 */
			private int x, y, z;

			/**
			 * Constructs this leash from the given fence post coordinates.
			 * @param tilex The x coordinate of the tile the fence post is in.
			 * @param tiley The y coordinate of the tile the fence post is in.
			 * @param tilez The z coordinate of the tile the fence post is in.
			 */
			public Fence(int tilex, int tiley, int tilez)
			{
				Tile(tilex, tiley, tilez);
			}

			/**
			 * Returns the x coordinate of the tile the fence post is in.
			 * @return The x coordinate of the tile the fence post is in.
			 */
			public int TileX()
			{
				return x;
			}
			/**
			 * Returns the y coordinate of the tile the fence post is in.
			 * @return The y coordinate of the tile the fence post is in.
			 */
			public int TileY()
			{
				return y;
			}
			/**
			 * Returns the z coordinate of the tile the fence post is in.
			 * @return The z coordinate of the tile the fence post is in.
			 */
			public int TileZ()
			{
				return z;
			}
			/**
			 * Sets the coordinates of the tile the fence is in.
			 * @param tilex The x coordinate of the tile the fence post is in.
			 * @param tiley The y coordinate of the tile the fence post is in.
			 * @param tilez The z coordinate of the tile the fence post is in.
			 */
			public void Tile(int tilex, int tiley, int tilez)
			{
				x = tilex;
				y = tiley;
				z = tilez;
			}

			/**
			 * Returns the tag for this fence leash.
			 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
			 * @return The tag for this fence leash.
			 */
			public Tag.Compound ToNBT(String name)
			{
				return new Tag.Compound(name, new Tag.Int("X", x),
				                              new Tag.Int("Y", y),
				                              new Tag.Int("Z", z));
			}
		}
		public static class UUID extends Leash
		{
			private long least, most;

			public UUID(long Least, long Most)
			{
				least = Least;
				most = Most;
			}

			public long Least()
			{
				return least;
			}
			public long Most()
			{
				return most;
			}
			public void UUID(long Least, long Most)
			{
				least = Least;
				most = Most;
			}

			/**
			 * Returns the tag for this UUID leash.
			 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
			 * @return The tag for this UUID leash.
			 */
			public Tag.Compound ToNBT(String name)
			{
				return new Tag.Compound(name, new Tag.Long("UUIDLeast", least),
				                              new Tag.Long("UUIDMost", most));
			}
		}
	}
	/**
	 * The leash this mob has.
	 */
	private Leash leash;

	/**
	 * Given an Entity ID, returns the Class object for the Mob class that represents that Entity ID.
	 * @param ID The Entity ID.
	 * @return The Class object for the Mob class that represents the Entity ID.
	 * @throws FormatException if the given Tile Entity ID is unknown.
	 */
	public static Class<? extends Mob> ClassFromID(String ID) throws FormatException
	{
		switch(ID)
		{
		case "Blaze":         return Blaze.class;
		case "CaveSpider":    return CaveSpider.class;
		case "Chicken":       return Chicken.class;
		case "Cow":           return Cow.class;
		case "Creeper":       return Creeper.class;
		case "EnderDragon":   return EnderDragon.class;
		case "Enerman":       return Enderman.class;
		case "Ghast":         return Ghast.class;
		case "Giant":         return Giant.class;
		case "LavaSlime":     return LavaSlime.class;
		case "MushroomCow":   return MushroomCow.class;
		case "Ozelot":        return Ozelot.class;
		case "Pig":           return Pig.class;
		case "PigZombie":     return PigZombie.class;
		case "Sheep":         return Sheep.class;
		case "Silverfish":    return Silverfish.class;
		case "Skeleton":      return Skeleton.class;
		case "Slime":         return Slime.class;
		case "SnowMan":       return SnowMan.class;
		case "Spider":        return Spider.class;
		case "Squid":         return Squid.class;
		case "Villager":      return Villager.class;
		case "VillagerGolem": return VillagerGolem.class;
		case "Wolf":          return Wolf.class;
		case "Zombie":        return Zombie.class;
		}
		throw new FormatException("Unknown Mob Entity ID: \""+ID+"\"");
	}

	/**
	 * Constructs a mob entity from the given tag.
	 * @param mob The tag from which to construct this mob entity.
	 * @throws FormatException if the given tag is invalid.
	 */
	public Mob(Tag.Compound mob) throws FormatException
	{
		super(mob);

		health = ((Tag.Short)mob.find(Tag.Type.SHORT, "Health")).v;
		attacktime = ((Tag.Short)mob.find(Tag.Type.SHORT, "AttackTime")).v;
		hurttime = ((Tag.Short)mob.find(Tag.Type.SHORT, "HurtTime")).v;
		deathtime = ((Tag.Short)mob.find(Tag.Type.SHORT, "DeathTime")).v;
		Tag.List effects = null;
		try
		{
			effects = (Tag.List)mob.find(Tag.Type.LIST, "ActiveEffects");
		}
		catch(FormatException e)
		{
		}
		if(effects != null)
		{
			if(effects.getContainedType() != Tag.Type.COMPOUND)
			{
				throw new FormatException("Invalid Potion Effects List; expected list of Compound, got list of "+effects.getContainedType());
			}
			for(Tag t : effects)
			{
				Tag.Compound effect = (Tag.Compound)t;
				activeeffects.put(Effect.FromID(((Tag.Byte)effect.find(Tag.Type.BYTE, "Id")).v),
				                  new EffectInfo(((Tag.Byte)mob.find(Tag.Type.BYTE, "Amplifier")).v,
				                                 ((Tag.Int)mob.find(Tag.Type.INT, "Duration")).v,
				                                 ((Tag.Byte)mob.find(Tag.Type.BYTE, "Ambient")).v  == 1 ? true : false));
			}
		}

		try
		{
			Tag.List equip = (Tag.List)mob.find(Tag.Type.LIST, "Equipment");
			equipment.put(EquipmentSlot.Hand,     new Inventory.Item((Tag.Compound)equip.get(EquipmentSlot.Hand.    ID())));
			equipment.put(EquipmentSlot.Boots,    new Inventory.Item((Tag.Compound)equip.get(EquipmentSlot.Boots.   ID())));
			equipment.put(EquipmentSlot.Leggings, new Inventory.Item((Tag.Compound)equip.get(EquipmentSlot.Leggings.ID())));
			equipment.put(EquipmentSlot.Chest,    new Inventory.Item((Tag.Compound)equip.get(EquipmentSlot.Chest.   ID())));
			equipment.put(EquipmentSlot.Helmet,   new Inventory.Item((Tag.Compound)equip.get(EquipmentSlot.Helmet.  ID())));

			Tag.List drops = (Tag.List)mob.find(Tag.Type.LIST, "DropChances");
			dropchances.put(EquipmentSlot.Hand,     ((Tag.Float)equip.get(EquipmentSlot.Hand.    ID())).v);
			dropchances.put(EquipmentSlot.Boots,    ((Tag.Float)equip.get(EquipmentSlot.Boots.   ID())).v);
			dropchances.put(EquipmentSlot.Leggings, ((Tag.Float)equip.get(EquipmentSlot.Leggings.ID())).v);
			dropchances.put(EquipmentSlot.Chest,    ((Tag.Float)equip.get(EquipmentSlot.Chest.   ID())).v);
			dropchances.put(EquipmentSlot.Helmet,   ((Tag.Float)equip.get(EquipmentSlot.Helmet.  ID())).v);

			canpickuploot = ((Tag.Byte)mob.find(Tag.Type.BYTE, "CanPickUpLoot")).v == 1 ? true : false;
			persistencerequired = ((Tag.Byte)mob.find(Tag.Type.BYTE, "PersistenceRequired")).v == 1 ? true : false;
		}
		catch(FormatException e)
		{
		}

		try
		{
			customname = ((Tag.String)mob.find(Tag.Type.STRING, "CustomName")).v;
		}
		catch(FormatException e)
		{
			customname = null;
		}
		try
		{
			customnamevisible = ((Tag.Byte)mob.find(Tag.Type.BYTE, "CustomNameVisible")).v == 1 ? true : false;
		}
		catch(FormatException e)
		{
			customnamevisible = false;
		}
		try
		{
			healf = ((Tag.Float)mob.find(Tag.Type.FLOAT, "HealF")).v;
		}
		catch(FormatException e)
		{
			healf = health;
		}
		try
		{
			if(((Tag.Byte)mob.find(Tag.Type.BYTE, "Leashed")).v == 1)
			{
				leash = Leash.FromNBT((Tag.Compound)mob.find(Tag.Type.COMPOUND, "Leash"));
			}
			else
			{
				leash = null;
			}
		}
		catch(FormatException e)
		{
			leash = null;
		}
	}
	/**
	 * Constructs a Mob from a position.
	 * @param X The X position.
	 * @param Y The Y position.
	 * @param Z The Z position.
	 */
	public Mob(double X, double Y, double Z)
	{
		super(X, Y, Z);
		health = 20;
		attacktime = 0;
		hurttime = 0;
		deathtime = 0;
	}

	/**
	 * Returns the hit points this mob entity has.
	 * @return The hit points this mob entity has.
	 */
	public short Health()
	{
		return health;
	}
	/**
	 * Sets the hit points this mob entity has.
	 * @param hp The hit points this mob entity has.
	 */
	public void Health(short hp)
	{
		health = hp;
	}

	/**
	 * Returns the number of ticks until invincibility wears off.
	 * @return The number of ticks until invincibility wears off.
	 */
	public short Invincibility()
	{
		return attacktime;
	}
	/**
	 * Sets the number of ticks until invincibility wears off.
	 * @param ticks The number of ticks until invincibility wears off.
	 */
	public void Invincibility(short ticks)
	{
		attacktime = ticks;
	}

	/**
	 * Returns the number of ticks until the red shade wears off.
	 * @return The number of ticks until the red shade wears off.
	 */
	public short RedShade()
	{
		return hurttime;
	}
	/**
	 * Sets the number of ticks until the red shade wears off.
	 * @param ticks The number of ticks until the red shade wears off.
	 */
	public void RedShade(short ticks)
	{
		hurttime = ticks;
	}

	/**
	 * Returns the number of ticks that this mob entity has been dead for.
	 * @return The number of ticks that this mob entity has been dead for.
	 */
	public short Death()
	{
		return deathtime;
	}
	/**
	 * Sets the number of ticks that this mob entity has been dead for.
	 * @param ticks The number of ticks that this mob entity has been dead for.
	 */
	public void Death(short ticks)
	{
		deathtime = ticks;
	}

	/**
	 * Returns the information about the given effect, or null if that effect is not present.
	 * @param id The effect to retrieve information about.
	 * @return The information about the given effect, or null if that effect is not present.
	 */
	public EffectInfo Effect(Effect id)
	{
		return activeeffects.get(id);
	}
	/**
	 * Sets the information for the given effect, or removes the effect.
	 * @param id The effect to alter.
	 * @param info The new information for the effect, or null to remove the effect.
	 */
	public void Effect(Effect id, EffectInfo info)
	{
		if(info == null)
		{
			activeeffects.remove(id);
		}
		else
		{
			activeeffects.put(id, info);
		}
	}

	/**
	 * Returns the item equipped in the given equipment slot, or null if no item is equipped there.
	 * @param e The equipment slot to check.
	 * @return The item equipped in the given equipment slot, or null if no item is equipped there.
	 */
	public Inventory.Item Equipment(EquipmentSlot e)
	{
		return equipment.get(e);
	}
	/**
	 * Sets or removes the item equipped in the given equipment slot.
	 * @param e The equipment slot to change.
	 * @param i The item to equip, or null to unequip.
	 */
	public void Equipment(EquipmentSlot e, Inventory.Item i)
	{
		equipment.put(e, i);
	}
	/**
	 * Returns the chance for an equipped item to be dropped when this mob dies, between 0 and 1 inclusive.
	 * @param e The equipment slot to check.
	 * @return The chance for an equipped item to be dropped when this mob dies, between 0 and 1 inclusive.
	 */
	public float DropChance(EquipmentSlot e)
	{
		return dropchances.get(e);
	}
	/**
	 * Sets the chance for the item to drop when the mob dies for the given equipment slot.
	 * @param e The equipment slot to check.
	 * @param chance The chance for the item to drop between 0 and 1 inclusive.
	 */
	public void DropChance(EquipmentSlot e, float chance)
	{
		dropchances.put(e, chance);
	}

	/**
	 * Returns whether or not this mob can pick up dropped items on the ground.
	 * @return Whether or not this mob can pick up dropped items on the ground.
	 */
	public boolean PicksUpLoot()
	{
		return canpickuploot;
	}
	/**
	 * Sets whether or not this mob can pick up dropped items on the ground.
	 * @param can Whether or not this mob can pick up dropped items on the ground.
	 */
	public void PicksUpLoot(boolean can)
	{
		canpickuploot = can;
	}

	/**
	 * Returns whether this mob is allowed to despawn or not.
	 * @return Whether this mob is allowed to despawn or not.
	 */
	public boolean CanDespawn()
	{
		return !persistencerequired;
	}
	/**
	 * Sets whether this mob is allowed to despawn or not.
	 * @param can Whether this mob is allowed to despawn or not.
	 */
	public void CanDespawn(boolean can)
	{
		persistencerequired = !can;
	}

	/**
	 * Returns the custom name for this mob, or null if the mob's name is not customized.
	 * @return The custom name for this mob, or null if the mob's name is not customized.
	 */
	public String CustomName()
	{
		return customname;
	}
	/**
	 * Sets or removes the custom name of this mob.
	 * @param name The custom name of this mob, or null to remove the custom name.
	 */
	public void CustomName(String name)
	{
		customname = name;
	}

	/**
	 * Returns whether or not the custom name should be displayed in a nametag.
	 * @return Whether or not the custom name should be displayed in a nametag.
	 */
	public boolean NametagVisible()
	{
		return customnamevisible;
	}
	/**
	 * Sets whether or not the custom name should be displayed in a nametag.
	 * @param visible Whether or not the custom name should be displayed in a nametag.
	 */
	public void NametagVisible(boolean visible)
	{
		customnamevisible = visible;
	}

	/**
	 * Returns the health in floating-point form. Not automatically synced with the integer health.
	 * @return The health in floating-point form.
	 */
	public float PreciseHealth()
	{
		return healf;
	}
	/**
	 * Sets the health in floating-point form. Not automatically synced with the integer health.
	 * @param health The health in floating-point form.
	 */
	public void PreciseHealth(float health)
	{
		healf = health;
	}

	/**
	 * Returns the leash for this mob, null if there is no leash.
	 * @return The leash for this mob, null if there is no leash.
	 */
	public Leash Leash()
	{
		return leash;
	}
	/**
	 * Sets or removes the leash for this mob.
	 * @param l The leash for this mob, null to remove the leash.
	 */
	public void Leash(Leash l)
	{
		leash = l;
	}

	/**
	 * Returns the tag for this mob entity.
	 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
	 * @return The tag for this mob entity.
	 */
	@Override public Tag.Compound ToNBT(String name)
	{
		Tag.Compound t = super.ToNBT(name);
		Tag.List effects;
		Tag.List equip, drops;
		t.add(new Tag.Short("Health", health),
		      new Tag.Short("AttackTime", attacktime),
		      new Tag.Short("HurtTime", hurttime),
		      new Tag.Short("DeathTime", deathtime),
		      effects = new Tag.List("ActiveEffects", Tag.Type.COMPOUND),
			  equip =   new Tag.List("Equipment", Tag.Type.COMPOUND),
		      drops =   new Tag.List("DropChances", Tag.Type.FLOAT),
		      new Tag.Byte("CanPickUpLoot", (byte)(canpickuploot ? 1 : 0)),
		      new Tag.Byte("PersistenceRequired", (byte)(persistencerequired ? 1 : 0)),
		      new Tag.Byte("CustomNameVisible", (byte)(customnamevisible ? 1 : 0)),
		      new Tag.Byte("Leashed", (byte)(leash != null ? 1 : 0)),
		      new Tag.Float("HealF", healf));
		if(customname != null)
		{
			t.add(new Tag.String("CustomName", customname));
		}
		if(leash != null)
		{
			t.add(leash.ToNBT("Leash"));
		}
		for(Map.Entry<Effect, EffectInfo> effect : activeeffects.entrySet())
		{
			effects.add(new Tag.Compound(null, new Tag.Byte("Id", effect.getKey().ID()),
											   new Tag.Byte("Amplifier", effect.getValue().amplifier),
											   new Tag.Int("Duration", effect.getValue().duration),
											   new Tag.Byte("ambient", (byte)(effect.getValue().ambient ? 1 : 0))));
		}
		equip.add(new Tag.Compound(null),
				  new Tag.Compound(null),
				  new Tag.Compound(null),
				  new Tag.Compound(null),
				  new Tag.Compound(null));
		for(Map.Entry<EquipmentSlot, Inventory.Item> e : equipment.entrySet())
		{
			equip.set(e.getKey().ID(), e.getValue().ToNBT(null));
		}
		drops.add(new Tag.Float(null, 0.0f),
				  new Tag.Float(null, 0.0f),
				  new Tag.Float(null, 0.0f),
				  new Tag.Float(null, 0.0f),
				  new Tag.Float(null, 0.0f));
		for(Map.Entry<EquipmentSlot, Float> e : dropchances.entrySet())
		{
			drops.set(e.getKey().ID(), new Tag.Float(null, e.getValue()));
		}
		return t;
	}

	/**
	 * This class is extend by mobs that can breed (MushroomCow, Wolf, Ozelot, Cow, Sheep, Chicken, Pig, Villager)
	 */
	public static abstract class Breedable extends Mob
	{
		/**
		 * The number of ticks before this mob stops searching for a mate and loses the hearts over its head.
		 */
		private int inlove;
		/**
		 * The number of ticks the mob has been an adult for. Negative values make it an infant, 0 or above make it adult. Above 0 prevents breeding.
		 */
		private int age;

		/**
		 * Constructs a Breedable mob entity from the given tag.
		 * @param breedable The tag from which to construct the Breedable mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Breedable(Tag.Compound breedable) throws FormatException
		{
			super(breedable);

			inlove = ((Tag.Int)breedable.find(Tag.Type.INT, "InLove")).v;
			age = ((Tag.Int)breedable.find(Tag.Type.INT, "Age")).v;
		}
		/**
		 * Constructs a Breedable from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Breedable(double X, double Y, double Z)
		{
			super(X, Y, Z);
			inlove = 0;
			age = 0;
		}

		/**
		 * Returns the number of ticks before this mob stops searching for a mate.
		 * @return The number of ticks before this mob stops searching for a mate.
		 */
		public int Love()
		{
			return inlove;
		}
		/**
		 * Sets the number of ticks before this mob stops searching for a mate.
		 * @param ticks The number of ticks before this mob stops searching for a mate.
		 */
		public void Love(int ticks)
		{
			inlove = ticks;
		}

		/**
		 * Returns the age of this mob in ticks. Negative values indicate infancy, positive values or 0 indicate adulthood. Values above 0 are the ticks until this mob may breed again.
		 * @return The age of this mob in ticks. Negative values indicate infancy, positive values or 0 indicate adulthood. Values above 0 are the ticks until this mob may breed again.
		 */
		public int Age()
		{
			return age;
		}
		/**
		 * Sets the age of this mob in ticks. Negative values indicate infancy, positive values or 0 indicate adulthood. Values above 0 are the ticks until this mob may breed again.
		 * @param ticks The age of this mob in ticks. Negative values indicate infancy, positive values or 0 indicate adulthood. Values above 0 are the ticks until this mob may breed again.
		 */
		public void Age(int ticks)
		{
			age = ticks;
		}

		/**
		 * Returns the tag for this Breedable mob entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Breedable mob entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Int("InLove", inlove),
				  new Tag.Int("Age", age));
			return t;
		}
	}
	/**
	 * This class is extended by mobs that can be tamed (Wolf, Ozelot).
	 */
	public static abstract class Tameable extends Breedable
	{
		/**
		 * The name of the player who owns this mob, or empty if this mob is wild.
		 */
		private String owner;
		/**
		 * Whether or not this mob has been told to sit and stay.
		 */
		private boolean sitting;

		/**
		 * Constructs a Tameable mob entity from the given tag.
		 * @param tameable The tag from which to construct the Tameable mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Tameable(Tag.Compound tameable) throws FormatException
		{
			super(tameable);

			owner = ((Tag.String)tameable.find(Tag.Type.STRING, "Owner")).v;
			sitting = ((Tag.Byte)tameable.find(Tag.Type.BYTE, "Sitting")).v != 0 ? true : false;
		}
		/**
		 * Constructs a Tameable from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Tameable(double X, double Y, double Z)
		{
			super(X, Y, Z);
			owner = "";
			sitting = false;
		}

		/**
		 * Returns the name of the player who owns this mob, or a blank string if the mob is wild.
		 * @return The name of the player who owns this mob, or a blank string if the mob is wild.
		 */
		public String owner()
		{
			return owner;
		}
		/**
		 * Sets the name of the player who owns this mob.
		 * @param name The name of the player who owns this mob, or a blank string if the mob is wild.
		 */
		public void Owner(String name)
		{
			owner = name;
		}

		/**
		 * Returns whether or not the mob has been told to sit and stay.
		 * @return Whether or not the mob has been told to sit and stay.
		 */
		public boolean Sitting()
		{
			return sitting;
		}
		/**
		 * Sets whether or not this tamed mob has been told to sit and stay.
		 * @param staying Whether or not this tamed mob has been told to sit and stay.
		 */
		public void Sitting(boolean staying)
		{
			sitting = staying;
		}

		/**
		 * Returns the tag for this Tameable mob entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Tameable mob entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.String("Owner", owner),
				  new Tag.Byte("Sitting", (byte)(sitting?1:0)));
			return t;
		}
	}

	/**
	 * Blaze mob entity
	 */
	public static class Blaze extends Mob
	{
		/**
		 * Constructs a Blaze mob entity from the given tag.
		 * @param blaze The tag from which to construct the Blaze mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Blaze(Tag.Compound blaze) throws FormatException
		{
			super(blaze);
		}
		/**
		 * Constructs a Blaze from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Blaze(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Cave Spider mob entity
	 */
	public static class CaveSpider extends Mob
	{
		/**
		 * Constructs a Cave Spider mob entity from the given tag.
		 * @param cavespider The tag from which to construct the CaveSpider mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public CaveSpider(Tag.Compound cavespider) throws FormatException
		{
			super(cavespider);
		}
		/**
		 * Constructs a Cave Spider from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public CaveSpider(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Chicken mob entity
	 */
	public static class Chicken extends Breedable
	{
		/**
		 * Constructs a Chicken mob entity from the given tag.
		 * @param chicken The tag from which to construct the Chicken mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Chicken(Tag.Compound chicken) throws FormatException
		{
			super(chicken);
		}
		/**
		 * Constructs a Chicken from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Chicken(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Cow mob entity
	 */
	public static class Cow extends Breedable
	{
		/**
		 * Constructs a Cow mob entity from the given tag.
		 * @param cow The tag from which to construct the Cow mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Cow(Tag.Compound cow) throws FormatException
		{
			super(cow);
		}
		/**
		 * Constructs a Cow from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Cow(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Creeper mob entity
	 */
	public static class Creeper extends Mob
	{
		/**
		 * Whether the creeper has been struck by lighting and is supercharged or not.
		 */
		private boolean powered;

		/**
		 * Constructs a Creeper mob entity from the given tag.
		 * @param creeper The tag from which to construct the Creeper mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Creeper(Tag.Compound creeper) throws FormatException
		{
			super(creeper);

			try
			{
				powered = ((Tag.Byte)creeper.find(Tag.Type.BYTE, "powered")).v != 0 ? true : false;
			}
			catch(FormatException e)
			{
				powered = false;
			}
		}
		/**
		 * Constructs a Creeper from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Creeper(double X, double Y, double Z)
		{
			super(X, Y, Z);
			powered = false;
		}

		/**
		 * Returns whether this Creeper has been struck by lightning and is Supercharged.
		 * @return Whether this Creeper has been struck by lightning and is Supercharged.
		 */
		public boolean Supercharged()
		{
			return powered;
		}
		/**
		 * Sets whether this Creeper has been struck by lightning and is Supercharged.
		 * @param charged Whether this Creeper has been struck by lightning and is Supercharged.
		 */
		public void Supercharged(boolean charged)
		{
			powered = charged;
		}

		/**
		 * Returns the tag for this Creeper mob entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Creeper mob entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Byte("powered", (byte)(powered?1:0)));
			return t;
		}
	}
	/**
	 * Ender Dragon mob entity
	 */
	public static class EnderDragon extends Mob
	{
		/**
		 * Constructs an Ender Dragon mob entity from the given tag.
		 * @param enderdragon The tag from which to construct the Ender Dragon mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public EnderDragon(Tag.Compound enderdragon) throws FormatException
		{
			super(enderdragon);
		}
		/**
		 * Constructs an Ender Dragon from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public EnderDragon(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Enderman mob entity
	 */
	public static class Enderman extends Mob
	{
		/**
		 * The ID of the block being carried.
		 */
		private short carried;
		/**
		 * The data/damage value of the block being carried.
		 */
		private short carrieddata;

		/**
		 * Constructs an Enderman mob entity from the given tag.
		 * @param enderman The tag from which to construct the Enderman mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Enderman(Tag.Compound enderman) throws FormatException
		{
			super(enderman);

			carried = ((Tag.Short)enderman.find(Tag.Type.SHORT, "carried")).v;
			carrieddata = ((Tag.Short)enderman.find(Tag.Type.SHORT, "carriedData")).v;
		}
		/**
		 * Constructs an Enderman from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Enderman(double X, double Y, double Z)
		{
			super(X, Y, Z);
			carried = 0;
			carrieddata = 0;
		}

		/**
		 * Returns the ID of the block being carried.
		 * @return The ID of the block being carried.
		 */
		public short BlockID()
		{
			return carried;
		}
		/**
		 * Sets the ID of the block being carried.
		 * @param ID The ID of the block being carried.
		 */
		public void BlockID(short ID)
		{
			carried = ID;
		}

		/**
		 * Returns the data for the block being carried.
		 * @return The data for the block being carried.
		 */
		public short BlockData()
		{
			return carrieddata;
		}
		/**
		 * Sets the data for the block being carried.
		 * @param data The data for the block being carried.
		 */
		public void BlockData(short data)
		{
			carrieddata = data;
		}

		/**
		 * Returns the tag for this Enderman mob entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Enderman mob entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Short("carried", carried),
				  new Tag.Short("carriedData", carrieddata));
			return t;
		}
	}
	/**
	 * Ghast mob entity
	 */
	public static class Ghast extends Mob
	{
		/**
		 * Constructs a Ghast mob entity from the given tag.
		 * @param ghast The tag from which to construct the Ghast mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Ghast(Tag.Compound ghast) throws FormatException
		{
			super(ghast);
		}
		/**
		 * Constructs a Ghast from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Ghast(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Giant mob entity
	 */
	public static class Giant extends Mob
	{
		/**
		 * Constructs a Giant mob entity from the given tag.
		 * @param giant The tag from which to construct the Giant mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Giant(Tag.Compound giant) throws FormatException
		{
			super(giant);
		}
		/**
		 * Constructs a Giant from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Giant(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Magma Cube mob entity
	 */
	public static class LavaSlime extends Slime
	{
		/**
		 * Constructs a Magma Cube mob entity from the given tag.
		 * @param magmacube The tag from which to construct the Magma Cube mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public LavaSlime(Tag.Compound magmacube) throws FormatException
		{
			super(magmacube);
		}
		/**
		 * Constructs a Magma Cube from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public LavaSlime(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Mooshroom mob entity
	 */
	public static class MushroomCow extends Breedable
	{
		/**
		 * Constructs a Mooshroom mob entity from the given tag.
		 * @param mooshroom The tag from which to construct the Mooshroom mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public MushroomCow(Tag.Compound mooshroom) throws FormatException
		{
			super(mooshroom);
		}
		/**
		 * Constructs a Mooshroom from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public MushroomCow(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Ocelot mob entity
	 */
	public static class Ozelot extends Tameable
	{
		/**
		 * Constructs an Ocelot mob entity from the given tag.
		 * @param ocelot The tag from which to construct the Ocelot mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Ozelot(Tag.Compound ocelot) throws FormatException
		{
			super(ocelot);
		}
		/**
		 * Constructs an Ocelot from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Ozelot(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Pig mob entity
	 */
	public static class Pig extends Breedable
	{
		/**
		 * Whether or not this pig has a saddle on it.
		 */
		private boolean saddle;

		/**
		 * Constructs a Pig mob entity from the given tag.
		 * @param pig The tag from which to construct the Pig mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Pig(Tag.Compound pig) throws FormatException
		{
			super(pig);

			saddle = ((Tag.Byte)pig.find(Tag.Type.BYTE, "Saddle")).v != 0 ? true : false;
		}
		/**
		 * Constructs a Pig from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Pig(double X, double Y, double Z)
		{
			super(X, Y, Z);
			saddle = false;
		}

		/**
		 * Returns whether or not this pig is wearing saddle.
		 * @return Whether or not this pig is wearing saddle.
		 */
		public boolean Saddle()
		{
			return saddle;
		}
		/**
		 * Sets whether or not this pig is wearing saddle.
		 * @param rideable Whether or not this pig is wearing saddle.
		 */
		public void Saddle(boolean rideable)
		{
			saddle = rideable;
		}

		/**
		 * Returns the tag for this Pig mob entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Pig mob entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Byte("Saddle", (byte)(saddle?1:0)));
			return t;
		}
	}
	/**
	 * Sheep mob entity
	 */
	public static class Sheep extends Breedable
	{
		/**
		 * Whether the wool on this Sheep has been sheared or not.
		 */
		private boolean sheared;
		/**
		 * An enumeration class for the colors sheep can be.
		 */
		public static enum Color
		{
			White,
			Orange,
			Magenta,
			LightBlue,
			Yellow,
			Lime,
			Pink,
			Gray,
			LightGray,
			Cyan,
			Purple,
			Blue,
			Brown,
			Green,
			Red,
			Black;

			public static Color FromID(int ordinal) throws FormatException
			{
				switch(ordinal)
				{
				case 0:		return White;
				case 1:		return Orange;
				case 2:		return Magenta;
				case 3:		return LightBlue;
				case 4:		return Yellow;
				case 5:		return Lime;
				case 6:		return Pink;
				case 7:		return Gray;
				case 8:		return LightGray;
				case 9:		return Cyan;
				case 10:	return Purple;
				case 11:	return Blue;
				case 12:	return Brown;
				case 13:	return Green;
				case 14:	return Red;
				case 15:	return Black;
				}
				throw new FormatException("Unknown wool color: "+ordinal);
			}
		}
		/**
		 * The color of the wool on this Sheep.
		 */
		private Color color;

		/**
		 * Constructs a Sheep mob entity from the given tag.
		 * @param sheep The tag from which to construct the Sheep mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Sheep(Tag.Compound sheep) throws FormatException
		{
			super(sheep);

			sheared = ((Tag.Byte)sheep.find(Tag.Type.BYTE, "Sheared")).v != 0 ? true: false;
			color = Color.FromID(((Tag.Byte)sheep.find(Tag.Type.BYTE, "Color")).v);
		}
		/**
		 * Constructs a Sheep from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Sheep(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}

		/**
		 * Returns whether or not this sheep has been sheared.
		 * @return Whether or not this sheep has been sheared.
		 */
		public boolean Sheared()
		{
			return sheared;
		}
		/**
		 * Sets whether or not this sheep has been sheared.
		 * @param naked Whether or not this sheep has been sheared.
		 */
		public void Sheared(boolean naked)
		{
			sheared = naked;
		}

		/**
		 * Returns the color of the wool on this sheep.
		 * @return The color of the wool on this sheep.
		 */
		public Color Color()
		{
			return color;
		}
		/**
		 * Sets the color of the wool on this sheep.
		 * @param color The color of the wool on this sheep.
		 */
		public void Color(Color color)
		{
			this.color = color;
		}

		/**
		 * Returns the tag for this Sheep mob entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Sheep mob entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Byte("Sheared", (byte)(sheared?1:0)),
				  new Tag.Byte("Color", (byte)color.ordinal()));
			return t;
		}
	}
	/**
	 * Silverfish mob entity
	 */
	public static class Silverfish extends Mob
	{
		/**
		 * Constructs a Silverfish mob entity from the given tag.
		 * @param silverfish The tag from which to construct the Silverfish mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Silverfish(Tag.Compound silverfish) throws FormatException
		{
			super(silverfish);
		}
		/**
		 * Constructs a Silverfish from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Silverfish(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Skeleton mob entity
	 */
	public static class Skeleton extends Mob
	{
		/**
		 * Constructs a Skeleton mob entity from the given tag.
		 * @param skeleton The tag from which to construct the Skeleton mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Skeleton(Tag.Compound skeleton) throws FormatException
		{
			super(skeleton);
		}
		/**
		 * Constructs a Skeleton from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Skeleton(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Slime mob entity
	 */
	public static class Slime extends Mob
	{
		/**
		 * The size of the Slime.
		 */
		private int size;

		/**
		 * Constructs a Slime mob entity from the given tag.
		 * @param slime The tag from which to construct the Slime mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Slime(Tag.Compound slime) throws FormatException
		{
			super(slime);
		}
		/**
		 * Constructs a Slime from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Slime(double X, double Y, double Z)
		{
			super(X, Y, Z);
			size = 0;
		}

		/**
		 * Returns the size of the slime.
		 * @return The size of the slime.
		 */
		public int Size()
		{
			return size;
		}
		/**
		 * Sets the size of the slime.
		 * @param size The size of the slime.
		 */
		public void Size(int size)
		{
			this.size = size;
		}

		/**
		 * Returns the tag for this Slime mob entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Slime mob entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Int("Size", size));
			return t;
		}
	}
	/**
	 * Snow Golem mob entity
	 */
	public static class SnowMan extends Mob
	{
		/**
		 * Constructs a Snow Golem mob entity from the given tag.
		 * @param snowman The tag from which to construct the Snow Golem mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public SnowMan(Tag.Compound snowman) throws FormatException
		{
			super(snowman);
		}
		/**
		 * Constructs a Snow Golem from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public SnowMan(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Spider mob entity
	 */
	public static class Spider extends Mob
	{
		/**
		 * Constructs a Spider mob entity from the given tag.
		 * @param spider The tag from which to construct the Spider mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Spider(Tag.Compound spider) throws FormatException
		{
			super(spider);
		}
		/**
		 * Constructs a Spider from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Spider(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Squid mob entity
	 */
	public static class Squid extends Mob
	{
		/**
		 * Constructs a Squid mob entity from the given tag.
		 * @param squid The tag from which to construct the Squid mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Squid(Tag.Compound squid) throws FormatException
		{
			super(squid);
		}
		/**
		 * Constructs a Squid from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Squid(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Villager mob entity
	 */
	public static class Villager extends Breedable
	{
		/**
		 * An enumeration class representing the various professions villagers can have.
		 */
		public static enum Profession
		{
			/**
			 * Brown Robe
			 */
			Farmer,
			/**
			 * White Robe
			 */
			Librarian,
			/**
			 * Purple Robe
			 */
			Priest,
			/**
			 * Black Apron
			 */
			Blacksmith,
			/**
			 * White Apron
			 */
			Butcher,
			/**
			 * Green Robe
			 */
			Villager;

			/**
			 * Given a profession ID, returns the enumeration constant that represents that ID.
			 * @param ordinal The ID.
			 * @return The enumeration constant that represents the given ID.
			 * @throws FormatException if the given ID is unknown.
			 */
			public static Profession FromID(int ordinal) throws FormatException
			{
				switch(ordinal)
				{
				case 0:	return Farmer;
				case 1:	return Librarian;
				case 2:	return Priest;
				case 3:	return Blacksmith;
				case 4:	return Butcher;
				case 5:	return Villager;
				}
				throw new FormatException("Unknown Villager Profession: "+ordinal);
			}
		}
		/**
		 * The profession of this villager.
		 */
		private Profession profession;
		/**
		 * Unknown, increases with trading.
		 */
		private int riches;
		/**
		 * A class that represents a trade option for a villager.
		 */
		public static class Trade
		{
			/**
			 * The number of times this trade has been used.
			 */
			private int uses;
			/**
			 * The items bought by this villager.
			 */
			private Inventory.Item buy, buyb;
			/**
			 * The item sold by this villager.
			 */
			private Inventory.Item sell;

			/**
			 * Constructs a Trade option from the given tag.
			 * @param trade The tag from which to construct this Trade option.
			 * @throws FormatException if the given tag is invalid.
			 */
			public Trade(Tag.Compound trade) throws FormatException
			{
				uses = ((Tag.Int)trade.find(Tag.Type.INT, "uses")).v;
				buy = new Inventory.Item((Tag.Compound)trade.find(Tag.Type.COMPOUND, "buy"));
				Tag t = trade.get("buyB");
				if(t == null)
				{
					buyb = null;
				}
				else
				{
					buyb = new Inventory.Item((Tag.Compound)t);
				}
				sell = new Inventory.Item((Tag.Compound)trade.find(Tag.Type.COMPOUND, "sell"));
			}
			/**
			 * Constructs a trade from one or two cost items and a sale item.
			 * @param costa The first cost item.
			 * @param costb The second cost item, or null.
			 * @param sale The sale item.
			 */
			public Trade(Inventory.Item costa, Inventory.Item costb, Inventory.Item sale)
			{
				uses = 0;
				buy = costa;
				buyb = costb;
				sell = sale;
			}

			/**
			 * Returns the number of times this trade has been used.
			 * @return The number of times this trade has been used.
			 */
			public int Uses()
			{
				return uses;
			}
			/**
			 * Sets the number of times this trade has been used.
			 * @param times The number of times this trade has been used.
			 */
			public void Uses(int times)
			{
				uses = times;
			}

			/**
			 * Returns the first item required to complete this Trade offer.
			 * @return The first item required to complete this Trade offer.
			 */
			public Inventory.Item CostA()
			{
				return buy;
			}
			/**
			 * Returns the second item required to complete this Trade offer, or null if there is no secondary requirement.
			 * @return The second item required to complete this Trade offer, or null if there is no secondary requirement.
			 */
			public Inventory.Item CostB()
			{
				return buyb;
			}
			/**
			 * Sets the first item required to complete this Trade offer.
			 * @param a The first item required to complete this Trade offer.
			 */
			public void CostA(Inventory.Item a)
			{
				buy = a;
			}
			/**
			 * Sets the second item required to complete this Trade offer.
			 * @param b The second item required to complete this Trade offer, or null if there is no secondary requirement.
			 */
			public void CostB(Inventory.Item b)
			{
				buyb = b;
			}
			/**
			 * Sets the required items for this Trade offer. The second item may be null.
			 * @param a The first item required to complete this Trade offer.
			 * @param b The second item required to complete this Trade offer, or null if there is no secondary requirement.
			 */
			public void Cost(Inventory.Item a, Inventory.Item b)
			{
				buy = a;
				buyb = b;
			}

			/**
			 * Returns the item given in return for the required Trade items.
			 * @return The item given in return for the required Trade items.
			 */
			public Inventory.Item Sale()
			{
				return sell;
			}
			/**
			 * Sets the item given in return for the required Trade items.
			 * @param sale The item given in return for the required Trade items.
			 */
			public void Sale(Inventory.Item sale)
			{
				sell = sale;
			}

			/**
			 * Returns the tag for this Trade offer.
			 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
			 * @return The tag for this Trade offer.
			 */
			public Tag.Compound ToNBT(String name)
			{
				if(buyb != null)
				{
					return new Tag.Compound(name, buy.ToNBT("buy"),
												  buyb.ToNBT("buyB"),
												  sell.ToNBT("sell"));
				}
				else
				{
					return new Tag.Compound(name, buy.ToNBT("buy"),
												  sell.ToNBT("sell"));
				}
			}
		}
		/**
		 * The list of trades this villager offers.
		 */
		private List<Trade> offers = new ArrayList<>();

		/**
		 * Constructs a Villager mob entity from the given tag.
		 * @param villager The tag from which to construct the Villager mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Villager(Tag.Compound villager) throws FormatException
		{
			super(villager);

			profession = Profession.FromID(((Tag.Int)villager.find(Tag.Type.INT, "Profession")).v);
			riches = ((Tag.Int)villager.find(Tag.Type.INT, "Riches")).v;
			Tag.List trades = (Tag.List)((Tag.Compound)villager.find(Tag.Type.COMPOUND, "Offers")).find(Tag.Type.LIST, "Recipes");
			if(trades.getContainedType() != Tag.Type.COMPOUND)
			{
				throw new FormatException("Expected list of compound tags, got list of: "+trades.getContainedType());
			}
			for(Tag t : trades)
			{
				offers.add(new Trade((Tag.Compound)t));
			}
		}
		/**
		 * Constructs a Villager from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Villager(double X, double Y, double Z)
		{
			super(X, Y, Z);
			profession = Profession.Villager;
			riches = 0;
		}

		/**
		 * Returns the tag for this Villager mob entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Villager mob entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			Tag.List trades;
			t.add(new Tag.Int("Profession", profession.ordinal()),
				  new Tag.Int("Riches", riches),
				  new Tag.Compound("Offers"), trades = new Tag.List("Recipes", Tag.Type.COMPOUND));
			for(Trade trade : offers)
			{
				trades.add(trade.ToNBT(null));
			}
			return t;
		}
	}

	/**
	 * Iron Golem
	 */
	public static class VillagerGolem extends Mob
	{
		/**
		 * Constructs an Iron Golem mob entity from the given tag.
		 * @param irongolem The tag from which to construct the Iron Golem mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public VillagerGolem(Tag.Compound irongolem) throws FormatException
		{
			super(irongolem);
		}
		/**
		 * Constructs an Iron Golem from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public VillagerGolem(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}

	/**
	 * Wolf mob entity
	 */
	public static class Wolf extends Tameable
	{
		/**
		 * Whether or not this wild wolf is aggressive toward players.
		 */
		private boolean angry;

		/**
		 * Constructs a Wolf mob entity from the given tag.
		 * @param wolf The tag from which to construct the Wolf mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Wolf(Tag.Compound wolf) throws FormatException
		{
			super(wolf);

			angry = ((Tag.Byte)wolf.find(Tag.Type.BYTE, "Angry")).v != 0 ? true : false;
		}
		/**
		 * Constructs a Wolf from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Wolf(double X, double Y, double Z)
		{
			super(X, Y, Z);
			angry = false;
		}

		/**
		 * Returns whether or not this wild wolf has been aggravated.
		 * @return Whether or not this wild wolf has been aggravated.
		 */
		public boolean Angry()
		{
			return angry;
		}
		/**
		 * Sets whether or not this wild wolf has been aggravated.
		 * @param aggressive Whether or not this wild wolf has been aggravated.
		 */
		public void Angry(boolean aggressive)
		{
			angry = aggressive;
		}

		/**
		 * Returns the tag for this Wolf mob entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Wolf mob entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Byte("Angry", (byte)(angry?1:0)));
			return t;
		}
	}
	/**
	 * Zombie mob entity
	 */
	public static class Zombie extends Mob
	{
		/**
		 * Constructs a Zombie mob entity from the given tag.
		 * @param zombie The tag from which to construct the Zombie mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Zombie(Tag.Compound zombie) throws FormatException
		{
			super(zombie);
		}
		/**
		 * Constructs a Zombie from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public Zombie(double X, double Y, double Z)
		{
			super(X, Y, Z);
		}
	}
	/**
	 * Zombie Pigman mob entity
	 */
	public static class PigZombie extends Mob
	{
		/**
		 * How angry this Zombie Pigman is at players.
		 */
		private short anger;

		/**
		 * Constructs a Zombie Pigman mob entity from the given tag.
		 * @param zombiepigman The tag from which to construct the Zombie Pigman mob entity.
		 * @throws FormatException if the given tag is invalid.
		 */
		public PigZombie(Tag.Compound zombiepigman) throws FormatException
		{
			super(zombiepigman);

			anger = ((Tag.Short)zombiepigman.find(Tag.Type.SHORT, "Anger")).v;
		}
		/**
		 * Constructs a Zombie Pigman from a position.
		 * @param X The X position.
		 * @param Y The Y position.
		 * @param Z The Z position.
		 */
		public PigZombie(double X, double Y, double Z)
		{
			super(X, Y, Z);
			anger = 0;
		}

		/**
		 * Returns the tag for this Zombie Pigman mob entity.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Zombie Pigman mob entity.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.add(new Tag.Short("Anger", anger));
			return t;
		}
	}
}