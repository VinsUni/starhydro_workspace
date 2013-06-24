package app.worker.map;

import java.awt.Point;

import javax.vecmath.Point3f;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.data.layers.FloatDataset;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.GridStatisticsProviderChangeRaiser;
import star.hydrology.events.SelectWatershedOriginRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.events.map.FlowUpstreamDirectionRaiser;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;
import star.hydrology.events.map.WatershedLayerEvent;
import star.hydrology.events.map.WatershedLayerRaiser;
import utils.Runner;
import app.worker.AbstractWorker;

@SignalComponent(extend = AbstractWorker.class, raises = { WatershedLayerRaiser.class })
@Properties( { @Property(name = "flowAccomulation", type = PaletteRenderableLayer.class), @Property(name = "flowDirection", type = PaletteRenderableLayer.class), @Property(name = "flowUpstream", type = PaletteRenderableLayer.class), @Property(name = "terrain", type = PaletteRenderableLayer.class),
        @Property(name = "threshold", type = float.class), @Property(name = "watershedOrigin", type = Point.class, getter = Property.PUBLIC), @Property(name = "center", type = Point3f.class) })
public class WatershedWorker extends WatershedWorker_generated
{
	FloatDataset watershed;
	long size = 0;
	int cols;
	int rows;

	void walk(int x, int y)
	{
		if (x < 0 || x >= cols || y < 0 || y >= rows)
		{
			return;
		}

		size++;
		watershed.setElementAt(x, y, getTerrain().getDataset().getElementAt(x, y));

		byte dirs = (byte) getFlowUpstream().getDataset().getElementAt(x, y);

		if ((dirs & 1) != 0)
		{
			walk(x - 1, y + 0);
		}
		if ((dirs & 2) != 0)
		{
			walk(x - 1, y - 1);
		}
		if ((dirs & 4) != 0)
		{
			walk(x - 0, y - 1);
		}
		if ((dirs & 8) != 0)
		{
			walk(x + 1, y - 1);
		}
		if ((dirs & 16) != 0)
		{
			walk(x + 1, y + 0);
		}
		if ((dirs & 32) != 0)
		{
			walk(x + 1, y + 1);
		}
		if ((dirs & 64) != 0)
		{
			walk(x + 0, y + 1);
		}
		if ((dirs & -128) != 0)
		{
			walk(x - 1, y + 1);
		}

	}

	float distance2d(Point3f a, Point3f b)
	{
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		float dz = 0;
		float d2 = dx * dx + dy * dy + dz * dz;
		return (float) Math.sqrt(d2);
	}

	Point getNearestRiver(Point3f point)
	{
		if (getFlowAccomulation() != null)
		{
			int rows = getFlowAccomulation().getDataset().getRows();
			int cols = getFlowAccomulation().getDataset().getCols();
			float distance = Float.MAX_VALUE;
			int xmin = -1;
			int ymin = -1;
			float[] f1 = new float[3];
			Point3f p = new Point3f();
			for (int y = 0; y < rows - 1; y++)
			{
				for (int x = 0; x < cols - 1; x++)
				{
					float accomulation = getFlowAccomulation().getDataset().getElementAt(x, y);
					if (accomulation > getThreshold())
					{
						getFlowAccomulation().getDataset().getPoint(x, y, f1);
						p.x = f1[0] - getCenter().x;
						p.y = f1[1] - getCenter().y;
						p.z = f1[2] - getCenter().z;
						float d = distance2d(p, point);
						if (d < distance)
						{
							distance = d;
							xmin = x;
							ymin = y;
						}
					}
				}
			}
			return new Point(xmin, ymin);
		}
		return null;
	}

	protected void setCenter(PaletteRenderableLayer layer)
	{
		if (layer.getDataset() instanceof GridwStat)
		{
			GridwStat gs = (GridwStat) layer.getDataset();
			setCenter(gs.getCenter());
			updateLayer();
		}
	}

	void updateLayer()
	{
		Point watershedOrigin = getWatershedOrigin();
		if (watershedOrigin != null)
		{
			try
			{
				watershed = (FloatDataset) ((FloatDataset) getFlowAccomulation().getDataset()).getSameCoverage(FloatDataset.class);
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			size = 0;
			long start = System.nanoTime();
			cols = watershed.getCols();
			rows = watershed.getRows();
			walk(watershedOrigin.x, watershedOrigin.y);
			long end = System.nanoTime();
			System.out.println("delay: " + (end - start));
			ProjectedTerrainMap m = new ProjectedTerrainMap();
			m.setXYZDataset(watershed);
			m.setLayerName("Watershed");
			setLayer(m);
		}
		else
		{
			(new WatershedLayerEvent(this, false)).raise();
		}
	}

	public int getLayerKind()
	{
		return LayerConstants.WATERSHED;
	}

	protected void raise()
	{
		long start = Runner.start();
		(new star.hydrology.events.map.WatershedLayerEvent(this)).raise();
		Runner.stop(start, "raise watershed layer event");
	}

	@Handles(raises = { WatershedLayerRaiser.class })
	protected void setTerrain(FilledMapLayerRaiser terrain, FlowaccumulationMapLayerRaiser flowAcc, FlowdirectionMapLayerRaiser flowDir, FlowUpstreamDirectionRaiser flowUpstream)
	{
		setTerrain(terrain.getLayer());
		setCenter(terrain.getLayer());
		setFlowAccomulation(flowAcc.getLayer());
		setFlowDirection(flowDir.getLayer());
		setFlowUpstream(flowUpstream.getLayer());
		setWatershedOrigin((Point) null);
		updateLayer();
	}

	@Handles(raises = {})
	protected void setAccumulationTreshold(GridStatisticsProviderChangeRaiser r)
	{
		if (r.getKind() == LayerConstants.STREAMS)
		{
			setThreshold(r.getCurrent());
		}
	}

	@Handles(raises = { WatershedLayerRaiser.class })
	protected void setWatershedOrigin(SelectWatershedOriginRaiser r)
	{
		Point3f point = r.getPoint();
		setWatershedOrigin(point != null ? getNearestRiver(point) : null);
		updateLayer();
	}

}
