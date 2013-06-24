package star.ui.components.titledcontainer;

import javax.swing.JLabel;

public class TitleLabel extends JLabel
{
	private static final long serialVersionUID = 1L;

	public TitleLabel(String text)
	{
		super(text);
		setForeground(java.awt.Color.white);
		setFont(CommonUI.get().getTitleFont(getFont()));
	}

}
