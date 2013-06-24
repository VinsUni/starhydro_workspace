package star.hydro.filemanager.common;

import java.io.Serializable;

import javax.swing.Icon;

public class ProcessedMapInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	String description = null;
	Icon icon = null;
	String name = null;

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Icon getIcon()
	{
		return icon;
	}

	public void setIcon(Icon icon)
	{
		this.icon = icon;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

}
