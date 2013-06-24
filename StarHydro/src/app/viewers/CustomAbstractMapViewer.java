package app.viewers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
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
import star.hydrology.ui.palette.Palette;
import star.hydrology.ui.palette.PaletteArrayFactory;

@SignalComponent(raises = { ViewerChangeRaiser.class, RenderableVisibleRaiser.class })
abstract public class CustomAbstractMapViewer extends CustomAbstractMapViewer_generated
{
	protected int PALETTE = 1;
	protected int GEOMETRY = 2;
	protected int GEOMETRYINDEX = 4;

	protected int dirtyFlag = 0;
	protected final int COLORS = 256;

	private static Object lock = new Object();

	private static PaletteRenderableLayer terrain = null;
	private Palette palette = null;
	float[] paletteColors = new float[4 * COLORS];
	private boolean visible = false;
	private int action;

	private int BAtype = BufferedImage.TYPE_INT_ARGB_PRE;
	private Point3f center = null;
	private boolean visibleLayerVisible = false;
	private MyShapeNode branchGroup;
	private MyShapeNode localBranchGroup;
	private static MyShapeNode sharedBranchGroup;
	

	private static BufferedImage[] imageMap = new BufferedImage[LayerConstants.LAYERS_COUNT];
	private static boolean[] visibleMap = new boolean[LayerConstants.LAYERS_COUNT];
	private static Palette[] paletteMap = new Palette[LayerConstants.LAYERS_COUNT];
	private static PaletteRenderableLayer[] colorMap = new PaletteRenderableLayer[LayerConstants.LAYERS_COUNT];
	private static PaletteRenderableLayer[] layerMap = new PaletteRenderableLayer[LayerConstants.LAYERS_COUNT];
	
	private static GeometryArray terrainArray = null;

	private static int GArows = -1;
	private static int GAcols = -1;

