package app.worker.pdf.reg;

import java.util.TreeMap;

import star.annotations.SignalComponent;
import star.hydrology.events.regionalization.RegCurvaturePDFEvent;
import star.hydrology.events.regionalization.RegCurvaturePDFRaiser;

@SignalComponent(raises = { RegCurvaturePDFRaiser.class })
public class CurvatureRaiser extends CurvatureRaiser_generated
{

	TreeMap<Integer, Float> data = null;

	public void raise(TreeMap<Integer, Float> data)
	{
		this.data = data;
		(new RegCurvaturePDFEvent(this)).raise();
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
