/*
 * $Id: StarContainer.java,v 1.22 2008/09/08 20:36:04 ceraj Exp $ 
 */

package star.j3d.ui;

import java.awt.Insets;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Fog;
import javax.media.j3d.Group;
import javax.media.j3d.Light;
import javax.media.j3d.LinearFog;
import javax.media.j3d.Node;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import star.event.Event;
import star.event.EventController;
import star.j3d.ui.signal.ResetSceneEvent;
import star.j3d.ui.signal.ResetSceneRaiser;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class StarContainer extends Container3D
{
	private static final long serialVersionUID = 1L;

	private StarContainerBehavior mBehaviorMgr;
	private SimpleUniverse mUniverse;
	private PickCanvas pickCanvas;

	private BranchGroup sceneRoot;
	// private TransformGroup sceneScale;
	private BranchGroup sceneObjects;
	private Background bgNode;
	private Switch mLightSwitch;
	private boolean resetScenePending = true;

	public final static BoundingSphere sInfiniteBounds = new BoundingSphere(new Point3d(), Double.MAX_VALUE);

	public void addNotify()
	{
		super.addNotify();
		setBackground(null);
		setForeground(null);
		getAdapter().setParent(getParent());
		getAdapter().addHandled(ResetSceneEvent.class);
		initialize();
		setVisible(true);
	}

	public StarContainerBehavior getViewerBehaviour()
	{
		return mBehaviorMgr;
	}

	private synchronized void initialize()
	{
		initScene();
		Vector3f light2dir = new Vector3f();
		getLight2().getDirection(light2dir);
		light2dir.scale(-1);
		getLight2().setDirection(light2dir);
		light2dir.scale(10.f);

		Vector3f light3dir = new Vector3f();
		getLight3().getDirection(light3dir);

		Vector3f l2crossl3 = new Vector3f();
		l2crossl3.cross(light2dir, light3dir);
		getLight3().setDirection(l2crossl3);
		l2crossl3.scale(10.f);

		// TODO:
		// teal.render.primitives.Arrow axis = new
		// teal.render.primitives.Arrow(new Vector3d(0,0,0), new
		// Vector3d(5,0,0));
		// axis.setColor(new Color(150,150,255));
		// mViewer.addDrawable(axis);

		// axis = new teal.render.primitives.Arrow(new Vector3d(0,0,0), new
		// Vector3d(0,5,0));
		// axis.setColor(new Color(150,255,150));
		// mViewer.addDrawable(axis);

		// axis = new teal.render.primitives.Arrow(new Vector3d(0,0,0), new
		// Vector3d(0,0,5));
		// axis.setColor(new Color(255,150,150));
		// mViewer.addDrawable(axis);

		initializeUI();
	}

	private void initScene()
	{
		getCanvas().setSize(this.getSize());

		mUniverse = new SimpleUniverse(getCanvas());
		mUniverse.getViewingPlatform().setNominalViewingTransform();
		initSceneRoot();
	}

	private void initSceneRoot()
	{
		sceneRoot = new BranchGroup();
		sceneRoot.setCapability(BranchGroup.ALLOW_DETACH);
		sceneRoot.setCapability(Group.ALLOW_AUTO_COMPUTE_BOUNDS_WRITE);
		sceneRoot.setCapability(Group.ALLOW_BOUNDS_READ);
		sceneRoot.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		sceneRoot.setCapability(Group.ALLOW_CHILDREN_WRITE);
		sceneRoot.setBoundsAutoCompute(true);
		/*
		 * sceneScale = new TransformGroup(); sceneScale.setCapability(Group.ALLOW_AUTO_COMPUTE_BOUNDS_WRITE);
		 * sceneScale.setCapability(Group.ALLOW_BOUNDS_READ); sceneScale.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		 * sceneScale.setCapability(Group.ALLOW_CHILDREN_WRITE); sceneScale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		 * sceneScale.setBoundsAutoCompute(true); sceneTrans = new TransformGroup(); sceneTrans.setCapability(Group.ALLOW_AUTO_COMPUTE_BOUNDS_WRITE);
		 * sceneTrans.setCapability(Group.ALLOW_BOUNDS_READ); sceneTrans.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		 * sceneTrans.setCapability(Group.ALLOW_CHILDREN_WRITE); sceneTrans.setCapability(Group.ALLOW_CHILDREN_READ);
		 * sceneTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); sceneTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		 * sceneTrans.setBoundsAutoCompute(true); sceneScale.addChild(sceneTrans); sceneScale.setBoundsAutoCompute(true); Transform3D t3d = new Transform3D();
		 * t3d.setScale(new Vector3d(0.2, 0.2, 0.2)); sceneScale.setTransform(t3d); sceneRoot.addChild(sceneScale);
		 */
		float[] bgColor = new float[4];
		getBackground().getComponents(bgColor);
		bgNode = new Background(bgColor[0], bgColor[1], bgColor[2]);
		bgNode.setApplicationBounds(sInfiniteBounds);
		bgNode.setCapability(Group.ALLOW_BOUNDS_READ);
		bgNode.setCapability(Background.ALLOW_COLOR_READ);
		bgNode.setCapability(Background.ALLOW_COLOR_WRITE);
		sceneRoot.addChild(bgNode);

		mLightSwitch = new Switch(Switch.CHILD_ALL);
		mLightSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
		sceneRoot.addChild(mLightSwitch);

		mBehaviorMgr = new StarContainerBehavior();
		mBehaviorMgr.setSchedulingBounds(StarContainer.sInfiniteBounds);
		mBehaviorMgr.setViewer(this);
		mBehaviorMgr.setRefreshOnDrag(true);
		((StarContainerBehavior) mBehaviorMgr).addToScene(sceneRoot);

		mBehaviorMgr.setNavigationMode(StarContainerBehavior.ORBIT);

		setDefaultLights();
		mUniverse.addBranchGraph(sceneRoot);

		pickCanvas = new PickCanvas(getCanvas(), sceneRoot);

		sceneObjects = new BranchGroup();
		sceneObjects.setCapability(BranchGroup.ALLOW_DETACH);
		sceneObjects.setCapability(Group.ALLOW_AUTO_COMPUTE_BOUNDS_WRITE);
		sceneObjects.setCapability(Group.ALLOW_BOUNDS_READ);
		sceneObjects.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		sceneObjects.setCapability(Group.ALLOW_CHILDREN_WRITE);
		sceneRoot.addChild(sceneObjects);
	}

	private void initializeUI()
	{
		clearUI();
	}

	public void removeNotify()
	{
		getAdapter().setParent(null);
		if (getLayout() instanceof EventController)
		{
			getAdapter().removeComponent((EventController) getLayout());
		}
		getAdapter().removeHandled(ResetSceneEvent.class);
		super.removeNotify();
	}

	public synchronized void setBounds(int x, int y, int width, int height)
	{
		super.setBounds(x, y, width, height);
		getCanvas().setSize(width, height);
	}

	private Insets insets = new Insets(20, 20, 20, 20);

	public Insets getInsets()
	{
		return insets;
	}

	public Insets getInsets(Insets insets)
	{
		return this.insets;
	}

	public void setInsets(Insets insets)
	{
		this.insets = insets;
	}

	public SimpleUniverse getUniverse()
	{
		return mUniverse;
	}

	public void removeBranchGroup(BranchGroup bg)
	{
		if (bg.isLive())
		{
			bg.detach();
		}
		Node parentnode = bg.getParent();
		if (parentnode == null)
		{
			sceneObjects.removeChild(bg);
		}
	}

	public void addBehavior(Behavior b)
	{
		mUniverse.getLocale().removeBranchGraph(sceneRoot);
		sceneRoot.addChild(b);
		mUniverse.getLocale().addBranchGraph(sceneRoot);
	}

	public void addBranchGroup(BranchGroup bg)
	{
		if (bg.isLive())
			return;
		Node parentnode = bg.getParent();
		if (parentnode == null)
		{
			sceneObjects.addChild(bg);
			if (resetScenePending)
			{
				resetScenePending = false;
				resetScene();
			}
		}
	}

	private AmbientLight ambientLightNode;
	private DirectionalLight light1;
	private DirectionalLight light2;
	private DirectionalLight light3;

	private void setDefaultLights()
	{
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100000.0);

		Color3f ambientColor = new Color3f(0.5f, 0.5f, 0.5f);
		ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		ambientLightNode.setCapability(Light.ALLOW_COLOR_WRITE);
		mLightSwitch.addChild(ambientLightNode);

		Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
		Color3f light2Color = new Color3f(0.3f, 0.3f, 0.4f);

		Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
		Vector3f light2Direction = new Vector3f(-6.0f, -2.0f, -1.0f);
		Vector3f light3Direction = new Vector3f(-6.0f, 2.0f, 1.0f);

		light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);
		light1.setCapability(DirectionalLight.ALLOW_STATE_WRITE);
		light1.setEnable(true);
		light1.setCapability(DirectionalLight.ALLOW_DIRECTION_READ);
		light1.setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);

		light2 = new DirectionalLight(light2Color, light2Direction);
		light2.setInfluencingBounds(bounds);
		light2.setCapability(DirectionalLight.ALLOW_STATE_WRITE);
		light2.setEnable(true);
		light2.setCapability(DirectionalLight.ALLOW_DIRECTION_READ);
		light2.setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);

		light3 = new DirectionalLight(light1Color, light3Direction);
		light3.setInfluencingBounds(bounds);
		light3.setCapability(DirectionalLight.ALLOW_STATE_WRITE);
		light3.setEnable(true);
		light3.setCapability(DirectionalLight.ALLOW_DIRECTION_READ);
		light3.setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);

		PlatformGeometry platformGeometry = new PlatformGeometry();
		platformGeometry.addChild(light1);
		platformGeometry.addChild(light2);
		platformGeometry.addChild(light3);
		getUniverse().getViewingPlatform().setPlatformGeometry(platformGeometry);
	}

	public Transform3D getViewTransform()
	{
		Transform3D trans = new Transform3D();
		mUniverse.getViewingPlatform().getViewPlatformTransform().getTransform(trans);
		return trans;
	}

	private DirectionalLight getLight2()
	{
		return light2;
	}

	private DirectionalLight getLight3()
	{
		return light3;
	}

	public PickCanvas getPickCanvas()
	{
		return pickCanvas;
	}

	Transform3D pTransform = new Transform3D();

	public void resetScene()
	{
		if (sceneObjects.getCapability(BranchGroup.ALLOW_CHILDREN_READ) && sceneObjects.numChildren() == 0)
		{
			resetScenePending = true;
		}
		BoundingSphere sphere = new BoundingSphere(sceneRoot.getBounds());
		Point3d p = new Point3d();
		sphere.getCenter(p);
		getUniverse().getViewer().getView().setFrontClipDistance(sphere.getRadius() / 100);
		getUniverse().getViewer().getView().setBackClipDistance(sphere.getRadius() * 10);
		Vector3d upDirection = new Vector3d(0., 1., 0.);
		Transform3D vTrans = new Transform3D();
		Point3d viewPlatformPos = new Point3d(0, 0, sphere.getRadius() * 2 * 2.5);
		pTransform.transform(viewPlatformPos);
		vTrans.lookAt(viewPlatformPos, new Point3d(0, 0, 0), upDirection);
		vTrans.invert();
		getUniverse().getViewingPlatform().getViewPlatformTransform().setTransform(vTrans);
		boolean setTrans = false;
		if (getUniverse().getViewingPlatform().getViewPlatformBehavior() instanceof OrbitBehavior)
		{
			OrbitBehavior b = (OrbitBehavior) getUniverse().getViewingPlatform().getViewPlatformBehavior();
			double val = sphere.getRadius() / 3.7;
			b.setTransFactors(val, val);
			setTrans = true;
		}
		System.out.println("Reset scene " + sphere.getRadius() + " " + p + " translation reset: " + setTrans);
		if( sphere.getRadius() <= 0 )
		{
			resetScenePending = true ;
		}
	}

	public void setResetSceneTransform(Transform3D pTransform)
	{
		if (pTransform == null)
		{
			this.pTransform = new Transform3D();
		}
		else
		{
			this.pTransform = pTransform;
		}
	}

	public void eventRaised(final Event event)
	{
		super.eventRaised(event);
		if (event instanceof ResetSceneEvent)
		{
			handleEvent((ResetSceneRaiser) event.getSource());
		}
	}

	private void handleEvent(ResetSceneRaiser event)
	{
		resetScene();
	}

	Fog fog = null;

	public void setFog(boolean visible)
	{
		if (fog == null && visible)
		{
			LinearFog fog = new LinearFog(new Color3f(.7f, .7f, .7f), 100, 200);
			fog.setInfluencingBounds(sInfiniteBounds);
			this.fog = fog;
			mUniverse.getLocale().removeBranchGraph(sceneRoot);
			sceneRoot.addChild(fog);
			mUniverse.getLocale().addBranchGraph(sceneRoot);
		}
		else if (fog != null && !visible)
		{
			mUniverse.getLocale().removeBranchGraph(sceneRoot);
			sceneRoot.removeChild(fog);
			mUniverse.getLocale().addBranchGraph(sceneRoot);
			fog = null;
		}
	}

	public PaintHook getPaintHook()
	{
		return getCanvas().getPaintHook();
	}

	public void setPaintHook(PaintHook paintHook)
	{
		getCanvas().setPaintHook(paintHook);
	}

}
