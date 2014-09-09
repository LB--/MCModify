package com.lb_stuff.mcmodify.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.lang.reflect.InvocationTargetException;

/**
 * The main class used by this NBT package, its static subclasses extend and implement it. The equals() and hashCode() methods are based entirely on the name of the tag.
 * @version 19133
 * @see <a href="http://minecraft.gamepedia.com/NBT_format">NBT format</a> on the Minecraft Wiki
 */
public abstract class Tag implements Cloneable
{
	/**
	 * The name of this tag.
	 */
	private java.lang.String name;
	/**
	 * The sole constructor for the abstract tag class.
	 * @param _name The name of the new tag. If this tag must always have a name, this parameter must not be null. If this tag must never have a name, this parameter must be null.
	 */
	public Tag(java.lang.String _name)
	{
		name = _name;
	}

	public static Tag deserialize(InputStream is) throws IOException
	{
		DataInputStream dis = new DataInputStream(is);
		final Type type = Type.fromId(dis.readByte());
		if(type == Type.END)
		{
			return new End();
		}
		final java.lang.String name = readString(dis);
		switch(type)
		{
			case BYTE:      return new Byte(name, is);
			case SHORT:     return new Short(name, is);
			case INT:       return new Int(name, is);
			case LONG:      return new Long(name, is);
			case FLOAT:     return new Float(name, is);
			case DOUBLE:    return new Double(name, is);
			case BYTEARRAY: return new ByteArray(name, is);
			case STRING:    return new String(name, is);
			case LIST:      return new List(name, is);
			case COMPOUND:  return new Compound(name, is);
			case INTARRAY:  return new IntArray(name, is);
			default: throw new IllegalStateException();
		}
	}
	private static java.lang.String readString(DataInputStream dis) throws IOException
	{
		short length = dis.readShort();
		if(length < 0)
		{
			throw new FormatException("String length was negative: "+length);
		}
		byte[] str = new byte[length];
		dis.readFully(str);
		return new java.lang.String(str, UTF8);
	}

	/**
	 * Returns the name of this tag, or null if this tag doesn't have a name.
	 * @return The name of this tag, or null if this tag doesn't have a name.
	 */
	public java.lang.String getName()
	{
		return name;
	}

	/**
	 * The enumeration class that represents the different tag types in the NBT format.
	 */
	public static enum Type
	{
		END,
		BYTE, SHORT, INT, LONG,
		FLOAT, DOUBLE,
		BYTEARRAY,
		STRING,
		LIST,
		COMPOUND,
		INTARRAY;

		/**
		 * Converts an integer ordinal to a tag type.
		 * @param id The integer ID of the tag.
		 * @return The Tag.Type corresponding to the ID.
		 * @throws IndexOutOfBoundsException if the ordinal is not with a valid range.
		 */
		public static Type fromId(int id)
		{
			switch(id)
			{
				case 0:  return END;
				case 1:  return BYTE;
				case 2:  return SHORT;
				case 3:  return INT;
				case 4:  return LONG;
				case 5:  return FLOAT;
				case 6:  return DOUBLE;
				case 7:  return BYTEARRAY;
				case 8:  return STRING;
				case 9:  return LIST;
				case 10: return COMPOUND;
				case 11: return INTARRAY;
				default: return null;
			}
		}
		/**
		 * Converts this type to its respective class.
		 * @return The class object for this type's relevant class.
		 */
		public Class<? extends Tag> getImplementingClass()
		{
			switch(this)
			{
				case END:       return End.class;
				case BYTE:      return Byte.class;
				case SHORT:     return Short.class;
				case INT:       return Int.class;
				case LONG:      return Long.class;
				case FLOAT:     return Float.class;
				case DOUBLE:    return Double.class;
				case BYTEARRAY: return ByteArray.class;
				case STRING:    return String.class;
				case LIST:      return List.class;
				case COMPOUND:  return Compound.class;
				case INTARRAY:  return IntArray.class;
				default: throw new IllegalStateException();
			}
		}

		/**
		 * A toString method that gives a nicer-looking name than one in ALL CAPS.
		 * @return The simple name of this type's relevant class (e.g. Compound).
		 */
		@Override public java.lang.String toString()
		{
			switch(this)
			{
				case BYTEARRAY: return"Byte Array";
				case INTARRAY: return"Int Array";
				default: return getImplementingClass().getSimpleName();
			}
		}
	}
	/**
	 * The polymorphic method to get which tag type this tag corresponds to.
	 * @return The tag type this tag corresponds to.
	 */
	public abstract Type getType();

