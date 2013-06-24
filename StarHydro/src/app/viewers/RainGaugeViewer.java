package app.viewers;

import star.annotations.SignalComponent;
import star.hydrology.events.interfaces.LayerConstants;

@SignalComponent(extend = AbstractMapViewer.class)
public class RainGaugeViewer extends RainGaugeViewer_generated
{
	@Override
	public int getKind()
	{
		return LayerConstants.RAINGAUGELAYER;
	}

	public int getLayerKind()
	{

		return getKind();
	}

	public String getViewerName()
	{
		return "Rain gauges";
	}

}
