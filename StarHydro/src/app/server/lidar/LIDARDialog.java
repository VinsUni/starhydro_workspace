package app.server.lidar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import star.hydrology.data.layers.UnprojectedSeamlessTerrainMap;
import utils.UIHelpers;
import app.Main;
import app.server.lidar.LIDARWorker.LIDARMap;

public class LIDARDialog
{
	List<LIDARMap> dataset;
	LIDARWorker worker;
	
	public Component getLidarDialog(final Main main , final JDialog dialog, final  LIDARWorker worker)
	{
		
		dataset = new ArrayList<LIDARMap>(worker.list());
		try
		{
			System.out.println(worker.getHTML());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		final JList list = new JList();
		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		final JButton open = new JButton("Open map");
		open.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				Object value = list.getSelectedValue();
				if (value != null && value instanceof LIDARMap)
				{
					Window c = UIHelpers.getWindow(open);
					Graphics g = c.getGraphics();
					if (true)
					{
						g.setColor(Color.white);
						g.fillRect(0, 0, c.getWidth(), c.getHeight());
						g.setColor(Color.blue);
						g.setFont(g.getFont().deriveFont(20.5f));
						String message = "Loading map, please wait...";
						Rectangle2D r = g.getFontMetrics().getStringBounds(message, g);
						g.drawString(message, (int) ((c.getWidth() - r.getWidth()) / 2), (int) ((c.getHeight() - r.getHeight()) / 2));
					}
					worker.load((LIDARMap) value);
					if (true)
					{
						g.setColor(Color.white);
						g.fillRect(0, 0, c.getWidth(), c.getHeight());
						g.setColor(Color.blue);
						g.setFont(g.getFont().deriveFont(20.5f));
						String message = "Map downloaded, opening map...";
						Rectangle2D r = g.getFontMetrics().getStringBounds(message, g);
						g.drawString(message, (int) ((c.getWidth() - r.getWidth()) / 2), (int) ((c.getHeight() - r.getHeight()) / 2));
					}
					String name = "LIDAR";
					UIHelpers.track("Open/" + name + "/" + ((LIDARMap) value).getArchive());
					main.setMap(new UnprojectedSeamlessTerrainMap(name));
					c.setVisible(false);
					c.dispose();
				}
				else
				{
					JOptionPane.showMessageDialog(main, "Select map");
				}
			}
		});
		ListModel model = new AbstractListModel()
		{

			public int getSize()
			{
				return dataset.size();
			}

			public Object getElementAt(int index)
			{
				return dataset.get(index);
			}
		};
		list.setModel(model);
		panel.add(BorderLayout.NORTH, new JLabel("Pick a map from below:"));
		panel.add(BorderLayout.CENTER, new JScrollPane(list));
		panel.add(BorderLayout.SOUTH, open);
		return panel;
	}
}
