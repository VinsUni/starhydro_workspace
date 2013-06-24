package starhydro.data.reader.srtm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

public class StarSRTMhgtSource extends AbstractSRTMhgtSource
{
	String prefix = "http://starapp.mit.edu/srtm_bz2" ;
	String listing = prefix + "/.listing.gz" ;
	TreeSet<String> files = null ;
	
	Set<String> getFiles()
	{
		if( files == null )
		{
			try
			{
			files = new TreeSet<String>();			
			BufferedReader reader = new BufferedReader( new InputStreamReader( new GZIPInputStream( new java.net.URL( listing ).openStream() ) ) ) ;
			String line ;
			while( (line = reader.readLine()) != null )
			{
				files.add( line ) ;
			}
			}
			catch( Exception ex)
			{
				ex.printStackTrace() ;
				files = null ;
			}
		}
		return files ;
	}

	java.net.URI getSRTM3URI( String filename ) throws URISyntaxException
	{
		Set<String> files = getFiles() ;
		for( String file : files )
		{
			if( file.startsWith( "./version2/SRTM3/" ) && file.contains(filename) )
			{
				String f2 = file ;
				while(! f2.startsWith( "/" ) )					
				{
					f2=f2.substring(1);
				}
				System.out.println( "SRTM Dataset: " + prefix+f2 );
				return new java.net.URI( prefix + f2 ) ;
			}
		}
		return null ;
	}
	java.net.URI getBestDatasetFile(int lng, int lat)
	{
		String filename = LngLat2FileName(lng, lat);
		try
		{
			java.net.URI file = getSRTM3URI(filename);
			if( file == null )
			{
				filename = filename.replace(".zip", ".bz2") ;
				file = getSRTM3URI(filename);
			}
			return file;
		}
		catch( Throwable t )
		{
			t.printStackTrace() ;
		}
		return null ;
	}

}
