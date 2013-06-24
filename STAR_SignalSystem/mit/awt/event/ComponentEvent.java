package mit.awt.event;

public class ComponentEvent extends star.event.Event
{
	private static final long serialVersionUID = 1L;

	public ComponentEvent(mit.awt.event.ComponentRaiser source)
	{
		super(source);
	}
}