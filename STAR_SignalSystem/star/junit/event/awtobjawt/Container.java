package star.junit.event.awtobjawt;

import javax.swing.JPanel;

import star.event.Adapter;
import star.event.EventController;

public class Container extends JPanel implements EventController
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
		getAdapter().addComponent(r);
		getAdapter().addContained(TestEvent.class);
	}

	public void removeNotify()
	{
		super.removeNotify();
	}

	transient Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}
}
