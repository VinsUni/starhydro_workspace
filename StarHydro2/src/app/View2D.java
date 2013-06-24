package app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.event.Adapter;
import starhydro.events.AccumulationThresholdChangedRaiser;
import starhydro.events.DelineateWatershedRaiser;
import starhydro.events.LoadTileRaiser;
import starhydro.events.ViewLocationChangeRaiser;
import starhydro.events.ViewScaleChangedRaiser;
import starhydro.events.WatershedAvailableRaiser;
import starhydro.model.FloatWorld;
import starhydro.model.WorldElevation;
import starhydro.utils.Point2DInteger;
import starhydro.utils.Rectangle2DInteger;
import starhydro.utils.tasks.DelayedTaskExecutor;
import starhydro.view2d.SimplePalette;

@SignalComponent(extend = JComponent.class, raises = { LoadTileRaiser.class, //
        DelineateWatershedRaiser.class, //
        ViewLocationChangeRaiser.class //
})
@Properties( { @Property(name = "scale", type = float.class, value = "1"),//
        @Property(name = "preferredSize", type = Dimension.class, getter = Property.PUBLIC, setter = Property.PUBLIC), //
        @Property(name = "preferredScrollableViewportSize", type = Dimension.class, value = "new java.awt.Dimension(800,600)", getter = Property.PUBLIC),//
        @Property(name = "scrollableTracksViewportHeight", type = boolean.class, value = "false", getter = Property.PUBLIC),//
        @Property(name = "scrollableTracksViewportWidth", type = boolean.class, value = "false", getter = Property.PUBLIC) //
})
public class View2D extends View2D_generated implements Scrollable
{
	private static final long serialVersionUID = 1L;

	public WorldElevation world = null;

	private Point2DInteger location = null;

	private Point2DInteger viewLocation = null;

	private Point2DInteger outletLocation = null;

	public float accumulationThreshold = Float.NaN;

	@Override
	public float getScale()
	{
		// TODO Auto-generated method stub
		return super.getScale();
	}

	public Point2DInteger getTileLocation()
	{
		return location;
	}

	public Point2DInteger getViewLocation()
	{
		return viewLocation;
	}

	public Point2DInteger getOutletLocation()
	{
		return outletLocation;
	}

	public void raise_LoadTileEvent(int x, int y)
	{
		location = new Point2DInteger(x, y);
		raise_LoadTileEvent();
	}

	private void raise_MouseOverEvent(int x, int y)
	{
		Point2DInteger location = new Point2DInteger(x, y);
		world.getCoordiantes(location.getX(), location.getY()).getCoords();
		coords.setText(world.getCoordiantes(location.getX(), location.getY()).getCoords());
	}

	private void raise_DelineateWatershedEvent(int x, int y)
	{
		outletLocation = new Point2DInteger(x, y);
		raise_DelineateWatershedEvent();
	}

	public void addNotify()
	{
		super.addNotify();
		MouseAdapter adapter = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				Point p = e.getPoint();
				float scale = getScale();
				int x = Math.round(p.x * scale);
				int y = Math.round(p.y * scale);

				if (e.getClickCount() == 1)
				{
					System.out.println("At " + p + " height = " + world.getTerrainHeight(x, y));
				}
				if (e.getClickCount() == 2)
				{
								
					if (scale > 32)
					{
						zoom(p);
					}
					else						
					{
						System.out.println("Load View2D " + x + " " + y);
						if (!world.isLoaded(x, y))
						{
							raise_LoadTileEvent(x, y);
						}
						else
						{
							if( scale > 8 )
							{
								zoom(p);
							}
							else
							{
								raise_DelineateWatershedEvent(x, y);
							}
						}
					}
				}
			}
			
