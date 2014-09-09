package com.lb_stuff.mcmodify.minecraft;

import com.lb_stuff.mcmodify.nbt.FormatException;
import com.lb_stuff.mcmodify.nbt.Tag;

/**
 * Represents a level.dat file
 */
public class Level
{
	/**
	 * The version of NBT used.
	 */
	private int version;
	/**
	 * The custom name of this level.
	 */
	private String levelname;
	/**
	 * Whether or not Cheats are enabled via the in-game chat. (aka If the player is OP on their single player server)
	 */
	private boolean allowcommands;
	/**
	 * The default game mode of the level.
	 */
	private Player.GameMode gametype;
	/**
	 * Whether or not the world is in Hardcore mode.
	 */
	private boolean hardcore;
	/**
	 * Whether or not special map features such as strongholds, villages, and abandoned mineshafts will be generated.
	 */
	private boolean mapfeatures;
	/**
	 * The game time in ticks since the start of the level, affects the day/night cycle.
	 */
	private long time;
	/**
	 * Whether or not it is raining/snowing.
	 */
	private boolean raining;
	/**
	 * The number of ticks before the rain state is toggled.
	 */
	private int raintime;
	/**
	 * Whether or not it is a thunderstorm and mobs can spawn in the daylight.
	 */
	private boolean thundering;
	/**
	 * The number of ticks before the thunderstorm state is toggled.
	 */
	private int thundertime;
	/**
	 * An enumeration class for the known generators.
	 */
	public static enum Generator
	{
		/**
		 * "default"
		 */
		Default,
		/**
		 * "flat"
		 */
		Superflat,
		/**
		 * "largeBiomes"
		 */
		LargeBiomes;

		public static Generator FromName(String name) throws FormatException
		{
			switch(name)
			{
			case "default":		return Default;
			case "flat":		return Superflat;
			case "largeBiomes":	return LargeBiomes;
			}
			throw new FormatException("Unkown Level Generator: \""+name+"\"");
		}
		@Override public String toString()
		{
			switch(this)
			{
			case Default:		return "default";
			case Superflat:		return "flat";
			case LargeBiomes:	return "largeBiomes";
			}
			return null;
		}
	}
	/**
	 * The map generator used to generate more of the map.
	 */
	private Generator generator;
	/**
	 * The version of the map generator.
	 */
	private int generatorversion;
	/**
	 * The random seed used to consistently generate terrain.
	 */
	private long randomseed;
	/**
	 * The world spawn coordinates.
	 */
	private int spawnx, spawny, spawnz;
	/**
	 * The Timestamp from when the level was last played.
	 */
	private long lastplayed;
	/**
	 * The estimated size in bytes of the level.
	 */
	private long sizeondisk;
	/**
	 * Player, either in level.dat or in a player.dat file
	 */
	public static class Player extends Mob
	{
		/**
		 * Whether or not the player is sleeping.
		 */
		private boolean sleeping;
		/**
		 * How long the player has been sleeping for.
		 */
		private short sleeptimer;
		/**
		 * Which dimension the player is in.
		 */
		private Dimension dimension;
		/**
		 * An enumeration class for the known game modes.
		 */
		public static enum GameMode
		{
			/**
			 * 0
			 */
			Survival,
			/**
			 * 1
			 */
			Creative,
			/**
			 * 2
			 */
			Adventure;

			public static GameMode FromID(int ordinal) throws FormatException
			{
				switch(ordinal)
				{
				case 0:	return Survival;
				case 1:	return Creative;
				case 2:	return Adventure;
				}
				throw new FormatException("Unknown Game Mode: "+ordinal);
			}
		}
		/**
		 * Which game mode the player is in.
		 */
		private GameMode playergametype;
		/**
		 * How much food the player has.
		 */
		private int foodlevel;
		/**
		 * The number of ticks before the food tick timer performs its next action.
		 */
		private int foodticktimer;
		/**
		 * How fast food is being exhausted.
		 */
		private float foodexhaustionlevel;
		/**
		 * How much saturation is left of the food.
		 */
		private float foodsaturationlevel;
		/**
		 * The (optional) coordinates of the player's bed.
		 */
		private Integer spawnx, spawny, spawnz;
		/**
		 * The current experience level.
		 */
		private int xplevel;
		/**
		 * The total number of experience points the player has.
		 */
		private int xptotal;
		/**
		 * The percent progress to the next XP level.
		 */
		private float xpp;
		/**
		 * The player's inventory.
		 */
		private Inventory inventory;
		/**
		 * The inventory of the Ender Chest for this player.
		 */
		private Inventory enderitems;
		/**
		 * Whether or not this player is allowed to fly.
		 */
		private boolean mayfly;
		/**
		 * The speed at which the player can fly.
		 */
		private float flyspeed;
		/**
		 * Whether or not the player is flying.
		 */
		private boolean flying;
		/**
		 * Whether or not the player is allowed to build.
		 */
		private boolean maybuild;
		/**
		 * Whether or not the player can instantly destroy blocks.
		 */
		private boolean instabuild;
		/**
		 * Whether or not the player is invulnerable.
		 */
		private boolean invulnerable;
		/**
		 * The speed at which the player can walk.
		 */
		private float walkspeed;

