/**
 * 
 */
package app.worker.pdf.reg;

import java.awt.Point;
import java.util.ArrayList;

import star.hydrology.events.map.RegionalizationRaiser;
import app.viewers.map.RegionalizationModel;

final class Data
{
	public Data(float delta_minmax, float delta_range)
	{
		this.delta_minmax = delta_minmax;
		this.delta_range = delta_range;
	}

	private float delta_minmax = 0;
	private float delta_range = 0;
	private float sum = 0;
	private float max = Float.MIN_VALUE;
	private float min = Float.MAX_VALUE;

	final void addPoint(float a)
	{
		sum += a;
		max = max > a ? max : a;
		min = min < a ? min : a;
	}

	final boolean isInMinMax(final float a)
	{
		return (a < min + delta_minmax) && (a > max - delta_minmax);
	}

	final boolean isInRange(final float a, final int points)
	{
		return Math.abs(sum / points - a) < delta_range;
	}

	final void addData(Data d)
	{
		sum += d.sum;
		max = max > d.max ? max : d.max;
		max = min < d.min ? max : d.min;
	}

	public float getSum()
    {
	    return sum;
    }

}

public class Statistics
{
	private final RegionalizeWorker worker;
	Data elevation, slope, curvature, topindex, aspect;
	private boolean elevationEnabledMinMax, elevationEnabledRange;
	private boolean slopeEnabledMinMax, slopeEnabledRange;
	private boolean curvatureEnabledMinMax, curvatureEnabledRange;
	private boolean topindexEnabledMinMax, topindexEnabledRange;
	private boolean aspectEnabledMinMax, aspectEnabledRange;
	Statistics downstream;
	ArrayList<Point> coordinates;

	public String toString()
	{
		return "{" + regionCode + ", " + points + ", " + channel + " " + accumulation / points + "}";
	}

	int points = 0;
	private double accumulation = 0;
	int regionCode;
	boolean channel = false;

	public Statistics(RegionalizeWorker worker)
	{
		this.worker = worker;
		regionCode = this.worker.nextCode(this);
	}

	final boolean isPointInChannel(int x, int y)
	{
		boolean ret = true;
		if (true)
		{
			float pn = this.worker.getFlowAcc().getDataset().getElementAt(x, y);
			boolean flag = channel == (pn > this.worker.getParameters().get(RegionalizationRaiser.Parameters.networkAccumulationThreshold));
			if (!flag)
			{
				worker.updateStatistics(RegionalizationRaiser.Parameters.networkAccumulationThreshold);
			}
			ret &= flag;
		}
		return ret;
	}

	final boolean isPointInElevation(int x, int y)
	{
		boolean ret = true;
		if (elevationEnabledRange || elevationEnabledMinMax)
		{
			float pe = this.worker.getFilled().getDataset().getElementAt(x, y);
			if (elevationEnabledRange)
			{
				boolean flag = elevation.isInRange(pe, points);
				if (!flag)
				{
					worker.updateStatistics(channel ? RegionalizationRaiser.Parameters.channelElevationRange : RegionalizationRaiser.Parameters.hillslopeElevationRange);
				}
				ret &= flag;
			}
			if (elevationEnabledMinMax)
			{
				boolean flag = elevation.isInMinMax(pe);
				if (!flag)
				{
					worker.updateStatistics(channel ? RegionalizationRaiser.Parameters.channelElevationMinMax : RegionalizationRaiser.Parameters.hillslopeElevationMinMax);
				}
				ret &= flag;
			}
		}
		return ret;
	}

	final boolean isPointInSlope(int x, int y)
	{
		boolean ret = true;
		if (slopeEnabledRange || slopeEnabledMinMax)
		{
			float pe = this.worker.getSlope().getDataset().getElementAt(x, y);
			if (slopeEnabledRange)
			{
				boolean flag = slope.isInRange(pe, points);
				if (!flag)
				{
					worker.updateStatistics(channel ? RegionalizationRaiser.Parameters.channelSlopeRange : RegionalizationRaiser.Parameters.hillslopeSlopeRange);
				}
				ret &= flag;
			}
			if (slopeEnabledMinMax)
			{
				boolean flag = slope.isInMinMax(pe);
				if (!flag)
				{
					worker.updateStatistics(channel ? RegionalizationRaiser.Parameters.channelSlopeMinMax : RegionalizationRaiser.Parameters.hillslopeSlopeMinMax);
				}
				ret &= flag;
			}
		}
		return ret;
	}

