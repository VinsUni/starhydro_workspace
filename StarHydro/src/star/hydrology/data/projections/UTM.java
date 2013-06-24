package star.hydrology.data.projections;

import star.hydrology.data.interfaces.Projection;

public class UTM implements Projection
{

	private static final long serialVersionUID = 1L;

	private final static int AUTOMATIC = 0;

	private int zone;

	public UTM(int zone)
	{
		super();
		this.zone = zone;
	}

	public String getName()
	{
		return "UTM";
	}

	public int getZone()
	{
		return zone;
	}

	public String toString()
	{
		return getName() + " " + (getZone() != 0 ? String.valueOf(zone) : "");
	}

	public String getProjectionParameter(String inputParameters)
	{
		StringBuffer ret = new StringBuffer();
		ret.append("PROJECTION UTM" + nl());
		if (inputParameters.toLowerCase().indexOf("datum") != -1)
		{
			ret.append("DATUM NAD83" + nl());
		}
		if (getZone() != AUTOMATIC)
		{
			ret.append("ZONE " + getZone() + nl());
		}
		else
		{
			ret.append("ZONE 18" + nl());
		}
		ret.append("ZUNITS METERS" + nl());
		ret.append("UNITS METERS" + nl());
		ret.append("PARAMETERS" + nl());
		return ret.toString();
	}

	private String nl()
	{
		return "\r\n";
	}
}
