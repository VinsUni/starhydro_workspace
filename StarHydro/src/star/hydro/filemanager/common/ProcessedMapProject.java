package star.hydro.filemanager.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.swing.Icon;

import star.annotations.SignalComponent;
import star.swing.events.ProgressEvent;
import star.swing.events.ProgressRaiser;
import edu.mit.star.plugins.filemanager.helpers.Project;
import edu.mit.star.plugins.filemanager.interfaces.ProjectException;

@SignalComponent(raises = ProgressRaiser.class)
public abstract class ProcessedMapProject extends ProcessedMapProject_generated implements Project
{
	public final static String SUFFIX = ".shm";
	public final static String TYPE = "change_map";
	ProcessedMapInfo info = null;
	int value = 0;
	String entryName = null;

	public Object getCachedInfo()
	{
		return info;
	}

	public void setCachedInfo(ProcessedMapInfo info)
	{
		this.info = info;
	}

	public String getDescription()
	{
		return info.getDescription();
	}

	public Icon getIcon()
	{
		return info.getIcon();
	}

	public String getName()
	{
		return info.getName();
	}

	public Object getParsed() throws ProjectException
	{
		try
		{
			value = 0;
			preGetParsed();
			MapData data = new MapData();
			JarInputStream jis = new JarInputStream(getSource());
			JarEntry entry;
			while ((entry = jis.getNextJarEntry()) != null)
			{
				System.out.println("Entry: " + entry.getName() + " " + entry.isDirectory() + " " + entry.getCompressedSize());
				if (!entry.isDirectory())
				{
					int len = 0;
					entryName = entry.getName();
					(new ProgressEvent(this)).raise();
					BufferedInputStream bis = new BufferedInputStream(jis);
					while (bis.read() != -1)
					{
						len++;
					}
					value += 20;
					if (Thread.currentThread().isInterrupted())
					{
						return null;
					}
					(new ProgressEvent(this)).raise();
					data.put(entry.getName(), "" + len);
					System.out.println("Entry: " + entry.getName() + " " + entry.isDirectory() + " " + len);
				}
				jis.closeEntry();
			}
			jis.close();
			postGetParsed(true);
			value = 100;
			entryName = null;
			(new ProgressEvent(this)).raise();
			return data;
		}
		catch (IOException e)
		{
			ProjectException ex = new ProjectException();
			ex.initCause(e);
			postGetParsed(false);
			throw ex;
		}
	}

	protected abstract void preGetParsed();

	protected abstract void postGetParsed(boolean success);

	public String getSourceName()
	{
		return null;
	}

	public String getType()
	{
		return TYPE;
	}

	public int getValue()
	{
		return value;
	}

	public String getString()
	{
		return entryName;
	}

}
