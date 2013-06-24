package star.junit.event.gate;

import star.event.Adapter;

public class ComponentA implements ARaiser
{
	private static final long serialVersionUID = 1L;
	Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}

	int value = 0;

	public void addNotify()
	{
	}

	public void removeNotify()
	{

	}

	public int getValue()
	{
		return value;
	}

	public void raise(int value)
	{
		this.value = value;
		(new AEvent(this)).raise();
	}

}
