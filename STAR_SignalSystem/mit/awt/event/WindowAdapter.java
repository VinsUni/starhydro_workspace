package mit.awt.event;

public class WindowAdapter implements java.awt.event.WindowListener
{
	private java.awt.event.WindowEvent windowEvent = null;

	public java.awt.event.WindowEvent getWindowEvent()
	{
		return this.windowEvent;
	}

	private mit.awt.event.WindowRaiser owner = null;

	public mit.awt.event.WindowRaiser getOwner()
	{
		return this.owner;
	}

	public WindowAdapter(mit.awt.event.WindowRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof java.awt.Window )
		{
			((java.awt.Window) owner).addWindowListener(this);
		}
	}

	public void windowActivated(java.awt.event.WindowEvent e)
	{
		setEvent(e);
	}

	public void windowClosed(java.awt.event.WindowEvent e)
	{
		setEvent(e);
	}

	public void windowClosing(java.awt.event.WindowEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				if( null != ((java.awt.Window) this.getOwner()).getParent() )
				{
					this.windowEvent = e;
					new mit.awt.event.WindowEvent(this.getOwner()).raise();
				}
				else
				{
					System.exit(0);
				}
			}
			else if( null == this.getOwner() )
			{
				System.exit(0);
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	public void windowDeactivated(java.awt.event.WindowEvent e)
	{
		setEvent(e);
	}

	public void windowDeiconified(java.awt.event.WindowEvent e)
	{
		setEvent(e);
	}

	public void windowIconified(java.awt.event.WindowEvent e)
	{
		setEvent(e);
	}

	public void windowOpened(java.awt.event.WindowEvent e)
	{
		setEvent(e);
	}

	private void setEvent(java.awt.event.WindowEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.windowEvent = e;
				new mit.awt.event.WindowEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}