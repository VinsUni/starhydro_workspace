package starhydro.model;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.ProgressMonitor;

import org.jdesktop.swingworker.SwingWorker;

import star.annotations.SignalComponent;
import starhydro.algorithms.AlgorithmsFactory;
import starhydro.algorithms.BackgroundProcess;
import starhydro.algorithms.DelineateWatershed;
import starhydro.algorithms.FlowAccumulationAlgorithm;
import starhydro.algorithms.FlowDirectionAlgorithm;
import starhydro.algorithms.PitFillingAlgorithm;
import starhydro.data.impl.ByteGrid1D;
import starhydro.data.impl.ByteGridManager;
import starhydro.data.impl.Coord4D;
import starhydro.data.impl.FloatGrid1D;
import starhydro.data.impl.FloatGridManager;
import starhydro.data.impl.FloatRangeImpl;
import starhydro.data.impl.GridManager;
import starhydro.data.impl.IntegerRangeImpl;
import starhydro.data.interfaces.ByteGrid;
import starhydro.data.interfaces.FloatGrid;
import starhydro.data.interfaces.FloatGridWritable;
import starhydro.data.interfaces.TileManager;
import starhydro.data.reader.ascii.AsciiGridWriter;
import starhydro.utils.FlowDirectionUtils;
import starhydro.utils.Point2DInteger;
import starhydro.utils.Rectangle2DInteger;
import starhydro.view2d.Palette;

@SignalComponent()
public abstract class FloatWorld extends FloatWorld_generated
{
	private AlgorithmsFactory algorithmFactory = new AlgorithmsFactory();
	private FloatGridManager heights = new FloatGridManager("Filled height data", getElementsPerGrid());
	private FloatGridManager raw_heights = new FloatGridManager("Raw height data", getElementsPerGrid());
	private FloatGridManager accumulation = new FloatGridManager("Flow accumulation data", getElementsPerGrid());
	private ByteGridManager directions = new ByteGridManager("Flow directions data", getElementsPerGrid());
	private ByteGridManager watershed = null;
	private Rectangle2DInteger watershedRange = null;

	public abstract int getElementsPerGrid();

	protected abstract TileManager getSource();

	public Coord4D getCoordiantes(int x, int y)
	{
		Coord4D ret = new Coord4D();
		int elementsPerGrid = getElementsPerGrid();
		ret.x = x % elementsPerGrid;
		ret.y = y % elementsPerGrid;
		ret.xx = x / elementsPerGrid;
		ret.yy = y / elementsPerGrid;
		return ret;
	}

	public boolean isLoaded(int x, int y)
	{
		final Coord4D c = getCoordiantes(x, y);
		return (heights.getTile(c.xx, c.yy) != null);

	}

	public boolean load(int x, int y)
	{
		final Coord4D c = getCoordiantes(x, y);
		if (heights.getTile(c.xx, c.yy) == null)
		{
			FloatGridWritable grid = (FloatGridWritable) getSource().getTile(c.xx - 180, 89 - c.yy);
			raw_heights.setTile(c.xx, c.yy, grid);
			loadMap(c.xx, c.yy);
		}
		return heights.getTile(c.xx, c.yy) != null;
	}

	private void loadMap(final int x, final int y)
	{
		new BackgroundProcess(this, new SwingWorker()
		{

			@Override
			protected Object doInBackground() throws Exception
			{
				setProgress(0);
				// FloatGridWritable grid = (FloatGridWritable) getSource().getTile(x - 180, 89 - y);
				// raw_heights.setTile(x, y, grid);
				setProgress(10);
				HashSet<Point2DInteger> reprocess = getAllRawTilesConnectedToTheTile(x, y);
				ArrayList<Rectangle2DInteger> rectArray = getRectangesFromTilePoints(reprocess);
				setProgress(20);
				calculatePitFilling(reprocess, rectArray, raw_heights, heights);
				setProgress(50);
				calculateFlowDirection(reprocess, rectArray);
				setProgress(60);
				calculateAccumulation(reprocess, rectArray, raw_heights, heights, directions, accumulation);
				setProgress(100);
				renderedHeightmap.needRerender();
				return null;
			}
		}, MessageFormat.format("Loading tile {0} , {1}", x, y));
	}

