package app.viewers;

import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.events.RenderableVisibleRaiser;
import star.hydrology.events.interfaces.PaletteRenderableLayer;

@SignalComponent(extend = AbstractMapViewer.class, raises = { RenderableVisibleRaiser.class })
public class PDFViewer extends PDFViewer_generated
{
	int layerConstant = -1;
	String layerName;

	public PDFViewer(int layerConstant, String layerName)
	{
		this.layerConstant = layerConstant;
		this.layerName = layerName;
	}

	Grid getGrid()
	{
		return getLayer().getDataset();
	}

	public PaletteRenderableLayer getLayer()
	{
		return getTerrain();
	}

	public int getKind()
	{
		return layerConstant;
	}

	public String getViewerName()
	{
		return layerName;
	}
	
	@Override
	public int hashCode()
	{
	    return layerConstant ^ this.getClass().hashCode() ;
	}
}