	/**
	 * The main serialization function. Serializes raw, uncompressed NBT data by polymorphically calling a payload serialization function.
	 * @param os The <code>OutputStream</code> to serialize to.
	 * @throws IOException if the output operation generates an exception.
	 */
	public final void serialize(OutputStream os) throws IOException
	{
		preSerialize(os);
		serializePayload(os);
	}
	/**
	 * The polymorphic method used to serialize only the tag's payload.
	 * @param os The <code>OutputStream</code> to serialize to.
	 * @throws IOException if the output operation generates an exception.
	 */
	protected abstract void serializePayload(OutputStream os) throws IOException;
	/**
	 * Represents the UTF-8 <code>Charset</code>.
	 */
	protected static final java.nio.charset.Charset UTF8;
	static
	{
		java.nio.charset.Charset temp = null;
		try
		{
			temp = java.nio.charset.Charset.forName("UTF-8");
		}
		catch(Throwable t)
		{
		}
		finally
		{
			UTF8 = temp;
		}
	}
	/**
	 * The method used to serialize the type and name of a tag.
	 * @param os The <code>OutputStream</code> to serialize to.
	 * @throws IOException if the output operation generates an exception.
	 */
	private void preSerialize(OutputStream os) throws IOException
	{
		os.write((byte)getType().ordinal());
		if(name != null)
		{
			new String(null, name).serializePayload(os);
		}
	}

	/**
	 * Used to create a visual, text-based representation of this tag.
	 * @return A visual, text-based representation of this tag.
	 */
	@Override public abstract java.lang.String toString();
	/**
	 * A utility method for either quoting the tag's name or returning nothing if the name is <code>null</code>.
	 * @return A space followed by the quoted name of the tag, or nothing if the name is null.
	 */
	protected final java.lang.String quoteName()
	{
		if(name != null)
		{
			return " \""+name+"\"";
		}
		return "";
	}

	/**
	 * Returns the hash code of the name of this tag.
	 * @return The hash code of the name of this tag.
	 */
	@Override public final int hashCode()
	{
		if(name == null)
		{
			return 0;
		}
		return name.hashCode();
	}
	/**
	 * Returns whether the given object is a Tag and has the same name.
	 * @param o The object to compare to.
	 * @return Whether the given object is a Tag and has the same name.
	 */
	@Override public final boolean equals(Object o)
	{
		if(o instanceof Tag)
		{
			Tag t = (Tag)o;
			if(name == null)
			{
				return t.name == null;
			}
			else if(t.name != null)
			{
				return name.equals(t.name);
			}
		}
		return false;
	}
	/**
	 * Returns whether the given tag has the same name.
	 * @param t The tag to compare to.
	 * @return Whether the given tag has the same name.
	 */
	public final boolean equals(Tag t)
	{
		if(name == null)
		{
			return t.name == null;
		}
		else if(t.name != null)
		{
			return name.equals(t.name);
		}
		return false;
	}

	/**
	 * Returns an independent clone of this tag.
	 * @return An independent clone of this tag.
	 */
	@Override public Tag clone()
	{
		try
		{
			return (Tag)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new IllegalStateException(e);
		}
	}
	/**
	 * Returns an independent clone of this tag with the given name.
	 * @param newname The new name for the clone, or null if the clone should not have a name.
	 * @return An independent clone of this tag with the given name.
	 */
	public final Tag clone(java.lang.String newname)
	{
		Tag t = clone();
		if(!(this instanceof End))
		{
			t.name = newname;
		}
		return t;
	}

	/**
	 * TAG_End
	 */
	public static final class End extends Tag
	{
		/**
		 * Constructs a new stateless End tag.
		 */
		public End()
		{
			super(null);
		}

		/**
		 * Returns the tag type corresponding to TAG_End.
		 * @return <code>Type.END</code>.
		 */
		@Override public Type getType()
		{
			return Type.END;
		}

		/**
		 * Does nothing, but prevents this class from being abstract.
		 * @param o The <code>OutputStream</code> to serialize to.
		 * @throws IOException when pigs fly.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
		}

		/**
		 * Returns "End".
		 * @return "End".
		 */
		@Override public java.lang.String toString()
		{
			return "End";
		}

		/**
		 * Returns <code>this</code>.
		 * @return <code>this</code>.
		 */
		@Override public End clone()
		{
			return this;
		}
	}

