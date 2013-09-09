/******************************************************************************\
|* Copyright © 2013 LB-Stuff                                                  *|
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

package com.LB_Stuff.NBT.Minecraft;

import com.LB_Stuff.NBT.Tag;
import com.LB_Stuff.NBT.FormatException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * The class used for various inventories.
 * @author LB
 */
public class Inventory
{
	/**
	 * Represents an Item in the Inventory.
	 */
	public static class Item
	{
		/**
		 * The item ID.
		 */
		private short id;
		/**
		 * The data value.
		 */
		private short damage;
		/**
		 * The number of items in the stack.
		 */
		private byte count;
		/**
		 * An enumeration class to represent the current known enchantments.
		 */
		public static enum Enchantment
		{
			Protection, FireProtection, FeatherFalling, BlastProtection, ProjectileProtection, Respiration, AquaAffinity,
			Sharpness, Smite, BaneOfArthropods, Knockback, FireAspect, Looting,
			Efficiency, SilkTouch, Unbreaking, Fortune,
			Power, Punch, Flame, Infinity;

			/**
			 * Given an enchantment ID, returns the corresponding <code>Enchantment</code>.
			 * @param ID The ID of the enchantment.
			 * @return The <code>Enchantment</code> that corresponds to the given ID.
			 * @throws FormatException if the given ID does not correspond to any known enchantment.
			 */
			public static Enchantment EnchantFromID(short ID) throws FormatException
			{
				switch(ID)
				{
				case 0:		return Protection;
				case 1:		return FireProtection;
				case 2:		return FeatherFalling;
				case 3:		return BlastProtection;
				case 4:		return ProjectileProtection;
				case 5:		return Respiration;
				case 6:		return AquaAffinity;

				case 16:	return Sharpness;
				case 17:	return Smite;
				case 18:	return BaneOfArthropods;
				case 19:	return Knockback;
				case 20:	return FireAspect;
				case 21:	return Looting;

				case 32:	return Efficiency;
				case 33:	return SilkTouch;
				case 34:	return Unbreaking;
				case 35:	return Fortune;

				case 48:	return Power;
				case 49:	return Punch;
				case 50:	return Flame;
				case 51:	return Infinity;
				}
				throw new FormatException("Unknown Enchantment ID: "+ID);
			}
			/**
			 * Returns the ID of this <code>Enchantment</code>.
			 * @return The ID of this <code>Enchantment</code>.
			 */
			public short ID()
			{
				switch(this)
				{
				case Protection:			return 0;
				case FireProtection:		return 1;
				case FeatherFalling:		return 2;
				case BlastProtection:		return 3;
				case ProjectileProtection:	return 4;
				case Respiration:			return 5;
				case AquaAffinity:			return 6;

				case Sharpness:				return 16;
				case Smite:					return 17;
				case BaneOfArthropods:		return 18;
				case Knockback:				return 19;
				case FireAspect:			return 20;
				case Looting:				return 21;

				case Efficiency:			return 32;
				case SilkTouch:				return 33;
				case Unbreaking:			return 34;
				case Fortune:				return 35;

				case Power:					return 48;
				case Punch:					return 49;
				case Flame:					return 50;
				case Infinity:				return 51;
				}
				throw new IllegalArgumentException();
			}
		}
		/**
		 * The enchantments on this item.
		 */
		private Map<Enchantment, Short/*lvl*/> enchantments = new HashMap<>();

		private String title;
		private String author;
		private List<String> pages;

		/**
		 * Constructs an inventory Item from the given tag.
		 * @param item The tag from which to construct this inventory Item.
		 * @throws FormatException if the given tag is invalid.
		 */
		public Item(Tag.Compound item) throws FormatException
		{
			id = ((Tag.Short)item.Find(Tag.Type.SHORT, "id")).v;
			damage = ((Tag.Short)item.Find(Tag.Type.SHORT, "Damage")).v;
			count = ((Tag.Byte)item.Find(Tag.Type.BYTE, "Count")).v;
			Tag.Compound tag = null;
			try
			{
				tag = (Tag.Compound)item.Find(Tag.Type.COMPOUND, "tag");
			}
			catch(FormatException e)
			{
			}
			if(tag != null)
			{
				Tag.List ench = null;
				try
				{
					ench = (Tag.List)tag.Find(Tag.Type.LIST, "ench");
				}
				catch(FormatException e)
				{
				}
				if(ench != null)
				{
					if(ench.Supports() != Tag.Type.COMPOUND)
					{
						throw new FormatException("Invalid Enchantment List");
					}
					for(int i = 0; i < ench.Size(); ++i)
					{
						Tag.Compound enchant  = (Tag.Compound)ench.Get(i);
						Enchantment e = Enchantment.EnchantFromID(((Tag.Short)enchant.Find(Tag.Type.SHORT, "id")).v);
						if(enchantments.containsKey(e))
						{
							throw new FormatException("Duplicate Enchantment: "+e);
						}
						enchantments.put(e, ((Tag.Short)enchant.Find(Tag.Type.SHORT, "lvl")).v);
					}
				}

				if(id == IDs.BookAndQuill || id == IDs.WrittenBook)
				{
					if(id == IDs.WrittenBook)
					{
						title = ((Tag.String)tag.Find(Tag.Type.STRING, "title")).v;
						author = ((Tag.String)tag.Find(Tag.Type.STRING, "author")).v;
					}
					Tag.List pagelist = (Tag.List)tag.Find(Tag.Type.LIST, "pages");
					if(pagelist.Supports() != Tag.Type.STRING)
					{
						throw new FormatException("Invalid Page List");
					}
					pages = new ArrayList<>();
					for(Tag t : pagelist)
					{
						pages.add(((Tag.String)t).v);
					}
				}
			}
		}
		/**
		 * Constructs a new item from the given ID, Data, and Count.
		 * @param ID The Item ID.
		 * @param data The data/damage value.
		 * @param n The stack count.
		 */
		public Item(int ID, int data, int n)
		{
			ID((short)ID);
			damage = (short)data;
			count = (byte)n;
		}

