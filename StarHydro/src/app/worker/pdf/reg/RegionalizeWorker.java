package app.worker.pdf.reg;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.data.layers.FloatDataset;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.CurvaturePDFLMapLayerRaiser;
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;
import star.hydrology.events.map.RegionalizationRaiser;
import star.hydrology.events.map.RegionalizationStatisticsEvent;
import star.hydrology.events.map.RegionalizationStatisticsRaiser;
import star.hydrology.events.map.RegionalizeWorkerMapLayerEvent;
import star.hydrology.events.map.RegionalizeWorkerMapLayerRaiser;
import star.hydrology.events.map.SlopePDFMapLayerRaiser;
import star.hydrology.events.map.TopindexPDFMapLayerRaiser;
import star.hydrology.events.map.WatershedLayerRaiser;
import app.viewers.map.RegionalizationModel;

@SignalComponent()
@Properties( { @Property(name = "flowAcc", type = PaletteRenderableLayer.class, getter = Property.PUBLIC), @Property(name = "flowDir", type = PaletteRenderableLayer.class, getter = Property.PUBLIC), @Property(name = "watershed", type = PaletteRenderableLayer.class, getter = Property.PUBLIC),
        @Property(name = "curvature", type = PaletteRenderableLayer.class, getter = Property.PUBLIC), @Property(name = "slope", type = PaletteRenderableLayer.class, getter = Property.PUBLIC), @Property(name = "topindex", type = PaletteRenderableLayer.class, getter = Property.PUBLIC),
        @Property(name = "terrain", type = PaletteRenderableLayer.class, getter = Property.PUBLIC), @Property(name = "filled", type = PaletteRenderableLayer.class, getter = Property.PUBLIC), @Property(name = "layer", type = PaletteRenderableLayer.class, getter = Property.PUBLIC),
        @Property(name = "watershedOrigin", type = Point.class, getter = Property.PUBLIC) })
public class RegionalizeWorker extends RegionalizeWorker_generated
{

	RegionalizationModel parameters;
	RegionalizationModel statistics = new RegionalizationModel();

	public RegionalizationModel getParameters()
	{
		return parameters;
	}

	public void setParameters(RegionalizationModel parameters)
	{
		this.parameters = parameters;
	}

	public RegionalizationModel getStatistics()
	{
		return statistics;
	}

	@Handles(raises = { RegionalizeWorkerMapLayerRaiser.class, RegionalizationStatisticsRaiser.class })
	protected void handle(final FlowaccumulationMapLayerRaiser flowAcc, final FlowdirectionMapLayerRaiser flowDir, final WatershedLayerRaiser watershed, final CurvaturePDFLMapLayerRaiser curvature, final SlopePDFMapLayerRaiser slope, final TopindexPDFMapLayerRaiser topindex, final FilledMapLayerRaiser filled,
	        final RegionalizationRaiser parameters)
	{
		if (watershed.getWatershedOrigin() != null)
		{
			setFlowAcc(flowAcc.getLayer());
			setFlowDir(flowDir.getLayer());
			setWatershed(watershed.getLayer());
			setCurvature(curvature.getLayer());
			setSlope(slope.getLayer());
			setTopindex(topindex.getLayer());
			setWatershedOrigin(watershed.getWatershedOrigin());
			setFilled(filled.getLayer());
			setParameters(parameters.getParameters());
			calculate();
		}
	}

	public int getLayerKind()
	{
		return LayerConstants.REGLAYER;
	}

	FloatDataset layergrid = null;

