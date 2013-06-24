package app;

import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

import mit.awt.xPanel;
import star.j3d.ui.StarContainer;
import star.j3d.ui.ViewerBehavior;

public class HydrologyContainer extends xPanel
{

	private static final long serialVersionUID = 1L;
	private StarContainer starContainer = null;

	public StarContainer getStarContainer()
	{
		if (null == this.starContainer)
		{
			this.starContainer = new StarContainer();
			this.starContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		return this.starContainer;
	}

	public void addNotify()
	{
		super.addNotify();
		setLayout(new BorderLayout());
		setBackground(new java.awt.Color(.1f, .15f, .0f));
		add(getStarContainer(), BorderLayout.CENTER);
		initializeUniverseView();

		PickerBehaviour selectManager = new PickerBehaviour();
		selectManager.setPickCanvas(getStarContainer().getPickCanvas());
		getStarContainer().addBehavior(selectManager);
		getAdapter().addComponent(selectManager);

		setResetSceneTransform();

		ViewerBehavior mBehaviorMgr = new ViewerBehavior();
		((ViewerBehavior) mBehaviorMgr).setSchedulingBounds(StarContainer.sInfiniteBounds);
		mBehaviorMgr.setViewer(getStarContainer());
		mBehaviorMgr.setRefreshOnDrag(false);
		mBehaviorMgr.setNavigationMode(ViewerBehavior.ORBIT | ViewerBehavior.ABOVE_GROUND);
		mBehaviorMgr.setVpTranslateScale(new Vector3d(1000, 1000, 2));
		getStarContainer().addBehavior(mBehaviorMgr);
	}

	private void setResetSceneTransform()
	{
		Transform3D t3d = new Transform3D();
		t3d.rotX(Math.PI / 5);
		getStarContainer().setResetSceneTransform(t3d);
	}

	private void initializeUniverseView()
	{
		getStarContainer().getUniverse().getViewer().getView().setBackClipDistance(10e12);
		getStarContainer().getUniverse().getViewer().getView().setFrontClipDistance(0.01);
		getStarContainer().getPickCanvas().setTolerance(0.f);
	}
}
