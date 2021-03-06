/* Generated by star.annotations.GeneratedClass, all changes will be lost */

package app.worker.pdf;

public abstract class TopIndexPDFWorker_generated extends java.lang.Object implements star.event.EventController, star.event.GatedListener, star.hydrology.events.TopIndexPDFRaiser, star.hydrology.events.map.TopindexPDFMapLayerRaiser
{
	private star.event.Adapter adapter;
	private star.hydrology.events.interfaces.PaletteRenderableLayer flowAcc;
	private star.hydrology.events.interfaces.PaletteRenderableLayer flowDir;
	private star.hydrology.events.interfaces.PaletteRenderableLayer layer;
	private static final long serialVersionUID = 1L;
	private star.hydrology.events.interfaces.PaletteRenderableLayer watershed;
	private java.awt.Point watershedOrigin;

	public  TopIndexPDFWorker_generated()
	{
		super();
	}
	 
	public void addNotify()
	{
		getAdapter().addGatedAnd( new Class[]{ star.hydrology.events.map.FlowaccumulationMapLayerEvent.class,star.hydrology.events.map.FlowdirectionMapLayerEvent.class,star.hydrology.events.map.WatershedLayerEvent.class},new Class[]{ star.hydrology.events.TopIndexPDFEvent.class,star.hydrology.events.map.TopindexPDFMapLayerEvent.class},true);
	}
	 
	abstract void calculateTopIndex(star.hydrology.events.map.FlowaccumulationMapLayerRaiser flowAcc, star.hydrology.events.map.FlowdirectionMapLayerRaiser flowDir, star.hydrology.events.map.WatershedLayerRaiser watershed);
	 
	private void eventAndGateRaisedHandles(final star.event.Event in_event[], final boolean valid)
	{
		final star.event.Event[] event = in_event;
		if( event != null && valid && event.length == 3 && event[0].getClass().getName().equals( "star.hydrology.events.map.FlowaccumulationMapLayerEvent" ) && event[1].getClass().getName().equals( "star.hydrology.events.map.FlowdirectionMapLayerEvent" ) && event[2].getClass().getName().equals( "star.hydrology.events.map.WatershedLayerEvent" ) &&  true )
		{
			(new star.hydrology.events.TopIndexPDFEvent(this,false)).raise();
		(new star.hydrology.events.map.TopindexPDFMapLayerEvent(this,false)).raise();
			utils.Runner.runOnThread(new Runnable() { 
				public void run() { 
					long start = System.nanoTime();
					calculateTopIndex( (star.hydrology.events.map.FlowaccumulationMapLayerRaiser)event[0].getSource(),(star.hydrology.events.map.FlowdirectionMapLayerRaiser)event[1].getSource(),(star.hydrology.events.map.WatershedLayerRaiser)event[2].getSource() );
					long end = System.nanoTime();
				if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".calculateTopIndex "  + ( end-start )/1000000 ); } }},this,1);
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
	 
	protected star.hydrology.events.interfaces.PaletteRenderableLayer getFlowAcc()
	{
		return this.flowAcc ;
	}
	 
	protected star.hydrology.events.interfaces.PaletteRenderableLayer getFlowDir()
	{
		return this.flowDir ;
	}
	 
	public star.hydrology.events.interfaces.PaletteRenderableLayer getLayer()
	{
		return this.layer ;
	}
	 
	protected star.hydrology.events.interfaces.PaletteRenderableLayer getWatershed()
	{
		return this.watershed ;
	}
	 
	public java.awt.Point getWatershedOrigin()
	{
		return this.watershedOrigin ;
	}
	 
	public void raise_TopIndexPDFEvent()
	{
		(new star.hydrology.events.TopIndexPDFEvent(this)).raise();
	}
	 
	public void raise_TopindexPDFMapLayerEvent()
	{
		(new star.hydrology.events.map.TopindexPDFMapLayerEvent(this)).raise();
	}
	 
	public void removeNotify()
	{
		getAdapter().removeGatedAnd( new Class[]{ star.hydrology.events.map.FlowaccumulationMapLayerEvent.class,star.hydrology.events.map.FlowdirectionMapLayerEvent.class,star.hydrology.events.map.WatershedLayerEvent.class},new Class[]{ star.hydrology.events.TopIndexPDFEvent.class,star.hydrology.events.map.TopindexPDFMapLayerEvent.class});
	}
	 
	protected void setFlowAcc(star.hydrology.events.interfaces.PaletteRenderableLayer flowAcc)
	{
		this.flowAcc = flowAcc ;
	}
	 
	protected void setFlowDir(star.hydrology.events.interfaces.PaletteRenderableLayer flowDir)
	{
		this.flowDir = flowDir ;
	}
	 
	protected void setLayer(star.hydrology.events.interfaces.PaletteRenderableLayer layer)
	{
		this.layer = layer ;
	}
	 
	protected void setWatershed(star.hydrology.events.interfaces.PaletteRenderableLayer watershed)
	{
		this.watershed = watershed ;
	}
	 
	protected void setWatershedOrigin(java.awt.Point watershedOrigin)
	{
		this.watershedOrigin = watershedOrigin ;
	}
	 
}