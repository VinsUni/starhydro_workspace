package star.junit.event.xawtlxawtxawtl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mit.swing.xJButton;

public class Raiser extends xJButton implements TestRaiser
{
	private static final long serialVersionUID = 1L;

	public Raiser()
	{
		super();
	}

	public void addNotify()
	{
		super.addNotify();
		setText("Raise");
		addActionListener(new ActionListener()
		{
			int i = 0;

			public void actionPerformed(ActionEvent e)
			{
				raise("Event " + i++);
			}

		});
	}

	public void removeNotify()
	{
		super.removeNotify();
	}

	public void raise(String text)
	{
		TestEvent e = new TestEvent(this);
		e.setText(text);
		e.raise();
	}
}
