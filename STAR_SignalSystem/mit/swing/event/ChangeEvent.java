package mit.swing.event;

public class ChangeEvent extends star.event.Event
{
	private static final long serialVersionUID = 1L;

	public ChangeEvent(mit.swing.event.ChangeRaiser source)
	{
		super(source);
	}
}