package star.hydro.filemanager.common;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.mit.star.plugins.filemanager.interfaces.AbstractFileProvider;
import edu.mit.star.plugins.filemanager.interfaces.AccessoryProvider;
import edu.mit.star.plugins.filemanager.interfaces.ProjectProvider;

public abstract class ProcessedMapArchive extends AbstractFileProvider implements ProjectProvider, AccessoryProvider
{
	protected String name = null;

	public ProcessedMapArchive(File parent, String file)
	{
		super(parent, file);
		this.name = file;
	}

	public JComponent getAccessory()
	{
		return new JPanel();
	}

	public String getTypeDescription()
	{
		return null;
	}

}
