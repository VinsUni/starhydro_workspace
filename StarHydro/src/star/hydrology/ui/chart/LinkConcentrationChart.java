package star.hydrology.ui.chart;

import org.jfree.chart.LegendItemCollection;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.LinkConcentrationRaiser;

@SignalComponent(extend = AbstractChart.class)
public class LinkConcentrationChart extends LinkConcentrationChart_generated
{

	private static final long serialVersionUID = 1L;

	protected String getTitle()
	{
		return "";
	}

	protected String getValueAxisLabel()
	{
		return "Height (m)";
	}

	@Handles(raises = {})
	void handleEvent(LinkConcentrationRaiser r)
	{
		reset();
		setLegend(new LegendItemCollection());
		getPlot().setDataset(0, null);
		getPlot().setRangeAxis(getAxis("", false));
		if (utils.Runner.isInterrupted())
		{
			return;
		}
		float min = r.getMinimumHeight();
		float max = r.getMaximumHeight();
		int points = 200 > (max - min) ? 200 : (int) (max - min);
		float[] x = new float[points];
		float[] y = new float[points];
		for (int i = 0; i < x.length; i++)
		{
			x[i] = (max - min) / points * i + min;
			y[i] = r.getAccomulationAt(x[i]);
		}
		if (utils.Runner.isInterrupted())
		{
			return;
		}
		addSeries(x, y, getTitle(), 0, 0, x.length, false, getLineRenderer());
		addLegend();

	}

}
