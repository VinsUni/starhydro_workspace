package app.ui;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import star.annotations.SignalComponent;
import star.hydrology.events.WidthFunctionRaiser;
import star.hydrology.ui.chart.WidthFunctionChart;

@SignalComponent(extend = JPanel.class)
public class WidthFns extends WidthFns_generated
{
	private static final long serialVersionUID = 1L;
	private boolean debug = false;

	@Override
	public void addNotify()
	{
		super.addNotify();
		MigLayout layout = new MigLayout(debug ? "debug 1000,fillx" : "fillx");
		setLayout(layout);
		add(new MyLabel("Straight line"), "span 2,growx,wrap ");
		add(new WidthFunctionChart(WidthFunctionRaiser.STRAIGHTLINE), "span 2,growx, wrap");
		add(new MyLabel("Geometric"), "span 2,growx, wrap");
		add(new WidthFunctionChart(WidthFunctionRaiser.GEOMETRIC), "span 2,growx, wrap");
		add(new MyLabel("Topological"), "span 2,growx, wrap ");
		add(new WidthFunctionChart(WidthFunctionRaiser.TOPOLOGIC), "span 2,growx, wrap");
	}
}
