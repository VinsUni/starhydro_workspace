package edu.mit.star.plugins.filemanager.helpers;

import java.io.File;
import java.io.IOException;

import javax.swing.Icon;

import edu.mit.star.plugins.filemanager.interfaces.AbstractFileProvider;
import edu.mit.star.plugins.filemanager.interfaces.AccessoryProvider;
import edu.mit.star.plugins.filemanager.interfaces.FileMassager;
import edu.mit.star.plugins.filemanager.interfaces.RootsConsumer;

public class FileSystemView extends javax.swing.filechooser.FileSystemView implements RootsConsumer
{

	boolean useNativeRoots = true;
	File[] roots;
	FileMassager factory;
	javax.swing.filechooser.FileSystemView nativeView;
	File defaultDirectory = null;

	public FileSystemView()
	{
		super();
		updateNativeView();
	}

	public boolean isRoot(File f)
	{
		return super.isRoot(f);
	}

	public Boolean isTraversable(File f)
	{
		return Boolean.valueOf(f.isDirectory());
	}

	public String getSystemDisplayName(File f)
	{
		String name = null;
		if (f != null)
		{
			name = f.getName();
		}
		return name;
	}

	public String getSystemTypeDescription(File f)
	{
		if (f instanceof AccessoryProvider)
		{
			return ((AccessoryProvider) f).getTypeDescription();
		}
		else
		{
			return super.getSystemTypeDescription(f);
		}
	}

	public Icon getSystemIcon(File f)
	{
		Icon ret = null;
		if (f instanceof AbstractFileProvider)
		{
			if (f instanceof Folder)
			{
				ret = this.nativeView.getSystemIcon(getHomeDirectory());
			}
		}
		else
		{
			ret = super.getSystemIcon(f);
		}
		return ret;
	}

	public boolean isParent(File folder, File file)
	{
		if (folder == null || file == null)
		{
			return false;
		}
		return folder.equals(file.getParentFile());
	}

	public File getChild(File parent, String fileName)
	{
		return createFileObject(parent, fileName);
	}

	public boolean isFileSystem(File f)
	{
		return false;
	}

	public File createNewFolder(File containingDir) throws IOException
	{
		return containingDir;
	}

	public boolean isHiddenFile(File f)
	{
		return f.isHidden();
	}

	public boolean isFileSystemRoot(File dir)
	{
		return super.isFileSystemRoot(dir);
	}

	public boolean isDrive(File dir)
	{
		return super.isDrive(dir);
	}

	public boolean isFloppyDrive(File dir)
	{
		return super.isFloppyDrive(dir);
	}

	public boolean isComputerNode(File dir)
	{
		return super.isComputerNode(dir);
	}

	public File[] getRoots()
	{
		java.io.File[] a = null;
		if (useNativeRoots)
		{
			updateNativeView();
			a = nativeView.getRoots();
		}
		java.io.File[] b = roots;
		int a_size = a != null ? a.length : 0;
		int b_size = b != null ? b.length : 0;
		java.io.File[] ret = new java.io.File[a_size + b_size];
		if (a != null)
		{
			System.arraycopy(a, 0, ret, 0, a_size);
		}
		if (b != null)
		{
			System.arraycopy(b, 0, ret, a_size, b_size);
		}
		// Apple Fix - this is funny :)
		File dflt = getDefaultDirectory();
		for (int i = 0; i < ret.length; i++)
		{
			if (dflt == ret[i])
			{
				File x = ret[0];
				ret[0] = ret[i];
				ret[i] = x;
				break;
			}
		}
		return ret;
	}

	public File getDefaultDirectory()
	{
		return defaultDirectory != null ? defaultDirectory : super.getDefaultDirectory();
	}

	public File createFileObject(File dir, String filename)
	{
		if (dir == null)
		{
			return new File(filename);
		}
		else
		{
			File[] str = getFiles(dir, false);
			if (str != null && str.length != 0)
			{
				for (int i = 0; i < str.length; i++)
				{
					if (filename.equals(str[i].getName()))
					{
						return str[i];
					}
				}
			}
			return new File(dir, filename);

		}
	}

	public File createFileObject(String path)
	{
		File f = new File(path);
		return f;
	}

	public File[] getFiles(File dir, boolean useFileHiding)
	{
		File[] ret = null;
		if (dir instanceof edu.mit.star.plugins.filemanager.interfaces.AbstractFileProvider)
		{
			ret = dir.listFiles();
		}
		else
		{
			ret = super.getFiles(dir, useFileHiding);
		}
		return (factory != null) ? factory.processFileArray(ret) : ret;
	}

	public File getParentDirectory(File dir)
	{
		File ret = dir.getParentFile();
		if (ret == null)
		{
			ret = getRoots()[0];
		}
		return ret;
	}

	public void setRoots(File[] roots)
	{
		this.roots = roots;
	}

	public void setAbstractFileFactory(FileMassager factory)
	{
		this.factory = factory;
	}

	public void setUseNativeRoots(boolean flag)
	{
		useNativeRoots = flag;
	}

	void updateNativeView()
	{
		nativeView = javax.swing.filechooser.FileSystemView.getFileSystemView();
	}

	public void setDefaultDirectory(File defaultDirectory)
	{
		this.defaultDirectory = defaultDirectory;
	}
}