		/**
		 * Returns the item ID for this inventory Item.
		 * @return The item ID for this inventory Item.
		 */
		public short ID()
		{
			return id;
		}
		/**
		 * Sets the item ID for this inventory Item.
		 * @param ID The item ID for this inventory Item.
		 */
		public final void ID(short ID)
		{
			id = ID;

			if(ID == IDs.BookAndQuill || ID == IDs.WrittenBook)
			{
				if(ID == IDs.WrittenBook)
				{
					title = author = "";
				}
				pages = new ArrayList<>();
			}
			else
			{
				title = author = null;
				pages = null;
			}
		}

		/**
		 * Returns the data/damage value for this inventory Item.
		 * @return The data/damage value for this inventory Item.
		 */
		public short Data()
		{
			return damage;
		}
		/**
		 * Sets the data/damage value for this inventory Item.
		 * @param data The data/damage value for this inventory Item.
		 */
		public void Data(short data)
		{
			damage = data;
		}

		/**
		 * Returns how many of this item is in the stack.
		 * @return How many of this item is in the stack.
		 */
		public byte Count()
		{
			return count;
		}
		/**
		 * Sets how many of this item is in the stack.
		 * @param n How many of this item is in the stack.
		 */
		public void Count(byte n)
		{
			count = n;
		}

		/**
		 * Returns the set of all the enchantments on this item.
		 * @return The set of all the enchantments on this item.
		 */
		public Set<Enchantment> Enchantments()
		{
			return enchantments.keySet();
		}
		/**
		 * Given an enchantment, returns the enchantment level, or null if the item does not have the given enchantment.
		 * @param enchant The enchantment to check the level of.
		 * @return The enchantment level, or null if the item does not have the given enchantment.
		 */
		public Short EnchantLevel(Enchantment enchant)
		{
			return enchantments.get(enchant);
		}
		/**
		 * Sets the level for the given enchantment, or removes it if the given level is null.
		 * @param enchant The enchantment to change the level of.
		 * @param level The new level for the enchantment, or null if the enchantment should be removed.
		 */
		public void EnchantLevel(Enchantment enchant, Short level)
		{
			if(level == null)
			{
				enchantments.remove(enchant);
			}
			else
			{
				enchantments.put(enchant, level);
			}
		}

		/**
		 * Returns the title of this book.
		 * @return The title of this book.
		 * @throws UnsupportedOperationException if this item is not a published book.
		 */
		public String Title() throws UnsupportedOperationException
		{
			if(id != IDs.WrittenBook)
			{
				throw new UnsupportedOperationException("This item is not a published book.");
			}
			return title;
		}
		/**
		 * Sets the title of this book.
		 * @param name The title of this book.
		 * @throws UnsupportedOperationException if this item is not a published book.
		 */
		public void Title(String name) throws UnsupportedOperationException
		{
			if(id != IDs.WrittenBook)
			{
				throw new UnsupportedOperationException("This item is not a published book.");
			}
			title = name;
		}
		/**
		 * Returns the author of this book.
		 * @return The author of this book.
		 * @throws UnsupportedOperationException if this item is not a published book.
		 */
		public String Author() throws UnsupportedOperationException
		{
			if(id != IDs.WrittenBook)
			{
				throw new UnsupportedOperationException("This item is not a published book.");
			}
			return author;
		}
		/**
		 * Sets the author of this book.
		 * @param name The author of this book.
		 * @throws UnsupportedOperationException if this item is not a published book.
		 */
		public void Author(String name) throws UnsupportedOperationException
		{
			if(id != IDs.WrittenBook)
			{
				throw new UnsupportedOperationException("This item is not a published book.");
			}
			author = name;
		}
		/**
		 * Returns the list of pages in this book.
		 * @return The list of pages in this book.
		 * @throws UnsupportedOperationException if this item is not a book & quill or published book.
		 */
		public List<String> Pages() throws UnsupportedOperationException
		{
			if(id != IDs.BookAndQuill && id != IDs.WrittenBook)
			{
				throw new UnsupportedOperationException("This item is not a book & quill or published book.");
			}
			return pages;
		}

