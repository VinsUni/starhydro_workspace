package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class FileUtils
{
	private final static int BUFFER_SIZE = 1024 * 1024;

	public static byte[] getStreamToByteArray(InputStream is) throws IOException
	{
		ArrayList<byte[]> data = new ArrayList<byte[]>();
		byte[] bytes;
		int offset = 0;
		int numRead = 0;
		int total_length = 0;
		do
		{
			offset = 0 ;
			bytes = new byte[BUFFER_SIZE];
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
			{
				offset += numRead;
			}
			total_length += offset;
			data.add(bytes);
		} while (numRead != -1);
		is.close();

		byte[] ret = new byte[total_length];
		for (int i = 0; i < data.size(); i++)
		{
			bytes = data.get(i);
			System.arraycopy(bytes, 0, ret, BUFFER_SIZE * i, ((i != (data.size() - 1)) ? bytes.length : offset));
		}
		return ret;
	}

	public static void copy(InputStream is, OutputStream os, int buffer_size) throws IOException
	{
		byte[] buffer = new byte[buffer_size];
		int len = 0;
		do
		{
			if ((len = is.read(buffer)) != -1)
			{
				os.write(buffer, 0, len);
			}
		} while (len != -1);
		os.flush();
	}

	public static void copy(InputStream is, OutputStream os) throws IOException
	{
		copy(is, os, BUFFER_SIZE);
	}

	public static byte[] deflate(byte[] bytes)
	{
		Deflater d = new Deflater();
		d.setInput(bytes);
		d.finish();
		byte[] compressed = new byte[1024 * 1024 * 4];
		int compressed_length = d.deflate(compressed);
		byte[] data = new byte[compressed_length];
		System.arraycopy(compressed, 0, data, 0, compressed_length);
		return data;

	}

	public static byte[] inflate(byte[] data) throws DataFormatException
	{
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		inflater.finished();
		byte[] ret = new byte[inflater.getRemaining()];
		int len = inflater.inflate(ret);
		return ret;
	}
}
