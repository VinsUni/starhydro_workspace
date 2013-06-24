package star.hydrology.data.teal;

import java.awt.Color;
import java.util.HashSet;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

public class MyShapeNode extends BranchGroup
{

	private HashSet<Shape3D> shapes = new HashSet<Shape3D>();

	public MyShapeNode()
	{
		super();
		setCapability(BranchGroup.ALLOW_DETACH);
		setCapability(Node.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
	}

	public void set(GeometryArray ta, Appearance app)
	{
		Shape3D s = new Shape3D();
		s.setGeometry(ta, 0);
		s.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		s.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
		s.setAppearance(app);
		shapes.add(s);
		addChild(s);
	}
	
	public void setAppearance( Appearance app )
	{
		for( Shape3D shape : shapes )
		{
			shape.setAppearance(app);
		}
	}

	public Appearance getDefaultAppearance()
	{
		Appearance app = new Appearance();
		Color3f c = new Color3f(Color.BLUE);
		Color3f emissive = new Color3f(Color.blue);
		float shininess = .5f;
		
		// Color
		ColoringAttributes coloringAttributes = new ColoringAttributes(new Color3f(c), ColoringAttributes.SHADE_GOURAUD);
		coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
		app.setColoringAttributes(coloringAttributes);

		// Material
		Material mat = app.getMaterial();
		if (mat == null)
		{
			mat = new Material();
			mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
			mat.setCapability(Material.ALLOW_COMPONENT_READ);
		}
		
		Color3f c3 = new Color3f(c);
		mat.setDiffuseColor(c3);
		mat.setSpecularColor(c3);
		c3.scale(0.9f);
		mat.setAmbientColor(c3);

		if (emissive != null)
		{
			mat.setEmissiveColor(new Color3f(emissive));
		}
		mat.setShininess(shininess * 128.f);
		app.setMaterial(mat);

		return app;
	}

	public Appearance getTextureAppearance(Texture tx)
    {
	    Appearance app = new Appearance();
	    
	    RenderingAttributes rat = new RenderingAttributes();
	    rat.setAlphaTestFunction(rat.GREATER_OR_EQUAL);
	    rat.setAlphaTestValue(.9f);
	    app.setRenderingAttributes(rat);
		app.setTexture(tx);
		TextureAttributes texAttrib = new TextureAttributes();		
		texAttrib.setPerspectiveCorrectionMode(TextureAttributes.FASTEST);
		TransparencyAttributes transp = new TransparencyAttributes();
		transp.setTransparency(TransparencyAttributes.BLENDED);
		transp.setTransparency(.5f);
		app.setTextureAttributes(texAttrib);
		app.setTransparencyAttributes(transp);		
		return app;
    }

	public Appearance getTextureAppearance(Texture tx, float xfactor, float yfactor )
    {
	    Appearance app = new Appearance();
	    
	    RenderingAttributes rat = new RenderingAttributes();
	    rat.setAlphaTestFunction(rat.GREATER_OR_EQUAL);
	    rat.setAlphaTestValue(.9f);
	    app.setRenderingAttributes(rat);
		app.setTexture(tx);
		TextureAttributes texAttrib = new TextureAttributes();		
		texAttrib.setPerspectiveCorrectionMode(TextureAttributes.FASTEST);
		TransparencyAttributes transp = new TransparencyAttributes();
		transp.setTransparency(TransparencyAttributes.BLENDED);
		transp.setTransparency(.5f);
		app.setTextureAttributes(texAttrib);
		app.setTransparencyAttributes(transp);		
		return app;
    }
};
