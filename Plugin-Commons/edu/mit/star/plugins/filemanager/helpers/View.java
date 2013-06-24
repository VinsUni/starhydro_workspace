package edu.mit.star.plugins.filemanager.helpers;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;

import edu.mit.star.plugins.filemanager.interfaces.AccessoryProvider;
import edu.mit.star.plugins.filemanager.interfaces.NotTraversable;

public class View extends FileView
{
	JFileChooser fc;

	public View(JFileChooser fc)
	{
		super();
		this.fc = fc;
	}

	public String getName(File f)
	{
		return super.getName(f);
	}

	public String getDescription(File f)
	{
		return super.getDescription(f);
	}

	public String getTypeDescription(File f)
	{
		return super.getTypeDescription(f);
	}

	public Icon getIcon(File f)
	{
		if (f instanceof AccessoryProvider)
		{
			AccessoryProvider ap = (AccessoryProvider) f;
			if (ap.getIcon() != null)
			{
				return ap.getIcon();
			}
		}
		return fc.getFileSystemView().getSystemIcon(f);
	}

	public Boolean isTraversable(File f)
	{
		if (f instanceof NotTraversable)
		{
			return Boolean.FALSE;
		}
		else if (f instanceof Folder)
		{
			return Boolean.TRUE;
		}
		else
		{
			return super.isTraversable(f);
		}
	}

}
