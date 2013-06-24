package app.worker.relief;

import java.awt.Point;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydro.rainfall.TimeSeries;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.events.SlopeAreaEvent;
import star.hydrology.events.SlopeAreaRaiser;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.SlopePDFMapLayerRaiser;
import star.hydrology.events.map.WatershedLayerRaiser;

@SignalComponent()
@Properties( { @Property(name = "watershed", type = PaletteRenderableLayer.class), @Property(name = "slope", type = PaletteRenderableLayer.class), @Property(name = "flowAcc", type = PaletteRenderableLayer.class), @Property(name = "origin", type = Point.class),
        @Property(name = "dataset", type = TimeSeries.class, getter = Property.PUBLIC) })
public class SlopeAreaWorker extends SlopeAreaWorker_generated
{
	@Handles(raises = { SlopeAreaRaiser.class })
	void calculateSlopeArea(WatershedLayerRaiser watershed, SlopePDFMapLayerRaiser slope, FlowaccumulationMapLayerRaiser flowAcc)
	{
		setWatershed(watershed.getLayer());
		setSlope(slope.getLayer());
		setFlowAcc(flowAcc.getLayer());
		setOrigin(watershed.getWatershedOrigin());
		calculate();
		(new SlopeAreaEvent(this)).raise();
	}

	private void calculate()
	{

		Grid dataset = getWatershed().getDataset();
		Grid flowAcc = getFlowAcc().getDataset();
		Grid slopeDS = getSlope().getDataset();
		TimeSeries ts = new TimeSeries();
		int rows = dataset.getRows();
		int cols = dataset.getCols();
		float cellSize = dataset.getCellsize();
		float max = rows * cols;
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{
				float height = dataset.getElementAt(x, y);
				float accomulation = flowAcc.getElementAt(x, y);
				float slope = slopeDS.getElementAt(x, y);
				if (!Float.isNaN(height) && accomulation > 0 && !Float.isNaN(accomulation))
				{
					ts.add(accomulation * cellSize * cellSize + (x + y * cols) / max / 100, slope);
				}
			}
		}
		setDataset(ts);
	}

}
