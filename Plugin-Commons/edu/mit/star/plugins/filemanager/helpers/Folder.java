package edu.mit.star.plugins.filemanager.helpers;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.JComponent;

import edu.mit.star.plugins.filemanager.interfaces.AbstractFileProvider;
import edu.mit.star.plugins.filemanager.interfaces.AccessoryProvider;

public class Folder extends AbstractFileProvider implements AccessoryProvider
{
	private static final long serialVersionUID = 1L;

	public final static String ADD_CHILD = "addChild";

	protected PropertyChangeSupport propertyChangeSupport = null;

	TreeSet<AbstractFileProvider> children;

	public Folder(AbstractFileProvider parent, String name)
	{
		super(parent, name);
		children = new TreeSet<AbstractFileProvider>();
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void addChild(AbstractFileProvider child)
	{
		children.add(child);
		propertyChangeSupport.firePropertyChange(ADD_CHILD, null, child);
	}

	public boolean isDirectory()
	{
		return true;
	}

	public File getCanonicalFile()
	{
		return this;
	}

	public boolean exists()
	{
		return true;
	}

	public File[] listFiles()
	{
		return (File[]) children.toArray(new File[0]);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public String getPath()
	{
		String path = super.getPath();
		return path;
	}

	public JComponent getAccessory()
	{
		return null;
	}

	public String getTypeDescription()
	{
		return null;
	}

	public Icon getIcon()
	{
		return null;
	}

}
