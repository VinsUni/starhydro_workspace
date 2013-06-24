package star.hydro.rainfall;

import java.util.ArrayList;

import star.annotations.Raiser;

@Raiser
public interface ConvolutedTimeSeriesRaiser extends star.event.Raiser
{
	ArrayList<TimeSeries> getTimeSeries();

	float getMinimumTime();

	float getMaximumTime();

	int getPoints();
}
