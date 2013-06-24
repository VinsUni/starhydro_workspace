package star.junit.event.throwing;

import star.event.Adapter;
import star.event.Event;
import star.events.common.DistributionExceptionEvent;

public class Listener implements star.event.Listener
{

	public void eventRaised(Event event)
	{
		if( !(event instanceof DistributionExceptionEvent) )
		{
			throw new RuntimeException("This is an exception " + event.getClass().getName());
		}
		else
		{
			System.out.println("received " + event);
		}
	}

	public void addNotify()
	{
		getAdapter().addHandled(TestEvent.class);
		getAdapter().addHandled(DistributionExceptionEvent.class);
	}

	Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}

	public void removeNotify()
	{
		getAdapter().removeHandled(TestEvent.class);
	}

}