	/**
	 * TAG_Byte
	 */
	public static final class Byte extends Tag
	{
		/**
		 * The value of this byte.
		 */
		public byte v;
		/**
		 * The normal constructor.
		 * @param name The name of this byte.
		 * @param b The initial value of the byte.
		 */
		public Byte(java.lang.String name, byte b)
		{
			super(name);
			v = b;
		}
		/**
		 * The DeserializePayload constructor.
		 * @param name The name of this byte.
		 * @param i The <code>InputStream</code> to deserialize the byte from.
		 * @throws IOException if the input operation generates an exception.
		 * @throws FormatException when pigs fly.
		 */
		public Byte(java.lang.String name, InputStream i) throws IOException, FormatException //DeserializePayload
		{
			this(name, (byte)i.read());
		}
		/**
		 * Returns the tag type corresponding to TAG_Byte.
		 * @return <code>Type.BYTE</code>
		 */
		@Override public Type getType()
		{
			return Type.BYTE;
		}
		/**
		 * Serializes the byte to the <code>OutputStream</code>.
		 * @param o The <code>OutputStream</code> to serialize the byte to.
		 * @throws IOException if the output operation generates an exception.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
			o.write(v);
		}
		/**
		 * Gives a textual representation of this byte in base-10.
		 * @return A textual representation of this byte in base-10.
		 */
		@Override public java.lang.String toString()
		{
			return"Byte"+quoteName()+": "+v+"";
		}
	}

	/**
	 * TAG_Short
	 */
	public static final class Short extends Tag
	{
		/**
		 * The value of this short.
		 */
		public short v;
		/**
		 * The normal constructor.
		 * @param name The name of this short.
		 * @param s The initial value of the short.
		 */
		public Short(java.lang.String name, short s)
		{
			super(name);
			v = s;
		}
		/**
		 * The DeserializePayload constructor.
		 * @param name The name of this short.
		 * @param i The <code>InputStream</code> to deserialize the short from.
		 * @throws IOException if the input operation generates an exception.
		 * @throws FormatException when pigs fly.
		 */
		public Short(java.lang.String name, InputStream i) throws IOException, FormatException //DeserializePayload
		{
			this(name, new DataInputStream(i).readShort());
		}
		/**
		 * returns the tag type corresponding to TAG_Short.
		 * @return <code>Type.SHORT</code>
		 */
		@Override public Type getType()
		{
			return Type.SHORT;
		}
		/**
		 * Serializes the short to the <code>OutputStream</code>.
		 * @param o The <code>OutputStream</code> to serialize the short to.
		 * @throws IOException if the output operation generates an exception.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
			new DataOutputStream(o).writeShort(v);
		}
		/**
		 * Gives a textual representation of this short in base-10.
		 * @return A textual representation of this short in base-10.
		 */
		@Override public java.lang.String toString()
		{
			return"Short"+quoteName()+": "+v+"";
		}
	}

	/**
	 * TAG_Int
	 */
	public static final class Int extends Tag
	{
		/**
		 * The value of this integer.
		 */
		public int v;
		/**
		 * The normal constructor.
		 * @param name The name of this integer.
		 * @param i The initial value of the integer.
		 */
		public Int(java.lang.String name, int i)
		{
			super(name);
			v = i;
		}
		/**
		 * The DeserializePayload constructor.
		 * @param name The name of this integer.
		 * @param i The <code>InputStream</code> to deserialize the integer from.
		 * @throws IOException if the input operation generates an exception.
		 * @throws FormatException when pigs fly.
		 */
		public Int(java.lang.String name, InputStream i) throws IOException, FormatException //DeserializePayload
		{
			this(name, new DataInputStream(i).readInt());
		}
		/**
		 * Returns the tag type corresponding to TAG_Int.
		 * @return <code>Type.INT</code>.
		 */
		@Override public Type getType()
		{
			return Type.INT;
		}
		/**
		 * Serializes the integer to the <code>OutputStream</code>.
		 * @param o The <code>OutputStream</code> to serialize the integer to.
		 * @throws IOException if the output operation generates an exception.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
			new DataOutputStream(o).writeInt(v);
		}
		/**
		 * Gives a textual representation of this integer in base-10.
		 * @return A textual representation of this integer in base-10.
		 */
		@Override public java.lang.String toString()
		{
			return"Int"+quoteName()+": "+v+"";
		}
	}

	/**
	 * TAG_Long
	 */
	public static final class Long extends Tag
	{
		/**
		 * The value of this long.
		 */
		public long v;
		/**
		 * The normal constructor.
		 * @param name The name of this long.
		 * @param l The initial value of the long.
		 */
		public Long(java.lang.String name, long l)
		{
			super(name);
			v = l;
		}
		/**
		 * The DeserializePayload constructor.
		 * @param name The name of this long.
		 * @param i The <code>InputStream</code> to deserialize the long from.
		 * @throws IOException if the input operation generates an exception.
		 * @throws FormatException when pigs fly.
		 */
		public Long(java.lang.String name, InputStream i) throws IOException, FormatException //DeserializePayload
		{
			this(name, new DataInputStream(i).readLong());
		}
		/**
		 * Returns the tag type corresponding to TAG_Long.
		 * @return <code>Type.LONG</code>.
		 */
		@Override public Type getType()
		{
			return Type.LONG;
		}
		/**
		 * Serializes the long to the <code>OutputStream</code>.
		 * @param o The <code>OutputStream</code> to serialize the long to.
		 * @throws IOException if the output operation generates an exception.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
			new DataOutputStream(o).writeLong(v);
		}
		/**
		 * Gives a textual representation of this long in base-10.
		 * @return A textual representation of this long in base-10.
		 */
		@Override public java.lang.String toString()
		{
			return"Long"+quoteName()+": "+v+"";
		}
	}

