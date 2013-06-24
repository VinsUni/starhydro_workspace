package edu.mit.star.plugins.filemanager.interfaces;

import java.io.File;

public interface RootsConsumer
{
	void setRoots(File[] roots);

	void setUseNativeRoots(boolean flag);

	void setDefaultDirectory(File defaultDirectory);
}
