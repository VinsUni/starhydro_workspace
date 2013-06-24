package app.worker.pdf.reg;

import java.util.TreeMap;

import star.annotations.SignalComponent;
import star.hydrology.events.regionalization.RegSlopePDFEvent;
import star.hydrology.events.regionalization.RegSlopePDFRaiser;

@SignalComponent(raises = { RegSlopePDFRaiser.class })
public class SlopeRaiser extends SlopeRaiser_generated
{

	TreeMap<Integer, Float> data = null;

	public void raise(TreeMap<Integer, Float> data)
	{
		this.data = data;
		(new RegSlopePDFEvent(this)).raise();
	}

	public float getMinimumHeight()
	{
		return data.firstKey();
	}

	public float getMaximumHeight()
	{
		return data.lastKey();
	}

	public float getPDF(float height)
	{
		Float f = data.get((int) height);
		if (f != null)
		{
			return f.floatValue();
		}
		else
		{
			return Float.NaN;
		}
	}
}