	/**
	 * TAG_Float
	 */
	public static final class Float extends Tag
	{
		/**
		 * The value of this float.
		 */
		public float v;
		/**
		 * The normal constructor.
		 * @param name The name of this float.
		 * @param f The initial value of the float.
		 */
		public Float(java.lang.String name, float f)
		{
			super(name);
			v = f;
		}
		/**
		 * The DeserializePayload constructor.
		 * @param name The name of this float.
		 * @param i The <code>InputStream</code> to deserialize the float from.
		 * @throws IOException if the input operation generates an exception.
		 * @throws FormatException when pigs fly.
		 */
		public Float(java.lang.String name, InputStream i) throws IOException, FormatException //DeserializePayload
		{
			this(name, new DataInputStream(i).readFloat());
		}
		/**
		 * Returns the tag type corresponding to TAG_Float.
		 * @return <code>Type.FLOAT</code>.
		 */
		@Override public Type getType()
		{
			return Type.FLOAT;
		}
		/**
		 * Serializes this float to the <code>OutputStream</code>
		 * @param o The <code>OutputStream</code> to serialize this long to.
		 * @throws IOException if the output operation generates an exception.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
			new DataOutputStream(o).writeFloat(v);
		}
		/**
		 * Gives a textual representation of this float in base-10.
		 * @return A textual representation of this float in base-10.
		 */
		@Override public java.lang.String toString()
		{
			return"Float"+quoteName()+": "+v+"";
		}
	}

	/**
	 * TAG_Double
	 */
	public static final class Double extends Tag
	{
		/**
		 * The value of this double.
		 */
		public double v;
		/**
		 * The normal constructor.
		 * @param name The name of this double.
		 * @param d The initial value of the double.
		 */
		public Double(java.lang.String name, double d)
		{
			super(name);
			v = d;
		}
		/**
		 * The DeserializePayload constructor.
		 * @param name The name of this double.
		 * @param i The <code>InputStream</code> to deserialize the double from.
		 * @throws IOException if the input operation generates an exception.
		 * @throws FormatException when pigs fly.
		 */
		public Double(java.lang.String name, InputStream i) throws IOException, FormatException //DeserializePayload
		{
			this(name, new DataInputStream(i).readDouble());
		}
		/**
		 * Returns the tag type corresponding to TAG_Double.
		 * @return <code>Type.DOUBLE</code>.
		 */
		@Override public Type getType()
		{
			return Type.DOUBLE;
		}
		/**
		 * Serializes the double to the <code>OutputStream</code>.
		 * @param o The <code>OutputStream</code> to serialize this double to.
		 * @throws IOException if the output operation generates an exception.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
			new DataOutputStream(o).writeDouble(v);
		}
		/**
		 * Gives a textual representation of this double in base-10.
		 * @return A textual representation of this double in base-10.
		 */
		@Override public java.lang.String toString()
		{
			return"Double"+quoteName()+": "+v+"";
		}
	}

	/**
	 * TAG_Byte_Array
	 */
	public static final class ByteArray extends Tag
	{
		/**
		 * The byte array in raw form.
		 */
		public byte[] v;
		/**
		 * The normal constructor.
		 * @param name The name of this byte array.
		 * @param b The initial byte array.
		 */
		public ByteArray(java.lang.String name, byte[] b)
		{
			super(name);
			v = b;
		}
		/**
		 * The DeserializePayload constructor.
		 * @param name The name of this byte array.
		 * @param i The <code>InputStream</code> to deserialize the byte array from.
		 * @throws IOException if the input operation generates an exception.
		 * @throws FormatException if the byte array size is negative.
		 */
		public ByteArray(java.lang.String name, InputStream i) throws IOException, FormatException //DeserializePayload
		{
			this(name, (byte[])null);
			DataInputStream dis = new DataInputStream(i);
			int size = dis.readInt();
			if(size < 0)
			{
				throw new FormatException("Byte Array size was negative: "+size);
			}
			v = new byte[size];
			dis.readFully(v);
		}
		/**
		 * Returns the tag type that corresponds to TAG_Byte_Array.
		 * @return <code>Type.BYTEARRAY</code>.
		 */
		@Override public Type getType()
		{
			return Type.BYTEARRAY;
		}
		/**
		 * Serializes the byte array to the <code>OutputStream</code>.
		 * @param o The <code>OutputStream</code> to serialize this byte array to.
		 * @throws IOException if the output operation generates an exception.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
			DataOutputStream dos = new DataOutputStream(o);
			dos.writeInt(v.length);
			dos.write(v);
		}
		/**
		 * Gives a textual representation of this byte array with each byte in base-10.
		 * @return A textual representation of this byte array with each byte in base-10.
		 */
		@Override public java.lang.String toString()
		{
			java.lang.String s = "";
			for(byte b : v)
			{
				if(s.length() != 0)
				{
					s += ", ";
				}
				s += b;
			}
			return"Byte Array"+quoteName()+": ["+s+"]";
		}

