package app.worker;

import java.awt.Point;

import javax.vecmath.Point3f;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.events.GridStatisticsProviderChangeRaiser;
import star.hydrology.events.SelectWatershedOriginRaiser;
import star.hydrology.events.StreamRootChangeEvent;
import star.hydrology.events.StreamRootChangeRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;
import app.worker.streamnetwork.Channel;
import app.worker.streamnetwork.Stream;

@SignalComponent(extend = AbstractWorker.class)
@Properties( { @Property(name = "flowAccomulation", type = PaletteRenderableLayer.class), @Property(name = "flowDirection", type = PaletteRenderableLayer.class), @Property(name = "terrain", type = PaletteRenderableLayer.class), @Property(name = "center", type = Point3f.class),
        @Property(name = "channelRoot", type = Channel.class), @Property(name = "watershedOrigin", type = Point.class), @Property(name = "streamRoot", type = Stream.class, getter = Property.PUBLIC) })
public class StreamNetworkWorker extends StreamNetworkWorker_generated
{
	private float threshold = 256;

	@Handles(raises = { StreamRootChangeRaiser.class })
	void setTerrain(FilledMapLayerRaiser terrain, FlowaccumulationMapLayerRaiser flowAcc, FlowdirectionMapLayerRaiser flowDir)
	{
		setTerrain(terrain.getLayer());
		setCenter(terrain.getLayer());
		setFlowAccomulation(flowAcc.getLayer());
		setFlowDirection(flowDir.getLayer());
		updateLayer();
	}

	@Handles(raises = { StreamRootChangeRaiser.class })
	void setAccumulationTreshold(GridStatisticsProviderChangeRaiser r)
	{
		if (r.getKind() == LayerConstants.STREAMS)
		{
			setAdjustableValue(r.getCurrent());
		}
	}

	@Handles(raises = { StreamRootChangeRaiser.class })
	void setWatershedOrigin(SelectWatershedOriginRaiser r)
	{
		enumerateStreams(r.getPoint());
	}

	public int getLayerKind()
	{
		return LayerConstants.STREAMNETWORK;
	}

	private void setCenter(PaletteRenderableLayer layer)
	{
		if (layer != null && layer.getDataset() instanceof GridwStat)
		{
			GridwStat gs = (GridwStat) layer.getDataset();
			Point3f center = gs.getCenter();
			setCenter(center);
		}
	}

	protected void setStreamRoot(Stream streamRoot)
	{
		super.setStreamRoot(streamRoot);
		(new StreamRootChangeEvent(this, streamRoot != null)).raise();
	}

	private void setAdjustableValue(float value)
	{
		this.threshold = value;
		updateLayer();
	}

	private float getAdjustableValue()
	{
		return this.threshold;
	}

	private Point getNearestRiver(Point3f point)
	{
		if (getFlowAccomulation() != null && getFlowDirection() != null)
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
					if (accomulation > getAdjustableValue())
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

	private float distance2d(Point3f a, Point3f b)
	{
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		float dz = 0;
		float d2 = dx * dx + dy * dy + dz * dz;
		return (float) Math.sqrt(d2);
	}

	private void enumerateStreams(Point3f point)
	{
		setWatershedOrigin(point != null && getCenter() != null ? getNearestRiver(point) : null);
		updateLayer();
	}

	private void updateLayer()
	{
		Point watershedOrigin = getWatershedOrigin();
		if (watershedOrigin != null)
		{
			Channel channelRoot = new Channel(getTerrain(), getFlowAccomulation());
			walk(watershedOrigin.x, watershedOrigin.y, channelRoot);
			setChannelRoot(channelRoot);
			Stream streamRoot = consolidate(channelRoot);
			setStreamRoot(streamRoot);
		}
		else
		{
			setChannelRoot(null);
			setStreamRoot(null);
		}
	}

	private int depth = 0;

	private void walk(int x, int y, Channel c)
	{
		try
		{
			depth++;
			c.addPoint(x, y);
			int n = getNeighbourCount(x, y);
			if (n == 1)
			{
				for (int i = 0; i < offsetX.length; i++)
				{
					if (offsetDir[i] == (int) getFlowDirection().getDataset().getElementAt(x + offsetX[i], y + offsetY[i]) && threshold < getFlowAccomulation().getDataset().getElementAt(x + offsetX[i], y + offsetY[i]))
					{
						walk(x + offsetX[i], y + offsetY[i], c);
					}
				}
			}
			else
			{
				for (int i = 0; i < offsetX.length; i++)
				{
					if (offsetDir[i] == (int) getFlowDirection().getDataset().getElementAt(x + offsetX[i], y + offsetY[i]) && threshold < getFlowAccomulation().getDataset().getElementAt(x + offsetX[i], y + offsetY[i]))
					{
						Channel child = new Channel(getTerrain(), getFlowAccomulation());
						child.addPoint(x, y);
						c.addChild(child);
						walk(x + offsetX[i], y + offsetY[i], child);
					}
				}
			}
			depth--;
		}
		catch (StackOverflowError stack)
		{
			System.err.println("Stack Error " + depth);
		}
	}

	private int[] offsetX = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
	private int[] offsetY = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
	private int[] offsetDir = new int[] { 16, 32, 64, 128, 1, 2, 4, 8 };

	private int getNeighbourCount(int x, int y)
	{
		int ret = 0;
		for (int i = 0; i < offsetX.length; i++)
		{
			if (offsetDir[i] == (int) getFlowDirection().getDataset().getElementAt(x + offsetX[i], y + offsetY[i]) && threshold < getFlowAccomulation().getDataset().getElementAt(x + offsetX[i], y + offsetY[i]))
			{
				ret++;
			}
		}
		return ret;
	}

	private Stream consolidate(Channel root)
	{
		Stream streams = new Stream(root);
		return streams;
	}
}
