package mit.awt.event;

public class MouseAdapter implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener
{
	private java.awt.event.MouseEvent mouseEvent = null;

	public java.awt.event.MouseEvent getMouseEvent()
	{
		return this.mouseEvent;
	}

	private mit.awt.event.MouseRaiser owner = null;

	public mit.awt.event.MouseRaiser getOwner()
	{
		return this.owner;
	}

	public MouseAdapter(mit.awt.event.MouseRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof java.awt.Component )
		{
			((java.awt.Component) owner).addMouseListener(this);
			((java.awt.Component) owner).addMouseMotionListener(this);
		}
	}

	public void mouseClicked(java.awt.event.MouseEvent e)
	{
		setEvent(e);
	}

	public void mouseEntered(java.awt.event.MouseEvent e)
	{
		setEvent(e);
	}

	public void mouseExited(java.awt.event.MouseEvent e)
	{
		setEvent(e);
	}

	public void mousePressed(java.awt.event.MouseEvent e)
	{
		setEvent(e);
	}

	public void mouseReleased(java.awt.event.MouseEvent e)
	{
		setEvent(e);
	}

	public void mouseDragged(java.awt.event.MouseEvent e)
	{
		setEvent(e);
	}

	public void mouseMoved(java.awt.event.MouseEvent e)
	{
		setEvent(e);
	}

	private void setEvent(java.awt.event.MouseEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.mouseEvent = e;
				new mit.awt.event.MouseEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}