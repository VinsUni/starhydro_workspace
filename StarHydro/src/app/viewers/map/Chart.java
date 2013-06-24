package app.viewers.map;

import java.util.TreeMap;

import org.jfree.chart.LegendItemCollection;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.map.RegionalizationRaiser;
import star.hydrology.events.map.RegionalizationStatisticsRaiser;
import star.hydrology.ui.chart.AbstractChart;

@SignalComponent(extend = AbstractChart.class)
public class Chart extends Chart_generated
{
	private static final long serialVersionUID = 1L;

	protected String getTitle()
	{
		return "Region size distribution";
	}

	protected String getValueAxisLabel()
	{
		return "size";
	}

	@Handles(raises = {}, handleValid = false)
	void handleInvalidEvent(RegionalizationStatisticsRaiser raiser)
	{
		reset();
	}

	@SuppressWarnings("unchecked")
	@Handles(raises = {})
	void handleEvent(RegionalizationStatisticsRaiser r)
	{
		reset();
		setLegend(new LegendItemCollection());
		getPlot().setDataset(0, null);
		getPlot().setRangeAxis(getAxis("", false));
		TreeMap<Integer, Integer> total = (TreeMap<Integer, Integer>) r.getStatistics().getMap(RegionalizationRaiser.Parameters.regionsMap);
		TreeMap<Integer, Integer> hillslope = (TreeMap<Integer, Integer>) r.getStatistics().getMap(RegionalizationRaiser.Parameters.hillslopeMap);
		TreeMap<Integer, Integer> channel = (TreeMap<Integer, Integer>) r.getStatistics().getMap(RegionalizationRaiser.Parameters.channelMap);
		int max = total.lastKey();
		int min = 1;
		int points = 200 < (max - min) ? 200 : (int) (max - min);
		float[] x = new float[points];
		float[] y = new float[points];
		float[] yh = new float[points];
		float[] yc = new float[points];
		for (int i = 0; i < x.length; i++)
		{
			x[i] = (max - min) / points * i + min;
			y[i] = total.get(new Integer((int) x[i])) != null ? total.get(new Integer((int) x[i])) : Float.NaN;
			yh[i] = hillslope.get((int) x[i]) != null ? hillslope.get((int) x[i]) : Float.NaN;
			yc[i] = channel.get((int) x[i]) != null ? channel.get((int) x[i]) : Float.NaN;
		}
		addSeries(x, y, "Total regions", 0, 0, x.length, true, getShapeRenderer());
		addSeries(x, yh, "Hillslope regions", 1, 0, x.length, true, getShapeRenderer());
		addSeries(x, yc, "Channel regions", 2, 0, x.length, true, getShapeRenderer());
		addLegend();

	}

}
