package star.hydrology.data.layers;

import java.awt.Color;

import star.hydrology.data.interfaces.Grid;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.ui.palette.Palette;

public abstract class AbstractGrid implements Grid
{
	private PaletteRenderableLayer layer;

	private PaletteRenderableLayer getElevationLayer()
	{
		return layer;
	}

	public AbstractGrid(PaletteRenderableLayer layer)
	{
		super();
		this.layer = layer;
		init();
	}

	public int getRows()
	{
		return getElevationLayer().getDataset().getRows();
	}

	public int getCols()
	{
		return getElevationLayer().getDataset().getCols();
	}

	public float getElementAt(int x, int y)
	{
		return isShowPoint(x, y) ? getElevationLayer().getDataset().getElementAt(x, y) : Float.NaN;
	}
	
	public Color getColorAt(int x, int y, Palette p)
	{
		return isShowPoint(x, y) ? getElevationLayer().getDataset().getColorAt(x, y, p) : p.getColor(0);
	}

	protected abstract boolean isShowPoint(int x, int y);

	protected abstract void init();

}
