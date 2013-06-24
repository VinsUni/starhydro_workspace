package app.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import star.annotations.SignalComponent;
import star.hydrology.ui.chart.horton.HortonNumberChart;
import star.hydrology.ui.chart.horton.LogScaleRaiser;
import star.hydrology.ui.chart.horton.TrendLineRaiser;

@SignalComponent(extend = JPanel.class, contains = { LogScaleRaiser.class, TrendLineRaiser.class })
public class HortonNumbers extends HortonNumbers_generated
{
	private static final long serialVersionUID = 1L;
	private boolean debug = false;

	@Override
	public void addNotify()
	{
		// TODO Auto-generated method stub
		super.addNotify();
		MigLayout layout = new MigLayout(debug ? "debug 1000,fillx" : "fillx, wrap 4");
		setLayout(layout);
		add(new MyLabel("Ratios"), "span 4, spanx , growx , wrap");
		// add(new JSeparator(SwingConstants.HORIZONTAL), "span 3,growx, wrap");

		add(new JLabel("<html>&nbsp; &nbsp;Area Ratio</html>"), "span 1");
		add(new HortonNumberLabel(HortonNumberChart.HortonNumbers.AREA), "span 1,growx");
		add(new JLabel("<html>&nbsp; &nbsp;Count Ratio</html>"), "span 1 ");
		add(new HortonNumberLabel(HortonNumberChart.HortonNumbers.COUNT), "span 1, growx , wrap");

		add(new JLabel("<html>&nbsp; &nbsp;Length Ratio</html>"), "span 1 ");
		add(new HortonNumberLabel(HortonNumberChart.HortonNumbers.LENGTH), "span 1,growx");
		add(new JLabel("<html>&nbsp; &nbsp;Slope Ratio</html>"), "span 1 ");
		add(new HortonNumberLabel(HortonNumberChart.HortonNumbers.SLOPE), "span 1, growx ,wrap");

		add(new MyLabel("Area"), "span 4, spanx , growx , wrap");
		add(new HortonNumberChart(HortonNumberChart.HortonNumbers.AREA), "span 4,growx, wrap");

		add(new MyLabel("Count"), "span 4, spanx , growx , wrap");
		add(new HortonNumberChart(HortonNumberChart.HortonNumbers.COUNT), "span 4,growx, wrap");

		add(new MyLabel("Lenght"), "span 4, spanx , growx , wrap");
		add(new HortonNumberChart(HortonNumberChart.HortonNumbers.LENGTH), "span 4,growx, wrap");

		add(new MyLabel("Slope"), "span 4, spanx , growx , wrap");
		add(new HortonNumberChart(HortonNumberChart.HortonNumbers.SLOPE), "span 4,growx, wrap");
	}
}
