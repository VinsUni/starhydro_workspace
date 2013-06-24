package starhydro.algorithms;

import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;

import star.annotations.SignalComponent;
import star.event.EventController;
import starhydro.events.BackgroundProcessRaiser;

@SignalComponent( raises={BackgroundProcessRaiser.class} )
public class BackgroundProcess extends BackgroundProcess_generated
{
	private SwingWorker worker;
	private String name ;
	@Override
	public void addNotify()
	{
	    super.addNotify();
	    SwingUtilities.invokeLater( new Runnable() {

			public void run()
            {
				raise_BackgroundProcessEvent();	            
            }} );
	}
	
	public BackgroundProcess( EventController parent , SwingWorker worker , String name )
	{
		parent.getAdapter().addComponent( this ) ;
		this.worker = worker ;
		this.name = name ;
	}

	public SwingWorker getSwingWorker()
    {
	    return worker;
    }
	
	public String getName() 
	{
		return name ;
	}
}