		/**
		 * Returns an independent clone of this Byte Array.
		 * @return An independent clone of this Byte Array.
		 */
		@Override public ByteArray clone()
		{
			ByteArray ba = (ByteArray)super.clone();
			ba.v = Arrays.copyOf(v, v.length);
			return ba;
		}
	}

	/**
	 * TAG_String
	 * <p>
	 * The reason for the use of the fully qualified name <code>java.lang.String</code> throughout this code.
	 */
	public static final class String extends Tag
	{
		/**
		 * The value of this string.
		 */
		public java.lang.String v;
		/**
		 * The normal constructor.
		 * @param name The name of this string.
		 * @param s The initial string.
		 */
		public String(java.lang.String name, java.lang.String s)
		{
			super(name);
			v = s;
		}
		/**
		 * The DeserializePayload constructor.
		 * @param name The name of this string.
		 * @param i The <code>InputStream</code> to deserialize this string from.
		 * @throws IOException if the input operation generates an exception.
		 * @throws FormatException if the string length is negative.
		 */
		public String(java.lang.String name, InputStream i) throws IOException, FormatException //DeserializePayload
		{
			this(name, readString(new DataInputStream(i)));
		}
		/**
		 * Returns the tag type that corresponds to TAG_String.
		 * @return <code>Type.STRING</code>.
		 */
		@Override public Type getType()
		{
			return Type.STRING;
		}
		/**
		 * Serializes this string to the <code>OutputStream</code>.
		 * @param o The <code>OutputStream</code> to serialize this string to.
		 * @throws IOException if the output operation generates an exception.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
			byte[] sarr = v.getBytes(UTF8);
			new DataOutputStream(o).writeShort((short)sarr.length);
			o.write(sarr);
		}
		/**
		 * Gives a textual representation of this string.
		 * @return A textual representation of this string.
		 */
		@Override public java.lang.String toString()
		{
			return"String"+quoteName()+": \""+v+"\"";
		}
	}

	/**
	 * TAG_List
	 */
	public static final class List extends Tag implements Iterable<Tag>
	{
		/**
		 * The tag type this tags supports.
		 */
		private final Type type;
		/**
		 * The list of tags in this list.
		 */
		private java.util.List<Tag> list = new ArrayList<>();
		/**
		 * The normal constructor.
		 * @param name The name of the tags.
		 * @param _type The tag type this tags supports.
		 * @param tags The initial list of tags.
		 * @throws IllegalArgumentException if the tag type is TAG_End or null, or a given tag is not a supported type or has a name that is not null.
		 */
		public List(java.lang.String name, Type _type, Tag... tags) throws IllegalArgumentException
		{
			super(name);
			if(_type == null)
			{
				throw new IllegalArgumentException("The tag type was null");
			}
			type = _type;
			if(type != Type.END)
			{
				for(Tag t : tags)
				{
					if(t.getName() != null)
					{
						throw new IllegalArgumentException("Tags in Lists must have null names; given tag had name: \""+t.getName()+"\"");
					}
					else if(t.getType() == type)
					{
						list.add(t);
					}
					else
					{
						throw new IllegalArgumentException(type+" required, given "+t.getType());
					}
				}
			}
		}
		/**
		 * The DeserializePayload constructor.
		 * @param name The name of the tags.
		 * @param i The <code>InputStream</code> to deserialize the tags from.
		 * @throws IOException if the input operation generates an exception.
		 * @throws FormatException if the tag type is TAG_End, the tags size is negative, or some other exception is thrown while deserializing the tags.
		 */
		public List(java.lang.String name, InputStream i) throws IOException, FormatException //DeserializePayload
		{
			super(name);
			type = Type.fromId(i.read());
			int size = new DataInputStream(i).readInt();
			if(size < 0)
			{
				throw new FormatException("List size is negative: "+size);
			}
			try
			{
				java.lang.reflect.Constructor<? extends Tag> c = type.getImplementingClass().getConstructor(java.lang.String.class, InputStream.class);
				for(int j = 0; j < size; ++j)
				{
					list.add(c.newInstance(null, i));
				}
			}
			catch(NoSuchMethodException|SecurityException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e)
			{
				throw new FormatException(e);
			}
		}
		/**
		 * Returns the tag type that corresponds to TAG_List.
		 * @return <code>Type.LIST</code>.
		 */
		@Override public Type getType()
		{
			return Type.LIST;
		}
		/**
		 * Serializes this tags to the <code>OutputStream</code>.
		 * @param o The <code>OutputStream</code> to serialize this tags to.
		 * @throws IOException if the output operation generates an exception.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
			o.write((byte)type.ordinal());
			new DataOutputStream(o).writeInt(list.size());
			for(int i = 0; i < list.size(); ++i)
			{
				list.get(i).serializePayload(o);
			}
		}
		/**
		 * Gives a textual representation of this tags with nice indenting even with nesting.
		 * @return A textual representation of this tags with nice indenting even with nesting.
		 */
		@Override public java.lang.String toString()
		{
			java.lang.String s = "";
			for(int i = 0; i < list.size(); ++i)
			{
				if(i != 0)
				{
					s += ",\n";
				}
				s += list.get(i);
			}
			return"List of "+type+""+quoteName()+": \n[\n"+Compound.preceedLinesWithTabs(s)+"\n]";
		}