		/**
		 * Constructs a Player from the given tag.
		 * @param player The tag from which to construct this Player.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Player(Tag.Compound player) throws FormatException
		{
			super(player);

			sleeping = ((Tag.Byte)player.find(Tag.Type.BYTE, "Sleeping")).v != 0 ? true : false;
			sleeptimer = ((Tag.Short)player.find(Tag.Type.SHORT, "SleepTimer")).v;
			dimension = Dimension.fromId(((Tag.Int)player.find(Tag.Type.INT, "Dimension")).v);
			playergametype = GameMode.FromID(((Tag.Int)player.find(Tag.Type.INT, "playerGameType")).v);
			foodlevel = ((Tag.Int)player.find(Tag.Type.INT, "foodLevel")).v;
			foodticktimer = ((Tag.Int)player.find(Tag.Type.INT, "foodTickTimer")).v;
			foodexhaustionlevel = ((Tag.Float)player.find(Tag.Type.FLOAT, "foodExhaustionLevel")).v;
			foodsaturationlevel = ((Tag.Float)player.find(Tag.Type.FLOAT, "foodSaturationLevel")).v;
			try
			{
				spawnx = ((Tag.Int)player.find(Tag.Type.INT, "SpawnX")).v;
				spawny = ((Tag.Int)player.find(Tag.Type.INT, "SpawnY")).v;
				spawnz = ((Tag.Int)player.find(Tag.Type.INT, "SpawnZ")).v;
			}
			catch(FormatException e)
			{
				spawnx = spawny = spawnz = null;
			}
			xplevel = ((Tag.Int)player.find(Tag.Type.INT, "XpLevel")).v;
			xptotal = ((Tag.Int)player.find(Tag.Type.INT, "XpTotal")).v;
			xpp = ((Tag.Float)player.find(Tag.Type.FLOAT, "XpP")).v;
			inventory = new Inventory((Tag.List)player.find(Tag.Type.LIST, "Inventory"));
			enderitems = new Inventory((Tag.List)player.find(Tag.Type.LIST, "EnderItems"));
			Tag.Compound abilities = (Tag.Compound)player.find(Tag.Type.COMPOUND, "abilities");
			mayfly = ((Tag.Byte)abilities.find(Tag.Type.BYTE, "mayfly")).v != 0 ? true : false;
			flyspeed = ((Tag.Float)abilities.find(Tag.Type.FLOAT, "flySpeed")).v;
			flying = ((Tag.Byte)abilities.find(Tag.Type.BYTE, "flying")).v != 0 ? true : false;
			maybuild = ((Tag.Byte)abilities.find(Tag.Type.BYTE, "mayBuild")).v != 0 ? true : false;
			instabuild = ((Tag.Byte)abilities.find(Tag.Type.BYTE, "instabuild")).v != 0 ? true : false;
			invulnerable = ((Tag.Byte)abilities.find(Tag.Type.BYTE, "invulnerable")).v != 0 ? true : false;
			walkspeed = ((Tag.Float)abilities.find(Tag.Type.FLOAT, "walkSpeed")).v;
		}

		/**
		 * Returns whether or not the player is sleeping.
		 * @return Whether or not the player is sleeping.
		 */
		public boolean Sleeping()
		{
			return sleeping;
		}
		/**
		 * Sets whether or not the player is sleeping.
		 * @param asleep Whether or not the player is sleeping.
		 */
		public void Sleeping(boolean asleep)
		{
			sleeping = asleep;
		}

