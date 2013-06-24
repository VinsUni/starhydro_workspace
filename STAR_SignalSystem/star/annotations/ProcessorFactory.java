package star.annotations;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class ProcessorFactory implements AnnotationProcessorFactory
{

	private static final Collection<String> supportedAnnotations = Arrays.asList("star.annotations.*");

	private static final Collection<String> supportedOptions = Collections.emptySet();

	SettableEnvironment processor = null;

	public Collection<String> supportedAnnotationTypes()
	{
		return supportedAnnotations;
	}

	public Collection<String> supportedOptions()
	{
		return supportedOptions;
	}

	String getClasspath(AnnotationProcessorEnvironment environment)
	{
		String classpath = environment.getOptions().get("-classpath");
		if( classpath == null )
			classpath = environment.getOptions().get("classpath");
		if( classpath == null )
			classpath = "";
		return classpath;
	}

	public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> arg0, AnnotationProcessorEnvironment arg1)
	{
		if( arg0.isEmpty() )
		{
			return AnnotationProcessors.NO_OP;
		}
		else
		{

			SettableEnvironment ap = null;
			try
			{
				if( processor == null )
				{
					ArrayList<URL> urls = new ArrayList<URL>();
					for (String s : getClasspath(arg1).split(File.pathSeparator))
					{
						urls.add((new File(s)).toURI().toURL());
					}
					arg1.getMessager().printNotice("ClassPath = " + urls);
					URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());

					Class obj = classLoader.loadClass("star.annotations.Processor");
					ap = (SettableEnvironment) obj.newInstance();
					ap.setEnvironment(arg1);
					processor = ap;
				}
				else
				{
					ap = processor;
				}
			}
			catch( NoClassDefFoundError ex )
			{
				ex.printStackTrace();
			}
			catch( Exception ex2 )
			{
				ex2.printStackTrace();
			}
			return ap;
		}
	}

}
