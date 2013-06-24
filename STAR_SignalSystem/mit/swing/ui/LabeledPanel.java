package mit.swing.ui;

public class LabeledPanel extends mit.swing.xJPanel
{
	private static final long serialVersionUID = 1L;
	public static final int ACROSS_UPPER_RIGHT = 0;
	public static final int ACROSS_UPPER_LEFT = 1;
	public static final int ACROSS_LOWER_RIGHT = 2;
	public static final int ACROSS_LOWER_LEFT = 3;

	public static final int DOWN_UPPER_RIGHT = 4;
	public static final int DOWN_UPPER_LEFT = 5;
	public static final int DOWN_LOWER_RIGHT = 6;
	public static final int DOWN_LOWER_LEFT = 7;

	private String label = "";

	protected String getLabel()
	{
		return this.label;
	}

	protected void setLabel(String label)
	{
		this.label = label;
	}

	private int alignment = ACROSS_UPPER_LEFT;

	protected int getAlignment()
	{
		return this.alignment;
	}

	protected void setAlignment(int alignment)
	{
		this.alignment = alignment;
	}

	public java.awt.Insets getInsets()
	{
		try
		{
			java.awt.FontMetrics fm = getFontMetrics(getFont());
			java.awt.Insets insets = new java.awt.Insets(fm.getMaxAscent() + fm.getMaxDescent() + 1, fm.getMaxAdvance() + 1, fm.getMaxAscent() + fm.getMaxDescent() + 1, fm.getMaxAdvance() + 1);
			return insets;
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		return new java.awt.Insets(0, 0, 0, 0);
	}

	public void paint(java.awt.Graphics g)
	{
		render(g);
	}

	public void update(java.awt.Graphics g)
	{
		render(g);
	}

	private void render(java.awt.Graphics g)
	{
		try
		{
			super.paint(g);

			java.awt.Dimension size = getSize();
			g.setColor(getBackground());
			g.draw3DRect(0, 0, size.width, size.height, true);

			drawBox(g);
			if( null != getLabel() )
			{
				drawLabel(g);
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	private void drawBox(java.awt.Graphics g)
	{
		java.awt.Insets insets = getInsets();
		java.awt.Dimension size = getSize();
		int[] xpoints = new int[5];
		xpoints[0] = (int) Math.ceil(insets.left / 2);
		xpoints[1] = size.width - (int) Math.ceil(insets.right / 2);
		xpoints[2] = size.width - (int) Math.ceil(insets.right / 2);
		xpoints[3] = (int) Math.ceil(insets.left / 2);
		xpoints[4] = (int) Math.ceil(insets.left / 2);

		int[] ypoints = new int[5];
		ypoints[0] = (int) Math.ceil(insets.top / 2);
		ypoints[1] = (int) Math.ceil(insets.top / 2);
		ypoints[2] = size.height - (int) Math.ceil(insets.bottom / 2);
		ypoints[3] = size.height - (int) Math.ceil(insets.bottom / 2);
		ypoints[4] = (int) Math.ceil(insets.top / 2);

		g.setColor(getBackground().darker());
		g.drawPolygon(xpoints, ypoints, 5);
		for (int i = 0; (xpoints.length != i) && (ypoints.length != i); i++)
		{
			xpoints[i] += 1;
			ypoints[i] += 1;
		}
		g.setColor(getBackground().brighter());
		g.drawPolygon(xpoints, ypoints, 5);
	}

	private void drawLabel(java.awt.Graphics g)
	{
		int PAD = 4;

		java.awt.Dimension size = getSize();
		java.awt.Insets insets = getInsets();
		int containerWidth = size.width - insets.right - insets.left;
		int containerHeight = size.height - insets.top - insets.bottom;

		String label = getLabel();
		java.awt.FontMetrics fm = getFontMetrics(getFont());
		int horzLabelWidth = fm.stringWidth(label);
		int horzLabelHeight = fm.getMaxAscent() + fm.getMaxDescent();
		int vertLabelWidth = fm.getMaxAdvance();
		int vertLabelCharHeight = (fm.getMaxAscent() + fm.getMaxDescent());
		int vertLabelHeight = label.length() * vertLabelCharHeight;

		switch( getAlignment() )
		{
		case ACROSS_UPPER_RIGHT:

			if( containerWidth > horzLabelWidth )
			{
				g.setColor(getBackground());
				g.fillRect(insets.left + containerWidth - horzLabelWidth, insets.top - horzLabelHeight, horzLabelWidth + PAD, horzLabelHeight);
				g.setColor(getForeground());
				g.drawString(label, insets.left + PAD / 2 + containerWidth - horzLabelWidth, insets.top - fm.getMaxDescent());
			}
			break;
		case ACROSS_UPPER_LEFT:
			if( containerWidth > horzLabelWidth )
			{
				g.setColor(getBackground());
				g.fillRect(insets.left, insets.top - horzLabelHeight, horzLabelWidth + PAD, horzLabelHeight);
				g.setColor(getForeground());
				g.drawString(label, insets.left + PAD / 2, insets.top - fm.getMaxDescent());
			}
			break;
		case ACROSS_LOWER_RIGHT:
			if( containerWidth > horzLabelWidth )
			{
				g.setColor(getBackground());
				g.fillRect(insets.left + containerWidth - horzLabelWidth, insets.top + containerHeight, horzLabelWidth + PAD, horzLabelHeight);
				g.setColor(getForeground());
				g.drawString(label, insets.left + PAD / 2 + containerWidth - horzLabelWidth, insets.top + containerHeight + fm.getMaxAscent());
			}
			break;
		case ACROSS_LOWER_LEFT:
			if( containerWidth > horzLabelWidth )
			{
				g.setColor(getBackground());
				g.fillRect(insets.left, insets.top + containerHeight, horzLabelWidth + PAD, horzLabelHeight);
				g.setColor(getForeground());
				g.drawString(label, insets.left + PAD / 2, insets.top + containerHeight + fm.getMaxAscent());
			}
			break;
		case DOWN_UPPER_RIGHT:
			if( containerHeight > vertLabelHeight )
			{
				g.setColor(getBackground());
				g.fillRect(insets.left + containerWidth, insets.top + PAD / 2, vertLabelWidth, vertLabelHeight + PAD / 2);
				g.setColor(getForeground());
				char[] ca = new char[1];
				for (int i = 0; label.length() != i; i++)
				{
					ca[0] = label.charAt(i);
					String str = new String(ca);
					g.drawString(str, insets.left + containerWidth + insets.right - (int) Math.ceil((fm.getMaxAdvance() + fm.stringWidth(str)) / 2), insets.top + PAD / 2 + (1 + i) * vertLabelCharHeight);
				}
			}
			break;
		case DOWN_LOWER_RIGHT:
			if( containerHeight > vertLabelHeight )
			{
				g.setColor(getBackground());
				g.fillRect(insets.left + containerWidth, insets.top + PAD / 2 + containerHeight - vertLabelHeight, vertLabelWidth, vertLabelHeight + PAD / 2);
				g.setColor(getForeground());
				char[] ca = new char[1];
				for (int i = 0; label.length() != i; i++)
				{
					ca[0] = label.charAt(i);
					String str = new String(ca);
					g.drawString(str, insets.left + containerWidth + insets.right - (int) Math.ceil((fm.getMaxAdvance() + fm.stringWidth(str)) / 2), insets.top + PAD / 2 + containerHeight - vertLabelHeight + (1 + i) * vertLabelCharHeight);
				}
			}
			break;
		case DOWN_UPPER_LEFT:
			if( containerHeight > vertLabelHeight )
			{
				g.setColor(getBackground());
				g.fillRect(insets.left - fm.getMaxAdvance(), insets.top + PAD / 2, fm.getMaxAdvance(), vertLabelHeight + PAD / 2);
				g.setColor(getForeground());
				char[] ca = new char[1];
				for (int i = 0; label.length() != i; i++)
				{
					ca[0] = label.charAt(i);
					String str = new String(ca);
					g.drawString(str, insets.left - (int) Math.ceil((fm.getMaxAdvance() + fm.stringWidth(str)) / 2), insets.top + PAD / 2 + (1 + i) * vertLabelCharHeight);
				}
			}
			break;
		case DOWN_LOWER_LEFT:
			if( containerHeight > vertLabelHeight )
			{
				g.setColor(getBackground());
				g.fillRect(PAD / 2, insets.top + containerHeight - vertLabelHeight, insets.left - PAD / 2, vertLabelHeight + PAD);
				g.setColor(getForeground());
				char[] ca = new char[1];
				for (int i = 0; label.length() != i; i++)
				{
					ca[0] = label.charAt(i);
					String str = new String(ca);
					g.drawString(str, insets.left - (int) Math.ceil((fm.getMaxAdvance() + fm.stringWidth(str)) / 2), insets.top + containerHeight - vertLabelHeight + PAD / 2 + (1 + i) * vertLabelCharHeight);
				}
			}
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args)
	{
		try
		{
			mit.swing.xJFrame f = new mit.swing.xJFrame(null, "LabeledPanel");
			f.getContentPane().setLayout(new java.awt.BorderLayout());
			f.setBackground(java.awt.Color.green);
			mit.swing.ui.LabeledPanel p = new mit.swing.ui.LabeledPanel();
			p.setLabel("This is a test");
			p.setAlignment(ACROSS_UPPER_LEFT);
			if( 2 == args.length )
			{
				p.setAlignment(Integer.parseInt(args[1]));
			}
			if( 0 < args.length )
			{
				p.setLabel(args[0]);
			}
			p.setBackground(java.awt.Color.pink);
			f.getContentPane().add(p, java.awt.BorderLayout.CENTER);
			f.pack();
			f.show();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
}