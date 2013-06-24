package star.hydro.rainfall.local;

import java.io.File;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.JComponent;

import star.hydro.rainfall.TimeSeries;
import edu.mit.star.plugins.filemanager.helpers.Project;
import edu.mit.star.plugins.filemanager.interfaces.AbstractFileProvider;
import edu.mit.star.plugins.filemanager.interfaces.AccessoryProvider;
import edu.mit.star.plugins.filemanager.interfaces.ProjectException;
import edu.mit.star.plugins.filemanager.interfaces.ProjectProvider;

public class TimeSeriesWrapper extends AbstractFileProvider implements AccessoryProvider, ProjectProvider, Project
{
	private static final long serialVersionUID = 1L;
	private TimeSeriesWrapperAccessory accessory = null;
	private TimeSeries series;

	public TimeSeriesWrapper(File parent, String file, TimeSeries series)
	{
		super(parent, file);
		this.series = series;
	}

	public JComponent getAccessory()
	{
		if (accessory == null)
		{
			accessory = new TimeSeriesWrapperAccessory(this);
		}
		return accessory;
	}

	public Icon getIcon()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getTypeDescription()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public TimeSeries getTimeSeries()
	{
		return series;
	}

	public Project getParsedObject()
	{
		return this;
	}

	public Object getCachedInfo()
	{
		return this;
	}

	public String getDescription()
	{
		return getName();
	}

	public Object getParsed() throws ProjectException
	{
		return series;
	}

	public InputStream getSource() throws ProjectException
	{
		return null;
	}

	public String getSourceName()
	{
		return getName();
	}

}
