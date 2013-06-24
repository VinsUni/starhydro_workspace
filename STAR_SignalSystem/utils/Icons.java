package utils;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import javax.swing.ImageIcon;

interface IconsC
{
	final static int size = 32;
}

public enum Icons
{
	MATE("resources/iconstexttowebdevpack/icontexto-webdev-mate-{0,number,000}x{0,number,000}.png"), //

	DELETE("resources/iconstexttowebdevpack/icontexto-webdev-cancel-{0,number,000}x{0,number,000}.png"), //
	ABOUT("resources/iconstexttowebdevpack/icontexto-webdev-about-{0,number,000}x{0,number,000}.png"), //
	ADD("resources/iconstexttowebdevpack/icontexto-webdev-add-{0,number,000}x{0,number,000}.png"), //
	ALERT("resources/iconstexttowebdevpack/icontexto-webdev-alert-{0,number,000}x{0,number,000}.png"), //
	ARROW_DOWN("resources/iconstexttowebdevpack/icontexto-webdev-arrow-down-{0,number,000}x{0,number,000}.png"), //
	ARROW_LEFT("resources/iconstexttowebdevpack/icontexto-webdev-arrow-left-{0,number,000}x{0,number,000}.png"), //
	ARROW_RIGHT("resources/iconstexttowebdevpack/icontexto-webdev-arrow-right-{0,number,000}x{0,number,000}.png"), //
	ARROW_UP("resources/iconstexttowebdevpack/icontexto-webdev-arrow-up-{0,number,000}x{0,number,000}.png"), //
	BULLET("resources/iconstexttowebdevpack/icontexto-webdev-bullet-{0,number,000}x{0,number,000}.png"), //
	CANCEL("resources/iconstexttowebdevpack/icontexto-webdev-cancel-{0,number,000}x{0,number,000}.png"), //
	COMMERCE("resources/iconstexttowebdevpack/icontexto-webdev-commerce-{0,number,000}x{0,number,000}.png"), //
	CONFIG("resources/iconstexttowebdevpack/icontexto-webdev-config-{0,number,000}x{0,number,000}.png"), //
	CONTACT("resources/iconstexttowebdevpack/icontexto-webdev-contact-{0,number,000}x{0,number,000}.png"), //
	EMOTICON_SAD("resources/iconstexttowebdevpack/icontexto-webdev-emoticon-sad-{0,number,000}x{0,number,000}.png"), //
	EMOTICON_SMILE("resources/iconstexttowebdevpack/icontexto-webdev-emoticon-smile-{0,number,000}x{0,number,000}.png"), //
	FAVORITES("resources/iconstexttowebdevpack/icontexto-webdev-favorites-{0,number,000}x{0,number,000}.png"), //
	FILE("resources/iconstexttowebdevpack/icontexto-webdev-file-{0,number,000}x{0,number,000}.png"), //
	INFO("resources/iconstexttowebdevpack/icontexto-webdev-info-{0,number,000}x{0,number,000}.png"), //
	HELP("resources/iconstexttowebdevpack/icontexto-webdev-help-{0,number,000}x{0,number,000}.png"), //
	MONEY("resources/iconstexttowebdevpack/icontexto-webdev-money-{0,number,000}x{0,number,000}.png"), //
	OK("resources/iconstexttowebdevpack/icontexto-webdev-ok-{0,number,000}x{0,number,000}.png"), //
	PRINT("resources/iconstexttowebdevpack/icontexto-webdev-print-{0,number,000}x{0,number,000}.png"), //
	REMOVE("resources/iconstexttowebdevpack/icontexto-webdev-remove-{0,number,000}x{0,number,000}.png"), //
	RSS_FEED("resources/iconstexttowebdevpack/icontexto-webdev-rss-feed-{0,number,000}x{0,number,000}.png"), //
	SEARCH("resources/iconstexttowebdevpack/icontexto-webdev-search-{0,number,000}x{0,number,000}.png"), //
	SECURITY("resources/iconstexttowebdevpack/icontexto-webdev-security-{0,number,000}x{0,number,000}.png"), //
	SITE_MAP("resources/iconstexttowebdevpack/icontexto-webdev-site-map-{0,number,000}x{0,number,000}.png"), //
	USER("resources/iconstexttowebdevpack/icontexto-webdev-user-{0,number,000}x{0,number,000}.png"), //
	DIALOG("resources/icontextowebdevsocialbookmarkpack-01/PNG/icontexto-webdev-social-bookmark-06-{0,number,000}x{0,number,000}.png"), HISTORY("resources/icontextowebdevsocialbookmarkpack-01/PNG/icontexto-webdev-social-bookmark-08-{0,number,000}x{0,number,000}.png"), //
	// YEAST_WT("resources/yeast/WTColony.png"),//
	YEAST_WT("resources/yeast/WTColony2.png"), //
	YEAST_WTR("resources/yeast/WTColonyReplica.png"), //
	YEAST_R("resources/yeast/RedColony.png"), //
	YEAST_RR("resources/yeast/RedColonyReplica.png"), //
	FLAG_DEFAULT("resources/flags/default.png"), //
	FLAG_US("resources/flags/us.png"), //
	FLAG_HT("resources/flags/ht.png"), //
	; //

	String resource;

	private String getResource()
	{
		return getResource(IconsC.size);
	}

	private String getResource(int size)
	{
		return MessageFormat.format(resource, size);
	}

	Icons(String resource)
	{
		this.resource = resource;
	}

	public ImageIcon getIcon()
	{
		try
		{
			return new ImageIcon(Icons.class.getClassLoader().getResource(getResource()));
		}
		catch (Exception ex)
		{
			return new ImageIcon(new byte[] { (byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x39, (byte) 0x61, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0xf8, (byte) 0xfc, (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x2c, (byte) 0x00,
			        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x02, (byte) 0x44, (byte) 0x01, (byte) 0x00, (byte) 0x3b });
		}
	}

	public ImageIcon getIcon(int size)
	{
		try
		{
			return new ImageIcon(Icons.class.getClassLoader().getResource(getResource(size)));
		}
		catch (Throwable ex)
		{
			return new ImageIcon(new byte[] { (byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x39, (byte) 0x61, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0xf8, (byte) 0xfc, (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x2c, (byte) 0x00,
			        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x02, (byte) 0x44, (byte) 0x01, (byte) 0x00, (byte) 0x3b });
		}
	}

	public BufferedImage getImage()
	{
		try
		{
			BufferedImage img = new BufferedImage(IconsC.size, IconsC.size, BufferedImage.TYPE_4BYTE_ABGR);
			img.getGraphics().drawImage(Toolkit.getDefaultToolkit().createImage(Icons.class.getClassLoader().getResource(getResource())), 0, 0, null);
			return img;
		}
		catch (Throwable t)
		{
			BufferedImage img = new BufferedImage(IconsC.size, IconsC.size, BufferedImage.TYPE_4BYTE_ABGR);
			return img;
		}

	}

	public Cursor getCursor()
	{
		try
		{
			Cursor c = Cursor.getSystemCustomCursor(this.toString());
			if (c == null)
			{
				c = Toolkit.getDefaultToolkit().createCustomCursor(getImage(), new Point(24, 24), this.toString());
			}
			return c;
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			return Cursor.getDefaultCursor();
		}
	}
}
