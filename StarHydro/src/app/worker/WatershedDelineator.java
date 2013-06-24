package app.worker;

import java.awt.Point;

import javax.vecmath.Point3f;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.Raiser;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.events.ApplicableLayerRaiser;
import star.hydrology.events.GridStatisticsProviderChangeRaiser;
import star.hydrology.events.Select3DRaiser;
import star.hydrology.events.Select3DWatershedOriginEvent;
import star.hydrology.events.Select3DWatershedOriginRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;

@SignalComponent(raises = { Select3DWatershedOriginRaiser.class })
@Properties( { @Property(name = "flowAccomulation", type = PaletteRenderableLayer.class), @Property(name = "flowDirection", type = PaletteRenderableLayer.class), @Property(name = "terrain", type = PaletteRenderableLayer.class) })
public class WatershedDelineator extends WatershedDelineator_generated
{
	private static class Point_Point3f
	{
		Point p2d;
		Point3f p3d;

		public Point_Point3f(final Point3f p3d, final Point p2d)
		{
			this.p3d = p3d;
			this.p2d = p2d;
		}
	}

	private boolean active = false;
	private boolean adjusting = false;
	private float threshold = 0;
	private Point3f center;

	public int getKind()
	{
		return LayerConstants.STREAMS;
	}

	protected Point3f getCenter()
	{
		return center;
	}

	@Handles(raises = {})
	protected void findDrainagePoint(ApplicableLayerRaiser tab)
	{
		active = (ApplicableLayerRaiser.watershedSetup.equals(tab.getName()));
	}

	@Handles(raises = { Select3DWatershedOriginRaiser.class }, concurrency = Handles.ASYNC)
	void findDrainagePoint(Select3DRaiser r)
	{
		if (active)
		{
			calculateDrainPoint(r.getPoint());
		}
	}

	@Handles(raises = {})
	void setAdjustableValue(GridStatisticsProviderChangeRaiser r)
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

	private void setCenter(PaletteRenderableLayer layer)
	{
		if (layer != null && layer.getDataset() instanceof GridwStat)
		{
			GridwStat gs = (GridwStat) layer.getDataset();
			center = gs.getCenter();
		}
	}

	@Handles(raises = { Select3DWatershedOriginRaiser.class }, concurrency = Raiser.POOLED)
	void setLayers(FilledMapLayerRaiser filled, FlowaccumulationMapLayerRaiser flowAcc, FlowdirectionMapLayerRaiser flowDir)
	{
		setTerrain(filled.getLayer());
		setCenter(filled.getLayer());
		setFlowAccomulation(flowAcc.getLayer());
		setFlowDirection(flowDir.getLayer());
	}

	private void setAdjustableValue(float value)
	{
		if (this.threshold != value)
		{
			this.threshold = value;
		}
	}

	private float getAdjustableValue()
	{
		return this.threshold;
	}

	private Point3f drainPoint;
	private Point drainPoint2d;

	private void calculateDrainPoint(Point3f point)
	{
		Point_Point3f p = getNearestRiver(point);
		drainPoint2d = p.p2d;
		drainPoint = p.p3d;
		System.out.println(this.getClass().getName() + " raise " + drainPoint);
		(new Select3DWatershedOriginEvent(this)).raise();
	}

	private Point_Point3f getNearestRiver(Point3f point)
	{
		if (getFlowAccomulation() != null && getFlowDirection() != null)
		{
			int rows = getFlowAccomulation().getDataset().getRows();
			int cols = getFlowAccomulation().getDataset().getCols();
			float distance = Float.MAX_VALUE;
			float[] f1 = new float[3];
			Point3f p = new Point3f();
			Point3f ret = new Point3f();
			Point ret2 = new Point();
			Point_Point3f r = new Point_Point3f(ret, ret2);
			for (int y = 0; y < rows - 1; y++)
			{
				for (int x = 0; x < cols - 1; x++)
				{
					float accomulation = getFlowAccomulation().getDataset().getElementAt(x, y);
					if (accomulation > getAdjustableValue())
					{
						getTerrain().getDataset().getPoint(x, y, f1);
						p.x = f1[0] - getCenter().x;
						p.y = f1[1] - getCenter().y;
						p.z = f1[2] - getCenter().z;
						float d = distance2d(p, point);
						if (d < distance)
						{
							distance = d;
							ret.x = p.x;
							ret.y = p.y;
							ret.z = p.z;
							ret2.x = x;
							ret2.y = y;
						}
					}
				}
			}
			return r;
		}
		return null;
	}

	private float distance2d(Point3f a, Point3f b)
	{
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		float dz = 0;
		float d2 = dx * dx + dy * dy + dz * dz;
		return (float) Math.sqrt(d2);
	}

	public Point3f getPoint()
	{
		return this.drainPoint;
	}

}
