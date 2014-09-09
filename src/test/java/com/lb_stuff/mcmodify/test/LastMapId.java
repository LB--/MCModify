package com.lb_stuff.mcmodify.test;

import com.lb_stuff.mcmodify.minecraft.CompressionScheme;
import com.lb_stuff.mcmodify.nbt.Tag;

import java.io.FileInputStream;

public class LastMapId
{
	public static void main(String[] args) throws Throwable
	{
		Tag.Compound idcounts;
		try(FileInputStream fis = new FileInputStream("idcounts.dat.in"))
		{
			idcounts = (Tag.Compound)Tag.deserialize(CompressionScheme.None.getInputStream(fis));
		}
		System.out.println("Last created map number: "+((Tag.Short)idcounts.find(Tag.Type.SHORT, "map")).v);
	}
}
