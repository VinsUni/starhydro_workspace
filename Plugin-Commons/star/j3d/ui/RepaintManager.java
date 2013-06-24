package star.j3d.ui;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import star.event.Adapter;
import star.event.EventController;
import star.j3d.ui.signal.DirtyRegionEvent;
import star.j3d.ui.signal.DirtyRegionRaiser;

class RepaintManager extends javax.swing.RepaintManager implements EventController, DirtyRegionRaiser
{

	Adapter adapter = new Adapter(this);
	RepaintManager self = this;

	public void addDirtyRegion(JComponent c, int x, int y, int w, int h)
	{
		synchronized (adapter)
		{
			super.addDirtyRegion(c, x, y, w, h);
			raise(c, x, y, w, h);
		}
	}

	@Override
	public synchronized void addInvalidComponent(JComponent invalidComponent)
	{
		super.addInvalidComponent(invalidComponent);
	}

	public Adapter getAdapter()
	{
		return adapter;
	}

	ArrayList<JComponent> dirtyComponent = new ArrayList<JComponent>();
	ArrayList<Rectangle> dirtyRegion = new ArrayList<Rectangle>();

	public JComponent getDirtyComponent()
	{
		synchronized (adapter)
		{
			return dirtyComponent.get(0);
		}
	}

	public Rectangle getDirtyRegion()
	{
		synchronized (adapter)
		{
			return dirtyRegion.get(0);
		}
	}

	void removeOne()
	{
		synchronized (adapter)
		{
			dirtyComponent.remove(0);
			dirtyRegion.remove(0);
		}

	}

	void addOne(JComponent c, int x, int y, int w, int h)
	{
		synchronized (adapter)
		{
			dirtyComponent.add(c);
			dirtyRegion.add(new Rectangle(x, y, w, h));

		}
	}

	private boolean inValidate = false;

	synchronized void raise(JComponent c, int x, int y, int w, int h)
	{
		if (!dirtyComponent.contains(c))
		{
			if (c.getWidth() > 0 && c.getHeight() > 0)
			{
				addOne(c, x, y, w, h);
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						synchronized (adapter)
						{
							(new DirtyRegionEvent(self)).raise();
							removeOne();
						}
					}
				});
			}
			else
			{
				Component parent = c.getParent();
				while (parent != null)
				{
					if (parent instanceof ComponentWrapper)
					{
						final ComponentWrapper p = (ComponentWrapper) parent;
						if (!inValidate)
						{
							inValidate = true;
							p.invalidate();
							((ComponentWrapper) p).validate();
							inValidate = false;
						}

						break;
					}
					parent = parent.getParent();
				}
			}
		}
	}

	public void addNotify()
	{

	}

	public void removeNotify()
	{

	}
}
