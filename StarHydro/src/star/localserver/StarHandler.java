package star.localserver;

import java.util.Map;


public interface StarHandler
{
	public boolean handle( String command , Map<String, String[]> params );
}
