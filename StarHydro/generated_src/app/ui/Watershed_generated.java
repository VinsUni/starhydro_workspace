/* Generated by star.annotations.GeneratedClass, all changes will be lost */

package app.ui;

public abstract class Watershed_generated extends javax.swing.JPanel implements star.event.EventController, star.event.GatedListener, star.event.Listener, star.hydrology.events.GridStatisticsProviderChangeRaiser, star.hydrology.events.Pick3DRaiser, star.hydrology.events.SelectWatershedOriginRaiser
{
	private star.event.Adapter adapter;
	private static final long serialVersionUID = 1L;

	public  Watershed_generated()
	{
		super();
	}
	 
	public  Watershed_generated(boolean boolean0)
	{
		super( boolean0);
	}
	 
	public  Watershed_generated(java.awt.LayoutManager layoutManager, boolean boolean0)
	{
		super( layoutManager,boolean0);
	}
	 
	public  Watershed_generated(java.awt.LayoutManager layoutManager)
	{
		super( layoutManager);
	}
	 
	public void addNotify()
	{
		super.addNotify();
		getAdapter().addHandled(star.hydrology.events.DrainageDensityAndStreamFrequencyEvent.class);
		getAdapter().addHandled(star.hydrology.events.StreamRootChangeEvent.class);
		getAdapter().addHandled(star.hydrology.events.map.WatershedLayerEvent.class);
		getAdapter().addHandled(star.hydrology.events.HypsometricCurveEvent.class);
		getAdapter().addHandled(star.hydrology.events.UnprojectedMapChangedEvent.class);
		getAdapter().addHandled(star.hydrology.events.Select3DWatershedOriginEvent.class);
		getAdapter().addHandled(star.hydrology.events.MainStreamLengthEvent.class);
		getAdapter().addHandled(star.hydrology.events.map.FilledMapLayerEvent.class);
		getAdapter().addHandled(star.hydrology.events.StreamOrderStatisticsEvent.class);
		getAdapter().addGatedAnd( new Class[]{ star.hydrology.events.AdjustableValueEvent.class,star.hydrology.events.map.FilledMapLayerEvent.class},new Class[]{},true);
		getAdapter().addGatedAnd( new Class[]{ star.hydrology.events.GridStatisticsProviderChangeEvent.class,star.hydrology.events.map.FilledMapLayerEvent.class},new Class[]{},true);
		getAdapter().addHandled(mit.awt.event.ActionEvent.class);
	}
	 
