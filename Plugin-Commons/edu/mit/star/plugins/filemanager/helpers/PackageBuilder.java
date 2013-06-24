package edu.mit.star.plugins.filemanager.helpers;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import plugin.Loader;
import plugin.PluginException;

import edu.mit.star.common.filemanager.FileManagerPlugin;
import edu.mit.star.plugins.filemanager.interfaces.ExportableEntry;
import edu.mit.star.plugins.filemanager.interfaces.FileMassager;
import edu.mit.star.plugins.filemanager.interfaces.ProjectException;

public abstract class PackageBuilder extends JFrame
{
	Properties properties = new Properties();
	JTree tree;
	JButton add;
	JButton remove;
	JButton save;
	DefaultMutableTreeNode root;
	DefaultTreeModel model;
	PackageBuilder self = this;

	public PackageBuilder()
	{
		super();
		init();
	}

	public abstract TreeCellRenderer getTreeCellRenderer();

	void init()
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		root = new DefaultMutableTreeNode("Package Root");
		model = new DefaultTreeModel(root);
		tree = new JTree(model);
		tree.setCellRenderer(getTreeCellRenderer());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		add = new JButton("Add");
		remove = new JButton("Remove");
		save = new JButton("Save");

		add.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				Project project = self.getProject();
				DefaultMutableTreeNode parentNode = null;
				parentNode = root;
				parentNode.add(new DefaultMutableTreeNode(project));
				model.reload();
				self.pack();
			}

		});

		remove.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				DefaultMutableTreeNode parentNode = null;
				TreePath parentPath = tree.getSelectionPath();

				if (parentPath == null)
				{
					parentNode = null;
				}
				else
				{
					parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
				}
				if (parentNode != null)
				{
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentNode.getParent();
					if (parent != null)
					{
						parent.remove(parentNode);
						model.reload();
						self.pack();
					}
				}

			}

		});

		save.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				File outputFile = getOutputFile();
				if (outputFile != null)
				{
					ArrayList<ExportableEntry> list = new ArrayList<ExportableEntry>();
					Enumeration en = root.depthFirstEnumeration();
					while (en.hasMoreElements())
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
						if (node.getUserObject() instanceof Project)
						{
							Project p = (Project) node.getUserObject();
							list.add(p);
						}
					}
					final Object[] cached = new Object[list.size()];
					for (int i = 0; i < list.size(); i++)
					{
						cached[i] = ((Project) list.get(i)).getCachedInfo();
					}
					ExportableEntry entry = new ExportableEntry()
					{

						public InputStream getSource() throws ProjectException
						{
							int INITIAL = 64 * 1024;
							ByteArrayOutputStream bos = new ByteArrayOutputStream(INITIAL);
							try
							{
								ObjectOutputStream oos = new ObjectOutputStream(bos);
								oos.writeObject(cached);
								oos.flush();
								oos.close();
								bos.close();
							}
							catch (IOException e)
							{
								e.printStackTrace();
								throw new ProjectException();
							}
							return new ByteArrayInputStream(bos.toByteArray());
						}

						public String getSourceName()
						{
							return Project.CACHED_INFO;
						}

					};
					list.add(entry);
					ExportableEntry[] projects = (ExportableEntry[]) list.toArray(new ExportableEntry[0]);
					try
					{
						FileOutputStream fos = new FileOutputStream(outputFile);
						Zipme.write(fos, projects);
						fos.flush();
						fos.close();
					}
					catch (FileNotFoundException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					catch (ProjectException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		});

		this.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(add);
		panel.add(remove);
		panel.add(save);
		this.getContentPane().add(BorderLayout.CENTER, new JScrollPane(tree));
		this.getContentPane().add(BorderLayout.SOUTH, panel);
		pack();

	}

	public abstract File[] getRoots();

	public abstract FileMassager getFileMassager();

	Project getProject()
	{
		Project ret = null;
		try
		{
			FileSystemView view = new FileSystemView();
			view.setRoots(getRoots());
			view.setAbstractFileFactory(getFileMassager());

			FileManagerPlugin fm = (FileManagerPlugin) (Loader.getDefaultLoader().getPlugin(FileManagerPlugin.class.getName(), "edu.mit.star.common.filemanager.FileManager"));

			Object ret2 = fm.open(self, view, properties);
			ret = (Project) ret2;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		catch (PluginException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	File getOutputFile()
	{
		JFileChooser fc = new JFileChooser();
		if (fc.showSaveDialog(self) == JFileChooser.APPROVE_OPTION)
		{
			return fc.getSelectedFile();
		}
		else
		{
			return null;
		}
	}
}
