package star.hydrology.ui.chart.horton;

import java.util.Hashtable;

import org.jfree.chart.LegendItemCollection;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.HortonNumberRaiser;
import star.hydrology.events.PaletteChangedRaiser;
import star.hydrology.events.StreamOrderStatisticsRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.ui.chart.AbstractChart;
import star.hydrology.ui.palette.Palette;

@SignalComponent(extend = AbstractChart.class, contains = { LogScaleRaiser.class, TrendLineRaiser.class })
public class HortonNumberChart extends HortonNumberChart_generated
{
	public enum HortonNumbers
	{
		AREA, SLOPE, LENGTH, COUNT
	};

	private static final long serialVersionUID = 1L;
	private Hashtable<String, Float> hortonLinReg;
	private float[] areas;
	private int[] count;
	private float[] length;
	private float[] slopes;
	private HortonNumbers selected = null;
	private boolean trendLine = false;
	private boolean logScale = false;
	private Palette palette;

	public HortonNumberChart(HortonNumbers number)
	{
		super();
		selected = number;
		setSupportsLogScale(true);
		setSupportsTrendLine(true);
	}

	@Handles(raises = {})
	void trendLine(TrendLineRaiser r)
	{
		trendLine = r.isSelected();
		updateGraph();
	}

	@Handles(raises = {})
	void logScale(LogScaleRaiser r)
	{
		logScale = r.isSelected();
		updateGraph();
	}

	@Handles(raises = {})
	void hortonTrends(HortonNumberRaiser r)
	{
		hortonLinReg = r.getHortonNumbers();
	}

	@Handles(raises = {})
	void paletteChange(PaletteChangedRaiser r)
	{
		if (r.getKind() == LayerConstants.STREAMNETWORK)
		{
			this.palette = r.getPalette();
			updateGraph();
		}
	}

	@Handles(raises = {})
	void hortonNumber(StreamOrderStatisticsRaiser r)
	{
		areas = r.getOrderAreas();
		count = r.getOrderCount();
		length = r.getOrderLength();
		slopes = r.getOrderSlopes();
		updateGraph();
	}

	protected String getValueAxisLabel()
	{
		return "Stream order";
	}

	private void updateGraph()
	{
		reset();
		setLegend(new LegendItemCollection());
		int index = 0;
		for (int i = 0; i < getPlot().getDatasetCount(); i++)
		{
			getPlot().setDataset(i, null);
			getPlot().setRangeAxis(i, null);
		}
		if (areas != null && selected == HortonNumbers.AREA)
		{
			addSeries(getIndexesAsArray(areas.length, 0), areas, "Areas (m2)", index++, 1, areas.length, logScale, getShapeRenderer(palette, 1, areas.length, areas.length - 1));
			float slope = Float.NaN;
			float intercept = Float.NaN;
			float uncertainty = Float.NaN;
			if (hortonLinReg != null && trendLine)
			{
				try
				{
					slope = hortonLinReg.get(HortonNumberRaiser.Ra_SLOPE).floatValue();
					intercept = hortonLinReg.get(HortonNumberRaiser.Ra_INTERCEPT).floatValue();
					uncertainty = hortonLinReg.get(HortonNumberRaiser.Ra_UNCERTAINTY).floatValue();
					addTrend(getIndexesAsArray(areas.length - 1, 1), "Areas", index++, slope, intercept, uncertainty, getLineRenderer());
				}
				catch (NullPointerException ex)
				{
				}
			}
		}
		if (count != null && selected == HortonNumbers.COUNT)
		{
			addSeries(getIndexesAsArray(count.length, 0), int2float(count), "Count", index++, 1, areas.length, logScale, getShapeRenderer(palette, 1, count.length, count.length - 1));
			float slope = Float.NaN;
			float intercept = Float.NaN;
			float uncertainty = Float.NaN;
			if (hortonLinReg != null && trendLine)
			{
				try
				{
					slope = hortonLinReg.get(HortonNumberRaiser.Rb_SLOPE).floatValue();
					intercept = hortonLinReg.get(HortonNumberRaiser.Rb_INTERCEPT).floatValue();
					uncertainty = hortonLinReg.get(HortonNumberRaiser.Rb_UNCERTAINTY).floatValue();
					addTrend(getIndexesAsArray(areas.length - 1, 1), "Count", index++, slope, intercept, uncertainty, getLineRenderer());
				}
				catch (NullPointerException ex)
				{
				}
			}
		}
		if (length != null && selected == HortonNumbers.LENGTH)
		{
			addSeries(getIndexesAsArray(length.length, 0), length, "Length (m)", index++, 1, areas.length, logScale, getShapeRenderer(palette, 1, length.length, length.length - 1));
			float slope = Float.NaN;
			float intercept = Float.NaN;
			float uncertainty = Float.NaN;
			if (hortonLinReg != null && trendLine)
			{
				try
				{
					slope = hortonLinReg.get(HortonNumberRaiser.Rl_SLOPE).floatValue();
					intercept = hortonLinReg.get(HortonNumberRaiser.Rl_INTERCEPT).floatValue();
					uncertainty = hortonLinReg.get(HortonNumberRaiser.Rl_UNCERTAINTY).floatValue();
					addTrend(getIndexesAsArray(length.length - 1, 1), "Length", index++, slope, intercept, uncertainty, getLineRenderer());

				}
				catch (NullPointerException ex)
				{
				}
			}
		}
		if (length != null && selected == HortonNumbers.SLOPE)
		{
			addSeries(getIndexesAsArray(slopes.length, 0), slopes, "Slope (rad)", index++, 1, areas.length, logScale, getShapeRenderer(palette, 1, slopes.length, slopes.length - 1));
			float slope = Float.NaN;
			float intercept = Float.NaN;
			float uncertainty = Float.NaN;
			if (hortonLinReg != null && trendLine)
			{
				try
				{
					slope = hortonLinReg.get(HortonNumberRaiser.Rs_SLOPE).floatValue();
					intercept = hortonLinReg.get(HortonNumberRaiser.Rs_INTERCEPT).floatValue();
					uncertainty = hortonLinReg.get(HortonNumberRaiser.Rs_UNCERTAINTY).floatValue();
					addTrend(getIndexesAsArray(slopes.length - 1, 1), "Slope", index++, slope, intercept, uncertainty, getLineRenderer());

				}
				catch (NullPointerException ex)
				{
				}
			}
		}
		if (index == 0)
		{
			getPlot().setRangeAxis(getAxis("", false));
		}

		addLegend();
	}

	private float[] getIndexesAsArray(int len, int offset)
	{
		float[] ret = new float[len];
		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = i + offset;
		}
		return ret;
	}

	protected String getTitle()
	{
		return "";
	}

}