		/**
		 * Adds all given tags to this list until a given tag is of a type not supported by this list.
		 * @param tags The tags to be added.
		 */
		public void add(Tag... tags)
		{
			for(Tag t : tags)
			{
				if(t.getName() != null)
				{
					throw new IllegalArgumentException("Tags in Lists must have null names; given tag had name: \""+t.getName()+"\"");
				}
				else if(t.getType() != type)
				{
					throw new IllegalArgumentException(type+" required, given "+t.getType());
				}
				list.add(t);
			}
		}
		/**
		 * Adds all the tags from the given list to this list.
		 * @param l The list from which to add the tags.
		 */
		public void addAll(Tag.List l)
		{
			if(l.getContainedType() != type)
			{
				throw new IllegalArgumentException(type+" required, given list of "+l.getContainedType());
			}
			for(Tag t : l.list)
			{
				list.add(t);
			}
		}
		/**
		 * Adds all the tags from the given collection to this list.
		 * @param c The collection from which to add the tags.
		 */
		public void addAll(Collection<? extends Tag> c)
		{
			for(Tag t : c)
			{
				add(t);
			}
		}
		/**
		 * Returns the number of tags in this list tag.
		 * @return The number of tags in this list tag.
		 */
		public int getSize()
		{
			return list.size();
		}
		/**
		 * Sets the tag at the given index in the list to the given tag.
		 * @param index The index to set.
		 * @param t The tag that the index will be set to.
		 */
		public void set(int index, Tag t)
		{
			if(t.getType() != type)
			{
				throw new IllegalArgumentException(type+" required, given "+t.getType());
			}
			list.set(index, t);
		}
		/**
		 * Inserts the given tags to this list at the specified index.
		 * @param index The index before which to insert.
		 * @param tags The tags to insert.
		 */
		public void insert(int index, Tag... tags)
		{
			for(Tag t : tags)
			{
				if(t.getName() != null)
				{
					throw new IllegalArgumentException("Tags in Lists must have null names; given tag had name: \""+t.getName()+"\"");
				}
				else if(t.getType() != type)
				{
					throw new IllegalArgumentException(type+" required, given "+t.getType());
				}
				list.add(index++, t);
			}
		}
		/**
		 * Inserts the tags from the given list to this list at the specified index.
		 * @param index The index at which to insert the tags.
		 * @param l The list from which to insert the tags.
		 */
		public void insertAll(int index, Tag.List l)
		{
			if(l.getContainedType() != type)
			{
				throw new IllegalArgumentException(type+" required, given list of "+l.getContainedType());
			}
			for(Tag t : l.list)
			{
				list.add(index++, t);
			}
		}
		/**
		 * Inserts the tags from the given collection to this list at the specified index.
		 * @param index The index at which to insert the tags.
		 * @param c The collection from which to insert the tags.
		 */
		public void insertAll(int index, Collection<? extends Tag> c)
		{
			for(Tag t : c)
			{
				insert(index++, t);
			}
		}
		/**
		 * Getter for individual tags in this list tag.
		 * @param index The index to get a tag from.
		 * @return The tag at the specified index.
		 */
		public Tag get(int index)
		{
			return list.get(index);
		}
		/**
		 * Removes the tag at the specified index from this list tag.
		 * @param index The index of the tag to remove.
		 * @return The tag that was removed.
		 */
		public Tag remove(int index)
		{
			return list.remove(index);
		}

		/**
		 * Returns the tag type supported by this tags.
		 * @return The tag type supported by this tags.
		 */
		public Type getContainedType()
		{
			return type;
		}

		/**
		 * Returns an iterator over this list tag.
		 * @return An iterator over this list tag.
		 */
		@Override public Iterator<Tag> iterator()
		{
			return list.iterator();
		}

