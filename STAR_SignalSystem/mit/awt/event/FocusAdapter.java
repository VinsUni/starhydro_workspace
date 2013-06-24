package mit.awt.event;

public class FocusAdapter implements java.awt.event.FocusListener
{
	private java.awt.event.FocusEvent focusEvent = null;

	public java.awt.event.FocusEvent getFocusEvent()
	{
		return this.focusEvent;
	}

	private mit.awt.event.FocusRaiser owner = null;

	public mit.awt.event.FocusRaiser getOwner()
	{
		return this.owner;
	}

	public FocusAdapter(mit.awt.event.FocusRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof java.awt.Component )
		{
			((java.awt.Component) owner).addFocusListener(this);
		}
	}

	public void focusGained(java.awt.event.FocusEvent e)
	{
		setEvent(e);
	}

	public void focusLost(java.awt.event.FocusEvent e)
	{
		setEvent(e);
	}

	private void setEvent(java.awt.event.FocusEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.focusEvent = e;
				new mit.awt.event.FocusEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}
