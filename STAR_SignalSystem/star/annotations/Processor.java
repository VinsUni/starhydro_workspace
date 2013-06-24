package star.annotations;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;

public class Processor implements AnnotationProcessor, SettableEnvironment
{

	AnnotationProcessorEnvironment environment = null;

	public void setEnvironment(AnnotationProcessorEnvironment environment)
	{
		this.environment = environment;
	}

	String getClasspath()
	{
		String classpath = environment.getOptions().get("-classpath");
		if( classpath == null )
			classpath = environment.getOptions().get("classpath");
		if( classpath == null )
			classpath = "";
		return classpath;
	}

	public void process()
	{
		try
		{
			ArrayList<URL> urls = new ArrayList<URL>();
			for (String s : getClasspath().split(File.pathSeparator))
			{
				urls.add((new File(s)).toURI().toURL());
			}
			URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
			process(classLoader, ClassesProcessor.class.getName());
			process(classLoader, RaiserProcessor.class.getName());
		}
		catch( MalformedURLException e )
		{
			e.printStackTrace();
		}
	}

	void process(ClassLoader classLoader, String className)
	{
		SettableEnvironment p;
		try
		{
			p = (SettableEnvironment) classLoader.loadClass(className).newInstance();
			p.setEnvironment(environment);
			p.process();
		}
		catch( InstantiationException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( ClassNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
