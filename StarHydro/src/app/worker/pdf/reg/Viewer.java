package app.worker.pdf.reg;

import star.annotations.SignalComponent;
import star.hydrology.events.interfaces.LayerConstants;
import app.viewers.AbstractMapViewer;

@SignalComponent(extend = AbstractMapViewer.class)
public class Viewer extends Viewer_generated
{
	public int getKind()
	{
		return LayerConstants.REGTREELAYER;
	}

	public String getViewerName()
	{
		return "Regionalization tree layer";
	}

}
