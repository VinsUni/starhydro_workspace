package star.tribs.io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import star.tribs.io.OutputReader.VoronoiPoints;
import star.tribs.io.Voronoi.Point2D;
import star.tribs.io.Voronoi.Polygon;
import star.tribs.io.Voronoi.Range;

public class VoronoiCanvas extends JComponent
{
	ArrayList<Polygon> polygons;
	Range range;
	float factor;
	float factorX, factorY;

	public ArrayList<Polygon> getPolygons()
	{
		return polygons;
	}

	public Range getRange()
	{
		return range;
	}

	boolean play = false;
	int col = 1;
	int currentHour = 0;

	Color colors[] = { Color.red, Color.blue, Color.green, Color.yellow, Color.magenta, Color.cyan, Color.orange, Color.pink };
	private VoronoiPoints[] points;

	public VoronoiCanvas(ArrayList<Polygon> polygons)
	{
		this.polygons = polygons;
		this.range = Voronoi.getRange(polygons);
		setOpaque(false);
		setBackground(Color.black);
		setDoubleBuffered(true);
	}

	public VoronoiCanvas(ArrayList<Polygon> polygons, float factor, VoronoiPoints[] points)
	{
		this.polygons = polygons;
		this.range = Voronoi.getRange(polygons);
		this.factor = factor;
		this.points = points;
		setOpaque(false);
		setBackground(Color.black);
		setDoubleBuffered(true);
	}

	protected void p(Graphics g, String currentHour, int col, VoronoiPoints points)
	{
		int i = 0;
		// System.out.println("Running update image " + currentHour + " " + col );
		g.setColor(Color.white);
		g.drawString(MessageFormat.format("{0} {1,number,000}", currentHour, col), 20, 20);
		for (Polygon poly : polygons)
		{
			java.awt.Polygon draw = new java.awt.Polygon();
			for (Point2D p : poly.getPoints())
			{
				draw.addPoint((int) ((p.x - range.min.x) / factor), (int) ((p.y - range.min.y) / factor));
			}
			g.setColor(getColor(poly.id, points, col));
			g.fillPolygon(draw);
		}

	}

	public interface GetColor
	{
		public Color getColor(int point);

		public Point2D getPoint(Point2D location);
	}

	java.awt.Polygon[] quickPolygoins;
	int[] quickIds;

	public void initQuickPoly()
	{
		if (quickPolygoins == null)
		{
			quickPolygoins = new java.awt.Polygon[polygons.size()];
			quickIds = new int[ polygons.size()];
			for (int i = 0; i < polygons.size(); i++)
			{
				Voronoi.Polygon poly = polygons.get(i);
				java.awt.Polygon draw = new java.awt.Polygon();
				for (Point2D p : poly.getPoints())
				{
					draw.addPoint((int) p.x, (int) p.y);
				}
				quickPolygoins[i]=draw;
				quickIds[i]=poly.id;
			}

		}
	}

	public void p(Graphics g, String currentHour, String variableName, GetColor colorFactory)
	{
		initQuickPoly();
		for(int i = 0 ; i < quickPolygoins.length ;i++)
		{
			g.setColor(colorFactory.getColor(quickIds[i]));
			g.fillPolygon(quickPolygoins[i]);		
		}

	}

	public void p(Graphics g, int currentHour, GetColor colorFactory)
	{
		int i = 0;
		g.setColor(Color.white);
		g.drawString(MessageFormat.format("{0,number,0000} {1,number,000}", currentHour, col), 20, 20);

		for (Polygon poly : polygons)
		{
			java.awt.Polygon draw = new java.awt.Polygon();
			for (Point2D p : poly.getPoints())
			{
				Point2D p2 = colorFactory.getPoint(p);
				draw.addPoint((int) p2.x, (int) p2.y);
			}
			g.setColor(colorFactory.getColor(poly.id));
			g.fillPolygon(draw);
		}
	}

	protected void p(Graphics g, int currentHour, int col, VoronoiPoints points)
	{
		int i = 0;
		// System.out.println("Running update image " + currentHour + " " + col );
		g.setColor(Color.white);
		g.drawString(MessageFormat.format("{0,number,0000} {1,number,000}", currentHour, col), 20, 20);
		for (Polygon poly : polygons)
		{
			java.awt.Polygon draw = new java.awt.Polygon();
			for (Point2D p : poly.getPoints())
			{
				draw.addPoint((int) ((p.x - range.min.x) / factor), (int) ((p.y - range.min.y) / factor));
			}
			g.setColor(getColor(poly.id, points, col));
			g.fillPolygon(draw);
		}

	}

	volatile Image image = null;
	boolean painted = false;
	boolean needUpdate = false;

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (image != null)
		{
			g.drawImage(image, 0, 0, null);
		}
		painted = true;
		if (needUpdate)
		{
			updateScreen();
		}

	}

	void updateScreen()
	{
		if (needUpdate)
		{
			needUpdate = false;
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					if (painted)
					{
						BufferedImage image2 = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
						p(image2.getGraphics(), currentHour, col, points[currentHour]);
						image = image2;
						java.io.File f = new java.io.File(MessageFormat.format("z:\\hydro\\R3\\Images\\img_{0}_{1,number,0000}{2}", col, currentHour, ".jpg"));
						if (!f.exists())
						{
							try
							{
								System.out.print("writing " + f);
								System.out.println(" " + ImageIO.write(image2, "JPEG", f));
							}
							catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
						painted = false;
						needUpdate = false;
						System.out.println("Frame " + currentHour + " " + col);
						repaint();
					}
					else
					{
						needUpdate = true;
					}
					if (play)
					{
						needUpdate = true;
						next();
					}
				}
			});
		}
	}

	java.awt.Color getColor(int id, VoronoiPoints points, int col)
	{
		float value = points.getNormalized(id, col);
		return new java.awt.Color(value, value, value);
	}

	@Override
	public Dimension getPreferredSize()
	{
		Dimension d = range.getSize();
		d.height = (int) (d.height / factor);
		d.width = (int) (d.width / factor);
		return d;
	}

	void next()
	{
		currentHour += 24;
		if (!(currentHour < points.length))
		{
			currentHour = 0;
		}
	}

	void setPlay(boolean play)
	{
		this.play = play;
		needUpdate = true;
		updateScreen();
	}

	void setCol(int col)
	{
		this.col = col;
		needUpdate = true;
		updateScreen();
	}

}
