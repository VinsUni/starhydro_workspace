package star.junit.event;

import java.util.HashMap;

import junit.framework.TestCase;
import star.event.Adapter;
import star.event.Event;
import star.event.Listener;
import star.junit.event.eventraiser.TestEvent;
import star.junit.event.eventraiser.TestRaiser;

public class EventRaiser extends TestCase implements Listener
{

	public void eventRaised(Event event)
    {
		System.out.println( event + " " + event.getSource() ) ;
    }

	public void addNotify()
    {
		
    }
	Adapter adapter = new Adapter( this ) ;
	public Adapter getAdapter()
    {
	    return adapter;
    }

	public void removeNotify()
    {
	    
    }
	
	@Override
	protected void setUp() throws Exception
	{
	    super.setUp();
		getAdapter().addHandled( TestEvent.class );
	}
	
	@Override
	protected void tearDown() throws Exception
	{
	    super.tearDown();
		getAdapter().removeHandled( TestEvent.class );
	}

	public void test1()
	{
		star.event.EventRaiser r = new star.event.EventRaiser() ;
		try
        {
			HashMap<String, Object> data = new HashMap<String, Object>();
	        r.raiseDataLessEvent( TestRaiser.class , this );
	        r.raiseDataEvent( TestRaiser.class , this , data );
        }
        catch( Throwable e )
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
	}
}
