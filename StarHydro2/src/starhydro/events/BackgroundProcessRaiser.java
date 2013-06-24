package starhydro.events;

import org.jdesktop.swingworker.SwingWorker;

import star.annotations.Raiser;

@Raiser
public interface BackgroundProcessRaiser extends star.event.Raiser
{
	SwingWorker getSwingWorker();
	String getName() ;
}
