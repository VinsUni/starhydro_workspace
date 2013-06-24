package star.junit.event.awtobjawt;

import javax.swing.JLabel;

import star.event.Adapter;
import star.event.Event;
import star.event.Listener;

public class Consumer extends JLabel implements Listener
{
	private static final long serialVersionUID = 1L;

	public Consumer()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public void addNotify()
	{
		super.addNotify();
		getAdapter().addHandled(TestEvent.class);
		setText("Dummy Text");
	}

	public void removeNotify()
	{
		super.removeNotify();
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
