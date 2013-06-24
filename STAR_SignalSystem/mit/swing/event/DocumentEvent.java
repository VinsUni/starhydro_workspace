package mit.swing.event;

public class DocumentEvent extends star.event.Event
{
	private static final long serialVersionUID = 1L;

	public DocumentEvent(mit.swing.event.DocumentRaiser source)
	{
		super(source);
	}
}