package com.lb_stuff.mcmodify.nbt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class NBTSerializable implements AutoNBTSerializable
{
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
	public @interface As
	{
		Tag.Type type();
	}

	public void toNBT(Tag.Compound tag)
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
		while(stack.size() > 0)
		{
			c = stack.get(stack.size()-1);
			for(Field f : c.getDeclaredFields())
			{
				try
				{
					Method m = f.getDeclaringClass().getDeclaredMethod("$"+f.getName(), Tag.Compound.class);
					m.setAccessible(true);
					m.invoke(this, tag);
				}
				catch(NoSuchMethodException|IllegalAccessException|InvocationTargetException e)
				{
				}
				f.setAccessible(true);
				try
				{
					Object v = f.get(this);
					tag.Add(objectToNBT(f.getName(), v));
				}
				catch(IllegalAccessException e)
				{
				}
			}
		}
	}
	private Tag objectToNBT(String name, Object v)
	{
		if(v instanceof Byte)
		{
			return new Tag.Byte(name, (Byte)v);
		}
		else if(v instanceof Short)
		{
			return new Tag.Short(name, (Short)v);
		}
		else if(v instanceof Integer)
		{
			return new Tag.Int(name, (Integer)v);
		}
		else if(v instanceof Long)
		{
			return new Tag.Long(name, (Long)v);
		}
		else if(v instanceof Float)
		{
			return new Tag.Float(name, (Float)v);
		}
		else if(v instanceof Double)
		{
			return new Tag.Double(name, (Double)v);
		}
		else if(v instanceof byte[])
		{
			return new Tag.ByteArray(name, (byte[])v);
		}
		else if(v instanceof int[])
		{
			return new Tag.IntArray(name, (int[])v);
		}
		else if(v instanceof Map<?, ?>)
		{
			Map<?, ?> m = (Map<?, ?>)v;
			//
		}
		else if(v instanceof Collection<?>)
		{
			Collection<?> c = (Collection<?>)v;
			Tag[] tags = new Tag[c.size()];
			int i = 0;
			Tag.Type type = null;
			for(Object o : c)
			{
				tags[i] = objectToNBT(null, o);
				if(i == 0)
				{
					type = tags[i].Type();
				}
				else if(type != tags[i].Type())
				{
					type = null;
				}
				++i;
			}
			if(type == null)
			{
				//
			}
			else
			{
				return new Tag.List(name, type, tags);
			}
		}
		return new Tag.String(name, v.toString());
	}
}
