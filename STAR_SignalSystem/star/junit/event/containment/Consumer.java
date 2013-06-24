package star.junit.event.containment;

import java.io.Serializable;

import star.event.Adapter;
import star.event.Event;
import star.event.Listener;

public class Consumer implements Listener, Serializable
{
	private static final long serialVersionUID = 1L;

	String text = null;

	/**
	 * @return Returns the text.
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * @param text
	 *            The text to set.
	 */
	public void setText(String text)
	{
		this.text = text;
	}

	public Consumer()
	{
		super();
	}

	public void addNotify()
	{
		getAdapter().addHandled(TestEvent.class);
		setText("Dummy Text");
	}

	public void removeNotify()
	{
		getAdapter().removeHandled(TestEvent.class);
	}

	public void eventRaised(Event event)
	{
		if( event instanceof TestEvent )
		{
			TestEvent e = (TestEvent) event;
			setText(e.getText());
		}
	}

	transient Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}
}
