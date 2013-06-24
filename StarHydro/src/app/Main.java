package app;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import plugin.Loader;
import plugin.PluginException;
import romi.RomiFactory;
import star.annotations.SignalComponent;
import star.event.EventController;
import star.hydrology.data.layers.UnprojectedSeamlessTerrainMap;
import star.hydrology.events.UnprojectedMapChangedEvent;
import star.hydrology.events.UnprojectedMapChangedRaiser;
import star.j3d.ui.signal.ResetSceneEvent;
import star.j3d.ui.signal.ResetSceneRaiser;
import star.localserver.StarHandler;
import utils.Memory;
import utils.Runner;
import utils.UIHelpers;
import app.server.lidar.LIDARDialog;
import app.server.lidar.LIDARWorker;
import app.server.lidar.LIDARWorker.LIDARMap;
import app.server.worker.ArcGISWorkerCachedWeb;
import app.server.worker.GISWorker;
import app.server.worker.JNLPPersist;
import app.viewers.AbstractMapViewer;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

class Restart implements Runnable
{
	public void run()
	{
		UIHelpers.track("Restart");
		AbstractMapViewer.clearStatic();
		Main.main(null);
	}

}

@star.annotations.Preferences
@SignalComponent(extend = Frame.class)
public class Main extends Main_generated implements EventController, UnprojectedMapChangedRaiser, ResetSceneRaiser
{
	private static final long serialVersionUID = 1L;
	private static String TITLE = "Title";
	private UnprojectedSeamlessTerrainMap map = null;
	public static Runnable hydro2;
	public static Main self;

	public java.util.prefs.Preferences getPreferences()
	{
		Preferences pref = super.getPreferences();
		return pref != null ? pref : java.util.prefs.Preferences.userRoot().node(this.getClass().getName());
	}

