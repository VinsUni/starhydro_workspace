package star.junit.event.throwing;

import star.event.Adapter;

public class Raiser implements TestRaiser
{

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

	Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}
}
