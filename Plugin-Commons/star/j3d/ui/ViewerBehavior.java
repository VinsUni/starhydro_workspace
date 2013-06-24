/*
 * $Id: ViewerBehavior.java,v 1.7 2007/10/18 19:29:35 cshubert Exp $ 
 */

package star.j3d.ui;

import java.awt.AWTEvent;
import java.awt.Cursor;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.vp.ViewPlatformBehavior;
import com.sun.j3d.utils.picking.PickCanvas;

public class ViewerBehavior extends Behavior
{

	/** Masks and flags for behaviors */
	public final static int NONE = 0x0;

	/** Object behavior modifiers */
	public final static int TRANSLATE_X = 0x1;
	public final static int TRANSLATE_Y = 0x2;
	public final static int TRANSLATE_Z = 0x4;
	public final static int TRANSLATE = 0x8;

	public final static int ROTATE_X = 0x10;
	public final static int ROTATE_Y = 0x20;
	public final static int ROTATE_Z = 040;
	public final static int ROTATE = 0x80;
	public final static int ZOOM = 0x100;

	/** ViewPlatformbehavior Navigation modifiers */
	public static final int VP_TRANSLATE = 0x1;
	public static final int VP_ROTATE = 0x2;
	public static final int VP_ZOOM = 0x4;

	/** ViewPlatformbehavior Navigation modes */
	public static final int ORBIT = 0x10;
	public static final int ORBIT_ALL = ORBIT | VP_TRANSLATE | VP_ROTATE | VP_ZOOM;

	public static final int EXAMINE = 0x20;
	public static final int FLY = 0x40;
	public static final int HOVER = 0x80;
	public static final int ATTACH = 0x100;

	public static final int ABOVE_GROUND = 0x200;

	private StarContainer mViewer = null;
	private PickCanvas pickCanvas = null;

	private WakeupOr mouseCriterion;

	private int navMode;

	private boolean refreshOnDrag = true;
	private boolean cursorOnDrag = true;

	private Cursor tmpCursor = null;
	private Cursor transparentCursor = null;
	private boolean cursorHidden = false;

	public ViewerBehavior()
	{
		mViewer = null;
		pickCanvas = null;
	}

	public void initialize()
	{
		WakeupCriterion[] mouseEvents = new WakeupCriterion[3];
		mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
		mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
		mouseEvents[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);

		mouseCriterion = new WakeupOr(mouseEvents);
		wakeupOn(mouseCriterion);
	}

	public void addToScene(BranchGroup scene)
	{
		scene.addChild(this);
	}

	public void setRefreshOnDrag(boolean state)
	{
		refreshOnDrag = state;
	}

	public boolean getRefreshOnDrag()
	{
		return refreshOnDrag;
	}

	public void setCursorOnDrag(boolean state)
	{
		cursorOnDrag = state;
	}

	public boolean getCursorOnDrag()
	{
		return cursorOnDrag;
	}

	public void setViewer(StarContainer viewer)
	{
		mViewer = viewer;
		pickCanvas = mViewer.getPickCanvas();
	}

	public StarContainer getViewer()
	{
		return mViewer;
	}

	public void setPickCanvas(PickCanvas canvas)
	{
		pickCanvas = canvas;
	}

	public PickCanvas getPickCanvas()
	{
		return pickCanvas;
	}

	public void setNavigationMode(int flag)
	{
		if ((flag != navMode) || (getVpBehavior() == null))
		{
			setVpBehavior(null);
			if ((flag & ORBIT) == ORBIT)
			{
				OrbitBehavior orbitBehavior = new OrbitBehavior((Canvas3D) mViewer.getCanvas(), OrbitBehavior.PROPORTIONAL_ZOOM | OrbitBehavior.REVERSE_ROTATE | OrbitBehavior.REVERSE_TRANSLATE);
				orbitBehavior.setSchedulingBounds(StarContainer.sInfiniteBounds);
				orbitBehavior.setEnable(true);
				orbitBehavior.setTranslateEnable(true);
				orbitBehavior.setRotateEnable(true);
				orbitBehavior.setZoomEnable(true);
				orbitBehavior.setMinRadius(10);
				orbitBehavior.setAboveGround((flag & ABOVE_GROUND) == ABOVE_GROUND);
				// orbitBehavior.setTransXFactor(100);
				// orbitBehavior.setTransYFactor(100);
				setVpBehavior(orbitBehavior);
			}
			navMode = flag;
		}
	}

