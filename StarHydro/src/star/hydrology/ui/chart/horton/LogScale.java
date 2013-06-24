package star.hydrology.ui.chart.horton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import star.annotations.SignalComponent;

@SignalComponent(extend = JCheckBox.class, raises = { LogScaleRaiser.class })
public class LogScale extends LogScale_generated
{
	private static final long serialVersionUID = 1L;

	public LogScale(String text)
	{
		super();
		setText(text);
	}

	public void addNotify()
	{
		super.addNotify();
		addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				raise_LogScaleEvent();
			}
		});
	}
}
