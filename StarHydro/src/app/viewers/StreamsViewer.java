package app.viewers;

import java.awt.Point;

import javax.media.j3d.Appearance;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.vecmath.Point3f;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.Raiser;
import star.annotations.SignalComponent;
import star.hydro.util.Utilities;
import star.hydrology.data.interfaces.AdjustableValue;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.data.teal.MyShapeNode;
import star.hydrology.events.AdjustableValueEvent;
import star.hydrology.events.AdjustableValueRaiser;
import star.hydrology.events.GridStatisticsProviderChangeRaiser;
import star.hydrology.events.RenderableVisibleRaiser;
import star.hydrology.events.ViewerChangeEvent;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;

@SignalComponent(extend = CustomAbstractMapViewer.class)
@Properties( { @Property(name = "flowAccomulation", type = PaletteRenderableLayer.class), @Property(name = "flowDirection", type = PaletteRenderableLayer.class) })
public class StreamsViewer extends StreamsViewer_generated implements RenderableVisibleRaiser, AdjustableValue
{

	private float threshold = 256;

	public int getKind()
	{
		return LayerConstants.STREAMS;
	}

	public String getViewerName()
	{
		return "Stream Network";
	}

	private void raiseViewerChange()
	{
		if (getLayer(getKind()) != null && getFlowAccomulation() != null && getFlowDirection() != null)
		{
			(new ViewerChangeEvent(this)).raise();
		}
		if (!adjValRaised)
		{
			adjValRaised = true;
			(new AdjustableValueEvent(this)).raise();
		}
	}

	private boolean adjValRaised = false;

	private boolean adjusting = false;

	@Handles(raises = { RenderableVisibleRaiser.class, AdjustableValueRaiser.class })
	protected void setAdjustableValue(GridStatisticsProviderChangeRaiser r)
	{
		if (!adjusting)
		{
			adjusting = true;
			if (r.getKind() == getKind())
			{
				setAdjustableValue(r.getCurrent());
			}
			adjusting = false;
		}
	}

	@Handles(raises = { RenderableVisibleRaiser.class }, concurrency = Raiser.POOLED)
	protected void setLayers(FilledMapLayerRaiser filled, FlowaccumulationMapLayerRaiser flowAcc, FlowdirectionMapLayerRaiser flowDir)
	{
		setLayer(filled.getLayer());
		setFlowAccomulation(flowAcc.getLayer());
		setColorLayer(flowAcc.getLayer());
		setFlowDirection(flowDir.getLayer());
		dirtyFlag |= GEOMETRY | GEOMETRYINDEX;
		updateVisibleLayer();
		raiseViewerChange();
	}

	protected void makeGeometry()
	{
		if (getFlowAccomulation() != null && getFlowDirection() != null)
		{
			MyShapeNode branchGroup = new MyShapeNode();
			GeometryTile tiles = makeGeometryIndexes1();
			if (Thread.currentThread().isInterrupted())
			{
				return;
			}
			float[] coords = tiles.coords;
			int[] colorIndex = tiles.colorIndex;
			if (coords.length != 0)
			{								
				LineArray array = new LineArray(coords.length / 3, 0 | IndexedLineArray.COORDINATES | IndexedLineArray.COLOR_3);
				array.setCoordinates(0, coords);
				int last = 0;
				try
				{
					byte[] colors = new byte[colorIndex.length * 3];

					for (int i = 0; i < colorIndex.length; i++)
					{
						if (colorIndex[i] >= 0)
						{
							colors[i * 3 + 0] = (byte) (255 * paletteColors[colorIndex[i] * 4 + 0]);
							colors[i * 3 + 1] = (byte) (255 * paletteColors[colorIndex[i] * 4 + 1]);
							colors[i * 3 + 2] = (byte) (255 * paletteColors[colorIndex[i] * 4 + 2]);
						}
						else
						{
							System.out.println("index for " + i + " is -1 ");
							colors[i * 3 + 0] = (byte) 0xff;
							colors[i * 3 + 1] = (byte) 0xff;
							colors[i * 3 + 2] = (byte) 0xff;
						}
					}

					array.setColors(0, colors);
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					System.err.println("For index i =" + last);
					System.err.println("ColorIndex[i]=" + colorIndex[last]);
					e.printStackTrace();
				}
				Appearance a = branchGroup.getDefaultAppearance();
				LineAttributes la = new LineAttributes();
				la.setLineAntialiasingEnable(false);
				la.setLineWidth(1.5f);
				a.setLineAttributes(la);
				branchGroup.set(array, a);
				setBranchGroup(branchGroup);
			}
		}
	}

