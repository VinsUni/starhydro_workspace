package app.server.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class JNLPPersist implements PersistInterface
{

	public static File getWorkspace()
	{
		String home = System.getProperty("user.home");
		File f = new File(home, "/starhydro_workspace");
		boolean ok = f.exists();
		if (!ok)
		{
			ok = f.mkdirs();
		}
		return f;
	}

	File getFile(String url)
	{
		File f = null;
		File ws = getWorkspace();
		if (ws != null)
		{
			String name = url.replace(File.pathSeparatorChar, '_').replace(File.separatorChar, '_').replace('/', '_').replace('\\','_');
			f = new File(ws, name);
			f.getParentFile().mkdirs();
		}
		return f;
	}

	public void clearWorkspace()
	{
		File f = getWorkspace();
		removeDir(f);
	}

	public void removeDir(File fIn)
	{
		int i;
		File f;
		String[] as;

		if (fIn.isDirectory()) // If it is a directory..
		{
			as = fIn.list(); // ....Get its file names

			for (i = 0; i < as.length; i++)
			{ // ......For every file name
				f = new File(fIn, as[i]); // ........Create file obj
				removeDir(f); // ..........and process it.
			}

			System.out.println("Remove " + fIn + " directory");
			fIn.delete();
			return;
		}

		System.out.println("Remove " + fIn + " file");
		fIn.delete();
		return;
	}

	public void save(String url, Object obj) throws IOException
	{
		try
		{
			File file = getFile(url);
			FileOutputStream fos = new FileOutputStream(file, false);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void save(String url, String obj) throws IOException
	{
		try
		{
			File file = getFile(url);
			FileOutputStream fos = new FileOutputStream(file, false);
			fos.write(obj.getBytes());
			fos.flush();
			fos.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public Object load(String url) throws IOException
	{
		Object ret = null;
		try
		{
			File file = getFile(url);
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream oos = new ObjectInputStream(fis);
			ret = oos.readObject();
			oos.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return ret;
	}

	public InputStream openStream(String url) throws IOException
	{
		File file = getFile(url);
		FileInputStream fis = new FileInputStream(file);
		return fis;
	}

	public OutputStream saveStream(String url) throws IOException
	{
		File file = getFile(url + ".tmp");
		FileOutputStream fos = new FileOutputStream(file);
		return fos;
	}

	public void commitStream(String url) throws IOException
	{
		File file_tmp = getFile(url + ".tmp");
		File file = getFile(url);
		file.delete();
		file_tmp.renameTo(file);
	}

}
