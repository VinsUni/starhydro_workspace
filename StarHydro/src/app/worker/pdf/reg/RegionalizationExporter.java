package app.worker.pdf.reg;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

import javax.vecmath.Point3f;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.events.map.RegionalizeWorkerMapLayerRaiser;
import app.server.worker.JNLPPersist;

@SignalComponent()
public class RegionalizationExporter extends RegionalizationExporter_generated
{
	final int INVALID = -1;

	PaletteRenderableLayer regions = null;
	private ArrayList<Statistics> regions_statistics;
	private PaletteRenderableLayer terrain;
	private TreeMap<Integer, ExportData> data;
	private HashMap<Statistics, Point3f> com;
	private TreeMap<Integer, Float> elevation;
	private TreeMap<Integer, Float> slope;
	private TreeMap<Integer, Float> curvature;
	private TreeMap<Integer, Float> topindex;
	ElevationRaiser elevationRaiser;
	SlopeRaiser slopeRaiser;
	CurvatureRaiser curvatureRaiser;
	TopindexRaiser topindexRaiser;

	@Override
	public void addNotify()
	{
		super.addNotify();
		elevationRaiser = new ElevationRaiser();
		slopeRaiser = new SlopeRaiser();
		curvatureRaiser = new CurvatureRaiser();
		topindexRaiser = new TopindexRaiser();
		getAdapter().addComponent(elevationRaiser);
		getAdapter().addComponent(slopeRaiser);
		getAdapter().addComponent(curvatureRaiser);
		getAdapter().addComponent(topindexRaiser);

	}

	@Handles(raises = {})
	protected void discretisizeLayer(RegionalizeWorkerMapLayerRaiser r, FilledMapLayerRaiser mr)
	{
		regions = r.getLayer();
		regions_statistics = r.getRegionArray();
		terrain = mr.getLayer();
		calculate();
		dump();
		dump2();
	}

