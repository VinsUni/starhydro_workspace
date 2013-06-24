package star.hydrology.ui.chart.horton;

import star.annotations.Raiser;

@Raiser
public interface TrendLineRaiser extends star.event.Raiser
{
	public boolean isSelected();
}
