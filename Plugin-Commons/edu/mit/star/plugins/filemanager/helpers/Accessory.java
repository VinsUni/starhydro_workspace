package edu.mit.star.plugins.filemanager.helpers;

import java.awt.Component;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import edu.mit.star.plugins.filemanager.interfaces.AccessoryProvider;

public class Accessory extends JPanel implements PropertyChangeListener
{
	private static final long serialVersionUID = 1L;
	JComponent self = null;
	JFileChooser fc = null;
	Component accessory = null;
	Component dummy = new JTextArea(15, 30);
	Thread t;

	public Accessory(JFileChooser fc)
	{
		fc.addPropertyChangeListener(this);
		this.fc = fc;
		this.self = this;
	}

	public void addNotify()
	{
		setLayout(new FlowLayout());
		if (accessory != null)
		{
			if (getComponentCount() != 0)
			{
				removeAll();
			}
			add(accessory);
		}
		else
		{
			if (fc.getCurrentDirectory() != null && fc.getSelectedFile() == null)
			{
				updateAccessory(fc.getCurrentDirectory());
			}
		}
		super.addNotify();
	}

	public void removeNotify()
	{
		while (getComponentCount() != 0)
		{
			remove(0);
		}
		// t.stop() ;
		super.removeNotify();
	}

	void updateListeners(PropertyChangeEvent e)
	{
		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(e.getPropertyName()))
		{
			Object oldDir = e.getOldValue();
			Object newDir = e.getNewValue();
			if (oldDir != null && oldDir instanceof Folder)
			{
				((Folder) oldDir).removePropertyChangeListener(this);
			}
			if (newDir != null && newDir instanceof Folder)
			{
				((Folder) newDir).addPropertyChangeListener(this);
			}
		}
	}

	void updateAccessory(final Object selected)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				boolean hasAccessoryNow = (getComponentCount() != 0);
				boolean needsAccessory = false;
				Component newAccessory = null;
				Component currentAccessory = hasAccessoryNow ? getComponent(0) : null;
				if (selected != null && selected instanceof AccessoryProvider)
				{
					newAccessory = ((AccessoryProvider) selected).getAccessory();
					needsAccessory = newAccessory != null;
				}

				if (hasAccessoryNow && needsAccessory)
				{
					if (newAccessory != currentAccessory)
					{
						accessory = newAccessory;
						remove(currentAccessory);
						add(newAccessory);
						self.updateUI();
					}
				}
				if (hasAccessoryNow && !needsAccessory)
				{
					accessory = null;
					remove(currentAccessory);
					self.updateUI();
				}
				if (!hasAccessoryNow && needsAccessory)
				{
					accessory = newAccessory;
					removeAll();
					add(newAccessory);
					self.updateUI();
				}
				if (!hasAccessoryNow && !needsAccessory)
				{
					// Do nothing
				}

			}
		});
	}

	public void updateUI(PropertyChangeEvent e)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				java.io.File f = fc.getSelectedFile();
				fc.updateUI();
				if (f != null)
				{
					fc.setSelectedFile(f);
				}
			}
		});

	}

	public synchronized void propertyChange(PropertyChangeEvent e)
	{
		String prop = e.getPropertyName();
		// System.err.println( "Property Change " + prop ) ;
		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop))
		{
			updateListeners(e);
			updateAccessory(e.getNewValue());
		}
		if (Folder.ADD_CHILD.equals(prop))
		{
			// updateUI(e);
			SwingUtilities.invokeLater(new Runnable()
			{

				public void run()
				{
					fc.rescanCurrentDirectory();
				}
			});

		}
		if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop))
		{
			java.io.File f = fc.getSelectedFile();
			if (f != null)
			{
				updateAccessory(f);
			}
		}
	}
}
