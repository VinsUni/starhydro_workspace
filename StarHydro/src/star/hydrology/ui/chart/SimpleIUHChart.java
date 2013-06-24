package star.hydrology.ui.chart;

import org.jfree.chart.LegendItemCollection;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.IUHRaiser;
import star.hydrology.events.PaletteChangedRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.ui.palette.Palette;

@SignalComponent(extend = AbstractChart.class)
public class SimpleIUHChart extends SimpleIUHChart_generated
{

	private IUHRaiser iuh = null;
	private Palette p = null;
	private Palette raingaugePalette = null;

	private static final long serialVersionUID = 1L;

	protected String getTitle()
	{
		return "";
	}

	protected String getValueAxisLabel()
	{
		return "Time";
	}

	@Handles(raises = {})
	void handleEvent(IUHRaiser r)
	{
		iuh = r;
		update();
	}

	@Handles(raises = {})
	void updatePalette(PaletteChangedRaiser r)
	{
		if (r.getKind() == LayerConstants.SIMPLEIUHLAYER)
		{
			p = r.getPalette();
			update();
		}
		if (r.getKind() == LayerConstants.RAINGAUGELAYER)
		{
			raingaugePalette = r.getPalette();
			update();
		}
	}

	private void update()
	{
		reset();
		if (iuh != null)
		{
			setLegend(new LegendItemCollection());
			getPlot().setDataset(0, null);
			getPlot().setRangeAxis(getAxis("", false));
			float min = iuh.getMinimum();
			float max = iuh.getMaximum();

			int points = iuh.getNumberOfBins(); // 200 > (max - min) ? 200 : (int) (max - min);
			float[] x = new float[points];
			float[] y = new float[points];
			for (int i = 0; i < x.length; i++)
			{
				x[i] = (max - min) / points * i + min;
				y[i] = iuh.getValueAt(x[i]);
				// System.out.println( x[i] + " " + y[i] );
			}
			if (utils.Runner.isInterrupted())
			{
				return;
			}
			if (p == null)
			{
				addSeries(x, y, "Outlet flow", 0, 0, x.length, false, getLineRenderer());
			}
			else
			{
				addSeries(x, y, "Outlet flow", 0, 0, x.length, false, getLineRenderer(p, min, max, x.length));
			}

			for (int gauge = 0; gauge < iuh.getNumberOfGauges(); gauge++)
			{
				updateRaingauges(gauge);
			}
			getChart().removeLegend();
			addLegend();
		}
	}

	private void updateRaingauges(int gauge)
	{
		float min = iuh.getMinimum();
		float max = iuh.getMaximum();

		int points = iuh.getNumberOfBins(); // 200 > (max - min) ? 200 : (int) (max - min);
		float[] x = new float[points];
		float[] y = new float[points];
		for (int i = 0; i < x.length; i++)
		{
			x[i] = (max - min) / points * i + min;
			y[i] = iuh.getValueAt(gauge, x[i]);
		}
		addSeriesReuseRange(x, y, "Gauge " + gauge + " flow", gauge + 1, 0, x.length, getLineRenderer(raingaugePalette.getColor(1.0f * gauge / iuh.getNumberOfGauges())));
	}
}
