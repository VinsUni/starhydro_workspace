package star.hydrology.events;

import star.annotations.Raiser;
import star.hydro.rainfall.TimeSeries;

@Raiser
public interface SlopeAreaRaiser extends star.event.Raiser
{
	TimeSeries getDataset();
}
