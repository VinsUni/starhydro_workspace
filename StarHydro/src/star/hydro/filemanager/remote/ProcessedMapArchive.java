package star.hydro.filemanager.remote;

import javax.swing.Icon;

import star.hydro.filemanager.common.ProcessedMapInfo;
import edu.mit.star.plugins.filemanager.helpers.Project;
import edu.mit.star.plugins.filemanager.interfaces.ProjectProvider;

public class ProcessedMapArchive extends star.hydro.filemanager.common.ProcessedMapArchive implements ProjectProvider
{
	private static final long serialVersionUID = 1L;
	private Server server = null;

	public ProcessedMapArchive(Server parent, String file)
	{
		super(parent, file);
		this.server = parent;
	}

	public Project getParsedObject()
	{
		ProcessedMapProject project = new ProcessedMapProject();
		ProcessedMapInfo info = new ProcessedMapInfo();
		info.setDescription("Dummy description");
		info.setName(name);
		info.setIcon(getIcon());
		project.setCachedInfo(info);
		project.prefix = server.urlPrefix;
		project.name = name + star.hydro.filemanager.common.ProcessedMapProject.SUFFIX;
		return project;
	}

	public Icon getIcon()
	{
		return utils.Icon.getIcon(this, utils.Icon.WORLD);
	}

}
