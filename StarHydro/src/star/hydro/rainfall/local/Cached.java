package star.hydro.rainfall.local;

import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JComponent;

import star.hydro.rainfall.TimeSeries;
import edu.mit.star.plugins.filemanager.helpers.Folder;
import edu.mit.star.plugins.filemanager.interfaces.AbstractFileProvider;
import edu.mit.star.plugins.filemanager.interfaces.AccessoryProvider;

public class Cached extends Folder implements AccessoryProvider
{

	private CachedAccessory accessory = null;
	private static final long serialVersionUID = 1L;

	public Cached(AbstractFileProvider parent, String name)
	{
		super(parent, name);
	}

	private TimeSeries getTime(int count, int duration, int spacing, float high)
	{
		TimeSeries time = new TimeSeries();
		int total = duration + spacing;
		time.add(-1f, 0);
		for (int i = 0; i < count; i++)
		{
			for (int j = 0; j < duration; j++)
			{
				time.add(i * total + j, 1);
			}
			for (int j = 0; j < spacing; j++)
			{
				time.add(i * total + duration + j, 0);
			}
		}
		time.add(count * total + 1, 0);
		return time;
	}

	private TimeSeries pulse()
	{
		TimeSeries time = new TimeSeries();
		time.add(-1f, 0);
		time.add(0, 12);
		time.add(1, 22);
		time.add(2, 8);
		time.add(3, 0);
		return time;
	}

	public File[] listFiles()
	{
		ArrayList<File> list = new ArrayList<File>();
		list.add(new TimeSeriesWrapper(this, "Single short storm (1h)", getTime(1, 1, 1, 12)));
		list.add(new TimeSeriesWrapper(this, "Single long storm (32h)", getTime(1, 32, 0, 12)));
		list.add(new TimeSeriesWrapper(this, "Pulse storm", pulse()));

		return list.toArray(new File[0]);
	}

	public JComponent getAccessory()
	{
		if (accessory == null)
		{
			accessory = new CachedAccessory(this);
		}
		return accessory;
	}

	private void reload()
	{
		propertyChangeSupport.firePropertyChange(ADD_CHILD, null, null);
	}

	public Icon getIcon()
	{
		return utils.Icon.getIcon(this, utils.Icon.WORLD_JAR);
	}

}