	private void loadPreferences()
	{
		try
		{
			plugin.preferences.Preferences pref = (plugin.preferences.Preferences) Loader.getDefaultLoader().getPlugin(plugin.preferences.Preferences.class.getName(), plugin.preferences.PreferencesImplementation.class.getName());
			Properties prop = new Properties();
			prop.load(this.getClass().getClassLoader().getResourceAsStream("preferences/default.properties"));
			pref.setApplication("StarHydro", prop);
		}
		catch (PluginException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void showInitialSelectionDialogBox()
	{
		JOptionPane.showConfirmDialog(this, getInitialSelection(), "Star Hydro - select map", 0);
	}

	private JPanel getInitialSelection()
	{
		String itemsList = getPreferences().get("watersheds", "Blue_River,Walnut_Gulch");
		String[] items = itemsList.split(",");
		String rows = "3dlu, p , 3dlu, 15dlu, 3dlu ";
		for (String item : items)
		{
			if (item != null)
			{
				rows += ", p, 3dlu";
			}
		}
		FormLayout layout = new FormLayout("5dlu, pref, pref, 5dlu", rows);
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		// Obtain a reusable constraints object to place components in the grid.
		CellConstraints cc = new CellConstraints();

		int row = 2;

		builder.addLabel("<html>To start using Star Hydro, select a map from below.<br>Delineate watershed by clicking on the \"Set watershed outlet button\" and clicking on the 3D map on the right", cc.xyw(2, row, 2));
		row += 2;
		builder.addSeparator("Select map", cc.xyw(2, row, 2));
		int c = 0;
		row += 2;
		for (final String name : items)
		{
			JButton b = new JButton(getPreferences().get(name, name));
			b.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					UIHelpers.track("Open/" + name);
					setMap(new UnprojectedSeamlessTerrainMap(name));
					Component c = (Component) e.getSource();
					while (!(c instanceof Window) && c != null)
					{
						c = c.getParent();
					}
					if (c != null)
					{
						((Window) c).setVisible(false);
						((Window) c).dispose();
					}
				}
			});
			builder.add(b, cc.xy(2 + c % 2, row));
			c++;
			row += 2 * ((c - 1) % 2);
		}
		JPanel panel = builder.getPanel();
		panel.setMaximumSize(new Dimension(1024, 768));
		return panel;
	}

	public Main(String project)
	{
		super();
		self = this;
		UIHelpers.addTracking("StarHydro");
		if (project != null)
		{
			map = new UnprojectedSeamlessTerrainMap(project);
		}
		init();
		UIHelpers.setDefaultLookAndFeel();

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
				quit();
			}
		});

		setARCWorker(this);
		loadPreferences();

		HydrologyViewer viewer = new HydrologyViewer();
		add(viewer);
		setTitle(getPreferences().get(TITLE, ""));
		pack();
		setVisible(true);
		setMenuBar(getAppMenuBar());
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				if (map == null)
				{
					// showInitialSelectionDialogBox();
					showLidarDialogBox();
				}
				else
				{
					setMap(map);
				}
			}
		});
	}

	public void restart()
	{
		this.setVisible(false);
		this.dispose();
		SwingUtilities.invokeLater(new Restart());
		System.runFinalization();
		System.gc();
		// showInitialSelectionDialogBox();
	}

	
	 final static LIDARWorker worker = new LIDARWorker(new StarHandler()
		{

			public boolean handle(String command, Map<String, String[]> params)
			{
				try
				{
					if ("/StarHydro/Open".equals(command) && params.containsKey("archive"))
					{
						String archive = params.get("archive")[0];
						boolean found = false ;
						for( LIDARMap m : worker.list() )
						{
							if( archive.equals( m.getArchive() ) ) 
							{
								found = true ;
								worker.load((LIDARMap) m);
								UIHelpers.track("Open/LIDAR/" + ((LIDARMap) m).getArchive());
								self.setMap(new UnprojectedSeamlessTerrainMap("LIDAR"));
								if( self.openDialog != null )
								{
									self.openDialog.dispose();
									self.openDialog = null;
								}
								return true;
							}
						}
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				return false;
			}
		});
	 
	 JDialog openDialog ;

	void showLidarDialogBox()
	{
		openDialog = new JDialog(this, true);
		JDialog dialog = openDialog;
		dialog.setTitle("Please select map");
		JTabbedPane tabs = new JTabbedPane();
		dialog.add(tabs);
		tabs.addTab("Prebuild watersheds", getInitialSelection());
		LIDARDialog lidar = new LIDARDialog();
		tabs.addTab("LIDAR data", lidar.getLidarDialog(this,dialog,worker));
		dialog.pack();
		UIHelpers.centerOnParent(dialog);
		dialog.setVisible(true);
	}

	ImageIcon currentIcon;

	void quit()
	{
		System.exit(0);
	}

	static class Shutdown extends Thread
	{
		public void run()
		{
			try
			{
				ImageIcon icon = new ImageIcon(new java.net.URL("http://starapp.mit.edu/star/hydro/icons/Shutdown.gif"));
				Runner.sleep(250);
			}
			catch (MalformedURLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static boolean shutdown = false;

	void init()
	{
		if (!shutdown)
		{
			try
			{
				ImageIcon icon = new ImageIcon(new java.net.URL("http://starapp.mit.edu/star/hydro/icons/Startup.gif"));
				currentIcon = icon;
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
			Runtime.getRuntime().addShutdownHook(new Shutdown());
		}
		shutdown = true;
	}

	static class OpenStarHydro2 implements Runnable
	{
		public void run()
		{
			System.gc();
			System.out.println(Memory.getUsed() + "b " + Memory.getUsed() / 1024 + "kb " + Memory.getUsed() / 1024 / 1024 + "Mb");
			app.Main.hydro2.run();
			System.gc();
		}
	}

	private MenuBar getAppMenuBar()
	{
		final Main main = this;
		MenuItem quit = new MenuItem("Quit");
		quit.setShortcut(new MenuShortcut(KeyEvent.VK_Q));
		quit.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				UIHelpers.track("Quit");
				quit();
			}
		});
		MenuItem open = new MenuItem("Open");
		open.setShortcut(new MenuShortcut(KeyEvent.VK_F));
		open.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				UIHelpers.track("Open");
				restart();
			}
		});

		MenuItem clear = new MenuItem("Clear local maps cache");
		clear.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				UIHelpers.track("ClearLocalMaps");
				int ret = JOptionPane.showConfirmDialog(main, "Delete cached content from local workspace?", "Local workspace cleared?", JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION)
				{
					(new JNLPPersist()).clearWorkspace();
				}
			}
		});

		Menu file = new Menu("File");
		file.add(open);
		if (hydro2 != null)
		{
			MenuItem openWorld = new MenuItem("Open World");
			openWorld.setShortcut(new MenuShortcut(KeyEvent.VK_F));
			openWorld.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					UIHelpers.track("OpenWorld");
					UIHelpers.disposeTop(main);
					Main.clearArcWorker(main);
					System.gc();
					SwingUtilities.invokeLater(new OpenStarHydro2());
					System.gc();
				}
			});
			file.add(openWorld);
		}
		file.add(clear);
		file.add(quit);
		file.setShortcut(new MenuShortcut(KeyEvent.VK_F));

		MenuItem about = new MenuItem("About");
		about.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				UIHelpers.track("About");
				JOptionPane.showMessageDialog(main, new AboutBox());
			}
		});

		Menu help = new Menu("Help");
		help.add(about);
		help.setShortcut(new MenuShortcut(KeyEvent.VK_F1));

		MenuItem reset = new MenuItem("Reset Scene");
		reset.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				UIHelpers.track("ResetScene");
				resetView();
			}
		});
		Menu view = new Menu("View");
		view.add(reset);
		MenuBar mb = new MenuBar();
		mb.add(file);
		mb.add(view);
		mb.add(help);
		return mb;
	}

	private void resetView()
	{
		(new ResetSceneEvent(this)).raise();
	}

	public void setMap(UnprojectedSeamlessTerrainMap map)
	{
		this.map = map;
		new UnprojectedMapChangedEvent(this).raise();
		resetView();
	}

	public UnprojectedSeamlessTerrainMap getMap()
	{
		return map;
	}

	private static void setARCWorker(Main main)
	{
		// RomiFactory.setSytemLocalObject(GISWorker.class,
		// ArcGISWorkerCached.getDefaultWorker());
		// main.getAdapter().addComponent(ArcGISWorkerCached.getDefaultWorker());

		RomiFactory.setSytemLocalObject(GISWorker.class, ArcGISWorkerCachedWeb.getDefaultWorker());
		main.getAdapter().addComponent(ArcGISWorkerCachedWeb.getDefaultWorker());
		if (ArcGISWorkerCachedWeb.getDefaultWorker() instanceof ArcGISWorkerCachedWeb)
		{
			((ArcGISWorkerCachedWeb) ArcGISWorkerCachedWeb.getDefaultWorker()).setFrame(main);
		}

		// RomiFactory.setSytemLocalObject(GISWorker.class,
		// ArcGISWorker.getDefaultWorker());
	}

	private static void clearArcWorker(Main main)
	{
		main.getAdapter().removeComponent(ArcGISWorkerCachedWeb.getDefaultWorker());
		if (ArcGISWorkerCachedWeb.getDefaultWorker() instanceof ArcGISWorkerCachedWeb)
		{
			((ArcGISWorkerCachedWeb) ArcGISWorkerCachedWeb.getDefaultWorker()).setFrame(null);
		}
	}

	// private static void setRemoteArcWorker()
	// {
	// RomiFactory.setSystemConnectionString(GISWorker.class, "192.168.159.128");
	// }

	public static void main(final String[] str)
	{
		System.out.println("start StarHydro");
		UIHelpers.addTracking("StarHydro");
		String projectName = null;
		if (str != null && str.length == 1)
		{
			if ("OpenStarHydro2".equals(str[0]))
			{
				app.Main.hydro2 = new Runnable()
				{
					public void run()
					{
						try
						{
							Method[] method = Class.forName("app.Main2").getMethods();
							for (Method m : method)
							{
								if ("main".equals(m.getName()))
								{
									UIHelpers.track("OpenStarHydro2");
									m.invoke(null, (Object) new String[0]);
								}
							}
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
				};
			}
		}
		if (str != null && str.length == 4)
		{
			if ("StarHydro2Import".equals(str[0]))
			{
				UIHelpers.track("StarHydro2Import");
				projectName = str[1];
				int outletX = Integer.parseInt(str[2]);
				int outletY = Integer.parseInt(str[3]);
			}
		}

		System.gc();
		Main m = new Main(projectName);
		m.toString();
	}

}