package app;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.PickRay;
import javax.media.j3d.SceneGraphPath;
import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.events.Pick3DRaiser;
import star.hydrology.events.Select3DEvent;
import star.hydrology.events.Select3DRaiser;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FilledMapLayerRaiser;
import utils.Format;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;

@SignalComponent(extend = Behavior.class, raises = { Select3DRaiser.class })
public class PickerBehaviour extends PickerBehaviour_generated
{
	static final String text = "Toggle to display coordinates as mouse moves over map.";
	static JToggleButton label = new JToggleButton(text);
	Point3f center = null;

	boolean enableMouseClick = true;
	boolean enableMouseMoved = true;

	private Point3d lower = new Point3d(-1e5, -1e5, -1);
	private Point3d upper = new Point3d(1e5, 1e5, -1);
	private static final double EPS = .000001;
	private PickCanvas pickCanvas;
	private Point3f point;
	private int button;
	private WakeupOr mouseCriterion;

	PaletteRenderableLayer terrain;

	@Handles(raises = {})
	protected void getTerrain(FilledMapLayerRaiser r)
	{
		terrain = r.getLayer();
	}

	public int getButton()
	{
		return button;
	}

	public void setButton(int button)
	{
		this.button = button;
	}

	public Point3f getPoint()
	{
		return point;
	}

	private void setPoint(double x, double y)
	{
		point = new Point3f();
		point.x = (float) x;
		point.y = (float) y;
		point.z = 0;
	}

