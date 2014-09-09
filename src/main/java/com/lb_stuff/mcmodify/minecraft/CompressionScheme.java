package com.lb_stuff.mcmodify.minecraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public enum CompressionScheme
{
	None,
	GZip,
	Zlib,
	;

	public static CompressionScheme fromId(byte id)
	{
		switch(id)
		{
			case 1: return GZip;
			case 2: return Zlib;
			default: return null;
		}
	}
	public byte getId()
	{
		switch(this)
		{
			case None: throw new IllegalArgumentException("There is no Id for compression scheme " + this);
			case GZip: return 1;
			case Zlib: return 2;
			default: throw new IllegalStateException();
		}
	}

	public InputStream getInputStream(InputStream original) throws IOException
	{
		switch(this)
		{
			case None: return original;
			case GZip: return new GZIPInputStream(original);
			case Zlib: return new InflaterInputStream(original);
			default: throw new IllegalStateException();
		}
	}
	public OutputStream getOutputStream(OutputStream original) throws IOException
	{
		switch(this)
		{
			case None: return original;
			case GZip: return new GZIPOutputStream(original);
			case Zlib: return new DeflaterOutputStream(original);
			default: throw new IllegalStateException();
		}
	}
}
