package com.lb_stuff.mcmodify.test.minecraft;

import com.lb_stuff.mcmodify.minecraft.CompressionScheme;
import com.lb_stuff.mcmodify.minecraft.Inventory;
import com.lb_stuff.mcmodify.minecraft.Level;
import com.lb_stuff.mcmodify.nbt.Tag;
import com.lb_stuff.mcmodify.test.TestingUtils;

import static com.lb_stuff.mcmodify.minecraft.IDs.WrittenBook;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LevelTest
{
	@Test
	public void general() throws Throwable
	{
		final Level level;
		try(FileInputStream fis = new FileInputStream(TestingUtils.getInputFile("level.dat")))
		{
			level = new Level((Tag.Compound)Tag.deserialize(CompressionScheme.GZip.getInputStream(fis)));
		}

		Level.Player player = level.Player();
		Inventory i = player.Inventory();
		Inventory.Item item = new Inventory.Item(WrittenBook, 0, 1);
		item.Title("Ethonian Battle Book");
		item.Author("Vechs");
		item.Pages().add("§4As you hold this book, you feel the power to §lincenerate§r§4 your enemies!");
		item.EnchantLevel(Inventory.Item.Enchantment.FireAspect, (short)2);
		i.Item(7, item);

		try(FileOutputStream fos = new FileOutputStream(TestingUtils.getOutputFile("level.dat")))
		{
			level.ToNBT("").serialize(CompressionScheme.GZip.getOutputStream(fos));
		}
	}
}
