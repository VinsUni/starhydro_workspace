package edu.mit.star.plugins.filemanager.helpers;

import javax.swing.Icon;

import edu.mit.star.plugins.filemanager.interfaces.ExportableEntry;
import edu.mit.star.plugins.filemanager.interfaces.ProjectException;

public interface Project extends ExportableEntry
{
	final String CACHED_INFO = "CachedInfoArray.ser";

	Icon getIcon();

	String getName();

	String getDescription();

	Object getParsed() throws ProjectException;

	Object getCachedInfo();
}
