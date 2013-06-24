package starhydro.data.reader.srtm;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class SRTMhgtReader
{
	public float[] read1D(InputStream is, int size)
	{
		float[] data = new float[size];
		try
		{
			BufferedInputStream bis = new BufferedInputStream(is, 65536);
			byte[] b = new byte[2];
			int i = 0;
			while( i < data.length )
			{
				int bytes = bis.read(b);
				if( bytes != 2 )
				{
					break;
				}
				int elevation = ((b[0] & 0xff) << 8) | (b[1] & 0xff);
				if( 32768 == elevation )
				{
					data[i] = Float.NaN;
				}
				else if( elevation > 32768 )
				{
					data[i] = elevation - 65536;
				}
				else
				{
					data[i] = elevation;
				}
				i++;
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			data = null;
		}
		return data;
	}

}
