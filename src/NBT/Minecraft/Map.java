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

package NBT.Minecraft;

import NBT.Tag;
import NBT.FormatException;
import java.awt.image.IndexColorModel;
import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * Maps - the in-game item
 * @author LB
 */
public class Map
{
	/**
	 * Size of the map, default 128x128.
	 */
	private short width, height;
	/**
	 * The zoom level, 2^scale square blocks per pixel.
	 */
	private byte scale;
	/**
	 * The center of the map (where it was created).
	 */
	private int xcenter, zcenter;
	/**
	 * The dimension the map shows terrain from.
	 */
	private Level.Player.Dimension dimension;
	/**
	 * The colors for maps.
	 */
	private static final Color[] MapColors;
	/**
	 * The Color Model for map colors.
	 */
	private static final IndexColorModel MapColorModel;
	static
	{
		final Color[] BaseMapColors = new Color[]
		{
			new Color(0, 0, 0, 0),
			new Color(127, 178, 56),
			new Color(247, 233, 163),
			new Color(167, 167, 167),
			new Color(255, 0, 0),
			new Color(160, 160, 255),
			new Color(167, 167, 167),
			new Color(0, 124, 0),
			new Color(255, 255, 255),
			new Color(164, 168, 184),
			new Color(183, 106, 47),
			new Color(112, 112, 112),
			new Color(64, 64, 255),
			new Color(104, 83, 50)
		};
		MapColors = new Color[BaseMapColors.length*4];
		for(int i = 0; i < BaseMapColors.length; ++i)
		{
			Color bc = BaseMapColors[i];
			MapColors[i*4 +0] = new Color((int)(bc.getRed()*180.0/255.0+0.5), (int)(bc.getGreen()*180.0/255.0+0.5), (int)(bc.getBlue()*180.0/255.0+0.5), bc.getAlpha());
			MapColors[i*4 +2] = bc;
			MapColors[i*4 +1] = MapColors[i*4 +3] = new Color((int)(bc.getRed()*220.0/255.0+0.5), (int)(bc.getGreen()*220.0/255.0+0.5), (int)(bc.getBlue()*220.0/255.0+0.5), bc.getAlpha());
		}
		byte[] r = new byte[MapColors.length],
			   g = new byte[MapColors.length],
			   b = new byte[MapColors.length],
			   a = new byte[MapColors.length];
		for(int i = 0; i < MapColors.length; ++i)
		{
			Color mc = MapColors[i];
			r[i] = (byte)mc.getRed();
			g[i] = (byte)mc.getGreen();
			b[i] = (byte)mc.getBlue();
			a[i] = (byte)mc.getAlpha();
		}
		MapColorModel = new IndexColorModel(8, MapColors.length, r, g, b, a);
	}
	/**
	 * The map data.
	 */
	private BufferedImage colors;

	/**
	 * Constructs a Map from the given tag.
	 * @param map The tag from which to construct this map.
	 * @throws FormatException If the given tag is invalid.
	 */
	public Map(Tag.Compound map) throws FormatException
	{
		map = (Tag.Compound)map.Find(Tag.Type.COMPOUND, "data");
		width = ((Tag.Short)map.Find(Tag.Type.SHORT, "width")).v;
		height = ((Tag.Short)map.Find(Tag.Type.SHORT, "height")).v;
		scale = ((Tag.Byte)map.Find(Tag.Type.BYTE, "scale")).v;
		dimension = Level.Player.Dimension.FromID(((Tag.Byte)map.Find(Tag.Type.BYTE, "dimension")).v);
		xcenter = ((Tag.Int)map.Find(Tag.Type.INT, "xCenter")).v;
		zcenter = ((Tag.Int)map.Find(Tag.Type.INT, "zCenter")).v;

		byte[] data = ((Tag.ByteArray)map.Find(Tag.Type.BYTEARRAY, "colors")).v;
		colors = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, MapColorModel);
		for(int i = 0; i < width; ++i)
		{
			for(int j = 0; j < height; ++j)
			{
				Color c = MapColors[data[i + j*width]];
				colors.setRGB(i, j, c.getRGB());
			}
		}
	}

	/**
	 * Returns the width of the map.
	 * @return The width of the map.
	 */
	public short Width()
	{
		return width;
	}
	/**
	 * Returns the height of the map.
	 * @return The height of the map.
	 */
	public short Height()
	{
		return height;
	}
	/**
	 * Returns the image for this map. Colors are automatically converted to map colors.
	 * @return The image for this map.
	 */
	public BufferedImage Image()
	{
		return colors;
	}
	/**
	 * Sets the width and height of the map and invalidates the image returned by <code>Image()</code>.
	 * @param w The new width of the map.
	 * @param h The new height of the map.
	 */
	public void Resize(short w, short h)
	{
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED, MapColorModel);
		for(int i = 0; i < width; ++i)
		{
			for(int j = 0; j < height; ++j)
			{
				img.setRGB(i, j, img.getRGB(i, j));
			}
		}
		width = w;
		height = h;
		colors = img;
	}

	/**
	 * Returns the scale for this map. The map shows 2^scale square blocks per pixel.
	 * @return The scale for this map. Default is 3.
	 */
	public byte Scale()
	{
		return scale;
	}
	/**
	 * Sets the scale for this map. The map will show 2^scale square blocks per pixel.
	 * @param zoom The scale for this map. Range is 0 to 4, inclusive.
	 */
	public void Scale(byte zoom)
	{
		scale = zoom;
	}

	/**
	 * Returns the dimension this map shows blocks from.
	 * @return The dimension this map shows blocks from.
	 */
	public Level.Player.Dimension Dimension()
	{
		return dimension;
	}
	/**
	 * Sets the dimension this map will show blocks from.
	 * @param dim The dimension this map will show blocks from.
	 */
	public void Dimension(Level.Player.Dimension dim)
	{
		dimension = dim;
	}

	/**
	 * Returns the X coordinate of the center of this map.
	 * @return The X coordinate of the center of this map.
	 */
	public int CenterX()
	{
		return xcenter;
	}
	/**
	 * Returns the Z coordinate of the center of this map.
	 * @return The Z coordinate of the center of this map.
	 */
	public int CenterZ()
	{
		return zcenter;
	}
	/**
	 * Sets the center coordinates of this map.
	 * @param x The X coordinate of the center of this map.
	 * @param z The Z coordinate of the center of this map.
	 */
	public void Center(int x, int z)
	{
		xcenter = x;
		zcenter = z;
	}

	/**
	 * Returns the tag for this Map.
	 * @param name The name the compound tag should have, or null if the compound tag should not have a name.
	 * @return The tag for this Map.
	 */
	public Tag.Compound ToNBT(String name)
	{
		byte[] data;
		Tag.Compound t = new Tag.Compound(name,
						 new Tag.Compound("data", new Tag.Short("width", width),
												  new Tag.Short("height", height),
												  new Tag.Byte("scale", scale),
												  new Tag.Byte("dimension", dimension.ID()),
												  new Tag.Int("xCenter", xcenter),
												  new Tag.Int("zCenter", zcenter),
												  new Tag.ByteArray("colors", data = new byte[width*height])));
		for(int i = 0; i < width; ++i)
		{
			for(int j = 0; j < height; ++j)
			{
				Color c = new Color(colors.getRGB(i, j));
				for(byte k = 0; k < MapColors.length; ++k)
				{
					if(c.equals(MapColors[k]))
					{
						data[i + j*width] = k;
						break;
					}
				}
			}
		}
		return t;
	}
}