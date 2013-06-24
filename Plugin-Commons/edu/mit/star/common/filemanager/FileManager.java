package edu.mit.star.common.filemanager;

import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;

import edu.mit.star.common.ui.CancelableDialog;
import edu.mit.star.plugins.filemanager.helpers.Accessory;
import edu.mit.star.plugins.filemanager.helpers.View;
import edu.mit.star.plugins.filemanager.interfaces.ProjectProvider;

public class FileManager implements FileManagerPlugin
{
	private static final long serialVersionUID = 1L;
	Object rr = null;

	public Object open(Component parent, FileSystemView view, Properties properties) throws FileNotFoundException
	{
		final JFileChooser fc = new JFileChooser(view);
		try
		{
			LookAndFeel lf = UIManager.getLookAndFeel();
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(fc);
			UIManager.setLookAndFeel(lf);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}

		fc.setFileView(new View(fc));
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setAccessory(new Accessory(fc));
		fc.setControlButtonsAreShown(true);
		fc.setIgnoreRepaint(false);
		int ret = fc.showOpenDialog(parent);
		if (fc.getCurrentDirectory() != null)
		{
			properties.setProperty(CURRENT_DIRECTORY, fc.getCurrentDirectory().toString());
			properties.setProperty(CURRENT_DIRECTORY_CLASS, fc.getCurrentDirectory().getClass().toString());
		}
		// TODO Auto-generated method stub
		if (fc.getSelectedFile() != null && ret == JFileChooser.APPROVE_OPTION)
		{
			if (fc.getSelectedFile() instanceof ProjectProvider)
			{
				// TODO: Dialog Box that allow process to run & be canceled!
				CancelableDialog.getCancelableDialog(parent, "Dowloading File", "<html><body><b>Please wait.</b><br>Opening and parsing file.</body></html>", new Runnable()
				{
					public void run()
					{
						rr = ((ProjectProvider) (fc.getSelectedFile())).getParsedObject();
					}
				});
				return rr;
			}
			else
			{
				return new FileInputStream(fc.getSelectedFile());
			}
		}
		else
		{
			return null;
		}
	}

}
