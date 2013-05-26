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

package NBT;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The generic "this data is wonky" exception.
 * @author LB
 */
public class FormatException extends Exception
{
	/**
	 * The tag(s) that caused the problem, if any.
	 */
	List<Tag> t = new ArrayList<>();

	/**
	 * Instantiates this exception with the given message.
	 * @param msg The message to be seen with this exception.
	 * @param tags The tag(s) (if any) that were malformed.
	 */
	public FormatException(String msg, Tag... tags)
	{
		super(msg);
		t.addAll(Arrays.asList(tags));
	}
	/**
	 * Instantiates this exception with the given message and cause.
	 * @param msg The message to be seen with this exception.
	 * @param cause The exception that caused the trouble in the first place.
	 * @param tags The tag(s) (if any) that were malformed.
	 */
	public FormatException(String msg, Throwable cause, Tag... tags)
	{
		super(msg, cause);
		t.addAll(Arrays.asList(tags));
	}
	/**
	 * Instantiates this exception with just the causing exception.
	 * @param cause The exception that caused the trouble in the first place.
	 * @param tags The tag(s) (if any) that were malformed.
	 */
	public FormatException(Throwable cause, Tag... tags)
	{
		super(cause);
		t.addAll(Arrays.asList(tags));
	}

	/**
	 * Returns the tag(s) (if any) that were malformed.
	 * @return The tag(s) (if any) that were malformed.
	 */
	public Tag[] Tags()
	{
		return t.toArray(new Tag[0]);
	}
	/**
	 * Adds tags to the list of the malformed tags.
	 * @param tags The tags that were malformed.
	 */
	public void Add(Tag... tags)
	{
		t.addAll(Arrays.asList(tags));
	}
}
