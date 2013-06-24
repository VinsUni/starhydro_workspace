package star.junit.event.gate;

import java.util.Arrays;

import star.event.Adapter;
import star.event.Event;
import star.event.GatedListener;

public class ComponentC implements GatedListener
{
	private static final long serialVersionUID = 1L;
	Adapter adapter = new Adapter(this);
	Event[] data = null;

	public Adapter getAdapter()
	{
		return adapter;
	}

	public void addNotify()
	{
		getAdapter().addGatedAnd(new Class[] { AEvent.class, BEvent.class }, null, true);
	}

	public void removeNotify()
	{
		getAdapter().removeGatedAnd(new Class[] { AEvent.class, BEvent.class }, null);
	}

	public void eventsRaised(Event[] event, boolean valid)
	{
		System.out.println(valid + " " + event[0] + " " + event[1]);
		this.data = event;
		Arrays.toString(event);
	}

	public Event[] getData()
	{
		return data;
	}

}
