package edu.mit.star.plugins.filemanager.interfaces;

import java.io.File;

public abstract class AbstractFileProvider extends java.io.File
{
	File parent;
	long lastModified = 0;
	long length = Long.MIN_VALUE;

	public AbstractFileProvider(File parent, String file)
	{
		super(parent, file);
		this.parent = parent;
	}

	public java.io.File getParentFile()
	{
		return parent;
	}

	public String[] list()
	{
		File[] file = listFiles();
		String[] ret = new String[file.length];
		for (int i = 0; i < file.length; i++)
		{
			ret[i] = file[i].getName();
		}
		return ret;
	}

	public boolean setLastModified(long time)
	{
		lastModified = time;
		return true;
	}

	public long lastModified()
	{
		return lastModified;
	}

	public long length()
	{
		if (length != Long.MIN_VALUE)
		{
			return length;
		}
		else
		{
			return super.length();
		}
	}

	public void setLength(long length)
	{
		this.length = length;
	}

}
