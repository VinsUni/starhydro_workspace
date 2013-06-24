package star.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

import mit.awt.event.ActionRaiser;
import mit.awt.event.MouseRaiser;
import mit.swing.xJLabel;
import mit.swing.xJPanel;
import mit.swing.xJSlider;
import mit.swing.xJTextField;
import mit.swing.event.ChangeRaiser;
import star.event.Event;
import star.event.Listener;

public class ComboSlider extends xJPanel implements Listener, ChangeRaiser
{

	private static final long serialVersionUID = 1L;
	String text = "Label";
	int value = 30;
	JLabel label;
	JTextField field;
	xJLabel arrow;
	JSlider slider;

	public void eventRaised(Event event)
	{
		if (event instanceof mit.awt.event.MouseEvent)
		{
			handleMouseEvent(((MouseRaiser) event.getSource()).getMouseEvent());
		}
		if (event instanceof mit.swing.event.ChangeEvent)
		{
			handleChangeEvent(((ChangeRaiser) event.getSource()).getChangeEvent());
		}
		if (event instanceof mit.awt.event.ActionEvent)
		{
			handleActionEvent(((ActionRaiser) event.getSource()).getActionEvent());
		}
	}

	void handleMouseEvent(MouseEvent event)
	{
		if (event.getID() == MouseEvent.MOUSE_ENTERED && event.getSource() == arrow)
		{
			slider.setVisible(true);
			arrow.setVisible(false);
			label.setText(getText().substring(0, 1));
			invalidate();
			repaint();
		}
		if (event.getID() == MouseEvent.MOUSE_EXITED && event.getSource() == slider)
		{
			slider.setVisible(false);
			arrow.setVisible(true);
			label.setText(getText());
			invalidate();
			repaint();
		}
	}

	void handleChangeEvent(ChangeEvent event)
	{
		if (event.getSource() == slider && slider.isVisible())
		{
			setValue(slider.getValue());
		}
	}

	void handleActionEvent(ActionEvent event)
	{
		if (event.getSource() == field)
		{
			try
			{
				int value = Integer.parseInt(field.getText());
				setValue(value);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				setValue(getValue());
			}
		}
	}

	public void addNotify()
	{
		super.addNotify();
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		label = new JLabel(getText());
		label.setMinimumSize(new Dimension(250, 20));
		field = new xJTextField();
		field.setColumns(3);
		field.getCaret().setBlinkRate(0);
		field.setMaximumSize(new Dimension(50, 20));
		field.addFocusListener(new FocusListener()
		{

			public void focusGained(FocusEvent e)
			{
			}

			public void focusLost(FocusEvent e)
			{
				try
				{
					int value = Integer.parseInt(field.getText());
					setValue(value);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					setValue(getValue());
				}
			}
		});
		arrow = new xJLabel();
		arrow.setText(getSuffixText());
		arrow.setForeground(Color.blue);

		slider = new xJSlider();
		slider.setMinimum(getMinimum());
		slider.setMaximum(getMaximum());
		slider.setVisible(false);

		slider.setValue(getValue());
		field.setText(String.valueOf(getValue()));

		add(label);
		add(field);
		add(arrow);
		add(slider);
		getAdapter().addHandled(mit.awt.event.MouseEvent.class);
		getAdapter().addHandled(mit.awt.event.ActionEvent.class);
		getAdapter().addHandled(mit.swing.event.ChangeEvent.class);
		getAdapter().addContained(mit.swing.event.DocumentEvent.class);
	}

	public void removeNotify()
	{
		getAdapter().removeHandled(mit.awt.event.MouseEvent.class);
		getAdapter().removeHandled(mit.awt.event.ActionEvent.class);
		getAdapter().removeHandled(mit.swing.event.ChangeEvent.class);
		super.removeNotify();
	}

	int minimum = 0;
	int maximum = 100;

	public int getMinimum()
	{
		return minimum;
	}

	public void setMinimum(int value)
	{
		this.minimum = value;
	}

	public int getMaximum()
	{
		return maximum;
	}

	public void setMaximum(int value)
	{
		this.maximum = value;
	}

	public int getValue()
	{
		return this.value;
	}

	public void setValue(int value)
	{
		this.value = value;
		if (field != null)
		{
			field.setText(String.valueOf(value));
		}
		if (slider != null)
		{
			slider.setValue(this.value);
		}
		(new mit.swing.event.ChangeEvent(this)).raise();
	}

	public void setText(String label)
	{
		this.text = label;
		if (this.label != null)
		{
			this.label.setText(text);
		}
	}

	public String getText()
	{
		return text;
	}

	public ComboSlider()
	{
		super();
	}

	public ChangeEvent getChangeEvent()
	{
		return new ChangeEvent(this);
	}

	public static void main(String[] str)
	{
		JFrame f = new JFrame("test");
		f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.PAGE_AXIS));
		f.add(new ComboSlider());
		f.add(new JLabel("test"));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);

	}

	String suffixText = "%";

	public String getSuffixText()
	{
		return suffixText;
	}

	public void setSuffixText(String value)
	{
		this.suffixText = value;
		if (arrow != null)
		{
			arrow.setText(value);
		}
	}

}
