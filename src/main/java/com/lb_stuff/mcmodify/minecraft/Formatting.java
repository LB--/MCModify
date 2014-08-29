package com.lb_stuff.mcmodify.minecraft;

import java.awt.Color;

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
}
