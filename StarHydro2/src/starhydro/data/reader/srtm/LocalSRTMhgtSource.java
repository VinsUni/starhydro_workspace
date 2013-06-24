package starhydro.data.reader.srtm;


public class LocalSRTMhgtSource extends AbstractSRTMhgtSource
{
	String prefix = "C:\\gis\\srtm\\e0srp01u.ecs.nasa.gov\\srtm\\version2\\";

	java.io.File getSRTM1File(String filename)
	{
		for (int i = 1; i <= 7; i++)
		{
			java.io.File f = new java.io.File(prefix + "SRTM1\\Region_0" + i + "\\" + filename);
			if( f.exists() )
			{
				return f;
			}
		}
		return null;
	}

	java.io.File getSRTM3File(String filename)
	{
		java.io.File file = new java.io.File(prefix + "SRTM3");
		for (java.io.File folder : file.listFiles())
		{
			java.io.File f = new java.io.File(folder, filename);
			if( f.exists() )
			{
				return f;
			}
		}
		return null;
	}

	java.net.URI getBestDatasetFile(int lng, int lat)
	{
		String filename = LngLat2FileName(lng, lat);
		java.io.File file = getSRTM3File(filename);
		return file.toURI();
	}


}
