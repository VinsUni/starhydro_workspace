package edu.mit.star.plugins.filemanager.interfaces;

import javax.swing.Icon;
import javax.swing.JComponent;

public interface AccessoryProvider
{
	public JComponent getAccessory();

	public String getTypeDescription();

	public Icon getIcon();
}
