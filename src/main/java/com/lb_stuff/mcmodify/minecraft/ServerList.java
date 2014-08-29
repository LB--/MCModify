package com.lb_stuff.mcmodify.minecraft;

import java.io.File;

/**
 * @see <a href="http://minecraft.gamepedia.com/Servers.dat_format">Servers.dat format</a> on the Minecraft Wiki
 */
public class ServerList
{
	private final File dir;
	public ServerList(File mcdir)
	{
		if(mcdir.isDirectory())
		{
			dir = mcdir;
			return;
		}
		else if(mcdir.isFile() && mcdir.getName().equalsIgnoreCase("servers.dat"))
		{
			dir = mcdir.getParentFile();
			return;
		}
		throw new IllegalArgumentException("\"mcdir\" must be the Minecraft directory or servers.dat file");
	}

	//
}
