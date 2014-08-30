package com.lb_stuff.mcmodify.minecraft;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

/**
 * @see <a href="http://minecraft.gamepedia.com/Formatting_codes">Formatting codes</a> on the Minecraft Wiki
 */
public enum Formatting
{
	BLACK       ('0', new Color(  0,  0,  0), new Color(  0,  0,  0)),
	DARK_BLUE   ('1', new Color(  0,  0,170), new Color(  0,  0, 42)),
	DARK_GREEN  ('2', new Color(  0,170,  0), new Color(  0, 42,  0)),
	DARK_AQUA   ('3', new Color(  0,170,170), new Color(  0, 42, 42)),
	DARK_RED    ('4', new Color(170,  0,  0), new Color( 42,  0,  0)),
	DARK_PURPLE ('5', new Color(170,  0,170), new Color( 42,  0, 42)),
	GOLD        ('6', new Color(255,170,  0), new Color( 42, 42,  0)), //maybe 63,42,0?
	GRAY        ('7', new Color(170,170,170), new Color( 42, 42, 42)),
	DARK_GRAY   ('8', new Color( 85, 85, 85), new Color( 21, 21, 21)),
	BLUE        ('9', new Color( 85, 85,255), new Color( 21, 21, 63)),
	GREEN       ('a', new Color( 85,255, 85), new Color( 21, 63, 21)),
	AQUA        ('b', new Color( 85,255,255), new Color( 21, 63, 63)),
	RED         ('c', new Color(255, 85, 85), new Color( 63, 21, 21)),
	LIGHT_PURPLE('d', new Color(255, 85,255), new Color( 63, 21, 63)),
	YELLOW      ('e', new Color(255,255, 85), new Color( 63, 63, 21)),
	WHITE       ('f', new Color(255,255,255), new Color( 63, 63, 63)),

	OBFUSCATED   ('k', null, null),
	BOLD         ('l', null, null),
	STRIKETHROUGH('m', null, null),
	UNDERLINE    ('n', null, null),
	ITALIC       ('o', null, null),
	RESET        ('r', null, null),
	;

	public static final int NUMBER_OF_COLORS = 16;
	public static final String FORMAT_CHARACTER = "\u00A7";

	private final char code;
	private final Color foreground;
	private final Color background;
	private Formatting(char c, Color f, Color b)
	{
		code = c;
		foreground = f;
		background = b;
	}

	@Override
	public String toString()
	{
		return FORMAT_CHARACTER+code;
	}

	public String getScoreboardTeamName()
	{
		return name().toLowerCase();
	}

	public char getCode()
	{
		return code;
	}

	public boolean isColor()
	{
		return foreground != null;
	}
	public Color getForegroundColor()
	{
		return foreground;
	}
	public Color getBackgroundColor()
	{
		return background;
	}

	//TODO: Use a less hacky solution that doesn't involve a BufferedImage
	private static final IndexColorModel ficm;
	private static final BufferedImage fip;
	static
	{
		byte[] r = new byte[NUMBER_OF_COLORS],
		       g = new byte[NUMBER_OF_COLORS],
		       b = new byte[NUMBER_OF_COLORS],
		       a = new byte[NUMBER_OF_COLORS];
		for(int i = 0; i < NUMBER_OF_COLORS; ++i)
		{
			Color f = values()[i].getForegroundColor();
			r[i] = (byte)f.getRed();
			g[i] = (byte)f.getGreen();
			b[i] = (byte)f.getBlue();
			a[i] = (byte)f.getAlpha();
		}
		ficm = new IndexColorModel(8, NUMBER_OF_COLORS, r, g, b, a);
		fip = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED, ficm);
	}
	public static Formatting byNearestForeground(Color c)
	{
		fip.setRGB(0, 0, c.getRGB());
		Color t = new Color(fip.getRGB(0, 0));
		for(int i = 0; i < NUMBER_OF_COLORS; ++i)
		{
			if(values()[i].getForegroundColor().equals(t))
			{
				return values()[i];
			}
		}
		throw new IllegalStateException();
	}
	private static final IndexColorModel bicm;
	private static final BufferedImage bip;
	static
	{
		byte[] r = new byte[NUMBER_OF_COLORS],
		       g = new byte[NUMBER_OF_COLORS],
		       b = new byte[NUMBER_OF_COLORS],
		       a = new byte[NUMBER_OF_COLORS];
		for(int i = 0; i < NUMBER_OF_COLORS; ++i)
		{
			Color f = values()[i].getBackgroundColor();
			r[i] = (byte)f.getRed();
			g[i] = (byte)f.getGreen();
			b[i] = (byte)f.getBlue();
			a[i] = (byte)f.getAlpha();
		}
		bicm = new IndexColorModel(8, NUMBER_OF_COLORS, r, g, b, a);
		bip = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED, bicm);
	}
	public static Formatting byNearestBackground(Color c)
	{
		bip.setRGB(0, 0, c.getRGB());
		Color t = new Color(bip.getRGB(0, 0));
		for(int i = 0; i < NUMBER_OF_COLORS; ++i)
		{
			if(values()[i].getBackgroundColor().equals(t))
			{
				return values()[i];
			}
		}
		throw new IllegalStateException();
	}
}
