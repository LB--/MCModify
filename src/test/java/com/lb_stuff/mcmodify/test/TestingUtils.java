package com.lb_stuff.mcmodify.test;

import java.io.File;
import java.net.URISyntaxException;

public final class TestingUtils
{
	private TestingUtils()
	{
		throw new UnsupportedOperationException();
	}

	public static File getInputFile(String name)
	{
		try
		{
			return new File(TestingUtils.class.getResource("/"+name+".in").toURI());
		}
		catch(URISyntaxException e)
		{
			return null;
		}
	}
	public static File getOutputFile(String name)
	{
		return new File(name+".out");
	}
}
