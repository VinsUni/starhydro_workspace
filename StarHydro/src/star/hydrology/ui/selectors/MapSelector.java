package star.hydrology.ui.selectors;

import star.hydrology.events.UnprojectedMapChangedEvent;
import star.hydrology.events.UnprojectedMapChangedRaiser;
import star.hydrology.events.interfaces.UnprojectedMap;

public class MapSelector extends ComboBoxSelector implements UnprojectedMapChangedRaiser
{
	private static final long serialVersionUID = 1L;

	private UnprojectedMap map;

	public MapSelector()
	{
		super();
	}

	void raiseEvent(Object item)
	{
		if (item instanceof UnprojectedMap)
		{
			map = (UnprojectedMap) item;
			(new UnprojectedMapChangedEvent(this)).raise();
		}
	}

	public UnprojectedMap getMap()
	{
		return map;
	}

}
