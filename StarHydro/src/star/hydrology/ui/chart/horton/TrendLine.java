package star.hydrology.ui.chart.horton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import star.annotations.Handles;
import star.annotations.SignalComponent;

@SignalComponent(extend = JCheckBox.class, raises = { TrendLineRaiser.class })
public class TrendLine extends TrendLine_generated
{
	private static final long serialVersionUID = 1L;

	public TrendLine(String text)
	{
		super();
		setText(text);
		setEnabled(false);
	}

	public void addNotify()
	{
		super.addNotify();
		setToolTipText("This tool is enabled only in log scale mode");
		addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				raise_TrendLineEvent();
			}
		});
	}

	@Handles(raises = { TrendLineRaiser.class })
	protected void handleLogScaleEvent(LogScaleRaiser r)
	{
		if (!r.isSelected())
		{
			setSelected(false);
			raise_TrendLineEvent();
		}
		setEnabled(r.isSelected());
	}

}