	void calculate()
	{
		if (getWatershed() == null)
		{
			return;
		}

		statistics = new RegionalizationModel();

		staticRegionArray = new ArrayList<Statistics>();

		channelCount = 0;
		hillsideCount = 0;

		try
		{
			layergrid = (FloatDataset) ((FloatDataset) getWatershed().getDataset()).getSameCoverage(FloatDataset.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Statistics s = new Statistics(this);
		Point watershedOrigin = getWatershedOrigin();
		walk(watershedOrigin.x, watershedOrigin.y, s);
		walk2(watershedOrigin.x, watershedOrigin.y);
		// walk3(watershedOrigin.x, watershedOrigin.y);
		layergrid.recalculateMinimumAndMaximum();
		ProjectedTerrainMap layer = new ProjectedTerrainMap();
		layer.setLayerName("Regions");
		layer.setDataset(layergrid);
		setLayer(layer);
		layergrid = null;
		raise();

		Iterator<Statistics> iter = staticRegionArray.iterator();
		TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> hillslopeMap = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> channelMap = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> mmap;
		int region = 0;
		int total_points = 0;
		while (iter.hasNext())
		{
			Statistics next = iter.next();
			mmap = next.channel ? channelMap : hillslopeMap;
			if (next.points > 0)
			{
				region++;
				total_points += next.points;
			}

			if (next != null && null != mmap.get(new Integer(next.points)))
			{
				mmap.put(new Integer(next.points), new Integer(mmap.get(new Integer(next.points)).intValue() + 1));

			}
			else
			{
				mmap.put(new Integer(next.points), new Integer(1));
			}

			if (next != null && null != map.get(new Integer(next.points)))
			{
				map.put(new Integer(next.points), new Integer(map.get(new Integer(next.points)).intValue() + 1));

			}
			else
			{
				map.put(new Integer(next.points), new Integer(1));
			}
		}
		System.out.println("Regions " + map);
		System.out.println("Regions (combined)" + region);

		statistics.put(RegionalizationRaiser.Parameters.totalPoints, new Float(total_points));
		statistics.put(RegionalizationRaiser.Parameters.totalRegions, new Float(region));
		statistics.putMap(RegionalizationRaiser.Parameters.regionsMap, map);
		statistics.putMap(RegionalizationRaiser.Parameters.hillslopeMap, hillslopeMap);
		statistics.putMap(RegionalizationRaiser.Parameters.channelMap, channelMap);
		System.out.println("Parameters " + getParameters());
		System.out.println("Statistics " + getStatistics());

		(new RegionalizationStatisticsEvent(this)).raise();
	}

	int[] offsetX = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
	int[] offsetY = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
	int[] offsetDir = new int[] { 16, 32, 64, 128, 1, 2, 4, 8 };
	int size;

	int nextCode(Statistics s)
	{
		staticRegionArray.add(s);
		return staticRegionArray.size() - 1;
	}

	ArrayList<Statistics> staticRegionArray;

	public ArrayList<Statistics> getRegionArray()
	{
		return staticRegionArray;
	}

	public void updateStatistics(RegionalizationRaiser.Parameters param)
	{
		Float x = statistics.get(param);
		if (x != null)
		{
			statistics.put(param, new Float(x.floatValue() + 1));
		}
		else
		{
			statistics.put(param, new Float(1));
		}
	}

	int channelCount = 0;
	int hillsideCount = 0;

	void walk(int x, int y, Statistics s)
	{
		s.addPoint(x, y);
		layergrid.setElementAt(x, y, s.regionCode);
		Statistics newS = null;
		for (int i = 0; i < offsetX.length; i++)
		{
			if (offsetDir[i] == (int) getFlowDir().getDataset().getElementAt(x + offsetX[i], y + offsetY[i]))
			{
				if (s.isPointInRange(x + offsetX[i], y + offsetY[i]))
				{
					newS = null;
					walk(x + offsetX[i], y + offsetY[i], s);
				}
				else
				{
					if (newS != null && newS.isPointInRange(x + offsetX[i], y + offsetY[i]))
					{
						walk(x + offsetX[i], y + offsetY[i], newS);
					}
					else
					{
						newS = new Statistics(this);
						newS.downstream = s;
						walk(x + offsetX[i], y + offsetY[i], newS);
					}
				}
			}
		}
	}

	void walk2(int x, int y)
	{
		Statistics self = staticRegionArray.get((int) layergrid.getElementAt(x, y));
		for (int i = 0; i < offsetX.length; i++)
		{
			if (offsetDir[i] == (int) getFlowDir().getDataset().getElementAt(x + offsetX[i], y + offsetY[i]))
			{

				Statistics s = staticRegionArray.get((int) layergrid.getElementAt(x + offsetX[i], y + offsetY[i]));
				float size = s.channel ? getParameters().get(RegionalizationRaiser.Parameters.channelMergeSizeThreshold) : getParameters().get(RegionalizationRaiser.Parameters.hillslopeMergeSizeThreshold);
				if (s.points < size && s.channel == self.channel)
				{
					if (self.channel)
					{
						updateStatistics(RegionalizationRaiser.Parameters.channelMergeSizeThreshold);
					}
					else
					{
						updateStatistics(RegionalizationRaiser.Parameters.hillslopeMergeSizeThreshold);
					}
					Statistics small = staticRegionArray.get((int) layergrid.getElementAt(x + offsetX[i], y + offsetY[i]));
					Statistics large = staticRegionArray.get((int) layergrid.getElementAt(x, y));
					large.addStatistics(small);
					layergrid.setElementAt(x + offsetX[i], y + offsetY[i], layergrid.getElementAt(x, y));
				}
				walk2(x + offsetX[i], y + offsetY[i]);
			}
		}
	}

	void walk3(int x, int y)
	{
		// only channels
		if (false && !staticRegionArray.get((int) layergrid.getElementAt(x, y)).channel)
		{
			layergrid.setElementAt(x, y, Float.NaN);
		}
		else
		{
			layergrid.setElementAt(x, y, layergrid.getElementAt(x, y) % 16);
		}
		for (int i = 0; i < offsetX.length; i++)
		{
			if (offsetDir[i] == (int) getFlowDir().getDataset().getElementAt(x + offsetX[i], y + offsetY[i]))
			{
				walk3(x + offsetX[i], y + offsetY[i]);
			}
		}
	}

	void raise()
	{
		(new RegionalizeWorkerMapLayerEvent(this)).raise();
	}

	float threshold = 0;

	public float getThreshold()
	{
		return threshold;
	}
}
