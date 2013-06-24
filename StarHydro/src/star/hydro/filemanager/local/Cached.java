package star.hydro.filemanager.local;

import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JComponent;

import star.hydro.filemanager.common.ProcessedMapProject;
import app.server.worker.JNLPPersist;
import edu.mit.star.plugins.filemanager.helpers.Folder;
import edu.mit.star.plugins.filemanager.interfaces.AbstractFileProvider;
import edu.mit.star.plugins.filemanager.interfaces.AccessoryProvider;

public class Cached extends Folder implements AccessoryProvider
{

	CachedAccessory accessory = null;
	private static final long serialVersionUID = 1L;

	public Cached(AbstractFileProvider parent, String name)
	{
		super(parent, name);
	}

	public File[] listFiles()
	{
		ArrayList<File> list = new ArrayList<File>();
		for (File f : JNLPPersist.getWorkspace().listFiles())
		{
			String name = f.getName();
			if (name.endsWith(ProcessedMapProject.SUFFIX))
			{
				list.add(new ProcessedMapArchive(this, name.substring(0, name.length() - ProcessedMapProject.SUFFIX.length())));
			}
		}
		return list.toArray(new File[0]);
	}

	public JComponent getAccessory()
	{
		if (accessory == null)
		{
			accessory = new CachedAccessory(this);
		}
		return accessory;
	}

	public void reload()
	{
		propertyChangeSupport.firePropertyChange(ADD_CHILD, null, null);
	}

	public Icon getIcon()
	{
		return utils.Icon.getIcon(this, utils.Icon.WORLD_JAR);
	}

}
