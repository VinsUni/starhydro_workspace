package star.hydro.rainfall;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.ui.chart.AbstractChart;
import star.hydrology.ui.palette.GrayPalette;
import star.hydrology.ui.palette.Palette;
import utils.ArrayNumerics;

@SignalComponent(extend = AbstractChart.class)
public class ConvoluteChart extends ConvoluteChart_generated
{

	private static final long serialVersionUID = 1L;
	private ArrayList<TimeSeries> ts;
	private float min = 0;
	private float max = 0;
	private int points = 100;

	@Handles(raises = {})
	public void handleRainfall(ConvolutionChartBinsRaiser r)
	{
		// points = r.getNumberOfConvolutionChartBins();
		updateChart();
	}

	@Handles(raises = {})
	public void handleRainfall(ConvolutedTimeSeriesRaiser r)
	{
		this.ts = r.getTimeSeries();
		this.max = r.getMaximumTime();
		this.min = r.getMinimumTime();
		this.points = r.getPoints();
		System.out.println(this.points);
		updateChart();
	}

	private void updateChart()
	{
		reset();
		if (ts == null)
		{
			return;
		}
		float diff = (max - min) / points;

		DefaultTableXYDataset table = new DefaultTableXYDataset();
		float[] colorValues = new float[ts.size()];
		Color[] colorValuesArray = new Color[ts.size()];
		boolean useColorValuesArray = true;
		int colorIndex = 0;
		TreeMap<Float, ArrayList<Float>> dump = new TreeMap<Float, ArrayList<Float>>();
		for (int j = 0; j < points; j++)
		{
			float offset = j * diff + min;
			ArrayList<Float> entry = new ArrayList<Float>();
			dump.put(offset, entry);
		}
		Iterator<TimeSeries> iter = ts.iterator();
		while (iter.hasNext())
		{
			TimeSeries ts = iter.next();
			XYSeries xyseries = new XYSeries("", true, false);

			float[] y = new float[points + 1];
			float[] x = new float[points + 1];
			float[] count = new float[points + 1];
			Iterator<Entry<Float, Float>> i = ts.getDataset().entrySet().iterator();
			while (i.hasNext())
			{
				Entry<Float, Float> entry = i.next();
				float index = ((entry.getKey() - min) / diff);
				float indexEnd = ((entry.getKey() + 1 - min) / diff);
				if ((int) index != (int) indexEnd)
				{
					float firstSection = ((int) (index + 1)) - index;
					x[(int) index] += entry.getKey() * firstSection / (indexEnd - index);
					y[(int) index] += entry.getValue() * firstSection / (indexEnd - index);
					count[(int) index] += firstSection / (indexEnd - index);

					for (int ii = (int) index + 1; ii < (int) indexEnd; ii++)
					{
						x[(int) ii] += entry.getKey() / (indexEnd - index);
						y[(int) ii] += entry.getValue() / (indexEnd - index);
						count[(int) ii] += 1 / (indexEnd - index);

					}

					float lastSection = indexEnd - (int) (indexEnd);
					x[(int) indexEnd] += entry.getKey() * lastSection / (indexEnd - index);
					y[(int) indexEnd] += entry.getValue() * lastSection / (indexEnd - index);
					count[(int) indexEnd] += lastSection / (indexEnd - index);

				}
				else
				{
					x[(int) index] += entry.getKey();
					y[(int) index] += entry.getValue();
					count[(int) index]++;
				}
			}
			for (int j = 0; j < points; j++)
			{
				if (count[j] != 0)
				{
					x[j] /= count[j];
					xyseries.add(j * diff + min, y[j] / diff);
					(dump.get(j * diff + min)).add(y[j] / diff);
				}
			}
			colorValues[colorIndex] = ts.getValue();
			colorValuesArray[colorIndex] = ts.getColor();
			if (ts.getColor() == null)
			{
				useColorValuesArray = false;
			}
			colorIndex++;
			table.addSeries(xyseries);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("time\tcumulative\tsum\tcontributions\n");
		float cumsum = 0;
		for (int j = 0; j < points; j++)
		{
			float offset = j * diff + min;
			ArrayList<Float> entry = dump.get(offset);
			float sum = ArrayNumerics.sum(entry.toArray(new Float[0]), true);
			sum *= diff;
			cumsum += sum;
			sb.append(offset);
			sb.append('\t');
			sb.append(cumsum);
			sb.append('\t');
			sb.append(sum);
			sb.append('\t');
			Iterator<Float> i = entry.iterator();
			while (i.hasNext())
			{
				sb.append(i.next() * diff);
				sb.append('\t');
			}
			sb.append('\n');
		}
		getArea().setText(sb.toString());

		NumberAxis domainAxis = getDomainAxis(getValueAxisLabel(), false);
		domainAxis.setAutoRange(true);
		getPlot().setDomainAxis(domainAxis);
		getPlot().setDataset(0, table);
		getPlot().setRangeAxis(0, getRangeAxis("", false));
		getPlot().mapDatasetToRangeAxis(0, 0);
		if (useColorValuesArray)
		{
			getPlot().setRenderer(0, getStackedRenderer(colorValuesArray));
		}
		else
		{
			getPlot().setRenderer(0, getStackedRenderer(p, colorValues));
		}

	}

	@Override
	protected String getTitle()
	{
		return "Convolution";
	}

	@Override
	protected String getValueAxisLabel()
	{
		return "Hours";
	}

	private Palette p = new GrayPalette(Color.red, "");

	@Handles(raises = {})
	void convolutionPalette(ConvolutionPaletteRaiser r)
	{
		p = r.getConvolutionPalette();
		updateChart();
	}
}
