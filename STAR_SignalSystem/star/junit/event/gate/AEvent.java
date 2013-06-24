package star.junit.event.gate;

import star.event.Event;

public class AEvent extends Event
{

	public AEvent(ARaiser source, boolean valid)
	{
		super(source, valid);
	}

	public AEvent(ARaiser source)
	{
		super(source);
	}

	private static final long serialVersionUID = 1L;

}
