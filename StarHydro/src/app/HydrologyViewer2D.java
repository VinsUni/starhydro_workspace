package app;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point3f;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.events.GridStatisticsProviderChangeRaiser;
import star.hydrology.events.Select3DRaiser;
import star.hydrology.events.StreamOrderStatisticsRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.ui.palette.Palette;
import utils.Format;
import app.worker.streamnetwork.Channel;
import app.worker.streamnetwork.Stream;

@SignalComponent(extend = JComponent.class, raises = { Select3DRaiser.class })
@Properties( { @Property(name = "scale", type = float.class, value = "1"),//
        @Property(name = "preferredSize", type = Dimension.class, getter = Property.PUBLIC, setter = Property.PUBLIC, value = "new java.awt.Dimension(800,600)"), //
        @Property(name = "preferredScrollableViewportSize", type = Dimension.class, value = "new java.awt.Dimension(800,600)", getter = Property.PUBLIC),//
        @Property(name = "scrollableTracksViewportHeight", type = boolean.class, value = "false", getter = Property.PUBLIC),//
        @Property(name = "scrollableTracksViewportWidth", type = boolean.class, value = "false", getter = Property.PUBLIC) //
})
public class HydrologyViewer2D extends HydrologyViewer2D_generated implements Scrollable
{
	private static final long serialVersionUID = 1L;

	static class Array<T>
	{
		T[] array;

		@SuppressWarnings("unchecked")
		public Array(int size)
		{
			array = (T[]) new Object[size];
		}

		void set(int index, T value)
		{
			array[index] = value;
		}

		T get(int index)
		{
			return array[index];
		}

		void clear()
		{
			for (int i = 0; i < array.length; i++)
			{
				array[i] = null;
			}
		}

	}

	Array<PaletteRenderableLayer> layers = new Array<PaletteRenderableLayer>(50);
	Array<Palette> palette = new Array<Palette>(50);
	Array<Boolean> visible = new Array<Boolean>(50);
	Array<BufferedImage> images = new Array<BufferedImage>(50);

	float treshold = 100000;
	Stream streamRoot = null;
	Point3f point = null;
	int button = 0;
	private Point3f center = new Point3f(0, 0, 0);

