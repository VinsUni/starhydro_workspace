/*
 * $Id: Canvas3D.java,v 1.22 2007/10/18 19:29:35 cshubert Exp $ 
 */

package star.j3d.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.j3d.J3DGraphics2D;

public class Canvas3D extends javax.media.j3d.Canvas3D
{
	private static final long serialVersionUID = 1L;
	boolean disableEvents = false;

	class MouseMotionListener extends java.awt.event.MouseAdapter implements javax.swing.event.MouseInputListener
	{
		boolean dragging = false;
		final Rectangle rect = new Rectangle(5, 5, 35, 25);
		int eye_state = 0;

		public void mouseClicked(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 2)
			{
				disableEvents = !disableEvents;
			}
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1 && rect.contains(e.getPoint()))
			{
				eye_state = (eye_state + 1) % 4;
				boolean uiVisible;
				boolean currentZorderAbove;
				boolean disableEvents2;
				switch (eye_state)
				{
				case 0:
					uiVisible = true;
					currentZorderAbove = true;
					disableEvents2 = true;
					break;
				case 1:
					uiVisible = true;
					currentZorderAbove = false;
					disableEvents2 = true;
					break;
				case 3:
					disableEvents2 = false;
					uiVisible = true;
					currentZorderAbove = false;
					break;
				case 2:
				default:
					uiVisible = false;
					currentZorderAbove = false;
					disableEvents2 = true;
					break;
				}
				if (disableEvents2 != disableEvents)
				{
					if (disableEvents2)
					{
						removeMouseMotionListener(this);
						disableEvents = disableEvents2;
					}
					else
					{
						addMouseMotionListener(this);
						disableEvents = disableEvents2;
					}
				}
				setUIVisible(uiVisible);
				currentRectangle = currentZorderAbove ? -1 : 0;
				validate();
				repaint();
			}
		}

		public void mouseDragged(MouseEvent e)
		{
			if (!disableEvents)
			{
				if (!dragging)
				{
					dragging = true;
					setUIVisible(false);
					update(e.getPoint());
				}
			}
		}

		public void mouseMoved(MouseEvent e)
		{
			if (!disableEvents)
			{
				if (dragging)
				{
					dragging = false;
					setUIVisible(true);
				}
				update(e.getPoint());
			}
		}

		public void mouseExited(MouseEvent e)
		{
			if (!disableEvents)
			{

				// System.out.println("mouseExited");
				update(e.getPoint());
			}
		}

		public void mouseEntered(MouseEvent e)
		{
			if (!disableEvents)
			{

				// System.out.println("mouseEntered");
				update(e.getPoint());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (!disableEvents)
			{
				setUIVisible(true);
				update(e.getPoint());
			}
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (!disableEvents)
			{
				setUIVisible(false);
				update(e.getPoint());
				Zreorder(false);
			}
		}

		void update(Point p)
		{
			if (!disableEvents)
			{
				int rectangle = getLayoutAreas(p);
				setUI(rectangle);
			}
		}

	};

	int currentRectangle = -1;
	HashMap<Component, Image> componentImages = new HashMap<Component, Image>();
	ArrayList<ComponentWrapper> bgComponents = new ArrayList<ComponentWrapper>();
	ArrayList<ComponentWrapper> fgComponents = new ArrayList<ComponentWrapper>();
	boolean uiVisible = true;
	boolean currentZorderAbove = false;
	boolean clearBg = false;

	MouseMotionListener listener;

	public void addNotify()
	{
		if (listener == null)
		{
			listener = new MouseMotionListener();
			addMouseMotionListener(listener);
			addMouseListener(listener);
		}
		super.addNotify();
	}

	public void removeNotify()
	{
		if (listener != null)
		{
			removeMouseMotionListener(listener);
			removeMouseListener(listener);
			listener = null;
		}
		super.removeNotify();
	}

	public Canvas3D(GraphicsConfiguration graphicsConfiguration)
	{
		super(graphicsConfiguration);
	}

	long currentTime;
	long newTime;
	boolean frameRate = false;
	Color frameRateColor = Color.yellow.darker();

	public void preRender()
	{
		if (clearBg)
		{
			J3DGraphics2D gr = getGraphics2D();
			gr.setClip(0, 0, getWidth(), getHeight());
			gr.setColor(getBackground());
			gr.fillRect(0, 0, getWidth(), getHeight());
			gr.flush(true);
			clearBg = false;
		}
		if (uiVisible)
		{
			paint(bgComponents, true, true);
		}
		if (frameRate)
		{
			currentTime = System.nanoTime();
		}
	}

	public void postRender()
	{
		frameRate = false;
		if (frameRate)
		{
			long lastNewTime = newTime;
			newTime = System.nanoTime();
			String str = (Math.round(1e10 / (newTime - currentTime)) / 10) + "fps (eff: " + (Math.round(1e10 / (newTime - lastNewTime)) / 10) + " fps)";
			J3DGraphics2D gr = getGraphics2D();
			gr.setClip(0, 0, getWidth(), getHeight());
			gr.setColor(frameRateColor);
			gr.drawString(str, getWidth() - 150, getHeight() - 0);

			if (!disableEvents)
			{
				gr.setColor(Color.yellow);
				gr.drawString("D", 25, 18);
			}
			if (uiVisible)
			{
				// open eye
				gr.setColor(Color.green);
				gr.drawArc(10, 10, 15, 10, 30, 120);
				gr.drawArc(10, 7, 15, 10, -30, -120);
				gr.fillArc(15, 10, 5, 5, 0, 360);
			}
			else
			{
				// closed eye
				gr.setColor(Color.red);
				gr.fillArc(10, 10, 15, 10, 30, 120);
				gr.drawArc(10, 7, 15, 10, -30, -120);
			}
			if (!currentZorderAbove)
			{
				// disabled
				gr.setColor(Color.red);
				gr.drawLine(10, 20, 25, 5);
			}
			gr.flush(true);
			// long newTime2 = System.nanoTime();
			// System.out.println(newTime2 - newTime);
		}
		if (uiVisible)
		{
			paint(fgComponents, false, true);
		}
		if (getPaintHook() != null)
		{
			getPaintHook().paint(getGraphics2D(), getSize());
		}
	}

	public synchronized void paint(final ArrayList<ComponentWrapper> components, boolean transparent, boolean flush)
	{
		if (uiVisible && components.size() != 0)
		{
			J3DGraphics2D gr = getGraphics2D();
			try
			{
				Iterator<ComponentWrapper> iter = components.iterator();
				while (iter.hasNext())
				{
					ComponentWrapper c = iter.next();
					if (!c.isValid())
					{
						c.validate();
					}
					c.paintImage(gr, transparent);
				}
			}
			catch (ConcurrentModificationException ex)
			{
			}
			gr.flush(flush);
		}

	}

	private int getLayoutAreas(Point p)
	{
		return (getParent().getLayout() != null) ? ((LayoutHelper) getParent().getLayout()).getLayoutAreas(p) : 0;
	}

	private void setUIVisible(boolean visible)
	{
		uiVisible = visible;
	}

	public void setUI(int rectangleBitMap)
	{
		if (currentRectangle != rectangleBitMap)
		{
			currentRectangle = rectangleBitMap;
			clearBg = true;
			validate();
		}
	}

	@Override
	public void validate()
	{
		Dimension size = getSize();
		if ((0 < size.width) && (0 < size.height))
		{
			boolean isAbove = paintComponents();
			Zreorder(isAbove);
		}
		super.validate();
	}

	boolean paintComponents()
	{
		boolean isAbove = false;
		ArrayList<ComponentWrapper> bgComponents = new ArrayList<ComponentWrapper>();
		ArrayList<ComponentWrapper> fgComponents = new ArrayList<ComponentWrapper>();

		bgComponents.clear();
		fgComponents.clear();
		Component[] c = getParent().getComponents();
		if (null != c)
		{
			for (int i = 0; i < c.length; i++)
			{
				Component component = c[i];
				if (!(component instanceof Canvas3D) && component.isVisible() && component.getWidth() > 0 && component.getHeight() > 0)
				{
					((ComponentWrapper) component).clearImages();
					if (((LayoutHelper) getParent().getLayout()).isComponentInRectangle(component, currentRectangle))
					{
						isAbove = true;
						fgComponents.add((ComponentWrapper) component);

					}
					else
					{
						bgComponents.add((ComponentWrapper) component);
					}
				}
			}
		}

		this.bgComponents = bgComponents;
		this.fgComponents = fgComponents;
		return isAbove;
	}

	private void Zreorder(boolean isAbove)
	{
		if (currentZorderAbove != isAbove)
		{
			currentZorderAbove = isAbove;
			setCursor(currentZorderAbove ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			Component[] c = getParent().getComponents();
			int zcanvas;
			int zother;

			if (isAbove)
			{
				zcanvas = c.length - 1;
				zother = 0;
			}
			else
			{
				zcanvas = 0;
				zother = 1;
			}

			for (int i = 0; c != null && i < c.length; i++)
			{
				try
				{
					getParent().setComponentZOrder(c[i], c[i] instanceof Canvas3D ? zcanvas : zother++);
				}
				catch (Exception ex)
				{
					System.err.println("Zorder exception for " + c[i].getClass().getName());
				}
			}
			// repaint();
		}
	}

	boolean inValidate = false;

	@Override
	public void repaint(long tm, int x, int y, int width, int height)
	{
		if (!getParent().isValid())
		{
			inValidate = true;
			if (!inValidate)
			{
				validate();
			}
			inValidate = false;
		}
		super.repaint(tm, x, y, width, height);
	}

	PaintHook paintHook = null;

	public PaintHook getPaintHook()
	{
		return paintHook;
	}

	public void setPaintHook(PaintHook paintHook)
	{
		this.paintHook = paintHook;
	}

}