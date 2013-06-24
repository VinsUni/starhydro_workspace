package star.junit.event.awtobjawt;

import java.io.Serializable;

import star.event.Adapter;

public class Raiser implements TestRaiser, Serializable
{
	private static final long serialVersionUID = 1L;

	public Raiser()
	{
		super();
	}

	public void addNotify()
	{
	}

	public void removeNotify()
	{
	}

	public void raise(String text)
	{
		TestEvent e = new TestEvent(this);
		e.setText(text);
		e.raise();
	}

	transient Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}
}
