/**
 * 
 */
package app.server.worker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyInputStream extends InputStream
{
	OutputStream os;
	InputStream is;

	public CopyInputStream(InputStream is, OutputStream os)
	{
		this.os = os;
		this.is = is;
	}

	public int available() throws IOException
	{
		return is.available();
	}

	@Override
	public void close() throws IOException
	{
		super.close();
		is.close();
		os.close();
	}

	@Override
	public boolean markSupported()
	{
		return false;
	}

	@Override
	public int read() throws IOException
	{
		int ret = is.read();
		if (ret != -1)
		{
			os.write(ret);
		}
		return ret;
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		int ret = is.read(b);
		if (ret != -1)
		{
			os.write(b, 0, ret);
		}
		return ret;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		int ret = is.read(b, off, len);
		if (ret != -1)
		{
			os.write(b, off, ret);
		}
		return ret;
	}

	@Override
	public long skip(long n) throws IOException
	{
		int counter = 0;
		while (n > counter)
		{
			int b = is.read();
			if (b != -1)
			{
				os.write(b);
				counter++;
			}
			else
			{
				break;
			}
		}
		return counter;
	}
}