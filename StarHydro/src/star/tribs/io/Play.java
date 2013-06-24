package star.tribs.io;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class Play extends JPanel
{
	JToggleButton button;
	JTextField field;
	VoronoiCanvas c;

	public Play(VoronoiCanvas canvas)
	{
		c = canvas;
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		setLayout(new FlowLayout());
		button = new JToggleButton("Play");
		field = new JTextField(8);
		JButton button2 = new JButton("Next");
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println(" button " + button.isSelected());
				c.setPlay(button.isSelected());
			}
		});
		field.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int col = Integer.parseInt(field.getText());
				System.out.println(" field " + col);
				c.setCol(col);
			}
		});
		button2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				c.next();
			}
		});

		add(button);
		add(field);
		add(button2);
	}
}
