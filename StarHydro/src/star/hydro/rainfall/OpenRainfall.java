package star.hydro.rainfall;

import java.awt.Component;
import java.io.File;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import plugin.Loader;
import plugin.PluginException;
import star.hydro.rainfall.local.Cached;
import star.hydro.rainfall.local.LocalTimeSeriesWrapper;
import edu.mit.star.common.filemanager.FileManagerPlugin;
import edu.mit.star.plugins.filemanager.helpers.FileSystemView;
import edu.mit.star.plugins.filemanager.helpers.Project;
import edu.mit.star.plugins.filemanager.interfaces.FileMassager;

public class OpenRainfall
{
	private Properties properties = new Properties();
	private File[] roots = null;

	public OpenRainfall()
	{
		super();
		roots = new File[0];
		File cached = new Cached(null, "Sample functions");
		addRoot(cached);
		setDefaultDirectory(cached);
	}

	private File defaultDirectory;

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
		return new FileMassager()
		{

			public File[] processFileArray(File[] array)
			{
				if (array != null)
				{
					for (int i = 0; i < array.length; i++)
					{
						if (array[i] != null && !(array[i] instanceof Project))
						{
							array[i] = new LocalTimeSeriesWrapper(array[i], component);
						}
					}
				}
				return array;
			}

		};
	}

	Component component;

	public Project getProject(Component parent)
	{
		this.component = parent;
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
			if (ret2 instanceof Project)
			{
				ret = (Project) ret2;
			}
			else
			{
				StringBuilder sb = new StringBuilder();
				sb.append("<html><b>Invalid file format.</b><hr> Supported format is:<br>");
				sb.append("first line should be: #StarHydro simple time series<br>");
				sb.append("all other lines are: Hour Precipitation<br>");
				sb.append("all other lines are: Hour Precipitation<hr>");
				sb.append("Example:<br>");
				sb.append("#StarHydro simple time series<br>");
				sb.append("0 12<br>");
				sb.append("1 6<br>");
				sb.append("2 3<br>");
				sb.append("<br></html>");
				JOptionPane.showMessageDialog(parent, new JLabel(sb.toString()));
			}
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

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Open");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		OpenRainfall map = new OpenRainfall();
		f.pack();
		f.setVisible(true);
		map.getProject(f);
	}

}
