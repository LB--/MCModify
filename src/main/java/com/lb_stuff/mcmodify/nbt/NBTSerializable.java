package com.lb_stuff.mcmodify.nbt;

import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Field;

public abstract class NBTSerializable implements AutoNBTSerializable
{
	public Tag.Compound toNBT()
	{
		List<Class<?>> stack = new ArrayList<>();
		Class<?> c = getClass();
		for(;;)
		{
			stack.add(c);
			if(c.equals(NBTSerializable.class))
			{
				break;
			}
			c = c.getSuperclass();
		}
		return null;
	}
	protected Tag fieldToNBT(Field f)
	{
		f.setAccessible(true);
		try
		{
			Object v = f.get(this);
			if(v instanceof Integer)
			{
				return new Tag.Int(f.getName(), (Integer)v);
			}
			else if(v instanceof Long)
			{
				return new Tag.Long(f.getName(), (Long)v);
			}
		}
		catch(IllegalAccessException ex)
		{
		}
		return null;
	}
}
