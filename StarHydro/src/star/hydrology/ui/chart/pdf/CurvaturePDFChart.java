package star.hydrology.ui.chart.pdf;

import org.jfree.chart.LegendItemCollection;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.CurvaturePDFRaiser;
import star.hydrology.events.regionalization.RegCurvaturePDFRaiser;
import star.hydrology.ui.chart.AbstractChart;

@SignalComponent(extend = AbstractChart.class)
public class CurvaturePDFChart extends CurvaturePDFChart_generated
{
	private static final long serialVersionUID = 1L;
	private CurvaturePDFRaiser r1;
	private RegCurvaturePDFRaiser r2;

	protected String getTitle()
	{
		return "Curvature PDF";
	}

	protected String getRangeAxisLabel()
	{
		return "";
	}

	protected String getValueAxisLabel()
	{
		return "1/m";
	}

	void update()
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
			addLegend();
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
			addSeries(x, y, "Regionalize " + getTitle(), 1, 0, x.length, false, getLineRenderer());

		}
	}

	@Handles(raises = {}, handleValid=false)
	void handleInvalidEvent(CurvaturePDFRaiser raiser)
	{
		r1 = null;
		update();
	}


	@Handles(raises = {})
	void handleEvent(CurvaturePDFRaiser r)
	{
		r1 = r;
		update();
	}

	@Handles(raises = {})
	void handle(RegCurvaturePDFRaiser r)
	{
		r2 = r;
		update();
	}
}
