package star.hydrology.ui.selectors;

import star.hydrology.data.interfaces.Projection;
import star.hydrology.events.ProjectionChangedEvent;
import star.hydrology.events.ProjectionChangedRaiser;

public class ProjectionSelector extends ComboBoxSelector implements ProjectionChangedRaiser
{
	private static final long serialVersionUID = 1L;

	private Projection projection;

	public ProjectionSelector()
	{
		super();
	}

	public void setProjection(Projection projection)
	{
		this.projection = projection;
	}

	public Projection getProjection()
	{
		return projection;
	}

	void raiseEvent(Object item)
	{
		this.setProjection((Projection) item);
		(new ProjectionChangedEvent(this)).raise();

	}

}
