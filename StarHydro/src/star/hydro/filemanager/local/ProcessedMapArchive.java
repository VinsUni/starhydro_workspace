package star.hydro.filemanager.local;

import javax.swing.Icon;

import star.hydro.filemanager.common.ProcessedMapInfo;
import edu.mit.star.plugins.filemanager.helpers.Project;

public class ProcessedMapArchive extends star.hydro.filemanager.common.ProcessedMapArchive
{
	private static final long serialVersionUID = 1L;
	Cached server = null;

	public ProcessedMapArchive(Cached parent, String file)
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
		project.name = name + star.hydro.filemanager.common.ProcessedMapProject.SUFFIX;
		return project;
	}

	public Icon getIcon()
	{
		return utils.Icon.getIcon(this, utils.Icon.WORLD_JAR);
	}
}