		/**
		 * Returns an independent clone of this List tag.
		 * @return An independent clone of this List tag.
		 */
		@Override public List clone()
		{
			List li = (List)super.clone();
			li.list = new ArrayList<>();
			for(int i = 0; i < list.size(); ++i)
			{
				li.list.add(list.get(i).clone());
			}
			return li;
		}
	}

	/**
	 * TAG_Compound
	 */
	public static final class Compound extends Tag implements Iterable<Tag>
	{
		/**
		 * The list of tags in this compound tag.
		 */
		private HashMap<java.lang.String, Tag> tags = new HashMap<>();
		/**
		 * The normal constructor.
		 * @param name The name of this compound tag.
		 * @param tags The initial tags in this compound tag.
		 * @throws IllegalArgumentException if the name of one of the given tags is null.
		 */
		public Compound(java.lang.String name, Tag... tags) throws IllegalArgumentException
		{
			super(name);
			for(Tag t : tags)
			{
				java.lang.String n = t.getName();
				if(n == null)
				{
					throw new IllegalArgumentException("Tag names cannot be null");
				}
				if(t instanceof End)
				{
					throw new IllegalArgumentException("Cannot manually add the End tag!");
				}
				this.tags.put(n, t);
			}
		}
		/**
		 * The DeserializePayload constructor.
		 * @param name The name of this compound tag.
		 * @param i The <code>InputStream</code> to deserialize the compound tag from.
		 * @throws IOException if the input operation generates an exception.
		 * @throws FormatException if some other exception is thrown while deserializing the compound tag.
		 */
		public Compound(java.lang.String name, InputStream i) throws IOException, FormatException //DeserializePayload
		{
			this(name);
			Tag t;
			while(!((t = deserialize(i)) instanceof End))
			{
				tags.put(t.getName(), t);
			}
		}
		/**
		 * Returns the tag type that corresponds to TAG_Compound.
		 * @return <code>Type.COMPOUND</code>.
		 */
		@Override public Type getType()
		{
			return Type.COMPOUND;
		}
		/**
		 * Serializes this compound tag to the <code>OutputStream</code>.
		 * @param o The <code>OutputStream</code> to serialize this compound tag to.
		 * @throws IOException if the output operation generates an exception.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
			for(Tag t : tags.values())
			{
				t.serialize(o);
			}
			new End().serialize(o);
		}
		/**
		 * Gives a textual representation of this compound tag with nice indenting even with nesting.
		 * @return A textual representation of this compound tag with nice indenting even with nesting.
		 */
		@Override public java.lang.String toString()
		{
			java.lang.String s = "";
			for(Tag t : tags.values())
			{
				if(s.length() != 0)
				{
					s += ",\n";
				}
				s += t;
			}
			return"Compound"+quoteName()+":\n{\n"+preceedLinesWithTabs(s)+"\n}";
		}
		/**
		 * Utility function used by this class and the List class for preceeding lines with tabs.
		 * @param s The string for which each line should be prefixed with a tab.
		 * @return The string with each line prefixed with an additional tab.
		 */
		/*default*/ static java.lang.String preceedLinesWithTabs(java.lang.String s)
		{
			return"\t"+s.replaceAll("\n", "\n\t");
		}

		/**
		 * Adds the tags to this compound tag.
		 * @param tags The tags to be added.
		 * @throws IllegalArgumentException if the name of one of the given tags is null.
		 */
		public void add(Tag... tags) throws IllegalArgumentException
		{
			for(Tag t : tags)
			{
				java.lang.String n = t.getName();
				if(n == null)
				{
					throw new IllegalArgumentException("Tag names cannot be null");
				}
				if(t.getType() == Type.END)
				{
					throw new IllegalArgumentException("Cannot manually add a TAG_End!");
				}
				this.tags.put(n, t);
			}
		}
		/**
		 * Adds all the tags from the given compound tag to this compound tag.
		 * @param c The compound tag from which to add the tags.
		 */
		public void addAll(Tag.Compound c)
		{
			tags.putAll(c.tags);
		}
		/**
		 * Adds the tags from the given collection to this compound tag.
		 * @param c The collection from which to add the tags.
		 * @throws IllegalArgumentException if the name of one of the given tags is null.
		 */
		public void addAll(Collection<? extends Tag> c) throws IllegalArgumentException
		{
			for(Tag t : c)
			{
				add(t);
			}
		}
		/**
		 * Returns the number of tags in this compound tag.
		 * @return The number of tags in this compound tag.
		 */
		public int getSize()
		{
			return tags.size();
		}

