package app.viewers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.swing.SwingUtilities;
import javax.vecmath.Point3f;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydro.util.Utilities;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.data.teal.MyShapeNode;
import star.hydrology.events.RenderableVisibleRaiser;
import star.hydrology.events.ViewerChangeRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.tribs.AnimationRaiser;
import star.hydrology.ui.palette.Palette;
import star.tribs.io.Voronoi;
import star.tribs.io.VoronoiCanvas;
import star.tribs.io.Voronoi.Point2D;
import star.tribs.io.Voronoi.Polygon;
import utils.ArrayNumerics;

@SignalComponent(raises = { ViewerChangeRaiser.class, RenderableVisibleRaiser.class })
abstract public class AbstractMapViewer extends AbstractMapViewer_generated
{
	private static Object lock = new Object();
	private static PaletteRenderableLayer terrain = null;
	private static Point3f center = null;

	protected final int PALETTE = 1;
	protected final int GEOMETRY = 2;
	private final int BAtype = BufferedImage.TYPE_INT_ARGB_PRE;

	protected static int dirtyFlag = 0;

	private static int counter = 0;
	private static MyShapeNode sharedBranchGroup;

	private static BufferedImage[] imageMap = new BufferedImage[LayerConstants.LAYERS_COUNT];
	private static boolean[] visibleMap = new boolean[LayerConstants.LAYERS_COUNT];
	private static Palette[] paletteMap = new Palette[LayerConstants.LAYERS_COUNT];
	private static PaletteRenderableLayer[] colorMap = new PaletteRenderableLayer[LayerConstants.LAYERS_COUNT];
	private static PaletteRenderableLayer[] layerMap = new PaletteRenderableLayer[LayerConstants.LAYERS_COUNT];

	private static GeometryArray terrainArray = null;

	private static int GArows = -1;
	private static int GAcols = -1;

	private int action;
	private MyShapeNode branchGroup;

	private AnimationRaiser animation;

	@Handles(raises = {})
	void handleAnimation(AnimationRaiser r)
	{
		if (getKind() == LayerConstants.TERRAIN)
		{
			System.out.println("Animation Event" + getKind() );
			this.animation = r;
			dirtyFlag |= PALETTE;
			updateVisibleLayer();
		}
	}

	public static void clearStatic()
	{
		dirtyFlag = 0;
		counter = 0;
		sharedBranchGroup = null;

		imageMap = new BufferedImage[LayerConstants.LAYERS_COUNT];
		visibleMap = new boolean[LayerConstants.LAYERS_COUNT];
		paletteMap = new Palette[LayerConstants.LAYERS_COUNT];
		colorMap = new PaletteRenderableLayer[LayerConstants.LAYERS_COUNT];
		layerMap = new PaletteRenderableLayer[LayerConstants.LAYERS_COUNT];

		terrainArray = null;
		GArows = -1;
		GAcols = -1;
	}