			private void zoom(Point p)
			{
				JViewport viewport = getViewport();
				if( viewport != null )
				{
					java.awt.Point vp = viewport.getViewPosition();
					Rectangle bounds = viewport.getBounds();
					int centerX = vp.x + bounds.width / 2 ;
					int centerY = vp.y + bounds.height / 2 ;
					System.err.println( "viewport move " + (p.x-centerX) + "," + (p.y-centerY));
					vp.x += (p.x-centerX);
					vp.y += (p.y-centerY);
					viewport.setViewPosition(vp);					
					scaleSlider.rescale(-1);
					vp.x -= (p.x-centerX)/2;
					vp.y -= (p.y-centerY)/2;
					viewport.setViewPosition(vp);					
					
					viewport.repaint();
				}			
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				e.consume();
				scaleSlider.rescale(e.getWheelRotation());

			}
		};
		addMouseListener(adapter);
		addMouseWheelListener(adapter);

		addMouseMotionListener(new MouseMotionAdapter()
		{
			Point initialPoint = null;
			Point finalPoint = null;

			@Override
			public void mouseMoved(MouseEvent e)
			{
				float scale = getScale();
				raise_MouseOverEvent(Math.round(e.getPoint().x * scale), Math.round(e.getPoint().y * scale));
			}

			public void mouseDragged(MouseEvent e)
			{
				if (initialPoint == null)
				{
					initialPoint = e.getPoint();
				}
				else
				{
					finalPoint = e.getPoint();
					DelayedTaskExecutor.getDelayedTaskExecutor().schedule(update, 100);
				}
			}

			Runnable update = new Runnable()
			{
				public void run()
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							if (initialPoint != null && finalPoint != null)
							{
								System.out.println((finalPoint.x - initialPoint.x) + " " + (finalPoint.y - initialPoint.y));
								JViewport viewport = getViewport();
								if (viewport != null)
								{
									java.awt.Point p = viewport.getViewPosition();
									p.x += initialPoint.x - finalPoint.x;
									p.y += initialPoint.y - finalPoint.y;
									viewport.setViewPosition(p);
									viewport.repaint();
								}
								finalPoint = null;
								initialPoint = null;
							}
						}
					});
				}
			};
		});
	}

	JLabel coords;

	private ScaleSlider scaleSlider;

	public View2D(WorldElevation world, JLabel coords)
	{
		this.world = world;
		this.coords = coords;
		setScale(1);
	}

	public void setScaleSlider(ScaleSlider slider)
	{
		this.scaleSlider = slider;
	}

	@Handles(raises = {})
	protected void handleThresholdChanged(AccumulationThresholdChangedRaiser r)
	{
		this.accumulationThreshold = r.getAccumulation();
		repaint();
	}

	@Handles(raises = {})
	protected void handleLoadTile(LoadTileRaiser r)
	{
		Point2DInteger location = r.getTileLocation();
		boolean point = world.load(location.getX(), location.getY());
		if (point)
		{
			repaint();
		}
	}

	@Handles(raises = { WatershedAvailableRaiser.class })
	protected void handleDelineateWatershed(DelineateWatershedRaiser r, AccumulationThresholdChangedRaiser r2)
	{
		Point2DInteger location = r.getOutletLocation();
		float accumulationThreshold = r2.getAccumulation();
		world.delineateWatershed(location.getX(), location.getY(), accumulationThreshold);
		repaint();
		raise_WatershedAvailableEvent();
	}

	@Handles(raises = {})
	protected void handleLocationChange(ViewLocationChangeRaiser r)
	{
		JViewport viewport = getViewport();
		if (viewport != null)
		{
			Point2DInteger location = r.getViewLocation();
			java.awt.Dimension d = viewport.getSize();
			viewport.setViewPosition(new java.awt.Point(location.getX() - d.width / 2, location.getY() - d.height / 2));
		}
	}

	@Handles(raises = {})
	protected void handleScaleChange(ViewScaleChangedRaiser r)
	{
		setScale(r.getScale());
	}

	protected void setScale(final float step)
	{
		final float oldStep = getScale();
		super.setScale(step);
		setPreferredSize(new Dimension(Math.round(360 * world.getElementsPerGrid() / step), Math.round(180 * world.getElementsPerGrid() / step)));
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final JViewport viewport = getViewport();
				if (viewport != null)
				{
					final java.awt.Point p = viewport != null ? viewport.getViewPosition() : null;
					java.awt.Dimension d = viewport.getSize();
					p.x = (int) (oldStep / step * (p.x + d.width / 2)) - d.width / 2;
					p.y = (int) (oldStep / step * (p.y + d.height / 2)) - d.height / 2;
					setSize(getPreferredSize());
					viewport.setViewPosition(p);
					// repaint();
				}
			}
		});
	}

	private JViewport getViewport()
	{
		Component parent = getParent();
		if (parent instanceof JViewport)
		{
			return (JViewport) parent;
		}
		else
		{
			return null;
		}
	}

	BufferedImage world_thumbnail;
	JLabel label = null;
	ImageIcon thumbnail;

	public JComponent getThumbnail()
	{
		thumbnail = new ImageIcon(new BufferedImage(360, 180, BufferedImage.TYPE_4BYTE_ABGR));
		world_thumbnail = new BufferedImage(360, 180, BufferedImage.TYPE_4BYTE_ABGR);
		world.renderBackground((Graphics2D) world_thumbnail.getGraphics(), new Rectangle2DInteger(0, 0, world.getElementsPerGrid() * 360, world.getElementsPerGrid() * 180), world.getElementsPerGrid(), false);
		this.label = new JLabel();
		label.setIcon(thumbnail);
		label.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				viewLocation = new Point2DInteger((int) (e.getPoint().x * world.getElementsPerGrid() / getScale()), (int) (e.getPoint().y * world.getElementsPerGrid() / getScale()));
				raise_ViewLocationChangeEvent();
			}
		});
		label.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent e)
			{
				viewLocation = new Point2DInteger((int) (e.getPoint().x * world.getElementsPerGrid() / getScale()), (int) (e.getPoint().y * world.getElementsPerGrid() / getScale()));
				raise_ViewLocationChangeEvent();
			}
		});
		return label;
	}

	protected void updateThumbnail(Rectangle r)
	{
		int ix = Math.round(1.0f * r.x * getScale() / world.getElementsPerGrid());
		int iy = Math.round(1.0f * r.y * getScale() / world.getElementsPerGrid());
		int iw = Math.round(1.0f * r.width * getScale() / world.getElementsPerGrid());
		int ih = Math.round(1.0f * r.height * getScale() / world.getElementsPerGrid());
		System.out.println("Rect " + ix + " " + iy + " -- " + iw + " " + ih);
		Graphics g = thumbnail.getImage().getGraphics();
		g.drawImage(world_thumbnail, 0, 0, null);
		g.setColor(new java.awt.Color(238, 99, 99));
		g.drawRect(ix, iy, iw, ih);
		label.repaint();

	}

	@Override
	protected void paintComponent(Graphics g)
	{
		System.out.println("paintComponent " + ((JViewport) getParent()).getViewPosition());
		super.paintComponent(g);
		drawLayers(g);
		drawArrows(g);
		drawScale(g);
		g.setColor(Color.white);
		Rectangle r = g.getClipBounds();
		g.drawString(r.toString(), r.x + 20, r.y + 20);
		g.drawRect(r.x + r.width / 2 - 2, r.y + r.height / 2 - 2, 5, 5);
		updateThumbnail(r);
	}

	private void drawArrows(Graphics g)
	{
		g.setColor(Color.WHITE);
		Rectangle r = g.getClipBounds();
		g.drawLine(r.x + 30, r.y + r.height - 20, r.x + 30, r.y + r.height - 40);
		g.drawLine(r.x + 25, r.y + r.height - 30, r.x + 35, r.y + r.height - 30);
		Polygon poly = new Polygon();
		poly.addPoint(r.x + 30, r.y + r.height - 40);
		poly.addPoint(r.x + 26, r.y + r.height - 35);
		poly.addPoint(r.x + 34, r.y + r.height - 35);
		g.fillPolygon(poly);
		g.drawString("N", r.x + 26, r.y + r.height - 42);
		g.drawString("S", r.x + 27, r.y + r.height - 8);
	}

	private void drawScale(Graphics g)
	{
		g.setColor(Color.WHITE);
		Rectangle r = g.getClipBounds();
		int unit = Math.round(360 / getScale());
		String text = "6 arc sec";
		g.drawLine(r.x + 50, r.y + r.height - 30, r.x + 50 + unit, r.y + r.height - 30);
		g.drawLine(r.x + 50, r.y + r.height - 28, r.x + 50, r.y + r.height - 30);
		g.drawLine(r.x + 50 + unit, r.y + r.height - 28, r.x + 50 + unit, r.y + r.height - 30);
		g.drawString(text, r.x + 50, r.y + r.height - 8);
	}

	private void drawLayers(Graphics g)
	{
		Rectangle r = g.getClipBounds();
		if (r.width > 0 && r.height > 0)
		{
			Image image = this.createImage(r.width, r.height);
			Graphics gr = image.getGraphics();
			renderLayers(r, gr);
			g.drawImage(image, r.x, r.y, null);
		}
	}

	private void renderLayers(Rectangle r, Graphics g)
	{
		float scale = getScale();
		Rectangle2DInteger r2 = new Rectangle2DInteger(r.x * scale, r.y * scale, r.width * scale, r.height * scale);
		SimplePalette palette = new SimplePalette(Color.green);
		SimplePalette palette_red = new SimplePalette(Color.blue);
		SimplePalette palette_ws = new SimplePalette(new Color(.5f, .5f, .5f, .8f));
		palette_red.setRange(0, 1);
		long point0 = System.nanoTime();
		world.renderBackground((Graphics2D) g, r2, scale, true);
		long point1 = System.nanoTime();
		g.setColor(java.awt.Color.red);
		g.drawString("Rendering " + Math.random() + " " + world.getCoordiantes(r2.getX(), r2.getY()).getCoords(), 75, 75);
		long point2 = System.nanoTime();
		world.renderHeightmap((Graphics2D) g, r2, scale, palette);
		long point3 = System.nanoTime();
		world.renderStreams((Graphics2D) g, r2, scale, palette_red, accumulationThreshold);
		long point4 = System.nanoTime();
		world.renderWatershed((Graphics2D) g, r2, scale, palette_ws);
		long point5 = System.nanoTime();
		System.out.println(MessageFormat.format("normal {1}ms terrain {0}ms streams {2}ms ws {3}ms background {4}ms", (point3 - point2) * 1e-6, (point2 - point1) * 1e-6, (point4 - point3) * 1e-6, (point5 - point4) * 1e-6, (point1 - point0) * 1e-6));
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 60;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 5;
	}

	public FloatWorld getView2D()
	{
		return world;
	}

}
