package mit.awt.event;

public class ContainerAdapter implements java.awt.event.ContainerListener
{
	private java.awt.event.ContainerEvent containerEvent = null;

	public java.awt.event.ContainerEvent getContainerEvent()
	{
		return this.containerEvent;
	}

	private mit.awt.event.ContainerRaiser owner = null;

	public mit.awt.event.ContainerRaiser getOwner()
	{
		return this.owner;
	}

	public ContainerAdapter(mit.awt.event.ContainerRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof java.awt.Container )
		{
			((java.awt.Container) owner).addContainerListener(this);
		}
	}

	public void componentAdded(java.awt.event.ContainerEvent e)
	{
		setEvent(e);
	}

	public void componentRemoved(java.awt.event.ContainerEvent e)
	{
		setEvent(e);
	}

	private void setEvent(java.awt.event.ContainerEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.containerEvent = e;
				new mit.awt.event.ContainerEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}