package star.annotations;

import java.io.PrintWriter;
import java.util.HashSet;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;

public abstract class AbstractProcessor implements SettableEnvironment
{
	AnnotationProcessorEnvironment environment = null;

	public void setEnvironment(AnnotationProcessorEnvironment environment)
	{
		this.environment = environment;
	}

	HashSet<Declaration> populate(String declName)
	{
		HashSet<Declaration> set = new HashSet<Declaration>();
		try
		{
			AnnotationTypeDeclaration atd = (AnnotationTypeDeclaration) environment.getTypeDeclaration(declName);
			for (Declaration declaration : environment.getDeclarationsAnnotatedWith(atd))
			{
				set.add(declaration);
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		return set;
	}

	String getRaiser(String handledClass)
	{
		if( handledClass.indexOf("Raiser") != -1 )
		{
			return handledClass;
		}
		if( handledClass.indexOf("Event") == -1 )
		{
			return handledClass + "Raiser";
		}
		return handledClass.substring(0, handledClass.lastIndexOf("Event")) + "Raiser";
	}

	String getEvent(String handledClass)
	{
		if( handledClass.indexOf("Event") != -1 )
		{
			return handledClass;
		}
		if( handledClass.indexOf("Raiser") == -1 )
		{
			return handledClass + "Event";
		}
		return handledClass.substring(0, handledClass.lastIndexOf("Raiser")) + "Event";
	}

	void writeClass(star.annotations.GeneratedClass generatedClass, String className)
	{
		try
		{
			StringBuffer message = new StringBuffer();
			try
			{
				Filer f = environment.getFiler();
				//environment.getMessager().printNotice("Writing class: " + className );
				PrintWriter pw = f.createSourceFile(className);
				pw.print(generatedClass.getSource());
				pw.close();
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
			if( message.length() != 0 )
			{
				environment.getMessager().printNotice(message.toString());
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}
