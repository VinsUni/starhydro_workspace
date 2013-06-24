package star.j3d.ui.signal;

import star.event.Event;

public class DirtyRegionEvent extends Event
{
	private static final long serialVersionUID = 1L;

	public DirtyRegionEvent(DirtyRegionRaiser source)
	{
		super(source);
	}

}
