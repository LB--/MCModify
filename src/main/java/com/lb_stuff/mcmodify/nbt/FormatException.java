package com.lb_stuff.mcmodify.nbt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The generic "this data is wonky" exception.
 */
public class FormatException extends IOException
{
	/**
	 * The tag(s) that caused the problem, if any.
	 */
	private List<Tag> t = new ArrayList<>();

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
