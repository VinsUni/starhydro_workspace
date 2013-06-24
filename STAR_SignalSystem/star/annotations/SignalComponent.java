package star.annotations;

public @interface SignalComponent
{
	public Class extend() default Object.class;

	public Class[] handles() default {};

	public Class[] raises() default {};

	public Class[] contains() default {};

	public Class[] excludeExternal() default {};

	public Class[] excludeInternal() default {};

	public Class[] components() default {};

}
