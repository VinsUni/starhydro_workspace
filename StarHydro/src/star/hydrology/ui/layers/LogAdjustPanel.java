package star.hydrology.ui.layers;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import mit.swing.xJLabel;
import mit.swing.xJPanel;
import mit.swing.xJSlider;
import mit.swing.event.ChangeEvent;
import mit.swing.event.ChangeRaiser;
import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.AdjustableValue;
import star.hydrology.events.AdjustableValueRaiser;
import star.hydrology.events.GridStatisticsProviderChangeEvent;
import star.hydrology.events.GridStatisticsProviderChangeRaiser;
import star.j3d.ui.ComponentWrapper;
import utils.Format;

@SignalComponent(extend = xJPanel.class, excludeExternal = { ChangeEvent.class })
public class LogAdjustPanel extends LogAdjustPanel_generated
{

	private static final long serialVersionUID = 1L;

	private xJSlider valueSelector = null;
	private xJLabel label = null;
	private float current = 0;
	private int kind;
	private boolean skip = false;
	private float minimum;
	private float maximum;
	private String text;
	private boolean paintLabel = false;

	public LogAdjustPanel(int kind, boolean paintLabel)
	{
		super();
		this.kind = kind;
		this.paintLabel = paintLabel;
	}

	public LogAdjustPanel(int kind)
	{
		this(kind, true);
	}

	public void addNotify()
	{
		super.addNotify();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		if (paintLabel)
		{
			label = new xJLabel();
			label.setText(getText());
			add(label);
		}
		valueSelector = new xJSlider();
		valueSelector.setOrientation(xJSlider.HORIZONTAL);
		add(valueSelector);
		ComponentWrapper.setBackground(this);
		// updateSelector();
	}

	private Dictionary<Integer, JLabel> getDictionary()
	{
		Dictionary<Integer, JLabel> d = new Hashtable<Integer, JLabel>();
		if (getMaximum() > 1000 && getMinimum() >= 1)
		{
			for (int i = 0; i <= 100; i += 25)
			{
				d.put(new Integer(i), new JLabel(Integer.toString((int) getValue(i))));
			}
		}
		else
		{
			for (int i = 0; i <= 100; i += 25)
			{
				d.put(new Integer(i), new JLabel(Format.formatNumber(getValue(i))));
			}
		}
		return d;
	}

	private void updateSelector(boolean raise)
	{
		if (getText() != null && label != null)
		{
			label.setText(getText());
		}
		if (getValueSelector() != null)
		{
			skip = true;
			float p = (float) ((Math.log(getCurrent()) - Math.log(getMinimum())) / (Math.log(getMaximum()) - Math.log(getMinimum())));
			if (Float.isNaN(p))
			{
				p = 0;
			}
			p = p < 0 ? 0 : p;
			p = p > 1 ? 1 : p;
			p = 100 * p;
			getValueSelector().getModel().setRangeProperties((int) p, 1, 0, 100, true);
			getValueSelector().setLabelTable(getDictionary());
			getValueSelector().setMajorTickSpacing(25);
			getValueSelector().setMinorTickSpacing(5);
			getValueSelector().setPaintTicks(true);
			getValueSelector().setPaintLabels(paintLabel);
			if (raise)
			{
				(new GridStatisticsProviderChangeEvent(this)).raise();
			}
			skip = false;
		}
	}

	/**
	 * @return Returns the current.
	 */
	public float getCurrent()
	{
		return current;
	}

	/**
	 * @param current
	 *            The current to set.
	 */
	private void setCurrent(float current)
	{
		this.current = current;
	}

	/**
	 * @return Returns the valueSelector.
	 */
	private xJSlider getValueSelector()
	{
		return valueSelector;
	}

	private void updateValue()
	{
		if (getValueSelector() != null)
		{
			float value = getValue(getValueSelector().getValue());
			setCurrent(value);
			(new GridStatisticsProviderChangeEvent(this)).raise();
		}

	}

	private float getValue(int valueSelectorValue)
	{
		return (float) Math.exp(valueSelectorValue * (Math.log(getMaximum()) - Math.log(getMinimum())) / 100 + Math.log(getMinimum()));
	}

	public int getKind()
	{
		return kind;
	}

	public float getMaximum()
	{
		return maximum;
	}

	public void setMaximum(float maximum)
	{
		this.maximum = maximum;
	}

	public float getMinimum()
	{
		return minimum;
	}

	public void setMinimum(float minimum)
	{
		this.minimum = minimum;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	@Handles(raises = { GridStatisticsProviderChangeRaiser.class })
	void handleEvent(ChangeRaiser raiser)
	{
		if (!skip && !valueSelector.getModel().getValueIsAdjusting())
		{
			updateValue();
		}
	}

	@Handles(raises = {})
	void handleEvent(AdjustableValueRaiser raiser)
	{
		if (raiser instanceof AdjustableValueRaiser && raiser.getKind() == getKind())
		{
			AdjustableValue r = (AdjustableValue) raiser;
			float delta = .1f;
			if ((r.getAdjustableMaximum() - getMaximum()) > delta || (r.getAdjustableMinimum() - getMaximum()) > delta || (r.getAdjustableValue() - getCurrent()) > delta)
			{
				setMaximum(r.getAdjustableMaximum());
				setMinimum(r.getAdjustableMinimum());
				setCurrent(r.getAdjustableValue());
				setText(r.getAdjustableName());
				updateSelector(true);
				setEnabled(true);
			}
		}
	}

	public void updateCurrent(float value)
	{
		setCurrent(value);
		if (getCurrent() > getMaximum())
		{
			setMaximum(getCurrent());
		}
		if (getCurrent() < getMinimum())
		{
			setMinimum(getCurrent());
		}
		updateSelector(false);
	}
}
