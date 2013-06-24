package star.hydro.rainfall;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.PaletteChangedRaiser;
import star.hydrology.events.RainfallGaugesTimeseriesRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.ui.chart.AbstractChart;
import star.hydrology.ui.palette.GrayPalette;
import star.hydrology.ui.palette.Palette;

@SignalComponent(extend = AbstractChart.class)
public class RainfallChart extends RainfallChart_generated
{

	private static final long serialVersionUID = 1L;
	private TimeSeries ts;
	private Collection<TimeSeries> timeseries;
	private Palette raingaugePalette = null;

	@Handles(raises = {})
	void updatePalette(PaletteChangedRaiser r)
	{
		if (r.getKind() == LayerConstants.RAINGAUGELAYER)
		{
			raingaugePalette = r.getPalette();
			updateChart2();
		}
	}

	@Handles(raises = {})
	void handleRainfall(RainfallGaugesTimeseriesRaiser r)
	{
		timeseries = r.getRainfallTimeseries();
		updateChart2();
	}

	@Handles(raises = {})
	public void handleRainfall(TimeSeriesRaiser r)
	{
		this.ts = r.getTimeSeries();
		updateChart();
	}

	private void updateChart2()
	{
		if (timeseries == null)
		{
			return;
		}
		reset();
		int gauge = 0;
		StringBuffer rainfallData = new StringBuffer();
		for (TimeSeries ts : timeseries)
		{
			updateRaingauges(ts, gauge++, timeseries.size());
			rainfallData.append(ts.getGauge() + "\t" + ts.getDataset() + "\n");
		}
		getArea().setText(rainfallData.toString());
	}

	private void updateRaingauges(TimeSeries ts, int gauge, int total)
	{
		System.out.println("updateRaingauges " + gauge + " " + total);
		float min = ts.getDataset().firstKey().floatValue();
		float max = ts.getDataset().lastKey().floatValue();

		int points = ts.getDataset().size();
		float[] x = new float[points];
		float[] y = new float[points];

		int i = 0;
		for (Entry<Float, Float> entry : ts.getDataset().entrySet())
		{
			x[i] = entry.getKey().floatValue() + (1.0f * gauge / total / 4);
			y[i] = entry.getValue().floatValue();
			i++;
		}
		XYItemRenderer br = new XYBarRenderer(1.0f - 1.0f / total / 4);
		br.setBasePaint(raingaugePalette.getColor(1.0f * gauge / total));
		addSeriesReuseRange(x, y, "Gauge " + gauge + " flow", gauge + 1, 0, x.length, br);
	}

	private void updateChart()
	{
		if (ts == null)
		{
			return;
		}
		reset();
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		if (ts.getDataset().size() < 200)
		{
			float[] x = new float[ts.getDataset().size()];
			float[] y = new float[ts.getDataset().size()];
			Iterator<Entry<Float, Float>> i = ts.getDataset().entrySet().iterator();
			int index = 0;
			while (i.hasNext())
			{
				Entry<Float, Float> entry = i.next();
				x[index] = entry.getKey();
				y[index] = entry.getValue();
				index++;
				max = Math.max(entry.getKey(), max);
				min = Math.min(entry.getKey(), min);

			}
			addSeries(x, y, getTitle(), 0, 0, x.length, false, getBarRenderer(p, min, max, index));
		}
		else
		{
			int step = ts.getDataset().size() / 200;
			float[] x = new float[ts.getDataset().size() / step];
			float[] y = new float[ts.getDataset().size() / step];
			Iterator<Entry<Float, Float>> i = ts.getDataset().entrySet().iterator();
			int index = 0;
			int subindex = 0;
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
				max = Math.max(entry.getKey(), max);
				min = Math.min(entry.getKey(), min);
			}
			addSeries(x, y, getTitle(), 0, 0, x.length, false, getBarRenderer(p, min, max, index));
		}

	}

	@Override
	protected String getTitle()
	{
		return "Rain fall";
	}

	@Override
	protected String getValueAxisLabel()
	{
		return "Hours";
	}

	Palette p = new GrayPalette(Color.red, "Gray red");

	@Handles(raises = {})
	void convolutionPalette(ConvolutionPaletteRaiser r)
	{
		p = r.getConvolutionPalette();
		updateChart();
	}
}
