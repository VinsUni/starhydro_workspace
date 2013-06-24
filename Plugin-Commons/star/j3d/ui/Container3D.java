package star.j3d.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.SwingUtilities;

import mit.awt.xPanel;
import star.event.Event;
import star.event.Listener;
import star.j3d.ui.signal.ImageCompleteEvent;
import star.j3d.ui.signal.ImageCompleteRaiser;

import com.sun.j3d.utils.universe.SimpleUniverse;

public class Container3D extends xPanel implements ContainerListener, Listener
{
	private static final long serialVersionUID = 1L;

	private Canvas3D canvas = null;

	public Canvas3D getCanvas()
	{
		if (null == this.canvas)
		{
			GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
			if( gc == null )
			{
				gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			}
			this.canvas = new Canvas3D(gc);
		}
		return this.canvas;
	}

	public Container3D()
	{
		addContainerListener(this);
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		add(getCanvas());
		// if( !(javax.swing.RepaintManager.currentManager(this) instanceof RepaintManager) )
		// {
		// RepaintManager rm = new RepaintManager();
		// javax.swing.RepaintManager.setCurrentManager(rm);
		// }
		// if( javax.swing.RepaintManager.currentManager(this) instanceof RepaintManager )
		// {
		// getAdapter().addComponent((RepaintManager) javax.swing.RepaintManager.currentManager(this));
		// }
		getAdapter().addHandled(ImageCompleteEvent.class);
	}

	@Override
	public void removeNotify()
	{
		super.removeNotify();
		// if( !(javax.swing.RepaintManager.currentManager(this) instanceof RepaintManager) )
		// {
		// getAdapter().removeComponent((RepaintManager) javax.swing.RepaintManager.currentManager(this));
		// }
		getAdapter().removeHandled(ImageCompleteEvent.class);
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		getCanvas().paint(g);
	}

	public void resetUI()
	{
		getCanvas().setUI(0);
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index)
	{
		if (comp instanceof star.j3d.ui.Canvas3D)
		{
			super.addImpl(comp, constraints, index);
		}
		else
		{
			ComponentWrapper c = new ComponentWrapper(comp);
			components.add(c);
			super.addImpl(c, constraints, index);
		}
	}

	public void clearUI()
	{
		synchronized (getAdapter())
		{
			Component[] c = getComponents();
			for (int i = 0; c != null && i < c.length; i++)
			{
				if (!(c[i] instanceof Canvas3D))
				{
					try
					{
						remove(c[i]);
					}
					catch (Exception ex)
					{
					}
				}
			}
			invalidate();
		}
	}

	@Override
	public void setLayout(LayoutManager mgr)
	{
		super.setLayout(new LayoutWrapper(mgr, this));
	}

	boolean inLayout = false;

	void setInLayout(boolean inLayout)
	{
		this.inLayout = inLayout;
	}

	@Override
	public int getComponentCount()
	{
		if (inLayout)
		{
			return components.size();
		}
		else
		{
			return super.getComponentCount();
		}
	}

	@Override
	public Component getComponent(int n)
	{
		if (inLayout)
		{
			return components.get(n);
		}
		else
		{
			return super.getComponent(n);
		}
	}

	java.util.ArrayList<Component> components = new java.util.ArrayList<Component>();

	public void componentAdded(ContainerEvent e)
	{
		if (e.getContainer() == this)
		{
			if (!(e.getChild() instanceof Canvas3D))
			{
				if (!components.contains(e.getChild()))
				{
					components.add(e.getChild());
				}
			}
		}
	}

	public void componentRemoved(ContainerEvent e)
	{
		if (e.getContainer() == this)
		{
			if (!(e.getChild() instanceof Canvas3D))
			{
				if (components.contains(e.getChild()))
				{
					components.remove(e.getChild());
				}
			}
		}
	}

	public void eventRaised(final Event event)
	{
		if (event instanceof ImageCompleteEvent)
		{
			handleEvent((ImageCompleteRaiser) event.getSource());
		}
	}

	protected void handleEvent(ImageCompleteRaiser raiser)
	{
		final Component child = /* getChildThatIsAncestorOf( */(ComponentWrapper) raiser/* ) */;
		if (child != null)
		{
			SwingUtilities.invokeLater(new Runnable()
			{

				public void run()
				{
					Rectangle r = child.getBounds();
					getCanvas().repaint(100, r.x, r.y, r.width, r.height);
				}
			});
		}
	}

	public Component getChildThatIsAncestorOf(Component c)
	{
		Container p;
		if (c == null || ((p = c.getParent()) == null))
		{
			return null;
		}
		while (p != null)
		{
			if (p.getParent() == this)
			{
				return p;
			}
			p = p.getParent();
		}
		return null;
	}

	public boolean getAllOrNone()
	{
		return true;
	}

}