		/**
		 * Returns the number of ticks the player has been sleeping for.
		 * @return The number of ticks the player has been sleeping for.
		 */
		public short SleepTicks()
		{
			return sleeptimer;
		}
		/**
		 * Sets the number of ticks the player has been sleeping for.
		 * @param ticks The number of ticks the player has been sleeping for.
		 */
		public void SleepTicks(short ticks)
		{
			sleeptimer = ticks;
		}

		/**
		 * Returns the dimension the player is in.
		 * @return The dimension the player is in.
		 */
		public Dimension Dimension()
		{
			return dimension;
		}
		/**
		 * Sets the dimension the player is in.
		 * @param d The dimension the player is in.
		 */
		public void Dimension(Dimension d)
		{
			dimension = d;
		}

		/**
		 * Returns the player's game mode.
		 * @return The player's game mode.
		 */
		public GameMode GameMode()
		{
			return playergametype;
		}
		/**
		 * Sets the player's game mode.
		 * @param gametype The player's game mode.
		 */
		public void GameMode(GameMode gametype)
		{
			playergametype = gametype;
		}

		/**
		 * Returns the number of food points the player has.
		 * @return The number of food points the player has.
		 */
		public int Food()
		{
			return foodlevel;
		}
		/**
		 * Sets the number of food points the player has.
		 * @param food The number of food points the player has.
		 */
		public void Food(int food)
		{
			foodlevel = food;
		}

		/**
		 * Returns the number of ticks before the food tick timer performs its next action.
		 * @return The number of ticks before the food tick timer performs its next action.
		 */
		public int FoodTick()
		{
			return foodticktimer;
		}
		/**
		 * Sets the number of ticks before the food tick timer performs its next action.
		 * @param ticks The number of ticks before the food tick timer performs its next action.
		 */
		public void FoodTick(int ticks)
		{
			foodticktimer = ticks;
		}

		/**
		 * Returns the food exhaustion level.
		 * @return The food exhaustion level.
		 */
		public float FoodExhaustion()
		{
			return foodexhaustionlevel;
		}
		/**
		 * Sets the food exhaustion level.
		 * @param exhaustion The food exhaustion level.
		 */
		public void FoodExhaustion(float exhaustion)
		{
			foodexhaustionlevel = exhaustion;
		}

		/**
		 * Returns the food saturation level.
		 * @return The food saturation level.
		 */
		public float FoodSaturation()
		{
			return foodsaturationlevel;
		}
		/**
		 * Sets the food saturation level.
		 * @param saturation The food saturation level.
		 */
		public void FoodSaturation(float saturation)
		{
			foodsaturationlevel = saturation;
		}

		/**
		 * Returns the X spawn coordinate, or null if no bed spawn is set.
		 * @return The X spawn coordinate, or null if no bed spawn is set.
		 */
		public Integer SpawnX()
		{
			return spawnx;
		}
		/**
		 * Returns the Y spawn coordinate, or null if no bed spawn is set.
		 * @return The Y spawn coordinate, or null if no bed spawn is set.
		 */
		public Integer SpawnY()
		{
			return spawny;
		}
		/**
		 * Returns the Y spawn coordinate, or null if no bed spawn is set.
		 * @return The Y spawn coordinate, or null if no bed spawn is set.
		 */
		public Integer SpawnZ()
		{
			return spawnz;
		}
		/**
		 * Sets the spawn coordinates. If any of the given parameters are null, the bed spawn is removed.
		 * @param x The X spawn coordinate.
		 * @param y The Y spawn coordinate.
		 * @param z The Z spawn coordinate.
		 */
		public void Spawn(Integer x, Integer y, Integer z)
		{
			spawnx = x;
			spawny = y;
			spawnz = z;
		}

		/**
		 * Returns the XP Level of this player.
		 * @return The XP Level of this player.
		 */
		public int XPLevel()
		{
			return xplevel;
		}
		/**
		 * Sets the XP Level of this player.
		 * @param level The XP Level of this player.
		 */
		public void XPLevel(int level)
		{
			xplevel = level;
		}

		/**
		 * Returns the total number of XP points this player has.
		 * @return The total number of XP points this player has.
		 */
		public int XP()
		{
			return xptotal;
		}
		/**
		 * Sets the total number of XP points this player has.
		 * @param total The total number of XP points this player has.
		 */
		public void XP(int total)
		{
			xptotal = total;
		}

