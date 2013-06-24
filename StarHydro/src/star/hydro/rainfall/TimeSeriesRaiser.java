package star.hydro.rainfall;

import star.annotations.Raiser;

@Raiser
public interface TimeSeriesRaiser extends star.event.Raiser
{
	TimeSeries getTimeSeries();
}
