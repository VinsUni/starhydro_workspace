package star.hydro.filemanager;

import java.awt.Component;
import java.io.File;
import java.util.Properties;

import plugin.Loader;
import plugin.PluginException;
import star.hydro.filemanager.local.Cached;
import star.hydro.filemanager.remote.Server;
import edu.mit.star.common.filemanager.FileManagerPlugin;
import edu.mit.star.plugins.filemanager.helpers.FileSystemView;
import edu.mit.star.plugins.filemanager.helpers.Project;
import edu.mit.star.plugins.filemanager.interfaces.FileMassager;

public class OpenMap
{
	Properties properties = new Properties();
	File[] roots = null;

	public OpenMap()
	{
		super();
		roots = new File[0];
		File cached = new Cached(null, "Cached data");
		File server = new Server(null, "Star App", "http://starapp.mit.edu/star/hydro/datasets/");
		addRoot(server);
		addRoot(cached);
		setDefaultDirectory(cached);
	}

	File defaultDirectory;

	public File getDefaultDirectory()
	{
		return defaultDirectory;
	}

	public void setDefaultDirectory(File defaultDirectory)
	{
		this.defaultDirectory = defaultDirectory;
	}

	public File[] getRoots()
	{
		return roots;
	}

	public void addRoot(File root)
	{
		File[] ra = new File[roots.length + 1];
		System.arraycopy(roots, 0, ra, 1, roots.length);
		ra[0] = root;
		roots = ra;
	}

	public FileMassager getFileMassager()
	{
		return null;
	}

	public Project getProject(Component parent)
	{
		Project ret = null;
		try
		{
			FileSystemView view = new FileSystemView();
			view.setRoots(getRoots());
			boolean currDirSet = false;
			if (!currDirSet && getDefaultDirectory() != null)
			{
				view.setDefaultDirectory(getDefaultDirectory());
			}
			System.out.println("Default directory: " + view.getDefaultDirectory());
			view.setAbstractFileFactory(getFileMassager());

			FileManagerPlugin fm = (FileManagerPlugin) (Loader.getDefaultLoader().getPlugin(FileManagerPlugin.class.getName(), "edu.mit.star.common.filemanager.FileManager"));

			Object ret2 = fm.open(parent, view, properties);
			ret = (Project) ret2;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		catch (PluginException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

}
