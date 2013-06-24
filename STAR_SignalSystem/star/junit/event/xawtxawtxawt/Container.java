package star.junit.event.xawtxawtxawt;

import mit.swing.xJPanel;
import star.event.EventController;

public class Container extends xJPanel implements EventController
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
}
