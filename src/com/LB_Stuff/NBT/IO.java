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

package com.LB_Stuff.NBT;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The input/output class for this NBT package.
 * Like the java.lang.Math class, this class is final, uninstantiable, and both of its methods are static.
 * @author LB
 */
public final class IO
{
	/**
	 * Reads an NBT structure from a GZipped <code>InputStream</code>.
	 * @param i The <code>InputStream</code> to read the GZipped NBT structure from.
	 * @return The root compound tag of the NBT structure.
	 * @throws java.io.IOException if the input operation generates an exception.
	 * @throws NBT.IO.FormatException if the NBT format isn't quite right.
	 */
	public static Tag.Compound Read(InputStream i) throws java.io.IOException, FormatException
	{
		return ReadUncompressed(new GZIPInputStream(i));
	}
	/**
	 * Reads an NBT structure from an uncompressed <code>InputStream</code>.
	 * @param i The <code>InputStream</code> to read the uncompressed NBT structure from.
	 * @return The root compound tag of the NBT structure.
	 * @throws java.io.IOException if the input operation generates an exception.
	 * @throws NBT.IO.FormatException if the NBT format isn't quite right.
	 */
	public static Tag.Compound ReadUncompressed(InputStream i) throws java.io.IOException, FormatException
	{
		int ch = i.read();
		if(ch == Tag.Type.COMPOUND.ordinal())
		{
			return new Tag.Compound(new Tag.String(null, i).v, i);
		}
		else if(ch == -1)
		{
			throw new FormatException("Unexpected end of stream before reading root tag");
		}
		throw new FormatException("Root tag was not a Compound tag; tag ID was "+ch);
	}

	/**
	 * Writes an NBT structure in a GZipped format to an <code>OutputStream</code>.
	 * @param nbt The root compound tag of the NBT structure.
	 * @param o The <code>OutputStream</code> to write the GZipped NBT structure to.
	 * @throws java.io.IOException if the output operation generates an exception.
	 */
	public static void Write(Tag.Compound nbt, OutputStream o) throws java.io.IOException
	{
		GZIPOutputStream go = new GZIPOutputStream(o);
		WriteUncompressed(nbt, go);
		go.finish();
	}
	/**
	 * Writes an NBT structure in an uncompressed format to an <code>OutputStream</code>.
	 * @param nbt The root compound tag of the NBT structure.
	 * @param o The <code>OutputStream</code> to write the uncompressed NBT structure to.
	 * @throws java.io.IOException if the output operation generates an exception.
	 */
	public static void WriteUncompressed(Tag.Compound nbt, OutputStream o) throws java.io.IOException
	{
		nbt.Serialize(o);
	}

	/**
	 * The constructor that you won't be using.
	 * @throws UnsupportedOperationException always.
	 */
	private IO() throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}
}