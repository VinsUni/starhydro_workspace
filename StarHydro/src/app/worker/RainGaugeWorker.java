package app.worker;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Point3f;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydro.rainfall.TimeSeries;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.data.layers.AbstractDataset;
import star.hydrology.data.layers.FloatDataset;
import star.hydrology.events.ApplicableLayerRaiser;
import star.hydrology.events.LayerChangedEvent;
import star.hydrology.events.LayerChangedRaiser;
import star.hydrology.events.RainfallGaugesRaiser;
import star.hydrology.events.RainfallGaugesTimeseriesRaiser;
import star.hydrology.events.Select3DRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;

@SignalComponent(raises = { RainfallGaugesTimeseriesRaiser.class })
@Properties( { @Property(name = "flowAccomulation", type = PaletteRenderableLayer.class), @Property(name = "flowDirection", type = PaletteRenderableLayer.class), @Property(name = "terrain", type = PaletteRenderableLayer.class) })
public class RainGaugeWorker extends RainGaugeWorker_generated
{
	private ArrayList<Point> raingauges = new ArrayList<Point>();
	private ArrayList<Point3f> raingauges3d = new ArrayList<Point3f>();
	private ArrayList<TimeSeries> timeseries = new ArrayList<TimeSeries>();
	private FloatDataset dataset;
	private PaletteRenderableLayer raingauge_layer;

	private Point3f center = null;

	private void setCenter(PaletteRenderableLayer layer)
	{
		if (layer != null && layer.getDataset() instanceof GridwStat)
		{
			GridwStat gs = (GridwStat) layer.getDataset();
			center = gs.getCenter();
		}
	}

	private Point3f getCenter()
	{
		return center;
	}

	private boolean active = false;

	@Handles(raises = {})
	void findDrainagePoint(ApplicableLayerRaiser tab)
	{
		active = (ApplicableLayerRaiser.hydrographic.equals(tab.getName()));
	}

	@Handles(raises = { RainfallGaugesRaiser.class })
	void findDrainagePoint(Select3DRaiser r)
	{
		if (active)
		{
			updatePoint(r.getPoint(), r.getButton());
			raise_RainfallGaugesEvent();
		}
	}

	private void updatePoint(Point3f point3d, int button)
	{
		Grid g = getTerrain().getDataset();
		java.awt.Point point = getPoint(point3d.x, point3d.y, g.getCellsize(), g.getRows(), g.getCols());
		if (button == MouseEvent.BUTTON1)
		{
			add(point, point3d);
		}
		else if (button == MouseEvent.BUTTON3)
		{
			remove(point, point3d);
		}
	}

	@Handles(raises = {})
	void setLayers(FilledMapLayerRaiser filled, FlowaccumulationMapLayerRaiser flowAcc, FlowdirectionMapLayerRaiser flowDir)
	{
		setTerrain(filled.getLayer());
		setCenter(filled.getLayer());
		setFlowAccomulation(flowAcc.getLayer());
		setFlowDirection(flowDir.getLayer());
	}

	@Handles(raises = { /* RainfallGaugesRaiser.class, LayerChangedRaiser.class */})
	void loadTimeseries(RainfallGaugesTimeseriesRaiser raiser)
	{
		if (!this.equals(raiser))
		{
			System.out.println(raiser.getRainfallGauges());
			System.out.println(raiser.getRainfallTimeseries());

			this.raingauges = new ArrayList<Point>();
			Grid g = getTerrain().getDataset();
			Point3f center = getCenter();
			for (Point3f point3d : raiser.getRainfallGauges())
			{
				java.awt.Point point = getPoint(point3d.x - center.x, point3d.y - center.y, g.getCellsize(), g.getRows(), g.getCols());
				raingauges.add(point);
			}
			this.raingauges3d = new ArrayList<Point3f>(raiser.getRainfallGauges());
			this.timeseries = new ArrayList<TimeSeries>(raiser.getRainfallTimeseries());
			updateLayer(null);
		}
	}

	@Handles(raises = { LayerChangedRaiser.class })
	void createLayer(star.hydrology.events.map.FilledMapLayerRaiser raiser)
	{
		PaletteRenderableLayer layer = raiser.getLayer();
		Grid grid = layer.getDataset();
		if (grid instanceof AbstractDataset)
		{
			try
			{
				this.dataset = (FloatDataset) ((AbstractDataset) grid).getSameCoverage(FloatDataset.class);
				this.dataset.clear();
				raingauge_layer = new PaletteRenderableLayer()
				{

					public Grid getDataset()
					{
						return dataset;
					}

					public String getLayerName()
					{
						return "Rain gauges";
					}
				};
				updateLayer(null);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}
		else
		{
			throw new RuntimeException("we support only abstract dataset here for now");
		}
	}

	private static java.awt.Point getPoint(float x, float y, float cellSize, int rows, int cols)
	{

		return new java.awt.Point(Math.round(x / cellSize) + cols / 2, -Math.round(y / cellSize) + rows / 2);
	}

	private void add(Point point, Point3f point3d)
	{
		if (!raingauges.contains(point) || raingauges.size() == 127)
		{
			raingauges.add(point);
			raingauges3d.add(point3d);
			updateLayer(point);
			updateTimeseries();
		}
	}

	private void remove(Point point, Point3f point3d)
	{
		float value = dataset.getElementAt(point.x, point.y);
		if (!Float.isNaN(value))
		{
			raingauges.remove((int) value);
			raingauges3d.remove((int) value);
		}
		raingauges.remove(point);
		raingauges3d.remove(point3d);
		updateLayer(point);
		updateTimeseries();
	}

	private void updateLayer(Point pp)
	{
		System.out.println("update layer " + pp);
		if (dataset != null)
		{
			for (int y = 0; y < dataset.getRows(); y++)
			{
				for (int x = 0; x < dataset.getCols(); x++)
				{
					double distance = Double.MAX_VALUE;
					float myindex = Float.NaN;

					for (int index = 0; index < raingauges.size(); index++)
					{
						Point p = raingauges.get(index);
						double mydistance = Point.distance(x, y, p.x, p.y);
						if (mydistance < distance)
						{
							distance = mydistance;
							myindex = index;
						}
					}
					dataset.setElementAt(x, y, myindex);
				}
			}
			dataset.recalculateMinimumAndMaximum();
			System.out.println("worker:" + dataset.getMaximum() + " " + dataset.getMinimum());
			(new LayerChangedEvent(this)).raise();
			// raise_RainfallGaugesEvent();
		}
	}

	private void updateTimeseries()
	{
		timeseries.clear();
		if (raingauges != null)
		{
			int index = 0;
			for (Point p : raingauges)
			{
				TimeSeries ts = new TimeSeries();
				ts.add(-1, 0);
				ts.add(index, 1);
				ts.add(index + 1, 0);
				timeseries.add(ts);
				index++;
			}
			raise_RainfallGaugesTimeseriesEvent();
		}

	}

	public PaletteRenderableLayer getLayer()
	{
		return raingauge_layer;
	}

	public int getLayerKind()
	{
		return LayerConstants.RAINGAUGELAYER;
	}

	public Collection<Point> getGauges()
	{
		return raingauges;
	}

	public Collection<Point3f> getRainfallGauges()
	{

		return raingauges3d;
	}

	public Collection<TimeSeries> getRainfallTimeseries()
	{
		return timeseries;
	}

}
