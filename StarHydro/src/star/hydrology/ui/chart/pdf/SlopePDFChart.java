package star.hydrology.ui.chart.pdf;

import org.jfree.chart.LegendItemCollection;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.SlopePDFRaiser;
import star.hydrology.events.regionalization.RegSlopePDFRaiser;
import star.hydrology.ui.chart.AbstractChart;

@SignalComponent(extend = AbstractChart.class)
public class SlopePDFChart extends SlopePDFChart_generated
{
	private static final long serialVersionUID = 1L;

	protected String getTitle()
	{
		return "Slope PDF";
	}

	protected String getRangeAxisLabel()
	{
		return "";
	}

	protected String getValueAxisLabel()
	{
		return "Degrees";
	}

	private SlopePDFRaiser r1;
	private RegSlopePDFRaiser r2;

	private void update()
	{
		reset();
		if (r1 != null)
		{
			setLegend(new LegendItemCollection());
			getPlot().setDataset(0, null);
			getPlot().setRangeAxis(getAxis("", false));
			float min = 0;
			float max = 90;
			for (int i = (int) max; i != 0; i--)
			{
				if (r1.getPDF(i) != 0)
				{
					max = i + 1;
					break;
				}
			}
			int points = (int) (max - min);
			float[] x = new float[points];
			float[] y = new float[points];
			for (int i = 0; i < x.length; i++)
			{
				x[i] = (max - min) / points * i + min;
				y[i] = r1.getPDF(x[i]);
			}
			addSeries(x, y, getRangeAxisLabel(), 0, 0, x.length, false, getLineRenderer());
			addLegend();
		}
		if (r2 != null)
		{
			float min = 0;
			float max = 90;
			for (int i = (int) max; i != 0; i--)
			{
				if (r2.getPDF(i) != 0)
				{
					max = i + 1;
					break;
				}
			}
			int points = (int) (max - min);
			float[] x = new float[points];
			float[] y = new float[points];
			for (int i = 0; i < x.length; i++)
			{
				x[i] = (max - min) / points * i + min;
				y[i] = r2.getPDF(x[i]);
			}
			addSeries(x, y, "Regionalizatoin " + getTitle(), 1, 0, x.length, false, getLineRenderer());

		}

	}

	@Handles(raises = {})
	void handleEvent(SlopePDFRaiser r)
	{
		r1 = r;
		update();
	}

	@Handles(raises = {})
	void handleEvent(RegSlopePDFRaiser r)
	{
		r2 = r;
		update();
	}

}
