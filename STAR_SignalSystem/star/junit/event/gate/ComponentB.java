package star.junit.event.gate;

import star.event.Adapter;
import star.event.Event;
import star.event.Listener;

public class ComponentB implements Listener, BRaiser
{
	private static final long serialVersionUID = 1L;
	Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}

	int value = 0;

	public void addNotify()
	{
		getAdapter().addHandled(AEvent.class);
	}

	public void removeNotify()
	{
		getAdapter().removeHandled(AEvent.class);
	}

	public int getValue()
	{
		return value;
	}

	public void raise(int value)
	{
		this.value = value;
		(new BEvent(this)).raise();
	}

	public void eventRaised(Event event)
	{
		if( event instanceof AEvent )
		{
			handleEvent((ARaiser) (event.getSource()));
		}
	}

	void handleEvent(ARaiser raiser)
	{
		raise(raiser.getValue() * raiser.getValue());
	}
}
