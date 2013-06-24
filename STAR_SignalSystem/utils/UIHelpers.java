package utils;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import star.event.EventController;

public class UIHelpers
{
	volatile static boolean fixScheduled = false;

	public static void fixBoundsOnce(final Component c, final ComponentListener listener)
	{
		Rectangle r = new Rectangle(c.getBounds());
		Rectangle s = c.getGraphicsConfiguration().getBounds();
		boolean setBounds = false;
		if (r.x < s.x || r.y < s.y || r.width > s.width || r.height > s.height)
		{
			if (r.x < s.x)
			{
				r.x = s.x + 5;
				setBounds = true;
			}
			if (r.y < s.y)
			{
				r.y = s.y + 5;
				setBounds = true;
			}
			if (r.width > s.width)
			{
				r.width = s.width - 10;
				setBounds = true;
			}
			if (r.height > s.height)
			{
				r.height = s.height - 10;
				setBounds = true;
			}
			System.out.println("scheduleFix?!");
			if (setBounds)
			{
				System.out.println("Fixing bounds, seems like component is out of the screen!");
				System.out.println("Original bounds are: " + c.getBounds());
				System.out.println("Screen bounds are: " + s);
				System.out.println("New window bounds are: " + s);
				if (c instanceof Frame)
				{
					System.out.println("Frame ext state " + ((Frame) c).getExtendedState());
					if ((((Frame) c).getExtendedState() & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH)
					{
						((Frame) c).setExtendedState(((Frame) c).getExtendedState() | Frame.MAXIMIZED_BOTH);
					}
					else
					{
						return;
					}
				}
				else
				{
					c.setBounds(r);
				}
				if (listener != null)
				{
					int q = JOptionPane.showConfirmDialog(c, "Window was out of bounds. Would you like to do auto-resize next time window is out of bounds?", "Auto resize window ?", JOptionPane.YES_NO_OPTION);
					if (q == JOptionPane.NO_OPTION)
					{
						c.removeComponentListener(listener);
					}
				}
			}

		}
	}

	private static void scheduleFix(final Component c, final ComponentListener listener)
	{
		if (!fixScheduled)
		{
			Timer t = new Timer(true);
			t.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					fixScheduled = false;
					fixBoundsOnce(c, listener);
				}
			}, 5000);
			fixScheduled = true;
		}
	}

	public static void registerScreenchangeHandler(Window w)
	{
		w.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentMoved(ComponentEvent e)
			{
				Component c = e.getComponent();
				Rectangle r = new Rectangle(c.getBounds());
				Rectangle s = c.getGraphicsConfiguration().getBounds();
				boolean fix = false;
				if (r.x < s.x || r.y < s.y || r.width > s.width || r.height > s.height)
				{
					fix = true;
				}
				if (fix)
				{
					scheduleFix(c, this);
				}
			}
		});
	}

	public static void removeAWTEvents(Class<? extends Event>[] eventClasses)
	{
		EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
		while (eq.peekEvent() != null)
		{
			AWTEvent event = eq.peekEvent();
			boolean extendsEvent = false;
			for (Class<? extends Event> c : eventClasses)
			{
				if (c.isInstance(event))
				{
					extendsEvent = true;
					break;
				}
			}
			if (extendsEvent)
			{
				try
				{
					eq.getNextEvent();
				}
				catch (Exception e)
				{

				}
			}
			else
			{
				break;
			}
		}
	}

	public static void openWebBrowser(String url)
	{
		try
		{
			if (OS.isMacOSX())
			{
				Runtime.getRuntime().exec("open " + url);
			}
			else if (OS.isWindows())
			{
				Runtime.getRuntime().exec("cmd /c start " + url);
			}
			else if (OS.isLinux())
			{
				Runtime.getRuntime().exec("firefox " + url);
			}
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static void layout(Component c)
	{
		if (c instanceof Window)
		{
			c.validate();
		}
		else
		{
			c = c.getParent();
			if (c != null)
			{
				layout(c);
			}
		}
	}

	public static void setDefaultLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void addDefaultWindowCloseEvent(Window w)
	{
		w.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
				System.exit(0);
			}
		});
	}

	public static void centerOnParent(Window w)
	{
		try
		{
			Rectangle pbounds = w.getParent().getBounds();
			Rectangle dbounds = w.getBounds();
			// w.setBounds(pbounds.x + (pbounds.width - dbounds.width) / 2, pbounds.y + (pbounds.height - dbounds.height) / 2, dbounds.width, dbounds.height +
			// 40);
			w.setLocation(pbounds.x + (pbounds.width - dbounds.width) / 2, pbounds.y + (pbounds.height - dbounds.height) / 2);
			w.validate();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static void centerOnParent(Window w, Window q)
	{
		try
		{
			Rectangle pbounds = q.getBounds();
			Rectangle dbounds = w.getBounds();
			w.setBounds(pbounds.x + (pbounds.width - dbounds.width) / 2, pbounds.y + (pbounds.height - dbounds.height) / 2, dbounds.width, dbounds.height + 40);
			w.validate();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static Point centerRectange(Rectangle outter, Rectangle inner)
	{
		int x = (outter.width - inner.width) / 2;
		int y = (outter.height - inner.height) / 2;
		return new Point(x, y);
	}

	public static void repaintTop(Component c)
	{
		while (c.getParent() != null)
		{
			c = c.getParent();
		}
		c.repaint();

	}

	public static void disposeTop(Component c)
	{
		while (c.getParent() != null)
		{
			c = c.getParent();
		}
		c.setVisible(false);
		if (c instanceof Window)
		{
			((Window) c).dispose();
		}
	}

	public static java.awt.Component getComponent(java.awt.MenuComponent item)
	{
		java.awt.MenuContainer c = item.getParent();
		if (c instanceof java.awt.Component)
		{
			return (java.awt.Component) c;
		}
		if (c instanceof java.awt.MenuComponent)
		{
			return getComponent((java.awt.MenuComponent) c);
		}
		return null;
	}

	public static java.awt.Window getWindow(Component item)
	{
		if (item instanceof Window)
		{
			return (java.awt.Window) item;
		}
		if (item.getParent() == null)
		{
			return null;
		}
		return getWindow(item.getParent());
	}

	public static java.awt.Frame getFrame(Component item)
	{
		if (item instanceof Frame)
		{
			return (java.awt.Frame) item;
		}
		if (item instanceof JPopupMenu)
		{
			return getFrame(((JPopupMenu) item).getInvoker());
		}
		if (item.getParent() == null)
		{
			return null;
		}
		return getFrame(item.getParent());
	}

	public static Component getComponent(Object item)
	{
		if (item instanceof Component)
		{
			return (Component) item;
		}
		if (item instanceof EventController)
		{
			return getComponent(((EventController) item).getAdapter().getParent());
		}
		throw new RuntimeException("Can not find parent");
	}

	public static java.awt.Image makeTransparent(java.awt.Image im, byte transparency)
	{
		if (im == null)
		{
			return null;
		}
		final int tr = transparency << 24;
		java.awt.image.ImageFilter filter = new java.awt.image.RGBImageFilter()
		{
			public final int filterRGB(int x, int y, int rgb)
			{
				return 0x00FFFFFF & rgb | tr;
			}
		};
		java.awt.image.ImageProducer ip = new java.awt.image.FilteredImageSource(im.getSource(), filter);
		java.awt.Image ret = Toolkit.getDefaultToolkit().createImage(ip);
		return ret;
	}

	public static Graphics addRenderingHints(Graphics g)
	{
		if (g instanceof Graphics2D)
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		}
		return g;
	}

	public static boolean tryNimbus()
	{
		try
		{
			String className = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
			Class.forName(className).toString();
			UIManager.setLookAndFeel(className);
			return true;
		}
		catch (Throwable t)
		{
			return false;
		}
	}

	public static boolean tryEASynth()
	{
		try
		{
			String className = "com.easynth.lookandfeel.EaSynthLookAndFeel";
			Class.forName(className).toString();
			UIManager.setLookAndFeel(className);
			return true;
		}
		catch (Throwable t)
		{
			return false;
		}
	}

	public static void setTooltipDelays(int initial, int dismiss, int reshow)
	{
		ToolTipManager manager = ToolTipManager.sharedInstance();
		manager.setInitialDelay(initial);
		manager.setDismissDelay(dismiss);
		manager.setReshowDelay(reshow);
	}

	/**
	 * @param c
	 *            JComponent
	 * @param propertyName
	 *            if getter public can export, if setter public can import
	 * @param method
	 *            TransferHandler.COPY|MOVE|LINK
	 */
	public static void makeExportable(JComponent c, String propertyName, final int method)
	{
		c.setTransferHandler(new TransferHandler(propertyName));
		c.addMouseMotionListener(new MouseMotionListener()
		{

			public void mouseMoved(MouseEvent e)
			{

			}

			public void mouseDragged(MouseEvent e)
			{
				JComponent jc = (JComponent) e.getSource();
				TransferHandler th = jc.getTransferHandler();
				if (th != null)
				{
					th.exportAsDrag(jc, e, method);
				}
			};
		});
	}

	void dump()
	{
		Comparator<Entry<Object, Object>> cmp = new Comparator<Entry<Object, Object>>()
		{
			public int compare(Entry<Object, Object> o1, Entry<Object, Object> o2)
			{
				return o1.getKey().toString().compareToIgnoreCase(o2.getKey().toString());
			}
		};
		Set<Entry<Object, Object>> set = new TreeSet<Entry<Object, Object>>(cmp);
		set.addAll(UIManager.getLookAndFeelDefaults().entrySet());
		System.out.println("-------------");
		for (Entry<Object, Object> i : set)
		{
			System.out.println(i.getKey() + "\t\t\t" + i.getValue());
		}

	}

	static class CustomGlassPane extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public CustomGlassPane()
		{
			setOpaque(false);
			setEnabled(false);
			addMouseMotionListener(new MouseMotionAdapter()
			{
				@Override
				public void mouseMoved(MouseEvent e)
				{
					setVisible(false);
					System.out.println(e);
					super.mouseMoved(e);
				}

				@Override
				public void mouseDragged(MouseEvent e)
				{
					System.out.println(e);
					super.mouseDragged(e);
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			JComponent visual = this.visual;
			if (visual != null)
			{
				g.setColor(Color.red);
				Dimension d = visual.getPreferredSize();

				java.awt.Point p = new java.awt.Point(x - d.width / 2, y - d.height / 2);
				if (OS.isMacOSX())
				{
					p.y += 4;
				}
				g.translate(p.x, p.y);
				visual.setSize(d);
				if (visual.getClass().getName().contains("JPanel"))
				{
					g.drawString("Here goes visual representation...", 0, 16);
				}
				else
				{
					visual.paint(g);
				}
				g.translate(-p.x, -p.y);
			}
		}

		public JComponent visual;
		public int x;
		public int y;
	};

	private static CustomGlassPane cgp = null;

	private static CustomGlassPane getCGP()
	{
		if (cgp == null)
		{
			cgp = new CustomGlassPane();
		}
		return cgp;
	}

	public static void setVisual(JComponent c)
	{
		getCGP().visual = c;
	}

	public static void displayVisual2()
	{
		java.awt.Point point = MouseInfo.getPointerInfo().getLocation();
		CustomGlassPane cgp = getCGP();
		SwingUtilities.convertPointFromScreen(point, getCGP());
		if (point.x != cgp.x || point.y != cgp.y)
		{
			cgp.x = point.x;
			cgp.y = point.y;
			if (image != null)
			{
				Graphics g = getFrame(cgp).getGraphics();
				g.drawImage(image, 0, 0, null);
				cgp.paintComponent(g);
			}
			if (cgp.visual == null)
			{
				Thread.currentThread().interrupt();
			}
			// cgp.repaint();
			// getFrame(cgp).invalidate();
		}
	}

	static boolean lastVisual = false;
	static BufferedImage image;

	public static void displayVisual2(boolean visible, JComponent c)
	{
		JFrame f = (JFrame) getFrame(c);
		if (f.getGlassPane() != getCGP())
		{
			f.setGlassPane(getCGP());
			getCGP().setSize(f.getSize());
			getCGP().setVisible(true);
		}
		if (visible && !lastVisual)
		{
			image = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = image.getGraphics();
			f.printAll(g);
			g.dispose();
			lastVisual = visible;
		}
		if (!visible && lastVisual)
		{
			lastVisual = visible;
			image = null;
			f.repaint();
		}
		displayVisual2();
	}

	public static void fixOpaque(Container container)
	{
		for (Component comp : container.getComponents())
		{
			if (comp instanceof JComponent)
			{
				((JComponent) comp).setOpaque(false);
			}
			if (comp instanceof Container)
			{
				fixOpaque((Container) comp);
			}
		}
	}

	public static void invalidate(Component c)
	{
		c.invalidate();
		if (c instanceof Container)
		{
			for (Component child : ((Container) c).getComponents())
			{
				invalidate(child);
			}
		}
	}

	public static void repaintAll(Component c)
	{
		c.repaint();
		if (c instanceof Container)
		{
			for (Component child : ((Container) c).getComponents())
			{
				repaintAll(child);
			}
		}
	}

	public static void setEnabled(Component c, boolean state)
	{
		c.setEnabled(false);
		if (c instanceof Container)
		{
			for (Component child : ((Container) c).getComponents())
			{

				setEnabled(child, state);
			}
		}
	}

	public static void setIcon(final String url, final Window frame)
	{
		try
		{
			ImageIcon icon = new ImageIcon(UIHelpers.class.getResource(url));
			icon.setImageObserver(new ImageObserver()
			{

				public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
				{
					if (infoflags == ImageObserver.ALLBITS)
					{
						frame.setIconImage(img);
					}
					return (infoflags == ImageObserver.ALLBITS);
				}
			});
			frame.setIconImage(icon.getImage());
			if (OS.isMacOSX())
			{
				MacOSX.setIcon(icon, frame);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		catch (Error er)
		{
			er.printStackTrace();
		}
	}

	private static void setTitle(final String title)
	{
		MacOSX.setTitle(title);
	}

	public static String getTitle()
	{
		return setTitle;
	}

	private static String setTitle = null;

	public static String getVersionString()
	{
		StringBuffer sb = new StringBuffer("v1/");
		try
		{
			sb.append(System.getProperty("java.version"));
			sb.append("/");
			if (OS.isLinux())
			{
				sb.append("Linux");
			}
			else if (OS.isMacOSX())
			{
				sb.append("MacOS");
			}
			else if (OS.isWindows())
			{
				sb.append("Win");
			}
			else
			{
				sb.append("Unknown");
			}
			sb.append("/");
			sb.append(System.getProperty("os.arch"));
			sb.append("/");
			sb.append(Runtime.getRuntime().availableProcessors());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		catch (Error ex2)
		{
			ex2.printStackTrace();
		}
		return sb.toString();
	}

	public static void addTracking(final String title)
	{
		setTitle(title);
		setTitle = title;
		try
		{
			Tracking.track(title, "StartApp/" + getVersionString());
			Runtime.getRuntime().addShutdownHook(new Thread()
			{
				@Override
				public void run()
				{
					Tracking.track(title, "StopApp");
				}
			});
		}
		catch (java.security.AccessControlException ex)
		{
			System.err.println("Can not register shutdown hook");
		}
	}

	public static void track(String event)
	{
		String title = getTitle();
		if (title == null)
		{
			(new RuntimeException("Title is not set! - not tracking.")).printStackTrace();
		}
		else
		{
			Tracking.track(title, event);
		}
	}

	public static KeyStroke getAcceleratorKey(char c)
	{
		return KeyStroke.getKeyStroke(Character.toUpperCase(c), OS.isMacOSX() ? KeyEvent.META_DOWN_MASK : KeyEvent.CTRL_DOWN_MASK);
	}

	public static void sleep(int i)
	{
		try
		{
			Thread.sleep(i);
		}
		catch (Exception ex)
		{
		}
	}

	private static RepaintManager oldRepaintManager;

	public static void registerWebRepaintManager(JFrame frame)
	{
		if (oldRepaintManager == null)
		{
			oldRepaintManager = RepaintManager.currentManager(frame);
			RepaintManager.setCurrentManager(new RepaintManager()
			{

				HashSet<Rectangle> list = new HashSet<Rectangle>();

				private void addDirtyRegion0(Container c, int x, int y, int w, int h)
				{
					if (c.isShowing() && w > 0 && h > 0)
					{
						Point p = c.getLocationOnScreen();
						synchronized (list)
						{
							list.add(new Rectangle(p.x + x, p.y + y, w, h));
						}
					}
				}

				@Override
				public void addDirtyRegion(Applet applet, int x, int y, int w, int h)
				{
					super.addDirtyRegion(applet, x, y, w, h);
					addDirtyRegion0(applet, x, y, w, h);
				}

				@Override
				public void addDirtyRegion(JComponent c, int x, int y, int w, int h)
				{
					super.addDirtyRegion(c, x, y, w, h);
					addDirtyRegion0(c, x, y, w, h);
				}

				@Override
				public void addDirtyRegion(Window window, int x, int y, int w, int h)
				{
					super.addDirtyRegion(window, x, y, w, h);
					addDirtyRegion0(window, x, y, w, h);
				}

				@Override
				public void paintDirtyRegions()
				{
					super.paintDirtyRegions();
					synchronized (list)
					{
						System.out.println("Send start.");
						Iterator<Rectangle> iter = list.iterator();
						if (iter.hasNext())
						{
							Rectangle send = iter.next();
							while (iter.hasNext())
							{
								Rectangle r = iter.next();
								if (send.contains(r))
								{
								}
								if (send.intersects(r))
								{
									send = send.union(r);
								}
								else
								{
									System.out.println("Send " + send);
									send = r;
								}
							}
							if (send != null)
							{
								System.out.println("Send " + send);
								send = null;
							}
						}

						// for (Rectangle r : list)
						// {
						// System.out.println("\tSend: " + r);
						// }
						list.clear();
						System.out.println("Send end.");
					}
				}
			});
		}

	}

	public static class PleaseWait implements Runnable
	{
		Frame f;
		int time;
		String text;
		static int done = 0;

		public PleaseWait(Frame f, int time, String text)
		{
			this.f = f;
			this.time = time;
			this.text = text;
			done--;
			(new Thread(this)).start();
		}

		public void run()
		{
			boolean painted = false;
			sleep(time);
			Dimension d = f.getSize();
			if (done != 0)
			{
				Graphics g = f.getGraphics();
				g.setFont(g.getFont().deriveFont(32f));
				int w = g.getFontMetrics().stringWidth(text);
				int h = g.getFontMetrics().getAscent();
				int dd = g.getFontMetrics().getDescent();

				g.setColor(new Color(64, 64, 64, 64));
				g.fillRect(0, 0, d.width, d.height);
				g.setColor(new Color(32, 32, 32, 192));
				int margin = 10;
				g.fillRoundRect((d.width - w) / 2 - margin, d.height / 2 - h - margin, w + margin * 2, h + margin * 2 + dd, 20, 20);
				g.setColor(Color.white);
				g.drawString(text, (d.width - w) / 2, d.height / 2);
				g.dispose();
				painted = true;
			}
			while (done != 0)
			{
				sleep(50);
			}
			if (painted)
			{
				SwingUtilities.invokeLater(new Runnable()
				{

					public void run()
					{
						synchronized (f.getTreeLock())
						{

							invalidate(f);
							RepaintManager.currentManager(f).addDirtyRegion(f, 0, 0, 5000, 5000);
							repaintAll(f);
							f.validate();
						}
					}
				});
			}
		}

		public void done()
		{
			done++;
		}
	}

}
