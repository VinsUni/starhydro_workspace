package star.hydrology.ui.chart;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydro.rainfall.TimeSeries;
import star.hydrology.events.SlopeAreaRaiser;

@SignalComponent(extend = AbstractChart.class)
public class SlopeAreaChart extends SlopeAreaChart_generated
{
	private static final long serialVersionUID = 1L;
	private TimeSeries ts;

	@Handles(raises = {})
	public void handleRainfall(SlopeAreaRaiser r)
	{
		this.ts = r.getDataset();
		updateChart();
	}

	private void updateChart()
	{
		int points = 200;
		reset();
		if (ts == null)
		{
			return;
		}
		TreeMap<Float, Float> dataset = ts.getDataset();
		if (dataset.size() < points)
		{
			float[] x = new float[dataset.size()];
			float[] y = new float[dataset.size()];
			Iterator<Entry<Float, Float>> i = dataset.entrySet().iterator();
			int index = 0;
			while (i.hasNext())
			{
				Entry<Float, Float> entry = i.next();
				x[index] = entry.getKey();
				y[index] = entry.getValue();
				index++;
			}
			if (utils.Runner.isInterrupted())
			{
				return;
			}
			addSeries(x, y, getTitle(), 0, 0, x.length, true, getShapeRenderer());
		}
		else
		{
			int step = dataset.size() / points;
			float[] x = new float[dataset.size() / step + 1];
			float[] y = new float[dataset.size() / step + 1];
			Iterator<Entry<Float, Float>> i = dataset.entrySet().iterator();
			int index = 0;
			int subindex = 0;
			if (utils.Runner.isInterrupted())
			{
				return;
			}
			while (i.hasNext())
			{
				Entry<Float, Float> entry = i.next();
				x[index] += entry.getKey();
				y[index] += entry.getValue();
				subindex++;
				if (subindex == step)
				{
					x[index] /= step;
					y[index] /= step;
					index++;
					subindex = 0;
				}
			}
			if (utils.Runner.isInterrupted())
			{
				return;
			}
			int last = utils.ArrayNumerics.findLastNonZero(y);
			x = utils.ArrayNumerics.trimFloatArray(x, last);
			y = utils.ArrayNumerics.trimFloatArray(y, last);
			addSeries(x, y, getTitle(), 0, 0, x.length, false, true, getShapeRenderer());
		}

	}

	@Override
	protected String getTitle()
	{
		return "Slope Area Chart";
	}

	@Override
	protected String getValueAxisLabel()
	{
		return "Area (m2)";
	}
}
