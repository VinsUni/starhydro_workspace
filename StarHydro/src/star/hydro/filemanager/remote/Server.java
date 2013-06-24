package star.hydro.filemanager.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.Icon;

import star.hydro.filemanager.common.ProcessedMapProject;
import edu.mit.star.plugins.filemanager.helpers.Folder;
import edu.mit.star.plugins.filemanager.interfaces.AbstractFileProvider;

public class Server extends Folder
{
	private static final long serialVersionUID = 1L;
	String urlPrefix = null;

	public Server(AbstractFileProvider parent, String name, String urlPrefix)
	{
		super(parent, name);
		this.urlPrefix = urlPrefix;
		try
		{
			addChildren();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	void addChildren() throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader((new URL(urlPrefix + "/index.txt")).openStream()));
		String name;
		while ((name = reader.readLine()) != null)
		{
			if (name.endsWith(ProcessedMapProject.SUFFIX))
			{
				addChild(new ProcessedMapArchive(this, name.substring(0, name.length() - ProcessedMapProject.SUFFIX.length())));
			}
		}
		reader.close();

	}

	public Icon getIcon()
	{
		return utils.Icon.getIcon(this, utils.Icon.WORLD);
	}

}
