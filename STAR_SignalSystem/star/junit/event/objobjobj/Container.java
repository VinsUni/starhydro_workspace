package star.junit.event.objobjobj;

import star.event.Adapter;
import star.event.EventController;

public class Container implements EventController
{

	public Consumer c;
	public Raiser r;

	public Container()
	{
		super();
	}

	public void addNotify()
	{
		c = new Consumer();
		r = new Raiser();
		getAdapter().addComponent(c);
		getAdapter().addComponent(r);
		getAdapter().addContained(TestEvent.class);
	}

	public void removeNotify()
	{
	}

	Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}
}