	private HashSet<Point2DInteger> getAllRawTilesConnectedToTheTile(int x, int y)
	{
		HashSet<Point2DInteger> ret = new HashSet<Point2DInteger>();
		Stack<Point2DInteger> stack = new Stack<Point2DInteger>();
		stack.push(new Point2DInteger(x, y));
		while (stack.size() != 0)
		{
			Point2DInteger p = stack.pop();
			ret.add(p);
			for (Point2DInteger offset : FlowDirectionUtils.offsetDirections)
			{
				Point2DInteger testPoint = new Point2DInteger(p.getX() + offset.getX(), p.getY() + offset.getY());
				if (raw_heights.getTile(testPoint.getX(), testPoint.getY()) != null && !ret.contains(testPoint))
				{
					stack.push(testPoint);
					ret.add(testPoint);
				}
			}
		}
		System.out.println("Connected points: " + ret);
		return ret;
	}

	private ArrayList<Rectangle2DInteger> getRectangesFromTilePoints(HashSet<Point2DInteger> reprocess)
	{
		ArrayList<Rectangle2DInteger> ret = new ArrayList<Rectangle2DInteger>();
		for (Point2DInteger p : reprocess)
		{
			ret.add(new Rectangle2DInteger(p.getX() * getElementsPerGrid(), p.getY() * getElementsPerGrid(), getElementsPerGrid(), getElementsPerGrid()));
		}
		return ret;
	}

	private void calculateAccumulation(HashSet<Point2DInteger> reprocess, ArrayList<Rectangle2DInteger> rectArray, FloatGrid raw_height_grid, FloatGridWritable height_grid, ByteGrid direction, FloatGridWritable accumulation_grid)
	{
		accumulation.clearTiles(reprocess, FloatGrid1D.getFactory(getElementsPerGrid(), getElementsPerGrid()));
		FlowAccumulationAlgorithm flowAccumulation = algorithmFactory.getFlowAccumulationAlgorithm();
		flowAccumulation.calculate(rectArray, height_grid, direction, accumulation_grid, getElementsPerGrid());
	}

	private void calculatePitFilling(HashSet<Point2DInteger> reprocess, ArrayList<Rectangle2DInteger> rectArray, FloatGridWritable raw_height_grid, FloatGridWritable height_grid)
	{
		heights.clearTiles(reprocess, FloatGrid1D.getFactory(getElementsPerGrid(), getElementsPerGrid()));
		PitFillingAlgorithm pits = algorithmFactory.getPitFilledAlgorithm();
		pits.calculate(rectArray, raw_height_grid, height_grid);
	}

	private void calculateFlowDirection(HashSet<Point2DInteger> reprocess, ArrayList<Rectangle2DInteger> rectArray)
	{
		directions.clearTiles(reprocess, ByteGrid1D.getFactory(getElementsPerGrid(), getElementsPerGrid()));
		FlowDirectionAlgorithm direction = algorithmFactory.getFlowDirectionAlgorithm();
		direction.calculate(rectArray, heights, directions);
	}

	public float getTerrainHeight(int x, int y)
	{
		return heights.get(x, y);
	}

	public float getAccumulation(int x, int y)
	{
		return accumulation.get(x, y);
	}

	private Collection<Rectangle2DInteger> anyVisible(final Rectangle2DInteger r, final GridManager manager)
	{
		ArrayList<Rectangle2DInteger> rectanges = new ArrayList<Rectangle2DInteger>();
		final int x0 = r.getX();
		final int y0 = r.getY();
		final int height = r.getHeight();
		final int width = r.getWidth();
		final int x1 = x0 + width;
		final int y1 = y0 + height;
		final Coord4D ul = getCoordiantes(x0, y0);
		final Coord4D br = getCoordiantes(x1, y1);
		for (int xx = ul.xx; xx <= br.xx; xx++)
		{
			for (int yy = ul.yy; yy <= br.yy; yy++)
			{
				if (manager.hasTile(xx, yy))
				{
					rectanges.add(r.intersect(manager.getRectangle(xx, yy)));
				}
			}
		}
		return rectanges;
	}

	public void renderStreams(Graphics2D g, Rectangle2DInteger r, float step, Palette palette, float threshold)
	{
		final int height = r.getHeight();
		final int width = r.getWidth();
		final int x0 = r.getX();
		final int y0 = r.getY();
		// final int x1 = x0 + width;
		// final int y1 = y0 + height;
		if (height > 0 && width > 0 && step < 16)
		{
			Collection<Rectangle2DInteger> rectangles = anyVisible(r, accumulation);
			if (rectangles.size() != 0)
			{
				BufferedImage image = new BufferedImage(Math.round(width / step), Math.round(height / step), BufferedImage.TYPE_INT_ARGB);
				for (Rectangle2DInteger rect : rectangles)
				{
					int rx = rect.getX();
					int ry = rect.getY();
					int rw = rect.getWidth();
					int rh = rect.getHeight();

					float myStep = step > 1 ? 1 : step;
					for (float y = ry; y < (ry + rh); y += myStep)
					{
						for (float x = rx; x < (rx + rw); x += myStep)
						{
							float value = accumulation.get(Math.round(x), Math.round(y));
							if (!Float.isNaN(value) && value > threshold)
							{
								image.setRGB((int) Math.floor((x - x0) / step), (int) Math.floor((y - y0) / step), palette.getColor(value).getRGB());
							}
						}
					}
					System.out.println("RenderHeightMap " + rect);
				}
				g.drawImage(image, 0, 0, null);
			}
		}
	}