	@Override
	public void addNotify()
	{
		super.addNotify();
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				if (e.getClickCount() == 2)
				{
					raiseClick(e.getPoint(), e.getButton());
					e.consume();
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				super.mouseMoved(e);
				raiseMove(e.getPoint());
			}
		});
	}

	void raiseMove(Point p)
	{
		if (layers.get(LayerConstants.FILLED) != null)
		{
			float[] ret = new float[3];
			layers.get(LayerConstants.FILLED).getDataset().getPoint((int) (p.x / getScale()), (int) (p.y / getScale()), ret);
			point = new Point3f(ret);
			PickerBehaviour.getLabel().setText("x (long) = " + Format.formatNumber(ret[0]) + " y (lat) = " + Format.formatNumber(ret[1]));

		}
	}

	void raiseClick(Point p, int button)
	{
		if (layers.get(LayerConstants.FILLED) != null)
		{
			float[] ret = new float[3];
			layers.get(LayerConstants.FILLED).getDataset().getPoint((int) (p.x / getScale()), (int) (p.y / getScale()), ret);
			point = new Point3f(ret);
			this.button = button;
			if (center != null)
			{
				point.sub(center);
			}
			raise_Select3DEvent();
		}
	}

	public int getButton()
	{
		return button;
	}

	public Point3f getPoint()
	{
		return point;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 60;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 5;
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		System.out.println("Paint component start");
		for (int i = 0; i < 50; i++)
		{
			System.out.println("Paint component " + i);
			paintComponent(i, g);
		}
		System.out.println("Paint component done" + g.getClip());
	}

	protected Image paintComponent(int i, Graphics g)
	{
		Image ret = null;
		boolean rendered = false;
		Boolean b = visible.get(i);
		if (b != null && b.booleanValue())
		{
			if (palette.get(i) != null)
			{
				if (i == LayerConstants.STREAMS)
				{
					Image img = images.get(i);
					if (img == null)
					{
						Dimension d = getSize();
						BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
						Graphics gr = image.getGraphics();
						System.out.println("Render " + i);
						renderStreams(gr, getScale());
						images.set(i, image);
						img = image;
					}
					g.drawImage(img, 0, 0, null);
					rendered = true;
				}
				else if (i == LayerConstants.STREAMNETWORK)
				{
					Image img = images.get(i);
					if (img == null)
					{
						Dimension d = getSize();
						BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
						Graphics gr = image.getGraphics();
						System.out.println("Render " + i);
						renderStreamNetwork(gr, getScale());

						images.set(i, image);
						img = image;
					}
					g.drawImage(img, 0, 0, null);
					rendered = true;
				}
				else if (layers.get(i) != null)
				{
					// if (images.get(i) == null )
					if (true)
					{
						Dimension d = mySize;
						BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
						Graphics gr = image.getGraphics();
						System.out.println("Render " + i + " " + d.width + " " + d.height + " " + getScale());
						renderLayer(i, gr);
						images.set(i, image);
					}
					rendered = true;
					Dimension d = getSize();
					Rectangle clip = g.getClipBounds();
					float scale = getScale();
					Rectangle src = new Rectangle((int) (clip.x / scale), (int) (clip.y / scale), (int) (clip.width / scale), (int) (clip.height / scale));
					g.drawImage(images.get(i), clip.x, clip.y, clip.x + clip.width, clip.y + clip.height, src.x, src.y, src.x + src.width, src.y + src.height, null);
					// g.drawImage(images.get(i), 0, 0, d.width, d.height, null);
					System.out.println("--- Paint " + clip + " " + src);
				}
			}
		}
		return ret;

	}

	private void renderStreamNetwork(Graphics g, float scale)
	{
		Palette palette = this.palette.get(LayerConstants.STREAMNETWORK);
		if (streamRoot != null)
		{
			walk(g, streamRoot, palette, streamRoot.getStreamOrder(), scale);
		}
	}

	private void walk(Graphics g, Stream localRoot, Palette palette, int maxOrder, float scale)
	{
		Graphics2D g2d = (Graphics2D) g;
		if (localRoot != null)
		{
			for (Channel c : localRoot.getParts())
			{
				int order = c.getChannelOrder();
				g2d.setStroke(new BasicStroke(order));
				g.setColor(palette.getColor((1.0f * order) / maxOrder));
				for (int i = 0; i < c.getPointsSize() - 1; i++)
				{
					Point p1 = c.getPoint2D(i);
					Point p2 = c.getPoint2D(i + 1);
					g2d.draw(new Line2D.Float(p1.x * scale, p1.y * scale, p2.x * scale, p2.y * scale));
				}
			}
			for (Stream c : localRoot.getChildren())
			{
				walk(g, c, palette, maxOrder, scale);
			}

		}

	}

	private void renderStreams(Graphics g, float scale)
	{
		Graphics2D g2d = (Graphics2D) g;

		PaletteRenderableLayer flowAccumulation = layers.get(LayerConstants.FLOWACC);
		PaletteRenderableLayer flowDirection = layers.get(LayerConstants.FLOWDIR);
		Palette palette = this.palette.get(LayerConstants.STREAMS);
		if (flowAccumulation != null && flowDirection != null)
		{
			Grid flowAcc = flowAccumulation.getDataset();
			Grid flowDir = flowDirection.getDataset();
			int rows = flowAcc.getRows();
			int cols = flowAcc.getCols();
			final Point offset = new Point();
			g2d.setStroke(new BasicStroke(1));
			for (int y = 0; y < rows; y++)
			{
				for (int x = 0; x < cols; x++)
				{
					float accomulation = flowAcc.getElementAt(x, y);
					if (accomulation > treshold)
					{
						star.hydro.util.Utilities.getDownstreamOffset((byte) flowDir.getElementAt(x, y), offset);
						if (x + offset.x >= 0 && x + offset.x < cols && y + offset.y >= 0 && y + offset.y < rows)
						{
							g2d.setColor(flowAcc.getColorAt(x, y, palette));
							g2d.draw(new Line2D.Float(x * scale, y * scale, (x + offset.x) * scale, (y + offset.y) * scale));
						}
					}
				}
			}
		}
	}

	protected float getScale()
	{
		float ret = 1;
		if (slider != null)
		{
			int value = slider.getModel().getValue();
			if (value >= 0)
			{
				ret = (value + 1);
			}
			else
			{
				ret = -1.0f / value;
			}
		}
		System.out.println("scale " + ret);
		return ret;
	}

	void renderLayer(int i, Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		float scale = getScale();
		PaletteRenderableLayer layer = this.layers.get(i);
		Grid grid = layer.getDataset();
		Palette palette = this.palette.get(i);
		int rows = grid.getRows();
		int cols = grid.getCols();
		for (int y = 0; y < rows; y++)
			for (int x = 0; x < cols; x++)
			{
				float value = grid.getElementAt(x, y);
				if (!Float.isNaN(value))
				{
					g2d.setColor(grid.getColorAt(x, y, palette));
					g2d.fill(new Rectangle2D.Float(x, y, 1, 1));
				}
			}
	}

	@Handles(raises = {})
	protected void updateTerrain(star.hydrology.events.map.FilledMapLayerRaiser raiser)
	{
		PaletteRenderableLayer layer = raiser.getLayer();
		if (layer != null && layer.getDataset() instanceof GridwStat)
		{
			GridwStat gs = (GridwStat) layer.getDataset();
			center = gs.getCenter();
		}
		Grid grid = layer.getDataset();
		mySize = new Dimension(grid.getCols(), grid.getRows());
		updatePreferredScrollableViewportSize();
		invalidate();
		repaint();
	}

	Dimension mySize = getPreferredSize();

	private void updatePreferredScrollableViewportSize()
	{
		Dimension d = mySize;
		float scale = getScale();
		Dimension d2 = new Dimension((int) (d.width * scale), (int) (d.height * scale));
		setPreferredScrollableViewportSize(d2);
		setPreferredSize(d2);
		setSize(d2);
		images.clear();
	}

	@Handles(raises = {})
	protected void updateLayers(star.hydrology.events.LayerChangedRaiser raiser)
	{
		layers.set(raiser.getLayerKind(), raiser.getLayer());
		// images.clear();
		updateVisibleLayer(raiser.getLayerKind());
	}

	@Handles(raises = {})
	protected void updatePalette(star.hydrology.events.PaletteChangedRaiser raiser)
	{
		palette.set(raiser.getKind(), raiser.getPalette());
		// images.clear();
		updateVisibleLayer(raiser.getKind());
	}

	@Handles(raises = {})
	protected void updateVisibility(star.hydrology.events.VisibilityChangedRaiser raiser)
	{
		visible.set(raiser.getKind(), Boolean.valueOf(raiser.isMapVisible()));
		repaint();
		// updateVisibleLayer(raiser.getKind());
	}

	@Override
	public void setSize(int width, int height)
	{
		images.clear();
		super.setSize(width, height);
	}

	@Override
	public void setSize(Dimension d)
	{
		images.clear();
		super.setSize(d);
	}

	private void updateVisibleLayer(int kind)
	{
		images.set(kind, null);
		repaint(500);
	}

	@Handles(raises = {})
	public void setStreamOrderStatistics(StreamOrderStatisticsRaiser r)
	{
		Stream rootStream = r.getRootStream();
		this.streamRoot = rootStream;
		updateVisibleLayer(LayerConstants.STREAMNETWORK);
	}

	@Handles(raises = {})
	protected void setThreshold(GridStatisticsProviderChangeRaiser r)
	{
		if (r.getKind() == LayerConstants.STREAMS)
		{
			setThreshold(r.getCurrent());
		}
	}

	void setThreshold(float value)
	{
		this.treshold = value;
		updateVisibleLayer(LayerConstants.STREAMS);
	}

	JSlider slider = null;

	JSlider getSlider()
	{
		if (slider == null)
		{
			slider = new JSlider(JSlider.VERTICAL, -30, 30, 0);
			slider.setPaintTicks(true);
			slider.setPaintTrack(true);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(5);
			slider.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					if (!slider.getModel().getValueIsAdjusting())
					{
						updatePreferredScrollableViewportSize();
						invalidate();
						repaint();
					}

				}
			});
		}
		return slider;

	}

}
