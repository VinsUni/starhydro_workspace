package app.viewers;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.event.Listener;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.teal.MyShapeNode;
import star.hydrology.events.RenderableVisibleRaiser;
import star.hydrology.events.StreamOrderStatisticsRaiser;
import star.hydrology.events.ViewerChangeEvent;
import star.hydrology.events.ViewerChangeRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import utils.FloatArrayList;
import app.worker.streamnetwork.Channel;
import app.worker.streamnetwork.Stream;

@SignalComponent(extend = CustomAbstractMapViewer.class)
public class StreamOrderViewer extends StreamOrderViewer_generated implements Listener, ViewerChangeRaiser, RenderableVisibleRaiser
{

	private Stream root = null;
	private int[] streamOrderCount = null;
	private FloatArrayList[] coords = null;

	@Handles(raises = { ViewerChangeRaiser.class })
	public void setStreamOrderStatistics(StreamOrderStatisticsRaiser r)
	{
		setStreamOrderStatistics(r.getRootStream(), r.getOrderCount());
		(new ViewerChangeEvent(this)).raise();
	}

	private int[] getStreamOrderCount()
	{
		return streamOrderCount;
	}

	private Stream getRootStream()
	{
		return root;
	}

	private void setStreamOrderStatistics(Stream root, int[] orderCount)
	{
		this.root = root;
		this.streamOrderCount = orderCount;
		dirtyFlag = -1;
		updateVisibleLayer();
	}

	public int getKind()
	{
		return LayerConstants.STREAMNETWORK;
	}

	public String getViewerName()
	{
		return "Stream Order Network";
	}

	protected void makeGeometry()
	{
		makeGeometryIndexes2();
		MyShapeNode branchGroup = new MyShapeNode();
		if (coords != null)
		{
			for (int order = 0; order < coords.length; order++)
			{
				float[] lineCoords = coords[order].getFloatArray();
				if (lineCoords.length != 0)
				{
					LineArray array = new LineArray(lineCoords.length / 3, 0 | IndexedLineArray.COORDINATES);
					array.setCoordinates(0, lineCoords);
					Appearance appearance = new Appearance();
					appearance.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);

					ColoringAttributes coloringAttributes = new ColoringAttributes(new Color3f(getPalette().getColor(order * 1.0f / coords.length)), ColoringAttributes.SHADE_GOURAUD);
					coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
					appearance.setColoringAttributes(coloringAttributes);
					LineAttributes lineAttributes = new LineAttributes();
					lineAttributes.setLineWidth(order);
					lineAttributes.setLinePattern(LineAttributes.PATTERN_SOLID);
					appearance.setLineAttributes(lineAttributes);
					branchGroup.set(array, appearance);
					setBranchGroup(branchGroup);
				}
			}
		}
		coords = null;
	}

	void makeGeometryIndexes2()
	{
		if (getRootStream() != null && getStreamOrderCount() != null)
		{
			FloatArrayList list[] = new FloatArrayList[getRootStream().getStreamOrder() + 1];
			for (int i = 0; i < list.length; i++)
			{
				list[i] = new FloatArrayList();
			}
			walk(getRootStream(), list);
			this.coords = list;
			dirtyFlag &= ~GEOMETRYINDEX;
		}
	}

	@Override
	public PaletteRenderableLayer getLayer( int kind )
	{
		if( kind != getKind() )
		{
			return super.getLayer(kind);
		}
		return new PaletteRenderableLayer()
		{

			public Grid getDataset()
			{
				// TODO Auto-generated method stub
				return null;
			}

			public String getLayerName()
			{
				// TODO Auto-generated method stub
				return "Dummy Layer";
			}

		};
	}

	private void walk(Stream localRoot, FloatArrayList[] list)
	{
		final float delta = 1f;
		if (localRoot != null)
		{
			for (Channel c : localRoot.getParts())
			{
				int order = c.getChannelOrder();
				Point3f center = getCenter();
				for (int i = 0; i < c.getPointsSize() - 1; i++)
				{
					float[] p1 = c.getPoint(i);
					float[] p2 = c.getPoint(i + 1);
					p1[0] -= center.x;
					p1[1] -= center.y;
					p1[2] += delta;
					p2[0] -= center.x;
					p2[1] -= center.y;
					p2[2] += delta;
					list[order].add(p1);
					list[order].add(p2);
				}
			}
			for (Stream c : localRoot.getChildren())
			{
				walk(c, list);
			}
		}
	}

}