		/**
		 * Returns the percent progress to the next XP level.
		 * @return The percent progress to the next XP level.
		 */
		public float XPPercent()
		{
			return xpp;
		}
		/**
		 * Sets the percent progress to the next XP level.
		 * @param percent The percent progress to the next XP level.
		 */
		public void XPPercent(float percent)
		{
			xpp = percent;
		}

		/**
		 * Returns the player inventory with no slot restrictions. Hotbar slots are 0 to 8, inventory slots are 9 to 35, armor slots are 100 to 103.
		 * @return The player inventory with no slot restrictions.
		 */
		public Inventory Inventory()
		{
			return inventory;
		}
		/**
		 * Returns the Ender Chest inventory with no slot restrictions. Slots are numbered 0 to 26.
		 * @return The Ender Chest inventory with no slot restrictions.
		 */
		public Inventory EnderInventory()
		{
			return enderitems;
		}

		/**
		 * Returns whether or not the player is allowed to fly.
		 * @return Whether or not the player is allowed to fly.
		 */
		public boolean MayFly()
		{
			return mayfly;
		}
		/**
		 * Sets whether or not the player is allowed to fly.
		 * @param flyable Whether or not the player is allowed to fly.
		 */
		public void MayFly(boolean flyable)
		{
			mayfly = flyable;
		}

		/**
		 * Returns the speed at which the player flies.
		 * @return The speed at which the player flies.
		 */
		public float FlySpeed()
		{
			return flyspeed;
		}
		/**
		 * Sets the speed at which the player flies.
		 * @param speed The speed at which the player flies.
		 */
		public void FlySpeed(float speed)
		{
			flyspeed = speed;
		}

		/**
		 * Returns whether or not the player is currently flying.
		 * @return Whether or not the player is currently flying.
		 */
		public boolean Flying()
		{
			return flying;
		}
		/**
		 * Sets whether or not the player is currently flying.
		 * @param hovering Whether or not the player is currently flying.
		 */
		public void Flying(boolean hovering)
		{
			flying = hovering;
		}

		/**
		 * Returns whether or not the player is allowed to place and break blocks.
		 * @return Whether or not the player is allowed to place and break blocks.
		 */
		public boolean MayBuild()
		{
			return maybuild;
		}
		/**
		 * Sets whether or not the player is allowed to place and break blocks.
		 * @param buildable Whether or not the player is allowed to place and break blocks.
		 */
		public void MayBuild(boolean buildable)
		{
			maybuild = buildable;
		}

		/**
		 * Returns whether or not the player can instantly break blocks.
		 * @return Whether or not the player can instantly break blocks.
		 */
		public boolean InstaBreak()
		{
			return instabuild;
		}
		/**
		 * Sets whether or not the player can instantly break blocks.
		 * @param instantbuild Whether or not the player can instantly break blocks.
		 */
		public void InstaBreak(boolean instantbuild)
		{
			instabuild = instantbuild;
		}

		/**
		 * Returns whether or not the player is impervious to damage other than that inflicted by the void or the /kill command.
		 * @return Whether or not the player is impervious to damage other than that inflicted by the void or the /kill command.
		 */
		public boolean Invincible()
		{
			return invulnerable;
		}
		/**
		 * Sets whether or not the player is impervious to damage other than that inflicted by the void or the /kill command.
		 * @param invulnerability Whether or not the player is impervious to damage other than that inflicted by the void or the /kill command.
		 */
		public void Invincible(boolean invulnerability)
		{
			invulnerable = invulnerability;
		}

		/**
		 * Returns the speed at which the player walks.
		 * @return The speed at which the player walks.
		 */
		public float WalkSpeed()
		{
			return walkspeed;
		}
		/**
		 * Sets the speed at which the player walks.
		 * @param speed The speed at which the player walks.
		 */
		public void WalkSpeed(float speed)
		{
			walkspeed = speed;
		}

