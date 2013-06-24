package mit.swing.event;

public class ListSelectionEvent extends star.event.Event
{
	private static final long serialVersionUID = 1L;

	public ListSelectionEvent(mit.swing.event.ListSelectionRaiser source)
	{
		super(source);
	}
}