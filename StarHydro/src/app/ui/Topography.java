package app.ui;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import star.annotations.SignalComponent;
import star.hydrology.ui.chart.HypsometricCurveChart;
import star.hydrology.ui.chart.LinkConcentrationChart;
import star.hydrology.ui.chart.SlopeAreaChart;
import app.worker.relief.SlopeAreaWorker;

@SignalComponent(extend = JPanel.class)
public class Topography extends Topography_generated
{

	private static final long serialVersionUID = 1L;
	private boolean debug = false;

	@Override
	public void addNotify()
	{
		super.addNotify();
		MigLayout layout = new MigLayout(debug ? "debug 1000,fillx" : "fillx");
		setLayout(layout);
		add(new MyLabel("Hypsometric curve"), "span 2,growx,wrap ");
		add(new HypsometricCurveChart(), "span 2,growx, wrap");
		add(new MyLabel("Link concentration"), "span 2,growx,wrap ");
		add(new LinkConcentrationChart(), "span 2,growx, wrap");
		add(new MyLabel("Slope-area relationship"), "span 2,growx, wrap ");
		add(new SlopeAreaChart(), "span 2,growx, wrap");
		getAdapter().addComponent(new SlopeAreaWorker());
	}
}