	final boolean isPointInCurvature(int x, int y)
	{
		boolean ret = true;
		if (curvatureEnabledRange || curvatureEnabledMinMax)
		{
			float pe = this.worker.getCurvature().getDataset().getElementAt(x, y);
			if (curvatureEnabledRange)
			{
				boolean flag = curvature.isInRange(pe, points);
				if (!flag)
				{
					worker.updateStatistics(channel ? RegionalizationRaiser.Parameters.channelCurvatureRange : RegionalizationRaiser.Parameters.hillslopeCurvatureRange);
				}
				ret &= flag;
			}
			if (curvatureEnabledMinMax)
			{
				boolean flag = curvature.isInMinMax(pe);
				if (!flag)
				{
					worker.updateStatistics(channel ? RegionalizationRaiser.Parameters.channelCurvatureMinMax : RegionalizationRaiser.Parameters.hillslopeCurvatureMinMax);
				}
				ret &= flag;
			}
		}
		return ret;
	}

	final boolean isPointInTopindex(int x, int y)
	{
		boolean ret = true;
		if (topindexEnabledRange || topindexEnabledMinMax)
		{
			float pe = this.worker.getTopindex().getDataset().getElementAt(x, y);
			if (topindexEnabledRange)
			{
				boolean flag = topindex.isInRange(pe, points);
				if (!flag)
				{
					worker.updateStatistics(channel ? RegionalizationRaiser.Parameters.channelTopindexRange : RegionalizationRaiser.Parameters.hillslopeTopindexRange);
				}
				ret &= flag;
			}
			if (topindexEnabledMinMax)
			{
				boolean flag = topindex.isInMinMax(pe);
				if (!flag)
				{
					worker.updateStatistics(channel ? RegionalizationRaiser.Parameters.channelTopindexMinMax : RegionalizationRaiser.Parameters.hillslopeTopindexMinMax);
				}
				ret &= flag;
			}
		}
		return ret;
	}

	final boolean isPointInAspect(int x, int y)
	{
		boolean ret = true;
		if (aspectEnabledRange || aspectEnabledMinMax)
		{
			float pe = getDirection(x, y);
			if (aspectEnabledRange)
			{
				boolean flag = aspect.isInRange(pe, points);
				if (!flag)
				{
					worker.updateStatistics(channel ? RegionalizationRaiser.Parameters.channelAspectRange : RegionalizationRaiser.Parameters.hillslopeAspectRange);
				}
				ret &= flag;
			}
			if (aspectEnabledMinMax)
			{
				boolean flag = aspect.isInMinMax(pe);
				if (!flag)
				{
					worker.updateStatistics(channel ? RegionalizationRaiser.Parameters.channelAspectMinMax : RegionalizationRaiser.Parameters.hillslopeAspectMinMax);
				}
				ret &= flag;
			}
		}
		return ret;
	}

	public boolean isPointInRange(int x, int y)
	{
		boolean ret = true;
		ret &= isPointInChannel(x, y);
		ret &= isPointInElevation(x, y);
		ret &= isPointInSlope(x, y);
		ret &= isPointInCurvature(x, y);
		ret &= isPointInTopindex(x, y);
		ret &= isPointInAspect(x, y);
		return ret;
	}

	RegionalizationModel getParameters()
	{
		return this.worker.getParameters();
	}

