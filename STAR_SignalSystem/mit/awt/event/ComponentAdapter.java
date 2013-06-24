package mit.awt.event;

public class ComponentAdapter implements java.awt.event.ComponentListener
{
	private java.awt.event.ComponentEvent componentEvent = null;

	public java.awt.event.ComponentEvent getComponentEvent()
	{
		return this.componentEvent;
	}

	private mit.awt.event.ComponentRaiser owner = null;

	public mit.awt.event.ComponentRaiser getOwner()
	{
		return this.owner;
	}

	public ComponentAdapter(mit.awt.event.ComponentRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof java.awt.Component )
		{
			((java.awt.Component) owner).addComponentListener(this);
		}
	}

	public void componentHidden(java.awt.event.ComponentEvent e)
	{
		setEvent(e);
	}

	public void componentMoved(java.awt.event.ComponentEvent e)
	{
		setEvent(e);
	}

	public void componentResized(java.awt.event.ComponentEvent e)
	{
		setEvent(e);
	}

	public void componentShown(java.awt.event.ComponentEvent e)
	{
		setEvent(e);
	}

	private void setEvent(java.awt.event.ComponentEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.componentEvent = e;
				new mit.awt.event.ComponentEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}