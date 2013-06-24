package star.hydrology.events;

import java.util.Set;

import star.annotations.Raiser;
import app.worker.widthfunction.Datapoint;

@Raiser()
public interface WidthFunctionRaiser extends star.event.Raiser
{
	public static int STRAIGHTLINE = 0;
	public static int TOPOLOGIC = 1;
	public static int GEOMETRIC = 2;

	public int getType();

	public Set<Datapoint> getData();
}
