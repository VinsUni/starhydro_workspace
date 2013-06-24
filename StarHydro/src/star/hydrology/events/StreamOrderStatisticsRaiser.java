package star.hydrology.events;

import star.annotations.Raiser;
import app.worker.streamnetwork.Stream;

@Raiser()
public interface StreamOrderStatisticsRaiser extends star.event.Raiser
{
	public float[] getOrderAreas();

	public int[] getOrderCount();

	public float[] getOrderLength();

	public float[] getOrderSlopes();

	public Stream getRootStream();

}
