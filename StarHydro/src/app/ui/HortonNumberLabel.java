package app.ui;

import javax.swing.JLabel;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.HortonNumberRaiser;
import star.hydrology.ui.chart.horton.HortonNumberChart.HortonNumbers;
import utils.Format;

@SignalComponent(extend = JLabel.class)
public class HortonNumberLabel extends HortonNumberLabel_generated
{

	private static final long serialVersionUID = 1L;
	private HortonNumbers number;

	public HortonNumberLabel(HortonNumbers number)
	{
		this.number = number;
	}

	@Handles(raises = {})
	void update(HortonNumberRaiser r)
	{
		double value = Float.NaN;
		try
		{
			if (HortonNumbers.AREA.equals(number))
			{
				value = Math.exp(r.getHortonNumbers().get(HortonNumberRaiser.Ra_SLOPE));
			}
			else if (HortonNumbers.SLOPE.equals(number))
			{
				value = Math.exp(-r.getHortonNumbers().get(HortonNumberRaiser.Rs_SLOPE));
			}
			else if (HortonNumbers.LENGTH.equals(number))
			{
				value = Math.exp(r.getHortonNumbers().get(HortonNumberRaiser.Rl_SLOPE));
			}
			else if (HortonNumbers.COUNT.equals(number))
			{
				value = Math.exp(-r.getHortonNumbers().get(HortonNumberRaiser.Rb_SLOPE));
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		setText(Format.formatNumber(value));
	}
}
