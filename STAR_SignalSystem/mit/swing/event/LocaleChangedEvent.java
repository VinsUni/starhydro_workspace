package mit.swing.event;

public class LocaleChangedEvent extends star.event.Event
{
	private static final long serialVersionUID = 1L;

	public LocaleChangedEvent(mit.swing.event.LocaleChangedRaiser source)
	{
		super(source);
	}
}
