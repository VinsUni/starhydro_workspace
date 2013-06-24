package star.junit.event.xawtlxawtxawtl;

import mit.swing.xJPanel;
import star.event.Event;
import star.event.EventController;
import star.event.Listener;

public class Container extends xJPanel implements EventController, Listener
{
	private static final long serialVersionUID = 1L;

	public Consumer c;
	public Raiser r;

	public Container()
	{
		super();
	}

	public void addNotify()
	{
		super.addNotify();
		c = new Consumer();
		r = new Raiser();
		add(c);
		add(r);
		getAdapter().addContained(TestEvent.class);
	}

	public void removeNotify()
	{
		super.removeNotify();
	}

	public void eventRaised(Event event)
	{
	}
}