	private void dump()
	{
		StringBuffer sb = new StringBuffer();
		Iterator<ExportData> iter = data.values().iterator();
		while (iter.hasNext())
		{
			sb.append(iter.next());
			sb.append("\n");
		}
		JNLPPersist persist = new JNLPPersist();
		try
		{
			persist.save("points.export", sb.toString());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void dump2()
	{
		StringBuffer sb = new StringBuffer();
		Iterator<ExportData> iter = data.values().iterator();
		HashSet<String> mydata = new HashSet<String>();
		while (iter.hasNext())
		{
			mydata.add(iter.next().toNodesString());
		}
		sb.append(mydata.size());
		sb.append("\n");
		Iterator<String> iter2 = mydata.iterator();
		while (iter2.hasNext())
		{
			sb.append(iter2.next());
			sb.append("\n");
		}

		JNLPPersist persist = new JNLPPersist();
		try
		{
			persist.save("tin0.nodes", sb.toString());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	void add(TreeMap<Integer, Float> collection, float value, float area)
	{
		if (collection.get((int) value) == null)
		{
			collection.put((int) value, 0f);
		}
		collection.put((int) value, collection.get((int) value) + area);
	}

	void normalize(TreeMap<Integer, Float> elevation)
	{
		float sum = 0;
		if (true)
		{
			Iterator<Float> iter = elevation.values().iterator();
			while (iter.hasNext())
			{
				sum = sum + iter.next();
			}
		}
		if (sum != 0)
		{
			Iterator<Integer> iter = elevation.keySet().iterator();
			while (iter.hasNext())
			{
				Integer key = iter.next();
				elevation.put(key, elevation.get(key) / sum);
			}
		}
	}

	void calculate()
	{
		this.elevation = new TreeMap<Integer, Float>();
		this.slope = new TreeMap<Integer, Float>();
		this.curvature = new TreeMap<Integer, Float>();
		this.topindex = new TreeMap<Integer, Float>();
		this.data = new TreeMap<Integer, ExportData>();
		this.com = new HashMap<Statistics, Point3f>();
		Iterator<Statistics> iter = regions_statistics.iterator();
		float cellSize = terrain.getDataset().getCellsize();
		while (iter.hasNext())
		{
			Statistics s = iter.next();
			ExportData data = new ExportData();
			data.id = s.regionCode;
			data.area = s.points * cellSize * cellSize;
			data.channel = s.channel;
			data.center_of_mass = getCenterOfMass(s);
			data.elevation = s.elevation.getSum() / s.points;
			data.slope = s.slope.getSum() / s.points;
			data.aspect = s.aspect.getSum() / s.points;
			data.polygon = getPolygon(s);
			data.downstream_id = s.downstream != null ? s.downstream.regionCode : 0;
			data.flow_path_length = s.downstream != null ? getCenterOfMass(s).distance(getCenterOfMass(s.downstream)) : 0;
			data.downstream_channel_id = INVALID;
			data.flow_path_to_channel_length = INVALID;
			this.data.put(data.id, data);
			add(elevation, data.elevation, data.area);
			add(slope, data.slope, data.area);
			add(curvature, s.curvature.getSum() / s.points, data.area);
			add(topindex, s.topindex.getSum() / s.points, data.area);
			if (s.downstream != null)
			{
				ExportData d = this.data.get(s.downstream.regionCode);
				if (d != null)
				{
					d.boundary = false;
				}
				else
				{
					System.err.println("can not find " + s.downstream.regionCode);
				}
			}
		}
		Iterator<Integer> iter2 = data.keySet().iterator();
		while (iter2.hasNext())
		{
			int id = iter2.next();
			ExportData data = this.data.get(id);
			if (data.downstream_channel_id == INVALID)
			{
				data.downstream_channel_id = getChannelID(data);
				data.flow_path_to_channel_length = getChannelFlowPath(data);
			}
		}
		normalize(elevation);
		normalize(slope);
		normalize(curvature);
		normalize(topindex);
		elevationRaiser.raise(elevation);
		slopeRaiser.raise(slope);
		curvatureRaiser.raise(curvature);
		topindexRaiser.raise(topindex);

	}

	int getChannelID(ExportData data)
	{
		if (data.channel || data.downstream_id == 0)
		{
			return data.id;
		}
		else
		{
			if (data.downstream_channel_id == INVALID)
			{
				data.downstream_channel_id = getChannelID(this.data.get(data.downstream_id));
			}
			return data.downstream_channel_id;
		}
	}

	float getChannelFlowPath(ExportData data)
	{
		if (data.channel || data.downstream_id == 0)
		{
			return 0;
		}
		else
		{
			if (data.flow_path_to_channel_length == INVALID)
			{
				data.flow_path_to_channel_length = data.flow_path_length + getChannelFlowPath(this.data.get(data.downstream_id));
			}
			return data.flow_path_to_channel_length;
		}
	}

	private Point3f getCenterOfMass(Statistics s)
	{
		if (com.get(s) != null)
		{
			return com.get(s);
		}
		Iterator<Point> iter = s.coordinates.iterator();
		Point sum = new Point(0, 0);
		int count = s.coordinates.size();
		while (iter.hasNext())
		{
			Point p = iter.next();
			sum.x += p.x;
			sum.y += p.y;
		}
		float[] array = new float[3];
		terrain.getDataset().getPoint(sum.x / count, sum.y / count, array);
		Point3f ret = new Point3f(array);
		com.put(s, ret);
		return ret;
	}

	private ArrayList<Point3f> getPolygon(Statistics s)
	{
		ArrayList<Point3f> ret = new ArrayList<Point3f>();
		Iterator<Point> iter = s.coordinates.iterator();
		while (iter.hasNext())
		{
			Point p = iter.next();
			float[] array = new float[3];
			terrain.getDataset().getPoint(p.x, p.y, array);
			ret.add(new Point3f(array));
		}
		return ret;
	}
}

class ExportData
{
	boolean boundary = true;
	int id;
	float area;
	boolean channel;
	Point3f center_of_mass;
	float elevation;
	float slope;
	float aspect;
	ArrayList<Point3f> polygon;
	int downstream_id;
	float flow_path_length;
	int downstream_channel_id;
	float flow_path_to_channel_length;

	public String toString()
	{
		return "\t" + id + "\t" + area + "\t" + (channel ? 1 : 0) + "\t" + center_of_mass.x + "\t" + center_of_mass.y + "\t" + center_of_mass.z + "\t" + elevation + "\t" + slope + "\t" + aspect + "\t" + downstream_id + "\t" + flow_path_length + "\t" + downstream_channel_id + "\t" + flow_path_to_channel_length
		/* + "\t" + polygon */;
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	public String toNodesString()
	{
		final int HILLSLOPE = 0;
		final int BOUNDARY = 1;
		final int OUTLET = 2;
		final int CHANNEL = 3;
		int type = HILLSLOPE;
		if (channel)
		{
			type = CHANNEL;
		}
		if (boundary)
		{
			type = BOUNDARY;
		}
		if (downstream_channel_id == 0)
		{
			type = OUTLET;
		}
		return "\t" + center_of_mass.x + "\t" + center_of_mass.y + "\t" + type + "\t" + center_of_mass.z;
	}
}
