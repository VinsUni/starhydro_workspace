package star.junit.event.gate;

import star.event.Event;

public class BEvent extends Event
{
	public BEvent(BRaiser source, boolean valid)
	{
		super(source, valid);
	}

	public BEvent(BRaiser source)
	{
		super(source);
	}

	private static final long serialVersionUID = 1L;
}
