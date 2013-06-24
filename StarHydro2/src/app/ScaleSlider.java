package app;

import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import starhydro.events.ViewScaleChangedRaiser;

@SignalComponent(extend = JSlider.class, raises = { ViewScaleChangedRaiser.class })
public class ScaleSlider extends ScaleSlider_generated implements ChangeListener
{
	private static final long serialVersionUID = 1L;
	private boolean internal = false;

	private Hashtable<Integer, JLabel> getLabels()
	{
		Hashtable<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
		for( int i = getMinimum() ; i <= getMaximum() ; i++ )
		{
			dict.put( i , new JLabel( getScaleString(i) )) ;
		}
		return dict;
	}
	
	private String getScaleString( int value )
	{
		NumberFormat f = NumberFormat.getInstance() ;
		f.setMinimumFractionDigits(0);
		double scale = getScaleFromValue(value);
		if( scale > 1 )
		{
			return "1:" + f.format(scale) ;
		}
		else
		{
			return f.format(1/scale) + ":1";
		}
	}
	private double getScaleFromValue( int value )
	{
		return Math.pow(2,value);
	}
	
	private int getValueFromScale( double scale )
	{
		return (int)Math.round(Math.log(scale)/Math.log(2));
	}

	public float getScale()
	{
		return (float) getScaleFromValue( getValue() ) ;
	}

	public void addNotify()
	{
		super.addNotify();
		setOrientation(JSlider.VERTICAL);
		setMaximum(9);
		setMinimum(-4);
		setValue(8);
		setLabelTable(getLabels());
		setMinorTickSpacing(1);
		setSnapToTicks(true);
		setPaintTicks(true);
		setPaintTrack(true);
		setPaintLabels(true);

		addChangeListener(this);
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run()
			{
				raise_ViewScaleChangedEvent();
			}
			
		}) ;
	}

	public void stateChanged(ChangeEvent e)
	{
		if( !internal && !getValueIsAdjusting() )
		{
			raise_ViewScaleChangedEvent();
		}
	}

	@Handles(raises = {})
	protected void handleScaleChange(ViewScaleChangedRaiser r)
	{
		System.out.println(r);
		float new_scale = r.getScale();
		if( new_scale != getScale() )
		{
			internal = true;
			setValueIsAdjusting(true);
			setValue(getValueFromScale(new_scale));
			setValueIsAdjusting(false);
			internal = false;
		}
	}

	public void rescale(int wheelRotation)
    {
		setValue(getValue()+wheelRotation);
    }

}
