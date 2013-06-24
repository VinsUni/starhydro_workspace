package star.annotations;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;

public interface SettableEnvironment extends AnnotationProcessor
{

	public abstract void setEnvironment(AnnotationProcessorEnvironment environment);

}