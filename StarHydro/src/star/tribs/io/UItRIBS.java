package star.tribs.io;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.l2fprod.common.swing.JDirectoryChooser;

public class UItRIBS extends JFrame
{
	JFrame self = this;
	JLabel voronoiAlert = new JLabel();
	JLabel prefixAlert = new JLabel();
	JTextField voronoiText = new JTextField("Z:\\hydro\\R3\\Output\\voronoi\\wg_voi", 30);
	JTextField prefixText = new JTextField("wg.", 30);
	JTextField suffixText = new JTextField("_00d", 30);
	JTextField folder = new JTextField("Z:\\hydro\\R3\\Output\\voronoi", 30);
	JTextField flvText = new JTextField("Z:\\hydro\\R3\\output.", 30);
	JTextField frameText = new JTextField("100", 30);
	JTextField scalingText = new JTextField("15", 30);
	JTextField columnsText = new JTextField("81", 30);
	JButton dirSelector = new JButton("Choose directory");
	JButton fileSelector = new JButton("Choose voronoi file");
	JButton flvSelector = new JButton("Choose output FLV");
	JButton runButton = new JButton("Run");

	@Override
	public void addNotify()
	{
		// TODO Auto-generated method stub
		super.addNotify();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new MigLayout());
		c.add(new JLabel("Folder with tRIBs output: "));
		c.add(folder);
		c.add(dirSelector, "wrap");
		c.add(new JLabel("Voronoi file:"));
		c.add(voronoiText);
		c.add(fileSelector, "wrap");
		c.add(new JLabel("Prefix:"));
		c.add(prefixText, "wrap");
		c.add(new JLabel("Suffix:"));
		c.add(suffixText, "wrap");
		c.add(new JLabel("Frame delay (ms):"));
		c.add(frameText, "wrap");
		c.add(new JLabel("FLV output:"));
		c.add(flvText);
		c.add(flvSelector, "wrap");
		c.add(new JLabel("Scaling:"));
		c.add(scalingText, "wrap");
		c.add(new JLabel("Columns:"));
		c.add(columnsText, "wrap");

		c.add(runButton, "center");
		dirSelector.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JDirectoryChooser chooser = new JDirectoryChooser(folder.getText());
				int choice = chooser.showOpenDialog(self);
				if (choice != JDirectoryChooser.CANCEL_OPTION)
				{
					System.out.println("Folder Selection: " + chooser.getSelectedFile().getAbsolutePath());
					folder.setText(chooser.getSelectedFile().getAbsolutePath());
				}

			}
		});

		fileSelector.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser(folder.getText());
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int choice = chooser.showOpenDialog(self);
				if (choice != JFileChooser.CANCEL_OPTION)
				{
					System.out.println("Voronoi Selection: " + chooser.getSelectedFile().getAbsolutePath());
					voronoiText.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});

		runButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreeSet<File> array = new TreeSet<File>();
				String prefix = prefixText.getText();
				String suffix = suffixText.getText();
				java.io.File file = new java.io.File(folder.getText());
				if (file.isDirectory())
				{
					File[] files = file.listFiles();
					for (java.io.File f : files)
					{
						String name = f.getName();
						if (name.startsWith(prefix) && name.endsWith(suffix))
						{
							array.add(f);
						}
					}
					int scaling = Integer.parseInt(scalingText.getText());
					int time = Integer.parseInt(frameText.getText());
					int columns = Integer.parseInt(columnsText.getText());
					OutputReader.process(new java.io.File(voronoiText.getText()), array.toArray(new java.io.File[array.size()]), new java.io.File(flvText.getText()), scaling, time, columns);
					JOptionPane.showMessageDialog(self, "Finished.");
				}
			}
		});

	}

	public static void main(String[] args)
	{
		UItRIBS app = new UItRIBS();
		app.pack();
		app.setVisible(true);
	}
}