	public BufferedImage getTextureImage()
	{
		if (GArows > 0 && GAcols > 0)
		{
			BufferedImage image = new BufferedImage(GAcols, GArows, BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.getGraphics();
			g.clearRect(0, 0, GAcols, GArows);
			for (int i = 0; i < imageMap.length ; i++)
			{
				if( i == LayerConstants.STREAMS) { continue ; } 
				if (visibleMap[i] ) 
				{
					System.out.println( "draw " + i );
					imageMap[i] = getBufferedImage(i);					
					g.drawImage(imageMap[i], 0, 0, null);
				}
			}
			g.setColor(new Color( 1f, 0f, 0f, 0.25f));
			g.drawLine(0, 0, 200, 200);
			g.dispose();
			return image;
		}
		return null;
	}

	public GeometryArray getTerrainArray()
	{
		synchronized (lock)
		{
			if (terrainArray == null && getTerrain() != null)
			{
				Grid grid = getTerrain().getDataset();
				int rows = grid.getRows();
				int cols = grid.getCols();

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
						tex2d[2 * index] = 1.0f * x / cols;
						tex2d[2 * index + 1] = 1 - 1.0f * y / rows;
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
				int arrayType = GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.COORDINATES | GeometryArray.USE_COORD_INDEX_ONLY | /*GeometryArray.BY_REFERENCE_INDICES |*/ GeometryArray.BY_REFERENCE;
				IndexedQuadArray ret = new IndexedQuadArray(rows * cols, arrayType, 1, new int[] { 0 }, indices.length);

				ret.setCoordinateIndices(0, indices);
				//ret.setCoordIndicesRef(indices);
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

	@Handles(raises = { ViewerChangeRaiser.class }, handleValid = false)
	void updateLayersInvalid(star.hydrology.events.LayerChangedRaiser r)
	{
		if (r.getLayerKind() == getKind())
		{
			setLayer(null);
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
			center = gs.getCenter();
			terrainArray = null;
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
		updateVisibleLayer();
	}

	public PaletteRenderableLayer getColorLayer(int i)
	{
		PaletteRenderableLayer ret = colorMap[i] ;
		return ret != null ? ret : layerMap[i] ;
	}

	private BufferedImage getBufferedImage(int i)
	{

		PaletteRenderableLayer layer = getColorLayer(i);
		Palette palette = getPalette(i);
		System.out.println( i + " " + palette + " " + layer );
		int transparent = new Color(0f,0f,0f,0f).getRGB();
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
						
						img.setRGB(x, y, transparent);
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
			Texture tx = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, cols, rows);
			tx.setImage(0, new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, img));
			ret.tx = tx;
		}
		return ret;
	}

	protected void makeGeometry()
	{
		GeometryArray array = getTerrainArray();
		GeometryTile tiles = makeGeometryIndexes();
		sharedBranchGroup = new MyShapeNode();
		if (tiles.tx != null && array != null )
		{
			sharedBranchGroup.set(array, sharedBranchGroup.getTextureAppearance(tiles.tx));
		}

	}

	public boolean isMapVisible()
	{
		return visible;
	}

	private void setVisible(boolean visible)
	{
		if (this.visible != visible)
		{
			this.visible = visible;
			visibleMap[getKind()] = visible;
			updateVisibleLayer();
		}
	}

	public void setLayer(PaletteRenderableLayer filledLayer)
	{
		layerMap[getKind()] = filledLayer ;
		dirtyFlag |= GEOMETRY | GEOMETRYINDEX;
	}

	protected PaletteRenderableLayer getLayer( int i )
	{
		return layerMap[i];
	}
	public Palette getPalette()
	{
		return palette;
	}

	private Palette getPalette(int i)
	{
		return paletteMap[i];
	}

	private void setPalette(Palette visibleLayerPalette)
	{
		if (this.palette != visibleLayerPalette)
		{
			this.palette = visibleLayerPalette;
			paletteColors = PaletteArrayFactory.getArray(palette, COLORS);
			paletteMap[getKind()] = palette;
			updateVisibleLayer();
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

	private void setTerrain(PaletteRenderableLayer terrain)
	{
		synchronized (lock)
		{
			this.terrain = terrain;
			terrainArray = null;
			updateVisibleLayer();
		}
	}

	public BranchGroup getBranchGroup()
	{
		return branchGroup;
	}

	void updateVisibleLayer()
	{
		updateVisibleLayerImpl();
	}

	void setBranchGroup( MyShapeNode myBranchGroup )
	{
		localBranchGroup = myBranchGroup ;
	}
	
	private void updateVisibleLayerImpl()
	{

		branchGroup = localBranchGroup != null ? localBranchGroup : sharedBranchGroup ;		
		removeBranchGroup = null;
		boolean removeCurrent = false;
		boolean addNew = false;

		if (branchGroup != null)
		{
			removeCurrent = true;
			setOldBranchGroup(getBranchGroup());
			visibleLayerVisible = false;
			branchGroup = null;
		}
		if (isMapVisible() && getCenter() != null && getTerrain() != null)
		{
			makeGeometry();
			branchGroup = localBranchGroup != null ? localBranchGroup : sharedBranchGroup ;
		}
		if (!Thread.currentThread().isInterrupted())
		{
			if (!visibleLayerVisible && isMapVisible() && getBranchGroup() != null)
			{
				addNew = true;
				visibleLayerVisible = true;
			}
			if (!isMapVisible() && visibleLayerVisible && getBranchGroup() != null)
			{
				removeCurrent = true;
				visibleLayerVisible = false;
			}
		}
		if (removeCurrent && addNew)
		{
			setAction(RenderableVisibleRaiser.REPLACE);
			raise_RenderableVisibleEvent();
		}
		else if (removeCurrent)
		{
			setAction(RenderableVisibleRaiser.REMOVE);
			raise_RenderableVisibleEvent();
		}
		else if (addNew)
		{
			setAction(RenderableVisibleRaiser.ADD);
			raise_RenderableVisibleEvent();
		}

	}

	private SoftReference<BranchGroup> removeBranchGroup = null;

	private void setOldBranchGroup(BranchGroup bg)
	{
		removeBranchGroup = new SoftReference<BranchGroup>(bg);
	}

	public BranchGroup getOldBranchGroup()
	{
		return removeBranchGroup != null ? removeBranchGroup.get() : null;
	}

}
