package star.junit.event.awtawtobj;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import star.event.Adapter;

public class Raiser extends JButton implements TestRaiser
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

	transient Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}
}
