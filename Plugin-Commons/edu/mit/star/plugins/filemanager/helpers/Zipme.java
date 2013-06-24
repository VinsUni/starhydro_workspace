package edu.mit.star.plugins.filemanager.helpers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.mit.star.plugins.filemanager.interfaces.ExportableEntry;
import edu.mit.star.plugins.filemanager.interfaces.ProjectException;

public class Zipme
{

	public Zipme()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public static void write(OutputStream zipStream, ExportableEntry[] entries) throws ProjectException, IOException
	{
		CheckedOutputStream checksum = new CheckedOutputStream(zipStream, new Adler32());
		ZipOutputStream out = new ZipOutputStream(checksum);
		final int BUFFER = 64 * 1024;
		byte[] data = new byte[BUFFER];
		for (int i = 0; i < entries.length; i++)
		{
			InputStream is = new BufferedInputStream(entries[i].getSource(), BUFFER);
			ZipEntry entry = new ZipEntry(entries[i].getSourceName());
			out.putNextEntry(entry);
			int count;
			while ((count = is.read(data, 0, BUFFER)) != -1)
			{
				out.write(data, 0, count);
			}
			is.close();
		}
		out.flush();
		out.close();
		checksum.close();
	}
}
