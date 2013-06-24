package star.hydro.rainfall;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.IUHRaiser;
import star.hydrology.events.PaletteChangedRaiser;
import star.hydrology.events.RainfallGaugesTimeseriesRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.ui.palette.Palette;

@SignalComponent()
public class Convolute extends Convolute_generated
{
	TimeSeries ts;
	Collection<TimeSeries> rts;
	IUHRaiser iuh;
	ArrayList<TimeSeries> ts2;
	float min, max;
	int points = 100;
	Palette p;

	public int getPoints()
	{
		return points;
	}

	@Handles(raises = {})
	protected void updatePalette(PaletteChangedRaiser r)
	{
		if (r.getKind() == LayerConstants.RAINGAUGELAYER)
		{
			p = r.getPalette();
			if (rts != null && iuh != null)
			{
				computeMany();
				(new ConvolutedTimeSeriesEvent(this)).raise();
			}
		}
	}

	@Handles(raises = { ConvolutedTimeSeriesRaiser.class })
	public void handleTimeSeries(RainfallGaugesTimeseriesRaiser rts)
	{
		this.rts = rts.getRainfallTimeseries();
		this.ts = null;
		if (iuh != null)
		{
			computeMany();
			(new ConvolutedTimeSeriesEvent(this)).raise();
		}
	}

	@Handles(raises = { ConvolutedTimeSeriesRaiser.class })
	public void handleTimeSeries(IUHRaiser iuh)
	{
		this.iuh = iuh;
		if (rts != null && iuh.getNumberOfGauges() == rts.size())
		{
			computeMany();
			System.out.println("adding ts2 " + ts2.size());
			(new ConvolutedTimeSeriesEvent(this)).raise();
		}
	}

	@Handles(raises = { ConvolutedTimeSeriesRaiser.class })
	public void handleTimeSeries(TimeSeriesRaiser ts)
	{
		this.ts = ts.getTimeSeries();
		this.rts = null;
		if (iuh != null)
		{
			compute();
			(new ConvolutedTimeSeriesEvent(this)).raise();
		}
	}

	public void computeMany()
	{
		this.min = 0;
		this.max = 0;
		ArrayList<TimeSeries> ts2 = new ArrayList<TimeSeries>();
		int index = 0;
		for (TimeSeries ts : rts)
		{
			Color c = p.getColor(1.0f * index / rts.size());
			computeOne(ts, ts2, c, index);
			index++;
		}

		this.ts2 = ts2;
		if (max - min < 24)
		{
			this.points = (int) (max - min) * 4;
		}
		else if (max - min < 200)
		{
			this.points = (int) (max - min) * 2;
		}
		else
		{
			this.points = Math.round(max - min + 1);
		}

	}

	Color darker(Color c)
	{
		float FACTOR = .90f;
		return new Color(Math.max((int) (c.getRed() * FACTOR), 0), Math.max((int) (c.getGreen() * FACTOR), 0), Math.max((int) (c.getBlue() * FACTOR), 0));
	}

	public void computeOne(TimeSeries ts, ArrayList<TimeSeries> ts2, Color c, int index)
	{
		float maximum = iuh.getMaximum();
		float minimum = iuh.getMinimum();
		float tau_delta = (maximum - minimum) / iuh.getNumberOfBins();
		float max = ts.getDataset().lastKey();
		float min = ts.getDataset().firstKey();

		this.min = 0;
		this.max = Math.max(this.max, maximum + max);

		Iterator<Entry<Float, Float>> iter = ts.getDataset().entrySet().iterator();
		while (iter.hasNext())
		{
			TimeSeries series = new TimeSeries();
			series.setColor(new java.awt.Color(c.getRGB()));
			c = darker(c);
			Entry<Float, Float> entry = iter.next();
			float time = entry.getKey();
			float perc = entry.getValue();

			if (perc != 0)
			{
				for (float tau = minimum; tau <= maximum; tau += tau_delta)
				{
					series.addValue(time - tau + maximum, perc * iuh.getValueAt(index, tau));
				}
				series.setValue((time - min) / (max - min));
				ts2.add(series);
			}
		}

	}

	public void compute()
	{
		ArrayList<TimeSeries> ts2 = new ArrayList<TimeSeries>();
		float maximum = iuh.getMaximum();
		float minimum = iuh.getMinimum();
		float tau_delta = (maximum - minimum) / iuh.getNumberOfBins();
		float max = ts.getDataset().lastKey();
		float min = ts.getDataset().firstKey();

		this.min = 0;
		this.max = maximum + max;

		Iterator<Entry<Float, Float>> iter = ts.getDataset().entrySet().iterator();
		while (iter.hasNext())
		{
			TimeSeries series = new TimeSeries();
			Entry<Float, Float> entry = iter.next();
			float time = entry.getKey();
			float perc = entry.getValue();

			if (perc != 0)
			{
				for (float tau = minimum; tau <= maximum; tau += tau_delta)
				{
					series.addValue(time - tau + maximum, perc * iuh.getValueAt(tau));
				}
				series.setValue((time - min) / (max - min));
				ts2.add(series);
			}
		}
		this.ts2 = ts2;
	}

	public ArrayList<TimeSeries> getTimeSeries()
	{
		return ts2;
	}

	public float getMinimumTime()
	{
		return min;
	}

	public float getMaximumTime()
	{
		return max;
	}

}
