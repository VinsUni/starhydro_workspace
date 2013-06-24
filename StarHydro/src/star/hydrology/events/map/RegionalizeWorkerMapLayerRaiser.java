package star.hydrology.events.map;

import java.util.ArrayList;

import star.annotations.Raiser;
import app.worker.pdf.reg.Statistics;

@Raiser
public interface RegionalizeWorkerMapLayerRaiser extends MapLayerRaiser
{
	public ArrayList<Statistics> getRegionArray();
}
