package star.hydrology.ui.chart.pdf;

import org.jfree.chart.LegendItemCollection;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.TopIndexPDFRaiser;
import star.hydrology.events.regionalization.RegTopindexPDFRaiser;
import star.hydrology.ui.chart.AbstractChart;

@SignalComponent(extend = AbstractChart.class)
public class TopIndexPDFChart extends TopIndexPDFChart_generated
{

	private static final long serialVersionUID = 1L;
	private TopIndexPDFRaiser r1;
	private RegTopindexPDFRaiser r2;

	protected String getTitle()
	{
		return "TopIndex PDF";
	}

	protected String getRangeAxisLabel()
	{
		return "";
	}

	protected String getValueAxisLabel()
	{
		return "ln(accu/tan(slope))";
	}

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
			int points = 200 > (max - min) ? 200 : (int) (max - min);
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
			addSeries(x, y, "Regionalization " + getTitle(), 1, 0, x.length, false, getLineRenderer());

		}

	}

	@Handles(raises={})
	void handleEvent(TopIndexPDFRaiser r)
	{
		r1 = r;
		update();
	}

	@Handles(raises = {})
	void handle(RegTopindexPDFRaiser r)
	{
		r2 = r;
		update();
	}
}
