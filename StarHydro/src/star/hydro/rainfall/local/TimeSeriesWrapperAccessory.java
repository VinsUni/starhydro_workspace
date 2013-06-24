package star.hydro.rainfall.local;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import star.event.Adapter;
import star.hydro.rainfall.RainfallChart;
import star.hydro.rainfall.TimeSeries;
import star.hydro.rainfall.TimeSeriesRaiser;

public class TimeSeriesWrapperAccessory extends JPanel implements TimeSeriesRaiser
{
	private static final long serialVersionUID = 1L;
	private TimeSeriesWrapper wrapper;
	private RainfallChart chart;
	private JLabel label;

	public TimeSeriesWrapperAccessory(TimeSeriesWrapper wrapper)
	{
		this.wrapper = wrapper;
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		label = new JLabel(wrapper.getName());
		chart = new RainfallChart();
		final TimeSeriesRaiser self = this;
		add(label);
		add(chart);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				chart.handleRainfall(self);

			}
		});
	}

	@Override
	public void removeNotify()
	{
		removeAll();
		super.removeNotify();
	}

	public TimeSeries getTimeSeries()
	{
		return wrapper.getTimeSeries();
	}

	public Adapter getAdapter()
	{
		return null;
	}
}