	int roundDown(int input, int step)
	{
		return input - (input % step);
	}

	int roundUp(int input, int step)
	{
		return input - (input % step) + step;
	}

	public void renderGrid(Graphics2D g, Rectangle2DInteger r, float step)
	{
		final int x0 = r.getX();
		final int y0 = r.getY();
		final int height = r.getHeight();
		final int width = r.getWidth();
		final int x1 = x0 + width;
		final int y1 = y0 + height;
		final Coord4D ul = getCoordiantes(x0, y0);
		final Coord4D br = getCoordiantes(x1, y1);
		int alternate = 0;
		int xstep = (br.xx - ul.xx) > 30 ? 10 : 1;
		int ystep = (br.yy - ul.yy) > 30 ? 10 : 1;
		int xystep = Math.max(xstep, ystep);
		xstep = xystep;
		ystep = xystep;

		System.out.println(MessageFormat.format("RenderGrid xstep={0} ystep={1} xdiff={2} ydiff={3}", xstep, ystep, ul.xx - br.xx, ul.yy - br.yy));
		for (int xx = roundDown(ul.xx, xstep); xx <= roundUp(br.xx, xstep); xx += xstep)
		{
			for (int yy = roundDown(ul.yy, ystep); yy <= roundUp(br.yy, ystep); yy += ystep)
			{
				alternate++;
				int xx0 = Math.round((xx * getElementsPerGrid() - x0) / step);
				int yy0 = Math.round((yy * getElementsPerGrid() - y0) / step);
				g.setColor(java.awt.Color.darkGray);
				g.drawLine(xx0, yy0, xx0, Math.round(yy0 + getElementsPerGrid() * ystep / step));
				g.drawLine(xx0, yy0, Math.round(xx0 + getElementsPerGrid() * xstep / step), yy0);
			}
		}
		if (step <= 32)
		{
			Stroke s = g.getStroke();
			g.setStroke(new BasicStroke(3f));
			for (int xx = roundDown(ul.xx, xstep); xx <= roundUp(br.xx, xstep); xx += xstep)
			{
				for (int yy = roundDown(ul.yy, ystep); yy <= roundUp(br.yy, ystep); yy += ystep)
				{
					int xx0 = Math.round((xx * getElementsPerGrid() - x0) / step);
					int yy0 = Math.round((yy * getElementsPerGrid() - y0) / step);
					if (yy % 10 == 0)
					{
						g.drawLine(xx0, yy0, Math.round(xx0 + getElementsPerGrid() * xstep / step), yy0);
					}
					if (xx % 10 == 0)
					{
						g.drawLine(xx0, yy0, xx0, Math.round(yy0 + getElementsPerGrid() * ystep / step));
					}
				}
			}
			g.setStroke(s);
		}
	}

	RenderedLayer renderedHeightmap = new RenderedLayer(java.awt.Color.white, true)
	{
		protected void render(Palette palette, int managerX, int managerY, starhydro.model.RenderedLayer.X ret)
		{
			float value = heights.get(managerX, managerY);
			if (!Float.isNaN(value))
			{
				ret.render = true;
				ret.c = palette.getColor(value);
			}
			else
			{
				ret.render = false;
			}
		}

		protected FloatGridManager getFloatGridManager()
		{
			return heights;
		}

	};

	public void renderHeightmap(Graphics2D g, Rectangle2DInteger r, float step, Palette palette)
	{
		BufferedImage image = renderedHeightmap.renderedImage(r, heights, step, palette);
		g.drawImage(image, 0, 0, null);
	}

	RenderedLayer renderedWatershed = new RenderedLayer(null, false)
	{
		protected void render(Palette palette, int managerX, int managerY, starhydro.model.RenderedLayer.X ret)
		{
			byte value = watershed.get(managerX, managerY);
			if (value != 0)
			{
				ret.render = true;
				ret.c = palette.getColor(value);
			}
			else
			{
				ret.render = false;
			}
		};

		protected FloatGridManager getFloatGridManager()
		{
			return null;
		}
	};

