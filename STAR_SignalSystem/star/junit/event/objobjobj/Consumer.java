package star.junit.event.objobjobj;

import star.event.Adapter;
import star.event.Event;
import star.event.Listener;

public class Consumer implements Listener
{

	public Consumer()
	{
		super();
		// TODO Auto-generated constructor stub
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

	Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}

	String text;

	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}
}
