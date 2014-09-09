package com.lb_stuff.mcmodify.test.nbt;

import com.lb_stuff.mcmodify.minecraft.CompressionScheme;
import com.lb_stuff.mcmodify.nbt.Tag;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TagTests
{
	@Test
	public void manualTimeAdjust() throws Throwable
	{
		final Tag.Compound level;
		try(FileInputStream fis = new FileInputStream("level.dat.in"))
		{
			level = (Tag.Compound)Tag.deserialize(CompressionScheme.GZip.getInputStream(fis));
		}

		System.out.println(level);

		Tag.Compound data = (Tag.Compound)level.get("Data");
		Tag.Long Time = (Tag.Long)data.find(Tag.Type.LONG, "Time");
		Time.v = 0L;

		try(FileOutputStream fos = new FileOutputStream("level.dat.out"))
		{
			level.serialize(CompressionScheme.GZip.getOutputStream(fos));
		}
	}
}