		/**
		 * Returns the tag for this inventory Item.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @param slot The slot this item should be indicated to have.
		 * @return The tag for this inventory Item.
		 */
		public Tag.Compound ToNBT(String name, byte slot)
		{
			Tag.Compound t = new Tag.Compound(name, new Tag.Byte("Slot", slot),
													new Tag.Short("id", id),
													new Tag.Short("Damage", damage),
													new Tag.Byte("Count", count));
			Tag.Compound tag;
			if(!enchantments.isEmpty() || pages != null)
			{
				tag = new Tag.Compound("tag");
				t.Add(tag);

				if(!enchantments.isEmpty())
				{
					Tag.List enchants;
					tag.Add(enchants = new Tag.List("ench", Tag.Type.COMPOUND));
					for(Map.Entry<Enchantment, Short> enchant : enchantments.entrySet())
					{
						try
						{
							enchants.Add(new Tag.Compound(null, new Tag.Short("id", enchant.getKey().ID()),
																new Tag.Short("lvl", enchant.getValue())));
						}
						catch(Tag.Type.MismatchException e)
						{
						}
					}
				}

				if(pages != null)
				{
					if(title != null && author != null)
					{
						tag.Add(new Tag.String("title", title),
								new Tag.String("author", author));
					}
					Tag.List pagelist;
					tag.Add(pagelist = new Tag.List("pages", Tag.Type.STRING));
					try
					{
						for(String page : pages)
						{
							pagelist.Add(new Tag.String(null, page));
						}
					}
					catch(Tag.Type.MismatchException e)
					{
					}
				}
			}
			return t;
		}
		/**
		 * Returns the tag for this inventory Item, without the slot.
		 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
		 * @param slot The slot this item should be indicated to have.
		 * @return The tag for this inventory Item.
		 */
		public Tag.Compound ToNBT(String name)
		{
			Tag.Compound t = ToNBT(name, (byte)-1);
			t.Remove("Slot");
			return t;
		}
	}
	/**
	 * The map of items to their slots.
	 */
	private Map<Byte/*slot*/, Item> items = new HashMap<>();

	/**
	 * Constructs an inventory from the given tag.
	 * @param inventory The tag from which to construct this Inventory.
	 * @throws FormatException if the given tag is invalid.
	 */
	public Inventory(Tag.List inventory) throws FormatException
	{
		if(inventory.Supports() != Tag.Type.COMPOUND)
		{
			if(inventory.Supports() == Tag.Type.BYTE)
			{
				return; //for EnderItems
			}
			throw new FormatException("Invalid Inventory List");
		}
		for(int i = 0; i < inventory.Size(); ++i)
		{
			Tag.Compound item = (Tag.Compound)inventory.Get(i);
			byte slot = ((Tag.Byte)item.Find(Tag.Type.BYTE, "Slot")).v;
			if(slot < 0)
			{
				throw new FormatException("Invalid Slot number: "+slot);
			}
			if(items.containsKey(slot))
			{
				throw new FormatException("Duplicate Inventory Slot: "+slot);
			}
			items.put(slot, new Item(item));
		}
	}
	/**
	 * Constructs an empty inventory.
	 */
	public Inventory()
	{
	}

	/**
	 * Returns the item at the given slot, or null if there is no item in that slot. Slot numbers must be in the range 0 to 127, inclusive.
	 * @param slot The slot number of the item, in the range 0 to 127 inclusive.
	 * @return The item at the given slot, or null if there is no item in that slot.
	 * @throws IllegalArgumentException if the given slot number is not within the range 0 to 127.
	 */
	public Item Item(int slot) throws IllegalArgumentException
	{
		if(slot < 0 || slot > 127)
		{
			throw new IllegalArgumentException("The slot number must be in the range 0 to 127 inclusive, given: "+slot);
		}
		return items.get((byte)slot);
	}
	/**
	 * Sets the item at the given slot, or null to remove the item at that slot. Slot numbers must be in the range 0 to 127, inclusive.
	 * @param slot The slot number of the item, in the range 0 to 127 inclusive.
	 * @param item The item at the given slot, or null to remove the item at that slot.
	 * @throws IllegalArgumentException if the given slot number is not within the range 0 to 127.
	 */
	public void Item(int slot, Item item) throws IllegalArgumentException
	{
		if(slot < 0 || slot > 127)
		{
			throw new IllegalArgumentException("The slot number must be in the range 0 to 127 inclusive, given: "+slot);
		}
		if(item == null)
		{
			items.remove((byte)slot);
		}
		else
		{
			items.put((byte)slot, item);
		}
	}

	/**
	 * Returns the tag for this Inventory.
	 * @param name The name the list tag should have, or null if the list tag should not have a name.
	 * @return The tag for this Inventory.
	 */
	public Tag.List ToNBT(String name)
	{
		Tag.List inventory = new Tag.List(name, Tag.Type.COMPOUND);
		try
		{
			for(Map.Entry<Byte, Item> slot : items.entrySet())
			{
				inventory.Add(slot.getValue().ToNBT(null, slot.getKey()));
			}
		}
		catch(Tag.Type.MismatchException e)
		{
		}
		return inventory;
	}
}