		/**
		 * Returns the tag for this Player.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @return The tag for this Player.
		 */
		@Override public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = super.ToNBT(name);
			t.remove("id");
			t.add(new Tag.Byte("Sleeping", (byte)(sleeping?1:0)),
				  new Tag.Short("SleepTimer", sleeptimer),
				  new Tag.Int("Dimension", dimension.getId()),
				  new Tag.Int("playerGameType", playergametype.ordinal()),
				  new Tag.Int("foodLevel", foodlevel),
				  new Tag.Int("foodTickTimer", foodticktimer),
				  new Tag.Float("foodExhaustionLevel", foodexhaustionlevel),
				  new Tag.Float("foodSaturationLevel", foodsaturationlevel));
			if(spawnx != null && spawny != null && spawnz != null)
			{
				t.add(new Tag.Int("SpawnX", spawnx),
					  new Tag.Int("SpawnY", spawny),
					  new Tag.Int("SpawnZ", spawnz));
			}
			t.add(new Tag.Int("XpLevel", xplevel),
				  new Tag.Int("XpTotal", xptotal),
				  new Tag.Float("XpP", xpp),
				  inventory.ToNBT("Inventory"),
				  enderitems.ToNBT("EnderItems"),
				  new Tag.Compound("abilities", new Tag.Byte("mayfly", (byte)(mayfly?1:0)),
												new Tag.Float("flySpeed", flyspeed),
												new Tag.Byte("flying", (byte)(flying?1:0)),
												new Tag.Byte("mayBuild", (byte)(maybuild?1:0)),
												new Tag.Byte("instabuild", (byte)(instabuild?1:0)),
												new Tag.Byte("invulnerable", (byte)(invulnerable?1:0)),
												new Tag.Float("walkSpeed", walkspeed)));
			return t;
		}
	}
	/**
	 * The single player player for this level, if there is one.
	 */
	private Player player;

	/**
	 * Constructs this Level from the given tag.
	 * @param level The tag from which to construct this level.
	 * @throws FormatException if the given tag is invalid.
	 */
	public Level(Tag.Compound level) throws FormatException
	{
		level = (Tag.Compound)level.find(Tag.Type.COMPOUND, "Data");
		version = ((Tag.Int)level.find(Tag.Type.INT, "version")).v;
		if(version != 19133)
		{
			throw new FormatException("Incorrect Version: "+version);
		}
		levelname = ((Tag.String)level.find(Tag.Type.STRING, "LevelName")).v;
		allowcommands = ((Tag.Byte)level.find(Tag.Type.BYTE, "allowCommands")).v != 0 ? true : false;
		gametype = Player.GameMode.FromID(((Tag.Int)level.find(Tag.Type.INT, "GameType")).v);
		hardcore = ((Tag.Byte)level.find(Tag.Type.BYTE, "hardcore")).v != 0 ? true : false;
		mapfeatures = ((Tag.Byte)level.find(Tag.Type.BYTE, "MapFeatures")).v != 0 ? true : false;
		time = ((Tag.Long)level.find(Tag.Type.LONG, "Time")).v;
		raining = ((Tag.Byte)level.find(Tag.Type.BYTE, "raining")).v != 0 ? true : false;
		raintime = ((Tag.Int)level.find(Tag.Type.INT, "rainTime")).v;
		thundering = ((Tag.Byte)level.find(Tag.Type.BYTE, "thundering")).v != 0 ? true : false;
		thundertime = ((Tag.Int)level.find(Tag.Type.INT, "thunderTime")).v;
		generator = Generator.FromName(((Tag.String)level.find(Tag.Type.STRING, "generatorName")).v);
		generatorversion = ((Tag.Int)level.find(Tag.Type.INT, "generatorVersion")).v;
		randomseed = ((Tag.Long)level.find(Tag.Type.LONG, "RandomSeed")).v;
		spawnx = ((Tag.Int)level.find(Tag.Type.INT, "SpawnX")).v;
		spawny = ((Tag.Int)level.find(Tag.Type.INT, "SpawnY")).v;
		spawnz = ((Tag.Int)level.find(Tag.Type.INT, "SpawnZ")).v;
		lastplayed = ((Tag.Long)level.find(Tag.Type.LONG, "LastPlayed")).v;
		sizeondisk = ((Tag.Long)level.find(Tag.Type.LONG, "SizeOnDisk")).v;
		Tag.Compound plr = null;
		try
		{
			plr = (Tag.Compound)level.find(Tag.Type.COMPOUND, "Player");
		}
		catch(FormatException e)
		{
		}
		if(plr != null)
		{
			player = new Player(plr);
		}
	}

	/**
	 * Returns the name of this level.
	 * @return The name of this level.
	 */
	public String Name()
	{
		return levelname;
	}
	/**
	 * Sets the name of this level.
	 * @param name The name of this level.
	 */
	public void Name(String name)
	{
		levelname = name;
	}

	/**
	 * Returns whether or not the player is OPped on their single player server and can use server commands.
	 * @return Whether or not the player is OPped on their single player server and can use server commands.
	 */
	public boolean Cheats()
	{
		return allowcommands;
	}
	/**
	 * Sets whether or not the player is OPped on their single player server and can use server commands.
	 * @param commands Whether or not the player is OPped on their single player server and can use server commands.
	 */
	public void Cheats(boolean commands)
	{
		allowcommands = commands;
	}

	/**
	 * Returns the default game mode for this world.
	 * @return The default game mode for this world.
	 */
	public Player.GameMode GameMode()
	{
		return gametype;
	}
	/**
	 * Sets the default game mode for this world.
	 * @param type The default game mode for this world.
	 */
	public void GameMode(Player.GameMode type)
	{
		gametype = type;
	}

	/**
	 * Returns whether the world will be deleted upon death, or if players will be banned from servers on death.
	 * @return Whether the world will be deleted upon death, or if players will be banned from servers on death.
	 */
	public boolean Hardcore()
	{
		return hardcore;
	}
	/**
	 * Sets whether the world will be deleted upon death, or if players will be banned from servers on death.
	 * @param angryhearts Whether the world will be deleted upon death, or if players will be banned from servers on death.
	 */
	public void Hardcore(boolean angryhearts)
	{
		hardcore = angryhearts;
	}

	/**
	 * Returns whether structures such as villages, strongholds, and mineshafts will generate.
	 * @return Whether structures such as villages, strongholds, and mineshafts will generate.
	 */
	public boolean Structures()
	{
		return mapfeatures;
	}
	/**
	 * Sets whether structures such as villages, strongholds, and mineshafts will generate.
	 * @param features whether structures such as villages, strongholds, and mineshafts will generate.
	 */
	public void Structures(boolean features)
	{
		mapfeatures = features;
	}

	/**
	 * Returns the number of ticks since the world was first created.
	 * @return The number of ticks since the world was first created.
	 */
	public long Time()
	{
		return time;
	}
	/**
	 * Sets the number of ticks since the world was first created.
	 * @param ticks The number of ticks since the world was first created.
	 */
	public void Time(long ticks)
	{
		time = ticks;
	}

	/**
	 * Returns whether or not it is raining/snowing.
	 * @return Whether or not it is raining/snowing.
	 */
	public boolean Raining()
	{
		return raining;
	}
	/**
	 * Sets whether or not it is raining/snowing.
	 * @param orsnowing Whether or not it is raining/snowing.
	 */
	public void Raining(boolean orsnowing)
	{
		raining = orsnowing;
	}
	/**
	 * Returns the number of ticks before the raining state is toggled.
	 * @return The number of ticks before the raining state is toggled.
	 */
	public int RainToggle()
	{
		return raintime;
	}
	/**
	 * Sets the number of ticks before the raining state is toggled.
	 * @param ticks The number of ticks before the raining state is toggled.
	 */
	public void RainToggle(int ticks)
	{
		raintime = ticks;
	}
	/**
	 * Returns whether or not the rain/snow is a thunderstorm.
	 * @return Whether or not the rain/snow is a thunderstorm.
	 */
	public boolean Storming()
	{
		return thundering;
	}
	/**
	 * Sets whether or not the rain/snow is a thunderstorm.
	 * @param andlightning Whether or not the rain/snow is a thunderstorm.
	 */
	public void Storming(boolean andlightning)
	{
		thundering = andlightning;
	}
	/**
	 * Returns the number of ticks before the thunderstorm state is toggled.
	 * @return The number of ticks before the thunderstorm state is toggled.
	 */
	public int StormToggle()
	{
		return thundertime;
	}
	/**
	 * Sets the number of ticks before the thunderstorm state is toggled.
	 * @param ticks The number of ticks before the thunderstorm state is toggled.
	 */
	public void StormToggle(int ticks)
	{
		thundertime = ticks;
	}

	/**
	 * Returns the map generator for this level.
	 * @return The map generator for this level.
	 */
	public Generator Generator()
	{
		return generator;
	}
	/**
	 * Sets the map generator for this level.
	 * @param oflevels The map generator for this level.
	 */
	public void Generator(Generator oflevels)
	{
		generator = oflevels;
	}
	/**
	 * Returns the version of the map generator for this level.
	 * @return The version of the map generator for this level.
	 */
	public int GeneratorVersion()
	{
		return generatorversion;
	}
	/**
	 * Sets the version of the map generator for this level.
	 * @param version The version of the map generator for this level.
	 */
	public void GeneratorVersion(int version)
	{
		generatorversion = version;
	}
	/**
	 * Returns the random seed used to generate consistent terrain.
	 * @return The random seed used to generate consistent terrain.
	 */
	public long Seed()
	{
		return randomseed;
	}
	/**
	 * Sets the random seed used to generate consistent terrain.
	 * @param random The random seed used to generate consistent terrain.
	 */
	public void Seed(long random)
	{
		randomseed = random;
	}

	/**
	 * Returns the X coordinate of the world spawn.
	 * @return The X coordinate of the world spawn.
	 */
	public int SpawnX()
	{
		return spawnx;
	}
	/**
	 * Returns the Y coordinate of the world spawn.
	 * @return The Y coordinate of the world spawn.
	 */
	public int SpawnY()
	{
		return spawny;
	}
	/**
	 * Returns the Z coordinate of the world spawn.
	 * @return The Z coordinate of the world spawn.
	 */
	public int SpawnZ()
	{
		return spawnz;
	}
	/**
	 * Sets the coordinates of the world spawn.
	 * @param x The X coordinate of the world spawn.
	 * @param y The Y coordinate of the world spawn.
	 * @param z The Z coordinate of the world spawn.
	 */
	public void Spawn(int x, int y, int z)
	{
		spawnx = x;
		spawny = y;
		spawnz = z;
	}

	/**
	 * Returns the Unix Time when the level was last played.
	 * @return The Unix Time when the level was last played.
	 */
	public long LastPlayed()
	{
		return lastplayed;
	}
	/**
	 * Sets the Unix Time when the level was last played.
	 * @param unixtime The Unix Time when the level was last played.
	 */
	public void LastPlayed(long unixtime)
	{
		lastplayed = unixtime;
	}

	/**
	 * Returns the estimated size of the level in bytes.
	 * @return The estimated size of the level in bytes.
	 */
	public long SizeOnDisk()
	{
		return sizeondisk;
	}
	/**
	 * Sets the estimated size of the level in bytes.
	 * @param estimated The estimated size of the level in bytes.
	 */
	public void SizeOnDisk(long estimated)
	{
		sizeondisk = estimated;
	}

	/**
	 * Returns the player for this level, or null if this is a multiplayer level file.
	 * @return The player for this level, or null if this is a multiplayer level file.
	 */
	public Player Player()
	{
		return player;
	}
	/**
	 * Sets the player for this level.
	 * @param p The player for this level, or null if this is a multiplayer level file.
	 */
	public void Player(Player p)
	{
		player = p;
	}

	/**
	 * Returns the tag for this Player.
	 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
	 * @return The tag for this Player.
	 */
	public Tag.Compound ToNBT(String name)
	{
		Tag.Compound data, t = new Tag.Compound(name,
						data = new Tag.Compound("Data", new Tag.Int("version", 19133),
														new Tag.String("LevelName", levelname),
														new Tag.Byte("allowCommands", (byte)(allowcommands?1:0)),
														new Tag.Int("GameType", gametype.ordinal()),
														new Tag.Byte("hardcore", (byte)(hardcore?1:0)),
														new Tag.Byte("MapFeatures", (byte)(mapfeatures?1:0)),
														new Tag.Long("Time", time),
														new Tag.Byte("raining", (byte)(raining?1:0)),
														new Tag.Int("rainTime", raintime),
														new Tag.Byte("thundering", (byte)(thundering?1:0)),
														new Tag.Int("thunderTime", thundertime),
														new Tag.String("generatorName", ""+generator),
														new Tag.Int("generatorVersion", generatorversion),
														new Tag.Long("RandomSeed", randomseed),
														new Tag.Int("SpawnX", spawnx),
														new Tag.Int("SpawnY", spawny),
														new Tag.Int("SpawnZ", spawnz),
														new Tag.Long("LastPlayed", lastplayed),
														new Tag.Long("SizeOnDisk", sizeondisk)));
		if(player != null)
		{
			data.add(player.ToNBT("Player"));
		}
		return t;
	}
}