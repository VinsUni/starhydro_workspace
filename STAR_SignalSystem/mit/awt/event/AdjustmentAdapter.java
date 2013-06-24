package mit.awt.event;

public class AdjustmentAdapter implements java.awt.event.AdjustmentListener
{
	private java.awt.event.AdjustmentEvent adjustmentEvent = null;

	public java.awt.event.AdjustmentEvent getAdjustmentEvent()
	{
		return this.adjustmentEvent;
	}

	private mit.awt.event.AdjustmentRaiser owner = null;

	public mit.awt.event.AdjustmentRaiser getOwner()
	{
		return this.owner;
	}

	public AdjustmentAdapter(mit.awt.event.AdjustmentRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof java.awt.Scrollbar )
		{
			((java.awt.Scrollbar) owner).addAdjustmentListener(this);
		}
		else if( owner instanceof java.awt.Adjustable )
		{
			((java.awt.Adjustable) owner).addAdjustmentListener(this);
		}
		else if( owner instanceof javax.swing.JScrollBar )
		{
			((javax.swing.JScrollBar) owner).addAdjustmentListener(this);
		}
	}

	public void adjustmentValueChanged(java.awt.event.AdjustmentEvent e)
	{
		setEvent(e);
	}

	private void setEvent(java.awt.event.AdjustmentEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.adjustmentEvent = e;
				new mit.awt.event.AdjustmentEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}