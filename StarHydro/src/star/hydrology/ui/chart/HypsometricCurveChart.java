package star.hydrology.ui.chart;

import org.jfree.chart.LegendItemCollection;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.HypsometricCurveRaiser;
import star.hydrology.events.PaletteChangedRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.ui.palette.Palette;
import utils.ArrayNumerics;

@SignalComponent(extend = AbstractChart.class)
public class HypsometricCurveChart extends HypsometricCurveChart_generated
{
	HypsometricCurveRaiser r = null;
	Palette p = null;

	private static final long serialVersionUID = 1L;

	protected String getTitle()
	{
		return "";
	}

	protected String getValueAxisLabel()
	{
		return "Relative accumulation area above height";
	}

	@Handles(raises = {})
	void handleEvent(HypsometricCurveRaiser r)
	{
		this.r = r;
		update();
	}

	@Handles(raises = {})
	void updatePalette(PaletteChangedRaiser r)
	{
		if (r.getKind() == LayerConstants.WATERSHED)
		{
			p = r.getPalette();
			update();
		}
	}

	private void update()
	{
		reset();
		if (r != null)
		{
			setLegend(new LegendItemCollection());
			getPlot().setDataset(0, null);
			getPlot().setRangeAxis(getAxis("", false));
			float min = r.getMinimumHeight();
			float max = r.getMaximumHeight();
			int points = 500 > (max - min) ? 500 : (int) (max - min);
			float[] x = new float[points];
			float[] y = new float[points];
			x[0] = min;
			y[0] = r.getAccomulationAt(x[0]);
			for (int i = 1; i < x.length; i++)
			{
				x[i] = (max - min) / points * i + min;
				y[i] = r.getAccomulationAt(x[i]);
			}
			if (utils.Runner.isInterrupted())
			{
				return;
			}
			ArrayNumerics.normalize(y);
			for (int i = x.length - 1; i >= 1; i--)
			{
				y[i - 1] = y[i - 1] + y[i];
			}
			if (utils.Runner.isInterrupted())
			{
				return;
			}
			if (p == null)
			{
				addSeries(y, x, "Elevation (m)", 0, 0, x.length, false, getLineRenderer());
			}
			else
			{
				addSeries(y, x, "Elevation (m)", 0, 0, x.length, false, getLineRenderer(p, max, min, x.length));
			}
			addLegend();
		}

	}
}
