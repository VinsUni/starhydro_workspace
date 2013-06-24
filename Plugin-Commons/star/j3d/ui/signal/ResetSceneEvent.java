package star.j3d.ui.signal;

import star.event.Event;
import star.event.Raiser;

public class ResetSceneEvent extends Event
{
	private static final long serialVersionUID = 1L;

	public ResetSceneEvent(Raiser source)
	{
		super(source);
	}

}
