package edu.mit.star.common.filemanager;

import java.io.FileNotFoundException;
import java.util.Properties;

import javax.swing.filechooser.FileSystemView;

import plugin.APIInterface;

public interface FileManagerPlugin extends APIInterface
{
	final static String CURRENT_DIRECTORY = "CURRENT_DIRECTORY";
	final static String CURRENT_DIRECTORY_CLASS = CURRENT_DIRECTORY + "_CLASS";

	public Object open(java.awt.Component parent, FileSystemView view, Properties properties) throws FileNotFoundException;
}
