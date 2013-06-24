package star.annotations;

import java.util.HashSet;
import java.util.Iterator;

import star.event.Event;

import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.type.InterfaceType;

public class RaiserProcessor extends AbstractProcessor
{
	public void process()
	{

		HashSet<Declaration> set = new HashSet<Declaration>();
		set.addAll(populate(Raiser.class.getName()));
		environment.getMessager().printNotice(set.toString());
		for (Declaration declaration : set)
		{
			processRaiserAnnotation(declaration);
		}
	}

	private void processRaiserAnnotation(Declaration declaration)
	{
		Raiser r = declaration.getAnnotation(Raiser.class);
		if( r != null )
		{
			String raiserName = declaration.toString();
			String className = getEvent(declaration.toString());
			String extendsName = Event.class.getName();
			if( declaration instanceof InterfaceDeclaration )
			{
				InterfaceDeclaration i = (InterfaceDeclaration) declaration;
				Iterator<InterfaceType> iter = i.getSuperinterfaces().iterator();
				while( iter.hasNext() )
				{
					InterfaceType next = iter.next();
					if( next.toString().endsWith("Raiser") )
					{
						extendsName = getEvent(next.toString());
						break;
					}
				}
			}
			star.annotations.GeneratedClass generatedClass = new star.annotations.GeneratedClass(className, GeneratedClass.PUBLIC);
			generatedClass.setParent(extendsName);
			generateEventConstructor(generatedClass, className, raiserName);
			generateEventRaise(generatedClass, declaration);
			writeClass(generatedClass, className);
		}
	}

	private void generateEventConstructor(star.annotations.GeneratedClass c, String className, String raiserName)
	{
		c.addMethod(GeneratedClass.PUBLIC, "", className.substring(className.lastIndexOf('.') + 1), new String[] { raiserName }, new String[] { "raiser" }, "super( raiser ) ;");
		c.addMethod(GeneratedClass.PUBLIC, "", className.substring(className.lastIndexOf('.') + 1), new String[] { "star.event.Raiser", "boolean" }, new String[] { "raiser", "valid" }, "super( raiser , valid ) ;");
		c.addMethod(GeneratedClass.PUBLIC, "", className.substring(className.lastIndexOf('.') + 1), new String[] { getEvent(raiserName) }, new String[] { "event" }, "super( event ) ;");
		c.addMember(GeneratedClass.PRIVATE | GeneratedClass.STATIC | GeneratedClass.FINAL, "long", "serialVersionUID", "1L");
	}

	private void generateEventRaise(star.annotations.GeneratedClass c, Declaration declaration)
	{
		int concurreny = declaration.getAnnotation(Raiser.class).concurrency();
		String method = "raiseImpl();";
		switch( concurreny )
		{
		case Raiser.POOLED:
		case Raiser.SWING:
			method = "" + "\njavax.swing.SwingUtilities.invokeLater(new Runnable()" + "\n{" + "\n" + "\n\tpublic void run()" + "\n\t{" + "\n\t\traiseImpl();" + "\n\t}" + "\n\t});";
			break;
		case Raiser.ASYNC:
			method = "" + "\nnew Thread(new Runnable()" + "\n{" + "\n" + "\n\tpublic void run()" + "\n\t{" + "\n\t\traiseImpl();" + "\n\t}" + "\n\t}).start();";
			break;
		}
		;
		c.addMethod(GeneratedClass.PUBLIC, "void", "raise", null, null, method);

	}

}
