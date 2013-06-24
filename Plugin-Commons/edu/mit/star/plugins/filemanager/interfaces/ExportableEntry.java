package edu.mit.star.plugins.filemanager.interfaces;

import java.io.InputStream;

public interface ExportableEntry
{
	InputStream getSource() throws ProjectException;

	String getSourceName();

}
