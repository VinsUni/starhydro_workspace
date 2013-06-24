/**
 * 
 */
package star.ui.components.titledcontainer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import utils.Icons;
import utils.UIHelpers;

class CloseButton extends JPanel
{
	private static final long serialVersionUID = 1L;
	private boolean visible;

	public CloseButton(boolean visible)
	{
		final CloseButton self = this;
		this.visible = visible;
		addMouseListener(new java.awt.event.MouseAdapter()
		{
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e)
			{
				setState(!self.visible);
			};
		});
		setLayout(new BorderLayout());
	}

	public void setState(boolean state)
	{
		visible = state;
		invalidate();
		updateTooltip();
		UIHelpers.getFrame(this).invalidate();
		UIHelpers.getFrame(this).validate();
		UIHelpers.getFrame(this).repaint();

	}

	private void updateTooltip()
	{
		if (visible)
		{
			setToolTipText("Hide");
		}
		else
		{
			setToolTipText("Show");
		}
	}

	boolean getState()
	{
		return visible;
	}

	private void init()
	{
		// setPreferredSize( new Dimension(16,16));
		setPreferredSize(new Dimension(16, 16));
		setOpaque(false);
	}

	@Override
	public void doLayout()
	{
		super.doLayout();
		init();
	}

	@Override
	protected void paintComponent(Graphics gr)
	{

		super.paintComponent(gr);
		if (visible)
		{
			Graphics2D g = (Graphics2D) gr;
			g.drawImage(Icons.REMOVE.getIcon(16).getImage(), 0, 0, null);
		}
		else
		{
			Graphics2D g = (Graphics2D) gr;
			g.drawImage(Icons.ADD.getIcon(16).getImage(), 0, 0, null);
		}
	}
}