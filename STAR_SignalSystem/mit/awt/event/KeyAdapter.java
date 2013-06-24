package mit.awt.event;

public class KeyAdapter implements java.awt.event.KeyListener
{
	private java.awt.event.KeyEvent keyEvent = null;

	public java.awt.event.KeyEvent getKeyEvent()
	{
		return this.keyEvent;
	}

	private mit.awt.event.KeyRaiser owner = null;

	public mit.awt.event.KeyRaiser getOwner()
	{
		return this.owner;
	}

	public KeyAdapter(mit.awt.event.KeyRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof java.awt.Component )
		{
			((java.awt.Component) owner).addKeyListener(this);
		}
	}

	public void keyPressed(java.awt.event.KeyEvent e)
	{
		setEvent(e);
	}

	public void keyReleased(java.awt.event.KeyEvent e)
	{
		setEvent(e);
	}

	public void keyTyped(java.awt.event.KeyEvent e)
	{
		setEvent(e);
	}

	private void setEvent(java.awt.event.KeyEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.keyEvent = e;
				new mit.awt.event.KeyEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}