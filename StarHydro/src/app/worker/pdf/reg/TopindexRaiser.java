package app.worker.pdf.reg;

import java.util.TreeMap;

import star.annotations.SignalComponent;
import star.hydrology.events.regionalization.RegTopindexPDFEvent;
import star.hydrology.events.regionalization.RegTopindexPDFRaiser;

@SignalComponent(raises = { RegTopindexPDFRaiser.class })
public class TopindexRaiser extends TopindexRaiser_generated
{
	private TreeMap<Integer, Float> data = null;

	void raise(TreeMap<Integer, Float> data)
	{
		this.data = data;
		(new RegTopindexPDFEvent(this)).raise();
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
