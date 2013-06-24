package star.ui;

import java.awt.MediaTracker;

public class ImageIcon extends javax.swing.ImageIcon
{
	private static final long serialVersionUID = 1L;

	public ImageIcon(byte[] imageData)
	{
		super(imageData);
		int count = 10;
		while (getImageLoadStatus() != MediaTracker.COMPLETE && count-- != 0)
		{
			try
			{
				Thread.sleep(200);
				loadImage(getImage());
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
