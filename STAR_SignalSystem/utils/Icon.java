package utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Icon
{
	public final static String SAVE = "toolbarButtonGraphics/general/Save24.gif";
	public final static String TIP = "toolbarButtonGraphics/general/TipOfTheDay24.gif";
	public final static String WORLD_LARGE = "toolbarButtonGraphics/development/WebComponent24.gif";
	public final static String WORLD = "toolbarButtonGraphics/development/WebComponent16.gif";
	public final static String WORLD_LARGE_JAR = "toolbarButtonGraphics/development/War24.gif";
	public final static String WORLD_JAR = "toolbarButtonGraphics/development/War16.gif";
	public final static String SPREADSHEET = "resources/spreadsheet.png";
	
	public static JLabel getIconComponent(final Object self, final String icon, final String tooltip, final Runnable action)
	{
		JLabel label = new JLabel();
		try
		{
			label.setIcon(new ImageIcon(self.getClass().getClassLoader().getResource(icon)));
		}
		catch( Exception ex )
		{
			label.setText("?");
		}
		label.setToolTipText(tooltip);
		if( action != null )
		{
			label.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					action.run();
				}

			});
		}
		return label;
	}

	public static ImageIcon getIcon(final Object self, final String icon)
	{
		return new ImageIcon(self.getClass().getClassLoader().getResource(icon));
	}
}