		/**
		 * Returns the tag with the given name.
		 * @param name The name of the tag.
		 * @return The tag with the given name, or null if the tag does not exist.
		 */
		public Tag get(java.lang.String name)
		{
			return tags.get(name);
		}
		/**
		 * Returns the tag with the given type and name or throws an exception.
		 * @param type The type of tag.
		 * @param n The name of the tag.
		 * @return The tag, guaranteed to be of the type specified.
		 * @throws FormatException if the tag doesn't exist or is of the wrong type.
		 */
		@Deprecated
		public Tag find(Type type, java.lang.String n) throws FormatException
		{
			Tag t = tags.get(n);
			if(t == null)
			{
				throw new FormatException("No tag with the name \""+n+"\"", this);
			}
			else if(t.getType() != type)
			{
				throw new FormatException("\""+n+"\" is "+t.getType()+" instead of "+type, this);
			}
			return t;
		}
		/**
		 * Finds the List tag from the given name that supports.
		 * @param n The name of the list.
		 * @param type The type of tag the list must support.
		 * @return The List tag, guaranteed to support the type specified.
		 * @throws FormatException if the list doesn't exist or supports the wrong type.
		 */
		@Deprecated
		public List findList(java.lang.String n, Type type) throws FormatException
		{
			Tag t = tags.get(n);
			if(t == null)
			{
				throw new FormatException("No List tag with the name \""+n+"\"", this);
			}
			else if(t.getType() != Type.LIST)
			{
				throw new FormatException("\""+n+"\" is "+t.getType()+" instead of a List tag", this);
			}
			List l = (List)t;
			if(l.getContainedType() != type)
			{
				throw new FormatException("\""+n+"\" supports "+l.getContainedType()+" instead of "+type, this);
			}
			return l;
		}
		/**
		 * Removes the tag with the given name.
		 * @param n The name of the tag to remove.
		 * @return The tag that was removed, or null if the tag didn't exist.
		 */
		public Tag remove(java.lang.String n)
		{
			return tags.remove(n);
		}

		/**
		 * Returns an iterator over this compound tag.
		 * @return An iterator over this compound tag.
		 */
		@Override public Iterator<Tag> iterator()
		{
			return tags.values().iterator();
		}

		/**
		 * Returns an independent clone of this Compound tag.
		 * @return An independent clone of this Compound tag.
		 */
		@Override public Compound clone()
		{
			Compound c = (Compound)super.clone();
			c.tags = new HashMap<>();
			for(Map.Entry<java.lang.String, Tag> e : tags.entrySet())
			{
				c.tags.put(e.getKey(), e.getValue().clone());
			}
			return c;
		}
	}

	/**
	 * TAG_Int_Array
	 */
	public static final class IntArray extends Tag
	{
		/**
		 * The integer array in raw form.
		 */
		public int[] v;
		/**
		 * The normal constructor.
		 * @param name The name of this integer array.
		 * @param i The initial integer array.
		 */
		public IntArray(java.lang.String name, int[] i)
		{
			super(name);
			v = i;
		}
		/**
		 * The DeserializePayload constructor.
		 * @param name The name of this integer array.
		 * @param i The <code>InputStream</code> to deserialize the integer array from.
		 * @throws IOException if the input operation generates an exception.
		 * @throws FormatException if the integer array size is negative.
		 */
		public IntArray(java.lang.String name, InputStream i) throws IOException, FormatException //DeserializePayload
		{
			this(name, (int[])null);
			DataInputStream dis = new DataInputStream(i);
			int size = dis.readInt();
			if(size < 0)
			{
				throw new FormatException("Integer Array size was negative: "+size);
			}
			v = new int[size];
			for(int j = 0; j < size; ++j)
			{
				v[j] = dis.readInt();
			}
		}
		/**
		 * Returns the tag type that corresponds to TAG_Int_Array (?).
		 * @return <code>Type.INTARRAY</code>.
		 */
		@Override public Type getType()
		{
			return Type.INTARRAY;
		}
		/**
		 * Serializes the integer array to the <code>OutputStream</code>.
		 * @param o The <code>OutputStream</code> to serialize this integer array to.
		 * @throws IOException if the output operation generates an exception.
		 */
		@Override protected void serializePayload(OutputStream o) throws IOException
		{
			DataOutputStream dos = new DataOutputStream(o);
			dos.writeInt(v.length);
			for(int i : v)
			{
				dos.writeInt(i);
			}
		}
		/**
		 * Gives a textual representation of this integer array with each integer in base-10.
		 * @return A textual representation of this integer array with each integer in base-10.
		 */
		@Override public java.lang.String toString()
		{
			java.lang.String s = "";
			for(int i : v)
			{
				if(s.length() != 0)
				{
					s += ", ";
				}
				s += i;
			}
			return"Int Array"+quoteName()+": ["+s+"]";
		}

		/**
		 * Returns an independent clone of this Integer Array.
		 * @return An independent clone of this Integer Array.
		 */
		@Override public IntArray clone()
		{
			IntArray ia = (IntArray)super.clone();
			ia.v = Arrays.copyOf(v, v.length);
			return ia;
		}
	}
}
