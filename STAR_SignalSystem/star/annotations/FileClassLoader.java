package star.annotations;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class FileClassLoader extends ClassLoader
{

	private String root;
	HashMap<String, Long> loadedClasses = new HashMap<String, Long>();

	public FileClassLoader(String rootDir, ClassLoader parent)
	{
		super(parent);
		if( rootDir == null )
			throw new IllegalArgumentException("Null root directory");
		root = rootDir;
	}

	@SuppressWarnings("unchecked")
	protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException
	{

		Class c = findLoadedClass(name);
		if( c == null )
		{
			try
			{
				c = findSystemClass(name);
			}
			catch( Exception e )
			{
			}
		}

		if( c == null )
		{
			try
			{
				c = getParent().loadClass(name);
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}

		if( c == null )
		{
			// Convert class name argument to filename
			// Convert package names into subdirectories
			String filename = name.replace('.', File.separatorChar) + ".class";
			try
			{
				loadedClasses.put(name, (new File(root, filename)).lastModified());
				byte data[] = loadClassData(filename);
				c = defineClass(name, data, 0, data.length);
				if( c == null )
					throw new ClassNotFoundException(name);
			}
			catch( IOException e )
			{
				throw new ClassNotFoundException("Error reading file: " + root + " " + filename);
			}
		}
		if( resolve )
			resolveClass(c);
		return c;
	}

	public boolean hasChanged(String name)
	{
		Long loadedTimestamp = loadedClasses.get(name);
		if( loadedTimestamp != null )
		{
			String filename = name.replace('.', File.separatorChar) + ".class";
			long timestamp = (new File(root, filename)).lastModified();
			return loadedTimestamp.longValue() == timestamp;
		}
		else
		{
			return true;
		}
	}

	private byte[] loadClassData(String filename) throws IOException
	{

		// Create a file object relative to directory provided
		File f = new File(root, filename);

		// Get size of class file
		int size = (int) f.length();

		// Reserve space to read
		byte buff[] = new byte[size];

		// Get stream to read from
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);

		// Read in data
		dis.readFully(buff);

		// close stream
		dis.close();

		// return data
		return buff;
	}
}