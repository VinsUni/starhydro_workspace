package star.hydrology.ui.chart.pdf;

import org.jfree.chart.LegendItemCollection;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.ElevationPDFRaiser;
import star.hydrology.events.regionalization.RegElevationPDFRaiser;
import star.hydrology.ui.chart.AbstractChart;

@SignalComponent(extend = AbstractChart.class)
public class ElevationPDFChart extends ElevationPDFChart_generated
{
	private static final long serialVersionUID = 1L;

	protected String getTitle()
	{
		return "Elevation PDF";
	}

	protected String getRangeAxisLabel()
	{
		return "";
	}

	protected String getValueAxisLabel()
	{
		return "Height (m)";
	}

	private ElevationPDFRaiser r1;
	private RegElevationPDFRaiser r2;


	private void update()
	{
		reset();
		if (r1 != null)
		{
			setLegend(new LegendItemCollection());
			getPlot().setDataset(0, null);
			getPlot().setRangeAxis(getAxis("", false));
			float min = r1.getMinimumHeight();
			float max = r1.getMaximumHeight();
			int points = 200 < (max - min) ? 200 : (int) (max - min);
			float[] x = new float[points];
			float[] y = new float[points];
			for (int i = 0; i < x.length; i++)
			{
				x[i] = (max - min) / points * i + min;
				y[i] = r1.getPDF(x[i]);
			}
			addSeries(x, y, getRangeAxisLabel(), 0, 0, x.length, false, getLineRenderer());
		}
		if (r2 != null)
		{
			float min = r2.getMinimumHeight();
			float max = r2.getMaximumHeight();
			int points = 200 < (max - min) ? 200 : (int) (max - min);
			float[] x = new float[points];
			float[] y = new float[points];
			for (int i = 0; i < x.length; i++)
			{
				x[i] = (max - min) / points * i + min;
				y[i] = r2.getPDF(x[i]);
			}
			addSeries(x, y, "Regionalization " + getTitle(), 1, 0, x.length, false, getLineRenderer());
		}
		addLegend();
	}

	@Handles(raises = {} , handleValid=false)
	void handleInvalidEvent(ElevationPDFRaiser raiser)
	{
		r1 = null;
		reset();
	}

	@Handles(raises = {})
	void handleEvent(ElevationPDFRaiser r)
	{
		r1 = r;
		update();

	}

	@Handles(raises = {})
	void handleEvent(RegElevationPDFRaiser r)
	{
		r2 = r;
		update();
	}

}
