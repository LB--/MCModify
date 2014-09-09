package com.lb_stuff.mcmodify.test;

import com.lb_stuff.mcmodify.minecraft.CompressionScheme;
import com.lb_stuff.mcmodify.minecraft.Map;
import com.lb_stuff.mcmodify.nbt.Tag;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MapGeneral
{
	public static void main(String[] args) throws Throwable
	{
		final Map map;
		try(FileInputStream fis = new FileInputStream("map.dat.in"))
		{
			map = new Map((Tag.Compound)Tag.deserialize(CompressionScheme.GZip.getInputStream(fis)));
		}

		BufferedImage mapimage = map.Image();
		ImageIO.write(mapimage, "png", new File("map.png.out"));

		Graphics2D g = mapimage.createGraphics();
		g.setColor(Color.red);
		g.drawString("My Minecraft Map", 16, 16);

		try(FileOutputStream fos = new FileOutputStream("map.dat.out"))
		{
			map.ToNBT("").serialize(CompressionScheme.GZip.getOutputStream(fos));
		}
	}
}