	private void eventAndGateRaisedHandles(final star.event.Event in_event[], final boolean valid)
	{
		final star.event.Event[] event = in_event;
		if( event != null && valid && event.length == 2 && event[0].getClass().getName().equals( "star.hydrology.events.AdjustableValueEvent" ) && event[1].getClass().getName().equals( "star.hydrology.events.map.FilledMapLayerEvent" ) &&  true )
		{
			
			utils.Runner.runOnThread(new Runnable() { 
				public void run() { 
					long start = System.nanoTime();
					initAccumulationTreshold( (star.hydrology.events.AdjustableValueRaiser)event[0].getSource(),(star.hydrology.events.map.FilledMapLayerRaiser)event[1].getSource() );
					long end = System.nanoTime();
				if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".initAccumulationTreshold "  + ( end-start )/1000000 ); } }},this,2);
		}
		if( event != null && valid && event.length == 2 && event[0].getClass().getName().equals( "star.hydrology.events.GridStatisticsProviderChangeEvent" ) && event[1].getClass().getName().equals( "star.hydrology.events.map.FilledMapLayerEvent" ) &&  true )
		{
			
			utils.Runner.runOnThread(new Runnable() { 
				public void run() { 
					long start = System.nanoTime();
					setAccumulationTreshold( (star.hydrology.events.GridStatisticsProviderChangeRaiser)event[0].getSource(),(star.hydrology.events.map.FilledMapLayerRaiser)event[1].getSource() );
					long end = System.nanoTime();
				if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".setAccumulationTreshold "  + ( end-start )/1000000 ); } }},this,2);
		}
	}
	 
	public void eventRaised(final star.event.Event event)
	{
		eventRaisedHandles(event);
	}
	 
	private void eventRaisedHandles(final star.event.Event event)
	{
		if( event.getClass().getName().equals( "star.hydrology.events.DrainageDensityAndStreamFrequencyEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			setDrainageDensity( (star.hydrology.events.DrainageDensityAndStreamFrequencyRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".setDrainageDensity "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.StreamRootChangeEvent" ) && !event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			resetArea( (star.hydrology.events.StreamRootChangeRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".resetArea "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.DrainageDensityAndStreamFrequencyEvent" ) && !event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			resetDrainageDensity( (star.hydrology.events.DrainageDensityAndStreamFrequencyRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".resetDrainageDensity "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.map.WatershedLayerEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			getWatershed( (star.hydrology.events.map.WatershedLayerRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".getWatershed "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.HypsometricCurveEvent" ) && !event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			resetHeights( (star.hydrology.events.HypsometricCurveRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".resetHeights "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.UnprojectedMapChangedEvent" ) && event.isValid() ) 
		{
			(new star.hydrology.events.SelectWatershedOriginEvent(this,false)).raise();
		 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			reset( (star.hydrology.events.UnprojectedMapChangedRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".reset "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.Select3DWatershedOriginEvent" ) && event.isValid() ) 
		{
			(new star.hydrology.events.SelectWatershedOriginEvent(this,false)).raise();
		 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			selectPoint( (star.hydrology.events.Select3DWatershedOriginRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".selectPoint "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.StreamRootChangeEvent" ) && !event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			resetStreamRoot( (star.hydrology.events.StreamRootChangeRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".resetStreamRoot "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.StreamRootChangeEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			setStreamRoot( (star.hydrology.events.StreamRootChangeRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".setStreamRoot "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.MainStreamLengthEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			setLength( (star.hydrology.events.MainStreamLengthRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".setLength "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.map.FilledMapLayerEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			getMap( (star.hydrology.events.map.FilledMapLayerRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".getMap "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.MainStreamLengthEvent" ) && !event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			resetLength( (star.hydrology.events.MainStreamLengthRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".resetLength "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.HypsometricCurveEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			setHeights( (star.hydrology.events.HypsometricCurveRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".setHeights "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.StreamOrderStatisticsEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			setTotalStreamLength( (star.hydrology.events.StreamOrderStatisticsRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".setTotalStreamLength "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "mit.awt.event.ActionEvent" ) && event.isValid() ) 
		{
			(new star.hydrology.events.Pick3DEvent(this,false)).raise();
		 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			select( (mit.awt.event.ActionRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".select "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.DrainageDensityAndStreamFrequencyEvent" ) && !event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			resetStreamFrequency( (star.hydrology.events.DrainageDensityAndStreamFrequencyRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".resetStreamFrequency "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.DrainageDensityAndStreamFrequencyEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			setStreamFrequency( (star.hydrology.events.DrainageDensityAndStreamFrequencyRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".setStreamFrequency "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.StreamRootChangeEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			setArea( (star.hydrology.events.StreamRootChangeRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".setArea "  + ( end-start )/1000000 ); } }},this,2);
		}
	}
	 
	public void eventsRaised(final star.event.Event event[], final boolean valid)
	{
		eventAndGateRaisedHandles(event,valid);
	}
	 
	public star.event.Adapter getAdapter()
	{
		if( adapter == null )
		{
			adapter = new star.event.Adapter(this);
		}
		return adapter;
	}
	 
	abstract void getMap(star.hydrology.events.map.FilledMapLayerRaiser r);
	 
	abstract void getWatershed(star.hydrology.events.map.WatershedLayerRaiser r);
	 
	abstract void initAccumulationTreshold(star.hydrology.events.AdjustableValueRaiser raiser, star.hydrology.events.map.FilledMapLayerRaiser map);
	 
	public void raise_GridStatisticsProviderChangeEvent()
	{
		(new star.hydrology.events.GridStatisticsProviderChangeEvent(this)).raise();
	}
	 
	public void raise_Pick3DEvent()
	{
		(new star.hydrology.events.Pick3DEvent(this)).raise();
	}
	 
	public void raise_SelectWatershedOriginEvent()
	{
		(new star.hydrology.events.SelectWatershedOriginEvent(this)).raise();
	}
	 
	public void removeNotify()
	{
		super.removeNotify();
		getAdapter().removeHandled(star.hydrology.events.DrainageDensityAndStreamFrequencyEvent.class);
		getAdapter().removeHandled(star.hydrology.events.StreamRootChangeEvent.class);
		getAdapter().removeHandled(star.hydrology.events.map.WatershedLayerEvent.class);
		getAdapter().removeHandled(star.hydrology.events.HypsometricCurveEvent.class);
		getAdapter().removeHandled(star.hydrology.events.UnprojectedMapChangedEvent.class);
		getAdapter().removeHandled(star.hydrology.events.Select3DWatershedOriginEvent.class);
		getAdapter().removeHandled(star.hydrology.events.MainStreamLengthEvent.class);
		getAdapter().removeHandled(star.hydrology.events.map.FilledMapLayerEvent.class);
		getAdapter().removeHandled(star.hydrology.events.StreamOrderStatisticsEvent.class);
		getAdapter().removeGatedAnd( new Class[]{ star.hydrology.events.AdjustableValueEvent.class,star.hydrology.events.map.FilledMapLayerEvent.class},new Class[]{});
		getAdapter().removeGatedAnd( new Class[]{ star.hydrology.events.GridStatisticsProviderChangeEvent.class,star.hydrology.events.map.FilledMapLayerEvent.class},new Class[]{});
		getAdapter().removeHandled(mit.awt.event.ActionEvent.class);
	}
	 
	abstract void reset(star.hydrology.events.UnprojectedMapChangedRaiser raiser);
	 
	abstract void resetArea(star.hydrology.events.StreamRootChangeRaiser raiser);
	 
	abstract void resetDrainageDensity(star.hydrology.events.DrainageDensityAndStreamFrequencyRaiser raiser);
	 
	abstract void resetHeights(star.hydrology.events.HypsometricCurveRaiser raiser);
	 
	abstract void resetLength(star.hydrology.events.MainStreamLengthRaiser raiser);
	 
	abstract void resetStreamFrequency(star.hydrology.events.DrainageDensityAndStreamFrequencyRaiser raiser);
	 
	abstract void resetStreamRoot(star.hydrology.events.StreamRootChangeRaiser raiser);
	 
	abstract void select(mit.awt.event.ActionRaiser raiser);
	 
	abstract void selectPoint(star.hydrology.events.Select3DWatershedOriginRaiser raiser);
	 
	abstract void setAccumulationTreshold(star.hydrology.events.GridStatisticsProviderChangeRaiser r, star.hydrology.events.map.FilledMapLayerRaiser map);
	 
	abstract void setArea(star.hydrology.events.StreamRootChangeRaiser raiser);
	 
	abstract void setDrainageDensity(star.hydrology.events.DrainageDensityAndStreamFrequencyRaiser raiser);
	 
	abstract void setHeights(star.hydrology.events.HypsometricCurveRaiser raiser);
	 
	abstract void setLength(star.hydrology.events.MainStreamLengthRaiser raiser);
	 
	abstract void setStreamFrequency(star.hydrology.events.DrainageDensityAndStreamFrequencyRaiser raiser);
	 
	abstract void setStreamRoot(star.hydrology.events.StreamRootChangeRaiser raiser);
	 
	abstract void setTotalStreamLength(star.hydrology.events.StreamOrderStatisticsRaiser r);
	 
}