	public BufferedImage getTextureImage()
	{
		if (GArows > 0 && GAcols > 0)
		{
			BufferedImage image = new BufferedImage(GAcols, GArows, BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.getGraphics();
			for (int i = 0; i < imageMap.length; i++)
			{
				if (i == LayerConstants.STREAMS || i == LayerConstants.STREAMNETWORK)
				{
					continue;
				}
				if (visibleMap[i])
				{
					if (imageMap[i] == null)
					{
						imageMap[i] = getBufferedImage(i);
					}
					g.drawImage(imageMap[i], 0, 0, null);
				}
			}
			if (animation != null)
			{
				animation.paint(g, GAcols, GArows);
			}
			// g.drawImage(getVoronoi(), 0, 0, null);
			g.dispose();
			return image;
		}
		return null;
	}

	VoronoiCanvas canvas = null;

	private VoronoiCanvas loadVoronoiPoints()
	{
		// canvas = null;
		if (canvas == null)
		{
			try
			{
				File tribsVoronoiOutputFolder = new File("Z:/hydro/R4/Output/voronoi");
				java.io.File voronoiFile = new java.io.File(tribsVoronoiOutputFolder, "blue_voi");
				ArrayList<Polygon> polygons = Voronoi.read(new FileInputStream(voronoiFile));
				Voronoi.Range range = Voronoi.getRange(polygons);
				float distX = range.max.x - range.min.x;
				float distY = range.max.y - range.min.y;

				float factorX = (distX / GAcols);
				float factorY = (distY / GArows);
				canvas = new VoronoiCanvas(polygons);
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return canvas;
	}

	static int step = 0;

	private BufferedImage getVoronoi()
	{
		VoronoiCanvas canvas = loadVoronoiPoints();
		BufferedImage image = new BufferedImage(GAcols, GArows, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();

		final float[] data = read(step);
		// final float[] data = new float[0];

		Grid grid = getTerrain().getDataset();
		Point3f center = getCenter();
		final float cellSize = grid.getCellsize();
		final float min_x = center.x - grid.getRows() / 2 * cellSize;
		final float min_y = center.y - grid.getCols() / 2 * cellSize;
		System.out.println(GAcols + " " + GArows);
		if (data != null)
		{
			ArrayNumerics.normalRange(data);

			canvas.p(g, 24 * step, new VoronoiCanvas.GetColor()
			{
				public Color getColor(int point)
				{
					float v = data[point];
					if (Float.isNaN(v) || v < 0 || v > 1)
					{
						return new java.awt.Color(1f, 0f, 0f, 0f);
					}
					return new java.awt.Color(v, v, v);
					// return new java.awt.Color((float) Math.random(), (float) Math.random(), (float) Math.random());
				}

				public Point2D getPoint(Point2D location)
				{
					Point2D p = new Point2D((location.x - min_x) / cellSize - 193, (GArows - (location.y - min_y) / cellSize - 193));
					return p;
				}

			});
			g.dispose();
			step++;
		}
		else
		{
			step = 0;
		}
		return image;

	}

	private float[] read(int step)
	{
		try
		{
			File file = new File("C:/tmp/blueriver/SoilMoist");
			RandomAccessFile f = new RandomAccessFile(file, "r");
			int hours = 7;
			int points = 45458;
			f.seek((4 * points + hours) * step);
			byte[] date = new byte[hours];
			f.readFully(date);
			System.out.println("read " + new String(date));
			byte[] bbpoints = new byte[points * 4];
			f.readFully(bbpoints);
			f.close();
			float[] ret = new float[points];
			java.nio.ByteBuffer.wrap(bbpoints).asFloatBuffer().get(ret);
			return ret;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public static java.awt.Image makeTransparent(java.awt.Image im)
	{
		if (im == null)
		{
			return null;
		}
		java.awt.image.ImageFilter filter = new java.awt.image.RGBImageFilter()
		{
			public final int filterRGB(int x, int y, int rgb)
			{
				if ((0x00FFFFFF & rgb) == 0)
				{
					return 0;
				}
				else
				{
					return rgb;
				}
			}
		};
		java.awt.image.ImageProducer ip = new java.awt.image.FilteredImageSource(im.getSource(), filter);
		java.awt.Image ret = Toolkit.getDefaultToolkit().createImage(ip);
		return ret;
	}

	public GeometryArray getTerrainArray()
	{
		synchronized (lock)
		{
			if (terrainArray == null && terrain != null)
			{
				Grid grid = terrain.getDataset();
				int rows = grid.getRows();
				int cols = grid.getCols();

				int rows2 = (int)Math.round(Math.pow(2, Math.ceil(Math.log(rows)/Math.log(2))));
				int cols2 = (int)Math.round(Math.pow(2, Math.ceil(Math.log(cols)/Math.log(2))));
					
				GArows = rows;
				GAcols = cols;

				final int COORDS = 3;
				final int TEX2D = 2;
				float[] points = new float[rows * cols * COORDS];
				float[] tex2d = new float[rows * cols * TEX2D];
				float[] point = new float[3];

				Point3f center = getCenter();

				for (int y = 0; y < rows; y++)
				{
					for (int x = 0; x < cols; x++)
					{
						int index = getIndex(x, y, rows, cols);
						grid.getPoint(x, y, point);
						tex2d[2 * index] = ( 1.0f * x / cols2); 
						tex2d[2 * index + 1] = ( 1.0f * (rows2-y) / rows2);
						points[3 * index] = (point[0] - center.x);
						points[3 * index + 1] = (point[1] - center.y);
						points[3 * index + 2] = Float.isNaN(point[2]) ? 0 : (point[2]);
					}
				}

				int[] indices = new int[4 * (rows * cols)];
				int pos = 0;
				for (int y = 0; y < rows - 1; y++)
				{
					for (int x = 0; x < cols - 1; x++)
					{
						indices[pos++] = getIndex(x, y, rows, cols);
						indices[pos++] = getIndex(x, y + 1, rows, cols);
						indices[pos++] = getIndex(x + 1, y + 1, rows, cols);
						indices[pos++] = getIndex(x + 1, y, rows, cols);
					}
				}

				indices = Utilities.trimIntArray(indices, pos);
				int arrayType = GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.COORDINATES | GeometryArray.USE_COORD_INDEX_ONLY |GeometryArray.BY_REFERENCE; // | GeometryArray.BY_REFERENCE_INDICES | GeometryArray.BY_REFERENCE;
				IndexedQuadArray ret = new IndexedQuadArray(rows * cols, arrayType, 1, new int[] { 0 }, indices.length);

				ret.setCoordinateIndices(0, indices);
//				ret.setCoordinates(0, points);
//				ret.setTextureCoordinate(2, 0, tex2d);
//				ret.setCoordIndicesRef(indices);
				ret.setCoordRefFloat(points);
				ret.setTexCoordRefFloat(0, tex2d);

				terrainArray = ret;

			}
			return terrainArray;
		}
	}

	private int getIndex(int x, int y, int rows, int cols)
	{
		return y * cols + x;
	}

	@Handles(raises = {})
	void updateTerrain(star.hydrology.events.map.FilledMapLayerRaiser raiser)
	{
		setCenter(raiser.getLayer());
		setTerrain(raiser.getLayer());
		updateVisibleLayer();
	}

	@Handles(raises = { ViewerChangeRaiser.class })
	void updateLayers(star.hydrology.events.LayerChangedRaiser r)
	{
		if (r.getLayerKind() == getKind())
		{
			setLayer(r.getLayer());
			updateVisibleLayer();
			raise_ViewerChangeEvent();
		}
	}

	@Handles(raises = {})
	void updatePalette(star.hydrology.events.PaletteChangedRaiser raiser)
	{
		if (raiser.getKind() == this.getKind())
		{
			setPalette(raiser.getPalette());
		}
	}

	@Handles(raises = {})
	void updateVisibility(star.hydrology.events.VisibilityChangedRaiser raiser)
	{
		if (raiser.getKind() == this.getKind())
		{
			setVisible(raiser.isMapVisible());
		}
	}

	private void setCenter(PaletteRenderableLayer layer)
	{
		if (layer != null && layer.getDataset() instanceof GridwStat)
		{
			GridwStat gs = (GridwStat) layer.getDataset();
			synchronized (lock)
			{
				if (center == null)
				{
					if (gs.getCenter() != null)
					{
						center = gs.getCenter();
						dirtyFlag |= GEOMETRY;
					}
				}
				else
				{
					if (!center.equals(gs.getCenter()))
					{
						center = gs.getCenter();
						dirtyFlag |= GEOMETRY;
					}
				}
			}
			updateVisibleLayer();
		}
	}

	protected Point3f getCenter()
	{
		return center;
	}

	abstract public int getKind();

	public void setColorLayer(PaletteRenderableLayer layer)
	{
		colorMap[getKind()] = layer;
		imageMap[getKind()] = null;
		if (isMapVisible())
		{
			dirtyFlag |= PALETTE;
			updateVisibleLayer();
		}
	}

	public PaletteRenderableLayer getColorLayer(int i)
	{
		PaletteRenderableLayer ret = colorMap[i];
		return ret != null ? ret : layerMap[i];
	}

	private BufferedImage getBufferedImage(int i)
	{

		PaletteRenderableLayer layer = getColorLayer(i);
		Palette palette = getPalette(i);
		int transparent = new Color(0f, 0f, 0f, 0f).getRGB();
		if (layer != null && palette != null)
		{
			float cmin = ((GridwStat) layer.getDataset()).getMinimum();
			float cmax = ((GridwStat) layer.getDataset()).getMaximum();
			int rows = layer.getDataset().getRows();
			int cols = layer.getDataset().getCols();
			BufferedImage img = new BufferedImage(cols, rows, BAtype);
			for (int y = 0; y < rows; y++)
			{
				for (int x = 0; x < cols; x++)
				{
					float cvalue = layer.getDataset().getElementAt(x, y);
					if (!Float.isNaN(cvalue))
					{
						img.setRGB(x, y, palette.getColor((cvalue - cmin) / (cmax - cmin)).getRGB());
					}
					else
					{

						// img.setRGB(x, y, transparent);
					}
				}
				if (Thread.currentThread().isInterrupted())
				{
					return null;
				}
			}
			return img;
		}
		return null;

	}

	private GeometryTile makeGeometryIndexes()
	{
		GeometryTile ret = new GeometryTile();
		BufferedImage img = getTextureImage();
		if (img != null)
		{ 
			int rows = getTerrain().getDataset().getRows();
			int cols = getTerrain().getDataset().getCols();
//			Texture tx = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, cols, rows);
//			tx.setImage(0, new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, img));

			int rows2 = (int)Math.round(Math.pow(2, Math.ceil(Math.log(rows)/Math.log(2))));
			int cols2 = (int)Math.round(Math.pow(2, Math.ceil(Math.log(cols)/Math.log(2))));

			BufferedImage img2 = new BufferedImage(cols2, rows2, BAtype);
			Graphics g = img2.getGraphics();
			g.drawImage(img, 0,0,null);
			g.dispose();
			img2.flush();
			Texture tx = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, cols2, rows2);
			tx.setImage(0, new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, img2));
			ret.tx = tx;
		}
		return ret;
	}

	private boolean makeGeometry()
	{
		if (dirtyFlag != 0)
		{
			System.out.println("makeGeometry START " + getKind() + " " + dirtyFlag);
			GeometryTile tiles;
			if (dirtyFlag == PALETTE && sharedBranchGroup != null)
			{
				tiles = makeGeometryIndexes();
				sharedBranchGroup.setAppearance(sharedBranchGroup.getTextureAppearance(tiles.tx));
				return false;
			}
			else
			{
				tiles = makeGeometryIndexes();
				float xfactor = 1 ;
				float yfactor = 1 ;
				
//				
//				int rows = getTerrain().getDataset().getRows();
//				int cols = getTerrain().getDataset().getCols();
//
//				int rows2 = (int)Math.round(Math.pow(2, Math.ceil(Math.log(rows)/Math.log(2))));
//				int cols2 = (int)Math.round(Math.pow(2, Math.ceil(Math.log(cols)/Math.log(2))));
//				
//				float xfactor = rows2 /rows;
//				float yfactor = cols2 / cols ;
				
				if( getTerrain() != null && getTerrain().getDataset() != null )
				{
					int rows = getTerrain().getDataset().getRows();
					int cols = getTerrain().getDataset().getCols();
	
					int rows2 = (int)Math.round(Math.pow(2, Math.ceil(Math.log(rows)/Math.log(2))));
					int cols2 = (int)Math.round(Math.pow(2, Math.ceil(Math.log(cols)/Math.log(2))));
					
					yfactor = 1.0f * rows /rows2;
					xfactor = 1.0f * cols / cols2 ;
					System.out.println( "factoring bottom " + xfactor + " " +yfactor );
					
					xfactor = 1.0f;
					yfactor = 1.0f;
				}
				else
				{
					System.out.println( "factoring N/A" );
				}
				GeometryArray array = getTerrainArray();

				sharedBranchGroup = new MyShapeNode()
				{
					@Override
					public String toString()
					{
						return "MyShapeNode-" + getKind() + "-" + counter++;
					}
				};
				if (tiles.tx != null && array != null)
				{
					sharedBranchGroup.set(array, sharedBranchGroup.getTextureAppearance(tiles.tx));
					dirtyFlag = 0;
					System.out.println("makeGeometry CLEAR " + getKind() + " " + dirtyFlag);
					return true;
				}
			}
		}
		return false;
	}

	public boolean isMapVisible()
	{
		return visibleMap[getKind()];
	}

	private void setVisible(boolean visible)
	{
		if (visibleMap[getKind()] != visible)
		{
			visibleMap[getKind()] = visible;
			dirtyFlag |= PALETTE;
			updateVisibleLayer();
		}
	}

	public void setLayer(PaletteRenderableLayer filledLayer)
	{
		layerMap[getKind()] = filledLayer;
		imageMap[getKind()] = null;
		if (visibleMap[getKind()])
		{
			dirtyFlag |= PALETTE;
		}
	}

	protected PaletteRenderableLayer getLayer(int i)
	{
		return layerMap[i];
	}

	public Palette getPalette()
	{
		return getPalette(getKind());
	}

	private Palette getPalette(int i)
	{
		return paletteMap[i];
	}

	private void setPalette(Palette visibleLayerPalette)
	{
		if (getPalette(getKind()) != visibleLayerPalette)
		{
			paletteMap[getKind()] = visibleLayerPalette;
			imageMap[getKind()] = null;
			if (isMapVisible())
			{
				dirtyFlag |= PALETTE;
				updateVisibleLayer();
			}
		}
	}

	private void setAction(int action)
	{
		this.action = action;
	}

	public int getAction()
	{
		return action;
	}

	public PaletteRenderableLayer getTerrain()
	{
		return terrain;
	}

	private void setTerrain(PaletteRenderableLayer t)
	{
		synchronized (lock)
		{
			if (terrain == null)
			{
				terrain = t;
				terrainArray = null;
				dirtyFlag |= GEOMETRY;
				updateVisibleLayer();
			}
			else
			{
				if (!terrain.equals(t))
				{
					terrain = t;
					terrainArray = null;
					dirtyFlag |= GEOMETRY;
					updateVisibleLayer();
				}
			}
		}
	}

	boolean once = false;
	volatile boolean animate = false;

	Timer t;

	void once()
	{
		once = true;
		if (!once)
		{
			once = true;
			t = new Timer();
			t.scheduleAtFixedRate(new TimerTask()
			{
				volatile boolean skip = false;

				@Override
				public void run()
				{
					if (!skip && animate)
					{
						skip = true;
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								dirtyFlag |= PALETTE;
								updateVisibleLayer();
								// skip = false;
							}
						});
					}
				}
			}, 1000, 300);
		}
	}

	void updateVisibleLayer()
	{
		once();
		updateVisibleLayerImpl();
	}

	private void updateVisibleLayerImpl()
	{
		synchronized (lock)
		{
			setBranchGroup(sharedBranchGroup);
			setOldBranchGroup(getBranchGroup());
			boolean changed = makeGeometry();
			if (!changed)
			{
				return;
			}
			setBranchGroup(sharedBranchGroup);
			if (getOldBranchGroup() == null && getBranchGroup() == null)
			{
				return;
			}
			if (getOldBranchGroup() != null && getBranchGroup() != null && getOldBranchGroup().toString().equals(getBranchGroup()))
			{
				return;
			}
			if (getOldBranchGroup() != null && getBranchGroup() == null)
			{
				setAction(RenderableVisibleRaiser.REMOVE);
				raise_RenderableVisibleEvent();
			}
			if (getOldBranchGroup() == null && getBranchGroup() != null)
			{
				setAction(RenderableVisibleRaiser.ADD);
				raise_RenderableVisibleEvent();
			}
			if (getOldBranchGroup() != null && getBranchGroup() != null)
			{
				setAction(RenderableVisibleRaiser.REPLACE);
				raise_RenderableVisibleEvent();
			}
		}

	}

	private BranchGroup removeBranchGroup = null;

	private void setOldBranchGroup(BranchGroup bg)
	{
		removeBranchGroup = bg;
	}

	public BranchGroup getOldBranchGroup()
	{
		return removeBranchGroup;
	}

	private void setBranchGroup(MyShapeNode g)
	{
		branchGroup = g;
	}

	public BranchGroup getBranchGroup()
	{
		return branchGroup;
	}

}
