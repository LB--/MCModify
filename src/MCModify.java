/******************************************************************************\
|* Copyright © 2012 LB-Stuff                                                  *|
|* All rights reserved.                                                       *|
|*                                                                            *|
|* Redistribution and use in source and binary forms, with or without         *|
|* modification, are permitted provided that the following conditions         *|
|* are met:                                                                   *|
|*                                                                            *|
|*  1. Redistributions of source code must retain the above copyright         *|
|*     notice, this list of conditions and the following disclaimer.          *|
|*                                                                            *|
|*  2. Redistributions in binary form must reproduce the above copyright      *|
|*     notice, this list of conditions and the following disclaimer in the    *|
|*     documentation and/or other materials provided with the distribution.   *|
|*                                                                            *|
|* THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS "AS IS" AND       *|
|* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE      *|
|* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE *|
|* ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE    *|
|* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL *|
|* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS    *|
|* OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)      *|
|* HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT *|
|* LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY  *|
|* OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF     *|
|* SUCH DAMAGE.                                                               *|
\******************************************************************************/

import java.io.FileInputStream;
import java.io.FileOutputStream;
import NBT.Tag;
import NBT.Serialization.NBTable;
import NBT.Minecraft.Map;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import NBT.Minecraft.Level;
import NBT.Minecraft.Inventory;
import static NBT.Minecraft.IDs.*;
import NBT.Minecraft.Region;
import NBT.Minecraft.Chunk;

/**
 * Main (test) Class
 * @author LB
 */
public class MCModify
{
	/**
	 * Tests out the library with Minecraft-splash-text-style comments!
	 * @param args Unused!
	 * @throws Throwable if an exception is thrown!
	 */
	public static void main(String[] args) throws Throwable
	{
		{
			System.out.println(">>>TEST 0: Load a level the hard way, change the time to 0, and save it.");
			Tag.Compound level;
			try(FileInputStream fis = new FileInputStream("level.dat"))
			{
				level = NBT.IO.Read(fis); //Read it from any Input stream!
			}

			System.out.println(level); //See the pretty structure!

			Tag.Compound data = (Tag.Compound)level.Get("Data"); //Dodge MC's absurdity!
			Tag.Long Time = (Tag.Long)data.Find(Tag.Type.LONG, "Time"); //Get the Time!
			Time.v = 0L; //Make it day!

			try(FileOutputStream fos = new FileOutputStream("level.dat_mca")) //Save it back...in the future!
			{
				NBT.IO.Write(level, fos); //Write it to any Output stream!
			}
		}

		System.out.println();

		{
			System.out.println(">>>TEST 1: Open idcounts.dat and display its number.");
			Tag.Compound idcounts;
			try(FileInputStream fis = new FileInputStream("idcounts.dat"))
			{
				idcounts = NBT.IO.ReadUncompressed(fis); //Also works for servers.dat!
			}
			System.out.println("Last created map number: "+((Tag.Short)idcounts.Find(Tag.Type.SHORT, "map")).v);
		}

		System.out.println();

		{
			System.out.println(">>>TEST 2: Serialize an NBTable class to a compound tag and load it back again.");
			Tag.Compound serializationTest = NBT.Serialization.IO.Serialize("Test", new TestClass(), false); //Serialize it!
			System.out.println(serializationTest); //Print it!

			System.out.println();

			TestClass tc = (TestClass)NBT.Serialization.IO.Deserialize(TestClass.class, null, serializationTest); //Deserialize it!
			System.out.println(tc); //Print the other it!
		}

		System.out.println();

		{
			System.out.println(">>>TEST 3: Load a map, save it to an image file, write some text on it, and save it to another map file.");
			Map map;
			try(FileInputStream fis = new FileInputStream("map.dat"))
			{
				map = new Map(NBT.IO.Read(fis));
			}

			BufferedImage mapimage = map.Image(); //Yep, Images!
			ImageIO.write(mapimage, "png", new File("map.png")); //Easy to save to any image format!

			Graphics2D g = mapimage.createGraphics(); //Yes, you can use a Graphics2D!
			g.setColor(Color.red); //TNT colored!
			g.drawString("My Minecraft Map", 16, 16); //Easy to draw to - the colors are handled for you!

			try(FileOutputStream fos = new FileOutputStream("map_.dat"))
			{
				NBT.IO.Write(map.ToNBT(""), fos);
			}
		}

		System.out.println();

		{
			System.out.println(">>>TEST 4: Load the level we saved earlier the easy way and add an item to the player's inventory.");
			Level level;
			try(FileInputStream fis = new FileInputStream("level.dat_mca"))
			{
				level = new Level(NBT.IO.Read(fis));
			}

			Level.Player player = level.Player(); //Edit the singplayer player!
			Inventory i = player.Inventory(); //Edit their inventory!
			Inventory.Item item = new Inventory.Item(WrittenBook, 0, 1); //Block & Item ID constants!
			item.Title("Ethonian Battle Book"); //Better than whatever Vechs made for Zisteau!
			item.Author("Vechs"); //Not even close to contradicting what I just said!
			item.Pages().add("§4As you hold this book, you feel the power to §lincenerate§r§4 your enemies!"); //You can't accidentally place books when you right click!
			item.EnchantLevel(Inventory.Item.Enchantment.FireAspect, (short)2); //Displayed below the author!
			i.Item(7, item);

			try(FileOutputStream fos = new FileOutputStream("level.dat_mca"))
			{
				NBT.IO.Write(level.ToNBT(""), fos);
			}
		}

		System.out.println();

		{
			System.out.println(">>>TEST 5: Load a region file and put an ender dragon in every chunk.");
			Region region = new Region(new File("r.0.0.mca"));
			Region newregion = new Region(new File("r.0.1.mca")); //Creates an empty region file for you if it doesn't exist!
			for(int x = 0; x < 31; ++x)
			{
				for(int z = 0; z < 31; ++z)
				{
					Chunk chunk = region.ReadChunk(x, z); //Simple as that!
					if(chunk != null)
					{
						chunk.Entities().add(new NBT.Minecraft.Mob.EnderDragon(x*16+8, 96, z*16+8)); //Entity lists!
					}
					newregion.WriteChunk(x, z, chunk); //Deletes the chunk if it is null!
				}
			}
		}
	}

	/**
	 * A class for testing.
	 */
	public static class TestClass implements NBTable
	{
		public byte m1;
		/*default*/ short m2;
		protected int m3;
		private long m4;
		private transient float m5;
		private static double m6;
		private String m7;

		/**
		 * Default constructor.
		 */
		public TestClass()
		{
			m1 = -120;
			m2 = 32765;
			m3 = 123456789;
			m4 = 7;
			m5 = -9876543210L;
			m5 = +0.1f;
			m6 = -0.1;
			m7 = "Hello, World!";
		}

		/**
		 * Deserialization constructor
		 * @param ignored Required for deserialization, but ignored.
		 */
		private TestClass(Tag.Compound ignored)
		{
			m5 = 1337;
			m7 = "";
		}

		@Override public String toString()
		{
			return "m1: "+m1
				+"\nm2: "+m2
				+"\nm3: "+m3
				+"\nm4: "+m4
				+"\nm5: "+m5
				+"\nm6: "+m6
				+"\nm7: "+m7;
		}
	}
}