	public void addNotify()
	{
		super.addNotify();
		setSchedulingBounds(new BoundingSphere(new Point3d(), Double.MAX_VALUE));
		getLabel().addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				enableMouseMoved = ((JToggleButton) e.getSource()).isSelected();
				if (!enableMouseMoved)
				{
					getLabel().setText(text);
					getLabel().setSize(getLabel().getPreferredSize());
				}
			}
		});
		label.setSelected(enableMouseMoved);
	}

	public void initialize()
	{
		WakeupCriterion[] mouseEvents = new WakeupCriterion[3];
		mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);

		mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
		mouseEvents[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
		mouseEvents[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_MOVED);
		// mouseEvents[3] = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED);

		mouseCriterion = new WakeupOr(mouseEvents);
		wakeupOn(mouseCriterion);
	}

	public void processStimulus(Enumeration criteria)
	{

		while (criteria.hasMoreElements())
		{
			Object next = criteria.nextElement();
			if (next instanceof WakeupOnAWTEvent)
			{
				WakeupOnAWTEvent e = (WakeupOnAWTEvent) next;
				AWTEvent[] events = e.getAWTEvent();
				if (events != null)
				{
					for (int i = 0; i < events.length; i++)
					{
						if (events[i] instanceof MouseEvent)
						{
							processMouseEvent((MouseEvent) events[i]);
						}
					}
				}
			}
		}
		wakeupOn(mouseCriterion);

	}

	private void processMouseEvent(MouseEvent event)
	{
		int mouseID = event.getID();
		if (mouseID == MouseEvent.MOUSE_PRESSED)
		{
			mousePressed(event);
		}
		if (mouseID == MouseEvent.MOUSE_MOVED)
		{
			mouseMoved(event);
		}
	}

	private void mouseMoved(MouseEvent event)
	{
		try
		{
			if (enableMouseMoved)
			{
				PickCanvas pickCanvas = getPickCanvas();
				pickCanvas.setShapeLocation(event);
				PickResult r = getPickCanvas().pickClosest();
				if (r != null)
				{
					r.setFirstIntersectOnly(true);
					SceneGraphPath p = r.getSceneGraphPath();
					Transform3D t = p.getTransform();
					t.invert();
					if (pickCanvas.getPickShape() instanceof PickRay)
					{
						PickRay ray = (PickRay) pickCanvas.getPickShape();
						Point3d origin = new Point3d();
						Vector3d direction = new Vector3d();
						ray.get(origin, direction);
						Point3d intersect = new Point3d();
						t.transform(origin);
						t.transform(direction);
						if (intersect(origin, direction, intersect))
						{
							raiseRayPoint(intersect.x, intersect.y);
						}
						else
						{
							raiseRayPoint(Double.NaN, Double.NaN);
						}
					}
					else
					{
						raiseRayPoint(Double.NaN, Double.NaN);
					}
				}
				else
				{
					raiseRayPoint(Double.NaN, Double.NaN);
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	private void mousePressed(MouseEvent event)
	{

		if (event.getClickCount() == 2)
		{
			PickCanvas pickCanvas = getPickCanvas();
			pickCanvas.setShapeLocation(event);
			PickResult r = getPickCanvas().pickClosest();
			try
			{
				if (r != null)
				{
					r.setFirstIntersectOnly(true);
					SceneGraphPath p = r.getSceneGraphPath();
					Transform3D t = p.getTransform();
					t.invert();
					if (pickCanvas.getPickShape() instanceof PickRay)
					{
						PickRay ray = (PickRay) pickCanvas.getPickShape();
						Point3d origin = new Point3d();
						Vector3d direction = new Vector3d();
						ray.get(origin, direction);
						Point3d intersect = new Point3d();
						t.transform(origin);
						t.transform(direction);
						if (intersect(origin, direction, intersect))
						{
							// enableMouseClick = false;
							raiseClickPoint(intersect.x, intersect.y, event.getButton());
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (!enableMouseClick && event.getButton() == MouseEvent.BUTTON1)
		{
			getLabel().setText("To select outlet, toggle \"Set watershed outlet point\".");
		}
	}

	private void raiseClickPoint(double x, double y, int button)
	{
		setPoint(x, y);
		setButton(button);
		(new Select3DEvent(this)).raise();
	}

	public static JToggleButton getLabel()
	{
		return label;
	}

	private void raiseRayPoint(double x, double y)
	{
		if (Double.isNaN(x) || Double.isNaN(y))
		{
			getLabel().setText("Move mouse over the map or toggle to stop tracking.");
		}
		else
		{
			final double x1 = x + ((center != null) ? center.x : 0);
			final double y1 = y + ((center != null) ? center.y : 0);
			SwingUtilities.invokeLater(new Runnable()
			{

				public void run()
				{
					getLabel().setText("x (long) = " + Format.formatNumber(x1) + " y (lat) = " + Format.formatNumber(y1));
				}
			});
		}
	}

	private boolean intersect(Point3d origin, Vector3d direction, Point3d intersect)
	{
		if (terrain == null)
		{
			return intersect2(origin, direction, intersect);
		}
		else
		{
			direction.normalize();
			boolean ret = false;
			Grid grid = terrain.getDataset();
			float cellsize = grid.getCellsize();
			int rows = grid.getRows();
			int cols = grid.getCols();

			int dir = direction.z < 0 ? 1 : -1;
			for (double step = 0; step < -dir * (origin.z / direction.z); step += cellsize)
			// for (double step = -dir * (origin.z / direction.z); step > 0; step -= cellsize)
			{
				int indexX = (int) ((origin.x + direction.x * step * dir) / cellsize + cols / 2);
				int indexY = (int) ((origin.y + direction.y * step * dir) / cellsize + rows / 2);
				if (indexX < 0 || indexX > cols || indexY < 0 || indexY > rows)
				{
					continue;
				}
				double heightZ = origin.z + direction.z * step * dir;
				double heightAt = grid.getElementAt(indexX, indexY);
				if (heightAt - heightZ > 0)
				// if( heightAt - heightZ < 0 )
				{
					float[] point = new float[3];
					grid.getPoint(indexX, indexY, point);
					intersect.x = point[0] - center.x;
					intersect.y = -(point[1] - center.y);
					intersect.z = point[2];
					ret = true;
					break;
				}
			}
			if (!ret)
			{
				ret = intersect2(origin, direction, intersect);
			}
			return ret;
		}
	}

	private boolean intersect2(Point3d origin, Vector3d direction, Point3d intersect)
	{
		double theta = 0.0;
		if (direction.x > 0.0)
		{
			theta = Math.max(theta, (lower.x - origin.x) / direction.x);
		}
		if (direction.x < 0.0)
		{
			{
				theta = Math.max(theta, (upper.x - origin.x) / direction.x);
			}
		}
		if (direction.y > 0.0)
		{
			theta = Math.max(theta, (lower.y - origin.y) / direction.y);
		}
		if (direction.y < 0.0)
		{
			theta = Math.max(theta, (upper.y - origin.y) / direction.y);
		}
		if (direction.z > 0.0)
		{
			theta = Math.max(theta, (lower.z - origin.z) / direction.z);
		}
		if (direction.z < 0.0)
		{
			theta = Math.max(theta, (upper.z - origin.z) / direction.z);
		}

		intersect.x = origin.x + theta * direction.x;
		intersect.y = origin.y + theta * direction.y;
		intersect.z = origin.z + theta * direction.z;

		if (intersect.x < (lower.x - EPS))
		{
			return false;
		}
		if (intersect.x > (upper.x + EPS))
		{
			return false;
		}
		if (intersect.y < (lower.y - EPS))
		{
			return false;
		}
		if (intersect.y > (upper.y + EPS))
		{
			return false;
		}
		if (intersect.z < (lower.z - EPS))
		{
			return false;
		}
		if (intersect.z > (upper.z + EPS))
		{
			return false;
		}

		return true;
	}

	private PickCanvas getPickCanvas()
	{
		return pickCanvas;
	}

	public void setPickCanvas(PickCanvas pickCanvas)
	{
		this.pickCanvas = pickCanvas;
	}

	@Handles(raises = {})
	protected void enablePicking(Pick3DRaiser pick)
	{
		enableMouseClick = true;
	}

	@Handles(raises = {})
	protected void getCenter(star.hydrology.events.map.FilledMapLayerRaiser r)
	{
		PaletteRenderableLayer layer = r.getLayer();
		if (layer != null && layer.getDataset() instanceof GridwStat)
		{
			GridwStat gs = (GridwStat) layer.getDataset();
			center = gs.getCenter();
		}

	}
}