	void addPoint(int x, int y)
	{
		float accumulation = worker.getFlowAcc().getDataset().getElementAt(x, y);
		if (points == 0)
		{
			channel = accumulation > getParameters().get(RegionalizationRaiser.Parameters.networkAccumulationThreshold);

			elevationEnabledMinMax = channel ? getParameters().getEnabled(RegionalizationRaiser.Parameters.channelElevationMinMax) : getParameters().getEnabled(RegionalizationRaiser.Parameters.hillslopeElevationMinMax);
			elevationEnabledRange = channel ? getParameters().getEnabled(RegionalizationRaiser.Parameters.channelElevationRange) : getParameters().getEnabled(RegionalizationRaiser.Parameters.hillslopeElevationRange);
			elevation = new Data(channel ? getParameters().get(RegionalizationRaiser.Parameters.channelElevationMinMax) : getParameters().get(RegionalizationRaiser.Parameters.hillslopeElevationMinMax), channel ? getParameters().get(RegionalizationRaiser.Parameters.channelElevationRange) : getParameters().get(
			        RegionalizationRaiser.Parameters.hillslopeElevationRange));

			slopeEnabledMinMax = channel ? getParameters().getEnabled(RegionalizationRaiser.Parameters.channelSlopeMinMax) : getParameters().getEnabled(RegionalizationRaiser.Parameters.hillslopeSlopeMinMax);
			slopeEnabledRange = channel ? getParameters().getEnabled(RegionalizationRaiser.Parameters.channelSlopeRange) : getParameters().getEnabled(RegionalizationRaiser.Parameters.hillslopeSlopeRange);
			slope = new Data(channel ? getParameters().get(RegionalizationRaiser.Parameters.channelSlopeMinMax) : getParameters().get(RegionalizationRaiser.Parameters.hillslopeSlopeMinMax), channel ? getParameters().get(RegionalizationRaiser.Parameters.channelSlopeRange) : getParameters().get(
			        RegionalizationRaiser.Parameters.hillslopeSlopeRange));

			curvatureEnabledMinMax = channel ? getParameters().getEnabled(RegionalizationRaiser.Parameters.channelCurvatureMinMax) : getParameters().getEnabled(RegionalizationRaiser.Parameters.hillslopeCurvatureMinMax);
			curvatureEnabledRange = channel ? getParameters().getEnabled(RegionalizationRaiser.Parameters.channelCurvatureRange) : getParameters().getEnabled(RegionalizationRaiser.Parameters.hillslopeCurvatureRange);
			curvature = new Data(channel ? getParameters().get(RegionalizationRaiser.Parameters.channelCurvatureMinMax) : getParameters().get(RegionalizationRaiser.Parameters.hillslopeCurvatureMinMax), channel ? getParameters().get(RegionalizationRaiser.Parameters.channelCurvatureRange) : getParameters().get(
			        RegionalizationRaiser.Parameters.hillslopeCurvatureRange));

			topindexEnabledMinMax = channel ? getParameters().getEnabled(RegionalizationRaiser.Parameters.channelTopindexMinMax) : getParameters().getEnabled(RegionalizationRaiser.Parameters.hillslopeTopindexMinMax);
			topindexEnabledRange = channel ? getParameters().getEnabled(RegionalizationRaiser.Parameters.channelTopindexRange) : getParameters().getEnabled(RegionalizationRaiser.Parameters.hillslopeTopindexRange);
			topindex = new Data(channel ? getParameters().get(RegionalizationRaiser.Parameters.channelTopindexMinMax) : getParameters().get(RegionalizationRaiser.Parameters.hillslopeTopindexMinMax), channel ? getParameters().get(RegionalizationRaiser.Parameters.channelTopindexRange) : getParameters().get(
			        RegionalizationRaiser.Parameters.hillslopeTopindexRange));

			aspectEnabledMinMax = channel ? getParameters().getEnabled(RegionalizationRaiser.Parameters.channelAspectMinMax) : getParameters().getEnabled(RegionalizationRaiser.Parameters.hillslopeAspectMinMax);
			aspectEnabledRange = channel ? getParameters().getEnabled(RegionalizationRaiser.Parameters.channelAspectRange) : getParameters().getEnabled(RegionalizationRaiser.Parameters.hillslopeAspectRange);
			aspect = new Data(channel ? getParameters().get(RegionalizationRaiser.Parameters.channelAspectMinMax) : getParameters().get(RegionalizationRaiser.Parameters.hillslopeAspectMinMax), channel ? getParameters().get(RegionalizationRaiser.Parameters.channelAspectRange) : getParameters().get(
			        RegionalizationRaiser.Parameters.hillslopeAspectRange));

			coordinates = new ArrayList<Point>();
		}

		points++;
		elevation.addPoint(this.worker.getFilled().getDataset().getElementAt(x, y));
		slope.addPoint(this.worker.getSlope().getDataset().getElementAt(x, y));
		curvature.addPoint(this.worker.getCurvature().getDataset().getElementAt(x, y));
		topindex.addPoint(this.worker.getTopindex().getDataset().getElementAt(x, y));
		aspect.addPoint(getDirection(x, y));
		coordinates.add(new Point(x, y));
	}

	final float max(float a, float b)
	{
		return a > b ? a : b;
	}

	final float min(float a, float b)
	{
		return a > b ? b : a;
	}

	final boolean inRange(float x, float max, float min, float delta)
	{
		return (x < (max + delta)) && (x > (min - delta));
	}

	void addStatistics(Statistics s)
	{
		accumulation += s.accumulation;
		aspect.addData(s.aspect);
		coordinates.addAll(s.coordinates);
		curvature.addData(s.curvature);
		elevation.addData(s.elevation);
		points += s.points;
		slope.addData(s.slope);
		topindex.addData(s.topindex);
	}

	private float getDirection(int x, int y)
	{
		double ret = 0;
		int dir = (int) this.worker.getFlowDir().getDataset().getElementAt(x, y);
		switch (dir)
		{
		case 1:
			ret = 0 * Math.PI / 4;
			break;
		case 2:
			ret = 1 * Math.PI / 4;
			break;
		case 4:
			ret = 2 * Math.PI / 4;
			break;
		case 8:
			ret = 3 * Math.PI / 4;
			break;
		case 16:
			ret = 4 * Math.PI / 4;
			break;
		case 32:
			ret = 5 * Math.PI / 4;
			break;
		case 64:
			ret = 6 * Math.PI / 4;
			break;
		case 128:
			ret = 7 * Math.PI / 4;
			break;
		default:
			(new RuntimeException("Should be in range 1-255")).printStackTrace();
			break;
		}
		return (float) (ret * 360 / 2 / Math.PI);
	}
}