	public Vector3d getVpTranslateScale()
	{
		return new Vector3d(((OrbitBehavior) getVpBehavior()).getTransXFactor(), ((OrbitBehavior) getVpBehavior()).getTransYFactor(), ((OrbitBehavior) getVpBehavior()).getZoomFactor());

	}

	public void setVpTranslateScale(Vector3d vec)
	{
		((OrbitBehavior) getVpBehavior()).setTransXFactor(vec.x);
		((OrbitBehavior) getVpBehavior()).setTransYFactor(vec.y);
		((OrbitBehavior) getVpBehavior()).setZoomFactor(vec.z);
	}

	public void processStimulus(Enumeration criteria)
	{
		WakeupCriterion wakeup;
		AWTEvent[] events;
		MouseEvent evt;

		while (criteria.hasMoreElements())
		{
			wakeup = (WakeupCriterion) criteria.nextElement();

			if (wakeup instanceof WakeupOnAWTEvent)
			{
				events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
				if (events.length > 0)
				{
					evt = (MouseEvent) events[events.length - 1];
					processEvent(evt);
				}
			}
		}
		wakeupOn(mouseCriterion);
	}

	public void mousePressed(MouseEvent me)
	{
		switch (me.getButton())
		{

		case MouseEvent.BUTTON1:
			pickCanvas.setShapeLocation(me);

		case MouseEvent.BUTTON3:
			getVpBehavior().setEnable(false);
			break;
		case MouseEvent.BUTTON2:
			getVpBehavior().setEnable(true);
			break;
		}
		if (null != getVpBehavior())
		{
			((MouseListener) getVpBehavior()).mousePressed(me);
		}
	}

	public void mouseReleased(MouseEvent me)
	{
		if (null != getVpBehavior())
		{
			((MouseListener) getVpBehavior()).mouseReleased(me);
		}

	}

	private void setVpBehavior(ViewPlatformBehavior vpb)
	{
		mViewer.getUniverse().getViewingPlatform().setViewPlatformBehavior(vpb);
	}

	private ViewPlatformBehavior getVpBehavior()
	{
		return mViewer.getUniverse().getViewingPlatform().getViewPlatformBehavior();
	}

	private void hideCursor(boolean state)
	{
		if (state)
		{
			if (mViewer instanceof Panel)
			{
				tmpCursor = ((Panel) mViewer).getCursor();
				((Panel) mViewer).setCursor(transparentCursor);
				cursorHidden = true;
			}
		}
		else
		{
			if (tmpCursor != null)
			{
				if (mViewer instanceof Panel)
				{
					((Panel) mViewer).setCursor(tmpCursor);
					tmpCursor = null;
					cursorHidden = false;
				}
			}
		}
	}

	private void processEvent(MouseEvent evt)
	{
		int mouseID = evt.getID();
		switch (mouseID)
		{
		case MouseEvent.MOUSE_PRESSED:
			int mButton = evt.getButton();
			mousePressed(evt);
			if ((!cursorOnDrag) && (mButton == MouseEvent.BUTTON1) && (!evt.isShiftDown()))
			{
				hideCursor(true);
			}
			break;

		case MouseEvent.MOUSE_RELEASED:
			if (cursorHidden)
			{
				hideCursor(false);
			}
			break;

		case MouseEvent.MOUSE_DRAGGED:

			mViewer.getUniverse().getViewingPlatform().getViewPlatformBehavior().setEnable(true);
			((MouseMotionListener) getVpBehavior()).mouseDragged(evt);
			break;

		case MouseEvent.MOUSE_MOVED:
		default:
			break;
		}
	}

}
