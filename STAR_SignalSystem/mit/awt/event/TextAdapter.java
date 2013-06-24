package mit.awt.event;

public class TextAdapter implements java.awt.event.TextListener
{
	private java.awt.event.TextEvent textEvent = null;

	public java.awt.event.TextEvent getTextEvent()
	{
		return this.textEvent;
	}

	private mit.awt.event.TextRaiser owner = null;

	public mit.awt.event.TextRaiser getOwner()
	{
		return this.owner;
	}

	public TextAdapter(mit.awt.event.TextRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof java.awt.TextComponent )
		{
			((java.awt.TextComponent) owner).addTextListener(this);
		}
	}

	public void textValueChanged(java.awt.event.TextEvent e)
	{
		setEvent(e);
	}

	private void setEvent(java.awt.event.TextEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.textEvent = e;
				new mit.awt.event.TextEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}