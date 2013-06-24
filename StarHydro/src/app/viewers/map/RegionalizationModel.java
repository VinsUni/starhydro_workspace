package app.viewers.map;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import star.hydrology.events.map.RegionalizationRaiser;

public class RegionalizationModel implements Cloneable
{

	private static final long serialVersionUID = 1L;
	private HashMap<RegionalizationRaiser.Parameters, Float> values = new HashMap<RegionalizationRaiser.Parameters, Float>();
	private HashMap<RegionalizationRaiser.Parameters, Boolean> enabled = new HashMap<RegionalizationRaiser.Parameters, Boolean>();
	private HashMap<RegionalizationRaiser.Parameters, TreeMap> maps = new HashMap<RegionalizationRaiser.Parameters, TreeMap>();

	public void putMap(RegionalizationRaiser.Parameters param, TreeMap value)
	{
		maps.put(param, value);
	}

	public TreeMap getMap(RegionalizationRaiser.Parameters param)
	{
		return maps.get(param);
	}

	public Float setValue(RegionalizationRaiser.Parameters key, Float value)
	{
		return values.put(key, value);
	}

	public Float put(RegionalizationRaiser.Parameters key, Float value)
	{
		return setValue(key, value);
	}

	public Set<RegionalizationRaiser.Parameters> keySet()
	{
		return values.keySet();
	}

	public Float get(RegionalizationRaiser.Parameters key)
	{
		Float ret = getValue(key);
		if (ret == null)
		{
			return new Float(0);
		}
		return ret;
	}

	public Float getValue(RegionalizationRaiser.Parameters key)
	{
		return values.get(key);
	}

	public Boolean setEnabled(RegionalizationRaiser.Parameters key, Boolean value)
	{
		return enabled.put(key, value);
	}

	public boolean getEnabled(RegionalizationRaiser.Parameters key)
	{
		return enabled.get(key);
	}

	@SuppressWarnings("unchecked")
	public Object clone()
	{
		RegionalizationModel ret = new RegionalizationModel();
		ret.values = (HashMap<RegionalizationRaiser.Parameters, Float>) values.clone();
		ret.enabled = (HashMap<RegionalizationRaiser.Parameters, Boolean>) enabled.clone();
		return ret;
	}
}
