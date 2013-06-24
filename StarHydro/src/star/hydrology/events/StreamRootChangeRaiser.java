package star.hydrology.events;

import star.annotations.Raiser;
import app.worker.streamnetwork.Stream;

@Raiser()
public interface StreamRootChangeRaiser extends star.event.Raiser
{
	Stream getStreamRoot();
}
