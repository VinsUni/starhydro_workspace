package star.annotations;

public @interface Generator
{
	public Class extend() default Object.class;
}
