package star.junit.event.containment;

import java.awt.Component;

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
		add((EventController) r);
		this.getAdapter().addComponent(c);
		add((EventController) c);
	}

	public void removeNotify()
	{
		remove((EventController) c);
		remove((EventController) r);
		super.removeNotify();
	}

	transient Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}

	void add(EventController c)
	{
		if( c instanceof Component )
		{
			super.add((Component) c);
		}
	}

	void remove(EventController c)
	{
		if( c instanceof Component )
		{
			super.remove((Component) c);
		}
	}

}