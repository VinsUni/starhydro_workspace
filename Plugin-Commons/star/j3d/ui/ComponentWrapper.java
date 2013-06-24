package star.j3d.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.Hashtable;

import mit.awt.xPanel;
import star.event.Adapter;
import star.event.Event;
import star.event.Listener;
import star.j3d.ui.signal.DirtyRegionEvent;
import star.j3d.ui.signal.DirtyRegionRaiser;
import star.j3d.ui.signal.ImageCompleteEvent;
import star.j3d.ui.signal.ImageCompleteRaiser;

public class ComponentWrapper extends xPanel implements Listener, ImageCompleteRaiser
{

	private static final long serialVersionUID = 1L;

	ComponentWrapper self = this;
	Component c;
	Image transparentImage;
	Image image;
	boolean optimized = false;

	public ComponentWrapper(Component c)
	{
		this.c = c;
		add(c);
		optimized = javax.swing.RepaintManager.currentManager(this) instanceof star.j3d.ui.RepaintManager;
		setBackground(this);
	}

	public static void setBackground(Component c)
	{
		c.setBackground(null);
		c.setForeground(null);
		if (c instanceof java.awt.Container)
		{
			java.awt.Container c2 = (java.awt.Container) c;
			for (Component cmp : c2.getComponents())
			{
				setBackground(cmp);
			}
		}
	}

	public void paintImage(Graphics g, boolean transparent)
	{
		// if( !optimized ) clearImages() ;
		if (c.getWidth() > 0 && c.getHeight() > 0)
		{
			Image image = getImage(transparent);
			Rectangle bounds = this.getBounds();
			g.setPaintMode();
			g.setClip(bounds.x, bounds.y, bounds.width, bounds.height);
			g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, this);
		}
	}

	public void clearImages()
	{
		transparentImage = null;
		image = null;
	}

	@Override
	public void paint(Graphics g)
	{
		Image image = getImage(false);
		Rectangle bounds = this.getBounds();
		g.setPaintMode();
		g.setClip(0, 0, bounds.width, bounds.height);
		g.drawImage(image, 0, 0, bounds.width, bounds.height, this);
	}

	@Override
	public void update(Graphics g)
	{
		if (isShowing())
		{
			paint(g);
		}
		// System.out.println( "update " ) ;
		// super.update(g);
	}

	private Image getImage()
	{
		Component c = this.c;
		Image img = null;
		if (c.getWidth() > 0 && c.getHeight() > 0)
		{
			img = createImage(c.getWidth(), c.getHeight());
			Graphics g = img.getGraphics();
			c.print(g);
			c.printAll(g);
			g.dispose();
		}
		return img;
	}

	int currentImage = 0;
	int updateImage = -1;

	public Image getImage(boolean transparent)
	{
		Image ret = null;
		if (transparent)
		{
			if (getWidth() > 0 && getHeight() > 0)
			{
				if (transparentImage == null)
				{
					transparentImage = makeTransparent(getImage(false));
				}
			}
			ret = transparentImage;
		}
		else
		{
			if (getWidth() > 0 && getHeight() > 0)
			{
				if (image == null)
				{
					currentImage = (currentImage + 1) & 0xffff;
					image = getImage();
				}
			}
			ret = image;
		}
		return ret;
	}

	private Image makeTransparent(Image im)
	{
		if (im == null)
		{
			return null;
		}
		ImageFilter filter = new RGBImageFilter()
		{
			public final int filterRGB(int x, int y, int rgb)
			{
				return 0x00FFFFFF & rgb | 0x7f000000;
			}
		};
		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		ip.addConsumer(new ImageConsumer()
		{

			public void imageComplete(int status)
			{
				// repaint();
				(new ImageCompleteEvent(self)).raise();
			}

			public void setColorModel(ColorModel model)
			{
				// TODO Auto-generated method stub

			}

			public void setDimensions(int width, int height)
			{
				// TODO Auto-generated method stub

			}

			public void setHints(int hintflags)
			{
				// TODO Auto-generated method stub

			}

			public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize)
			{
				// TODO Auto-generated method stub

			}

			public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize)
			{
				// TODO Auto-generated method stub

			}

			public void setProperties(Hashtable<?, ?> props)
			{
				// TODO Auto-generated method stub

			}
		});
		Image ret = Toolkit.getDefaultToolkit().createImage(ip);
		return ret;
	}

	Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}

	@Override
	public void validate()
	{
		super.validate();
		clearImages();
	}

	public void addNotify()
	{
		setLayout(new BorderLayout());
		getAdapter().addHandled(DirtyRegionEvent.class);
		super.addNotify();
		setBackground(c);
	}

	public void removeNotify()
	{
		getAdapter().removeHandled(DirtyRegionEvent.class);
		super.removeNotify();
	}

	public void eventRaised(final Event event)
	{
		if (event instanceof DirtyRegionEvent)
		{
			handleEvent((DirtyRegionRaiser) event.getSource());
		}
	}

	protected void handleEvent(DirtyRegionRaiser raiser)
	{
		if (this.isAncestorOf(raiser.getDirtyComponent()))
		{
			/* remove next for lines if after uncomment still broken */
			if (c.isVisible() != super.isVisible())
			{
				setVisible(c.isVisible());
			}
			validate();
			repaint(100);

		}
		else if (raiser.getDirtyComponent().isShowing() && this.isShowing())
		{
			Rectangle this_rect = getBounds();
			this_rect.setLocation(getLocationOnScreen());
			Rectangle c_rect = raiser.getDirtyComponent().getBounds();
			c_rect.setLocation(raiser.getDirtyComponent().getLocationOnScreen());
			if (this_rect.intersects(c_rect))
			{
				invalidate();
				validate();
			}
		}
	}

	public String toString()
	{
		return c.toString();
	}

}
