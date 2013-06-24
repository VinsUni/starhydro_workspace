package app;

import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import star.annotations.SignalComponent;
import starhydro.events.AccumulationThresholdChangedRaiser;

@SignalComponent(extend = JSlider.class, raises = { AccumulationThresholdChangedRaiser.class })
public class AccumulationThresholdSlider extends AccumulationThresholdSlider_generated implements ChangeListener
{
	private static final long serialVersionUID = 1L;

	private Hashtable<Integer, JLabel> getLabels()
	{
		Hashtable<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
		for (int i = getMinimum(); i <= getMaximum(); i += 10)
		{
			float acc = getAccumulation(i);
			dict.put(i, new JLabel("<html>" + (acc >= 100000 ? acc / 1000000 + " km<sup>2</sup></html>" : acc + " m<sup>2</sup></html>")));
		}
		return dict;
	}

	public float getAccumulation(int i)
	{
		final float ret = (float) Math.exp((i - 1) / 10 * Math.log(10));
		return ret;

	}

	public float getAccumulation()
	{
		final float ret = getAccumulation(getValue());
		System.out.println(this.getClass().getName() + " " + getValue() + " " + ret);
		return ret;
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		super.setOrientation(JSlider.VERTICAL);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				setMinimum(19);
				setMaximum(91);
				setValue(70);
				setLabelTable(getLabels());
				setPaintLabels(true);
				setPaintTicks(true);
				raise_AccumulationThresholdChangedEvent();
			}
		});
		addChangeListener(this);
	}

	public void stateChanged(ChangeEvent e)
	{
		if( !getValueIsAdjusting() )
		{
			raise_AccumulationThresholdChangedEvent();
		}
	}
}