	private GeometryTile makeGeometryIndexes1()
	{
		GeometryTile tiles = null;
		if (getFlowAccomulation() != null && getFlowDirection() != null)
		{
			int rows = getLayer(getKind() ).getDataset().getRows();
			int cols = getLayer(getKind() ).getDataset().getCols();
			tiles = makeTileGeometryIndexes(0, rows, 0, cols);
			if (Thread.currentThread().isInterrupted())
			{
				return null;
			}
			dirtyFlag &= ~GEOMETRYINDEX;
		}
		return tiles;
	}

	private GeometryTile makeTileGeometryIndexes(int row_start, int row_end, int col_start, int col_end)
	{
		final float delta = 2f;

		int row_step = 1;
		int col_step = 1;
		int rows = (row_end - row_start) / row_step;
		int cols = (col_end - col_start) / col_step;
		int length = rows * cols;
		int coordAdd = 3;
		int[] colorIndex = new int[length * 4];
		float[] coords = new float[length * 4 * 3];
		int coordAdd2D = 2;
		int[] coords2D = new int[length * 4 * 2];

		float cmin = ((GridwStat) getColorLayer( getKind() ).getDataset()).getMinimum();
		float cmax = ((GridwStat) getColorLayer( getKind() ).getDataset()).getMaximum();

		float[] f1 = new float[3];
		float[] f2 = new float[3];

		int pos = 0;
		final Point offset = new Point();
		final Point3f center = getCenter();
		for (int y = 0; y < rows - 1; y++)
		{
			if (Thread.currentThread().isInterrupted())
			{
				return null;
			}
			for (int x = 0; x < cols - 1; x++)
			{
				float accomulation = getFlowAccomulation().getDataset().getElementAt(x * col_step + col_start, y * row_step + row_start);
				if (accomulation > getAdjustableValue())
				{
					star.hydro.util.Utilities.getDownstreamOffset((byte) getFlowDirection().getDataset().getElementAt(x * col_step + col_start, y * row_step + row_start), offset);
					if( offset.x == 0 && offset.y == 0)
					{
						continue;
					}
					if (x + offset.x >= 0 && x + offset.x < cols && y + offset.y >= 0 && y + offset.y < rows)
					{
						getLayer(getKind() ).getDataset().getPoint(x * col_step + col_start, y * row_step + row_start, f1);
						getLayer(getKind() ).getDataset().getPoint((x + offset.x) * col_step + col_start, (y + offset.y) * row_step + row_start, f2);

						if (!Float.isNaN(f1[2]) && !Float.isNaN(f2[2]))
						{
							coords[pos * coordAdd + 0] = f1[0] - center.x;
							coords[pos * coordAdd + 1] = f1[1] - center.y;
							coords[pos * coordAdd + 2] = f1[2] + delta;

							coords[(pos + 1) * coordAdd + 0] = f2[0] - center.x;
							coords[(pos + 1) * coordAdd + 1] = f2[1] - center.y;
							coords[(pos + 1) * coordAdd + 2] = f2[2] + delta;

							coords2D[pos * coordAdd2D + 0] = x;
							coords2D[pos * coordAdd2D + 1] = y;

							coords2D[(pos + 1) * coordAdd2D + 0] = x + offset.x;
							coords2D[(pos + 1) * coordAdd2D + 1] = y + offset.y;

							float cvalue = getColorLayer(getKind() ).getDataset().getElementAt(x * col_step + col_start, y * row_step + row_start);
							int cindex = (int) ((COLORS - 1) * (cvalue - cmin) / (cmax - cmin));
							colorIndex[pos] = cindex;
							colorIndex[pos + 1] = cindex;
							pos += 2;
						}
					}

				}
			}
		}

		coords = Utilities.trimFloatArray(coords, pos * coordAdd);
		colorIndex = Utilities.trimIntArray(colorIndex, pos);
		coords2D = Utilities.trimIntArray(coords2D, pos * coordAdd2D);

		GeometryTile ret = new GeometryTile();
		ret.colorIndex = colorIndex;
		ret.coords = coords;
		ret.coords2D = coords2D;

		return ret;
	}

	public float getAdjustableMaximum()
	{
		return ((GridwStat) getFlowAccomulation().getDataset()).getMaximum();
	}

	public float getAdjustableMinimum()
	{
		float min = ((GridwStat) getFlowAccomulation().getDataset()).getMinimum();
		if (min <= 0)
		{
			min = 1;
		}
		return min;
	}

	public String getAdjustableName()
	{
		return "Stream accumulation threshold";
	}

	public float getAdjustableValue()
	{
		return threshold;
	}

	private void setAdjustableValue(float value)
	{
		if (this.threshold != value)
		{
			this.threshold = value;
			dirtyFlag |= GEOMETRYINDEX;
			updateVisibleLayer();
			(new AdjustableValueEvent(this)).raise();
		}
	}
}