	public void renderWatershed(Graphics2D g, Rectangle2DInteger r, float step, Palette palette)
	{
		if (watershed != null)
		{
			BufferedImage image = renderedWatershed.renderedImage(r, watershed, step, palette);
			g.drawImage(image, 0, 0, null);
		}
	}

	public void normalizePalette(Rectangle2DInteger r, Palette palette)
	{
		int height = r.getHeight();
		int width = r.getWidth();
		int x0 = r.getX();
		int y0 = r.getY();
		int x1 = x0 + width;
		int y1 = y0 + height;

		if (height > 0 && width > 0 && anyVisible(r, heights).size() != 0)
		{
			Coord4D ul = getCoordiantes(x0, y0);
			Coord4D br = getCoordiantes(x1, y1);
			FloatRangeImpl range = new FloatRangeImpl();
			for (int yy = ul.yy; yy <= br.yy; yy++)
			{
				for (int xx = ul.xx; xx <= br.xx; xx++)
				{
					range.addRange(heights.getValueRange(xx, yy));
				}
			}
			palette.setRange(range.getMin(), range.getMax());
		}
	}

	public void delineateWatershed(final int x, final int y, float accumulationThreshold)
	{
		System.out.println("Delinete watershed " + accumulationThreshold + " long " + x + " lat " + y);
		if (!Float.isNaN(accumulation.get(x, y)))
		{
			final IntegerRangeImpl xrange = new IntegerRangeImpl();
			final IntegerRangeImpl yrange = new IntegerRangeImpl();
			watershed = new ByteGridManager("Watershed", getElementsPerGrid())
			{
				@Override
				public void set(int x, int y, byte value)
				{
					Coord4D c = getCoordinates(x, y);
					if (!hasTile(c.xx, c.yy))
					{
						ArrayList<Point2DInteger> p = new ArrayList<Point2DInteger>();
						p.add(new Point2DInteger(c.xx, c.yy));
						clearTiles(p, ByteGrid1D.getFactory(getElementsPerGrid(), getElementsPerGrid()));
					}
					super.set(x, y, value);
					xrange.addValue(x);
					yrange.addValue(y);
				}
			};
			DelineateWatershed delineator = algorithmFactory.getDelineateWatershedAlgorithm();
			delineator.calculate(x, y, watershed, directions, accumulation, accumulationThreshold);
			watershedRange = new Rectangle2DInteger(xrange.getMin(), yrange.getMin(), xrange.getMax() - xrange.getMin(), yrange.getMax() - yrange.getMin());
			renderedWatershed.needRerender();
		}
		else
		{
			System.err.println("Can not delineate - unknown accumulation at point.");
		}
	}

	public Rectangle2DInteger getWatershedRange()
	{
		return watershedRange;
	}

	public boolean saveForStarHydro(java.io.File directory, ProgressMonitor monitor) throws IOException
	{

		java.io.FileOutputStream fos;
		java.io.File f;

		monitor.setProgress(0);
		monitor.setNote("Saving filled terrain");
		if (monitor.isCanceled())
		{
			return false;
		}

		f = new java.io.File(directory.getAbsolutePath()+"_filled.xyz");
		f.delete();
		fos = new java.io.FileOutputStream(f);
		AsciiGridWriter.saveFloatGrid(fos, heights, watershedRange, getElementsPerGrid());
		fos.close();

		monitor.setProgress(25);
		monitor.setNote("Saving projected terrain");
		if (monitor.isCanceled())
		{
			return false;
		}

		f = new java.io.File(directory.getAbsolutePath()+"_projected.xyz");
		f.delete();
		fos = new java.io.FileOutputStream(f);
		AsciiGridWriter.saveFloatGrid(fos, heights, watershedRange, getElementsPerGrid());
		fos.close();

		monitor.setProgress(50);
		monitor.setNote("Saving flow accumulation terrain");
		if (monitor.isCanceled())
		{
			return false;
		}

		f = new java.io.File(directory.getAbsolutePath()+"_flowacc.xyz");
		f.delete();
		fos = new java.io.FileOutputStream(f);
		AsciiGridWriter.saveFloatGrid(fos, accumulation, watershedRange, getElementsPerGrid());
		fos.close();

		monitor.setProgress(75);
		monitor.setNote("Saving flow direction terrain");
		if (monitor.isCanceled())
		{
			return false;
		}

		f = new java.io.File(directory.getAbsolutePath()+"_flowdir.xyz");
		f.delete();
		fos = new java.io.FileOutputStream(f);
		AsciiGridWriter.saveByteGrid(fos, directions, watershedRange, getElementsPerGrid());
		fos.close();

		monitor.setProgress(100);
		monitor.setNote("Done");
		if (monitor.isCanceled())
		{
			return false;
		}

		return true;
	}
}
