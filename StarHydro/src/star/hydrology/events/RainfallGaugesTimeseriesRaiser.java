package star.hydrology.events;

import java.util.Collection;

import javax.vecmath.Point3f;

import star.annotations.Raiser;
import star.hydro.rainfall.TimeSeries;

@Raiser
public interface RainfallGaugesTimeseriesRaiser extends star.event.Raiser
{
	Collection<TimeSeries> getRainfallTimeseries();

	Collection<Point3f> getRainfallGauges();
}
