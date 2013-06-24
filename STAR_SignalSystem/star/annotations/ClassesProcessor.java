package star.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import star.event.Adapter;
import star.event.Event;
import star.event.EventController;

import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.MirroredTypeException;

public class ClassesProcessor extends AbstractProcessor
{
	private boolean profile = true;
	private boolean debug = false;
	private final static String EXTENDS = "extend";
	private final static String HANDLES = "handles";
	private final static String RAISES = "raises";
	private final static String CONTAINS = "contains";
	private final static String EXCLUDEINTERNAL = "excludeInternal";
	private final static String EXCLUDEEXTERNAL = "excludeExternal";
	private final static String COMPONENTS = "components";

	public void process()
	{
		processClasses();

	}

	private void processClasses()
	{
		HashMap<Declaration, HashSet<FieldDeclaration>> fieldMap = new HashMap<Declaration, HashSet<FieldDeclaration>>();

		HashMap<Declaration, HashSet<MethodDeclaration>> methodMap = new HashMap<Declaration, HashSet<MethodDeclaration>>();
		Collection<Declaration> methodSet = environment.getDeclarationsAnnotatedWith((AnnotationTypeDeclaration) environment.getTypeDeclaration(Handles.class.getName()));
		for (Declaration declaration : methodSet)
		{
			MethodDeclaration md = (MethodDeclaration) declaration;
			Declaration type = md.getDeclaringType();
			if (!methodMap.containsKey(type))
			{
				methodMap.put(type, new HashSet<MethodDeclaration>());
			}
			methodMap.get(type).add(md);
		}

		HashSet<Declaration> set = new HashSet<Declaration>();
		set.addAll(populate(SignalComponent.class.getName()));
		set.addAll(populate(Preferences.class.getName()));
		set.addAll(populate(Properties.class.getName()));
		for (Declaration declaration : set)
		{
			try
			{
				processListenerAnnotations(declaration, methodMap.get(declaration), fieldMap.get(declaration));
			}
			catch (Exception ex)
			{
				environment.getMessager().printError(declaration + " " + ex.toString());
			}
		}
	}

	private void processListenerAnnotations(Declaration declaration, HashSet<MethodDeclaration> methodDeclarations, HashSet<FieldDeclaration> fieldDeclarations)
	{
		String className = getClassName(declaration);
		String extendsName = Object.class.getName();
		try
		{
			if (getClasses(declaration, EXTENDS).iterator().hasNext())
			{
				extendsName = getClasses(declaration, EXTENDS).iterator().next().toString();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		star.annotations.GeneratedClass generatedClass = new star.annotations.GeneratedClass(className, GeneratedClass.PUBLIC | GeneratedClass.ABSTRACT);
		generatedClass.setParent(extendsName);
		generatedClass.addMember(GeneratedClass.PRIVATE | GeneratedClass.STATIC | GeneratedClass.FINAL, "long", "serialVersionUID", "1L");
		if (hasAnnotation(declaration, SignalComponent.class))
		{
			generateEventController(declaration, extendsName, generatedClass, methodDeclarations);
			generateListener(declaration, extendsName, generatedClass, methodDeclarations);
			generateRaiser(declaration, generatedClass, methodDeclarations);
		}
		if (hasAnnotation(declaration, Preferences.class))
		{
			generatePreferences(declaration, generatedClass, getAnnotatedClassName(declaration));
		}
		if (hasAnnotation(declaration, Properties.class))
		{
			generateProperties(declaration, generatedClass, getAnnotatedClassName(declaration));
		}

		writeClass(generatedClass, className);

	}

	private void generateProperties(Declaration declaration, star.annotations.GeneratedClass generatedClass, String className)
	{
		Properties properties = declaration.getAnnotation(Properties.class);
		Property[] props = properties.value();
		for (Property p : props)
		{
			String name = p.name();
			String type = null;
			String value = p.value();
			String params = p.params();
			boolean isArray = false;
			try
			{
				p.type().toString();
			}
			catch (MirroredTypeException ex)
			{
				type = ex.getTypeMirror().toString();
			}
			try
			{
				isArray = p.type().isArray();
			}
			catch (MirroredTypeException ex)
			{
				isArray = ex.getTypeMirror().toString().endsWith("[]");
			}

			if (value.equals("new"))
			{
				value = "new " + type + params + "()";
			}
			generatedClass.addMember(GeneratedClass.PRIVATE, type + params, name, value.equals("") ? null : value);
			if (p.getter() != Property.NOT_GENERATED)
			{
				String body = "return this." + name + " ;";
				generatedClass.addMethod(p.getter() == Property.PROTECTED ? GeneratedClass.PROTECTED : GeneratedClass.PUBLIC, type + params, "get" + capitalize(name), null, null, body);
				if ((type + params).equals("boolean"))
				{
					generatedClass.addMethod(p.getter() == Property.PROTECTED ? GeneratedClass.PROTECTED : GeneratedClass.PUBLIC, type + params, "is" + capitalize(name), null, null, body);
				}

			}
			if (p.getter() != Property.NOT_GENERATED && isArray)
			{
				String type2 = type.substring(0, type.length() - 2);
				String body = "return this." + name + "[i] ;";
				generatedClass.addMethod(p.getter() == Property.PROTECTED ? GeneratedClass.PROTECTED : GeneratedClass.PUBLIC, type2 + params, "get" + capitalize(name), new String[] { "int" }, new String[] { "i" }, body);
				if ((type + params).equals("boolean"))
				{
					generatedClass.addMethod(p.getter() == Property.PROTECTED ? GeneratedClass.PROTECTED : GeneratedClass.PUBLIC, type2 + params, "is" + capitalize(name), new String[] { "int" }, new String[] { "i" }, body);
				}

			}

			if (p.setter() != Property.NOT_GENERATED)
			{
				String body = "this." + name + " = " + name + " ;";
				generatedClass.addMethod(p.setter() == Property.PROTECTED ? GeneratedClass.PROTECTED : GeneratedClass.PUBLIC, "void", "set" + capitalize(name), new String[] { type + params }, new String[] { name }, body);
			}
		}
	}

	private static String capitalize(String s)
	{
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	private String getAnnotatedClassName(Declaration declaration)
	{
		return declaration.toString();
	}

	private void generateEventController(Declaration declaration, String extendsName, star.annotations.GeneratedClass generatedClass, HashSet<MethodDeclaration> methodDeclarations)
	{
		boolean hasAdapter = hasMethod(extendsName, "getAdapter", null);
		if (!hasAdapter)
		{
			generatedClass.addMember(GeneratedClass.PRIVATE, Adapter.class.getName(), "adapter", null);
			generatedClass.addMethod(GeneratedClass.PUBLIC, Adapter.class.getName(), "getAdapter", null, null, "if( adapter == null )\n{\n\tadapter = new " + Adapter.class.getName() + "(this);\n}\nreturn adapter;");
		}

		if (getClasses(declaration, COMPONENTS).size() > 0)
		{
			generatedClass.addMember(GeneratedClass.PRIVATE, "Object[]", "starcomponents", "new Object[0]");
		}

		boolean hasAddNotify = hasMethod(extendsName, "addNotify", null);
		generatedClass.addMethod(GeneratedClass.PUBLIC, "void", "addNotify", null, null, generateAddNotifyBody(declaration, hasAddNotify, methodDeclarations));

		boolean hasRemoveNotify = hasMethod(extendsName, "removeNotify", null);
		generatedClass.addMethod(GeneratedClass.PUBLIC, "void", "removeNotify", null, null, generateRemoveNotifyBody(declaration, hasRemoveNotify, methodDeclarations));
		generatedClass.addInterface(EventController.class.getName());

		generateConstructors(declaration, extendsName, generatedClass);
	}

	private void generateConstructors(Declaration declaration, String extendsName, star.annotations.GeneratedClass generatedClass)
	{
		try
		{
			// environment.getTypeDeclaration(extendsName).getClass().getConstructors()
			Constructor[] constructors = environment.getTypeDeclaration(extendsName).getClass().getConstructors();
			if (constructors == null || constructors.length == 0)
			{
				try
				{
					constructors = this.getClass().getClassLoader().loadClass(extendsName).getConstructors();
				}
				catch (Throwable t)
				{

				}
			}
			// Class.forName(extendsName).getConstructors() ;
			for (Constructor c : constructors)
			{
				int modifiers = c.getModifiers();
				if (modifiers == Modifier.PUBLIC)
				{
					// environment.getMessager().printNotice( c.toString() ) ;
					// environment.getMessager().printNotice( c.getName() ) ;
					// environment.getMessager().printNotice( Arrays.toString( c.getGenericExceptionTypes() ) ) ;
					ArrayList<String> throwables = new ArrayList<String>();
					for (Type t : c.getGenericExceptionTypes())
					{
						if (t instanceof Class)
						{
							throwables.add(((Class) t).getCanonicalName());
						}
					}
					ArrayList<String> args = new ArrayList<String>();
					ArrayList<String> names = new ArrayList<String>();
					StringBuffer body = new StringBuffer();
					body.append("super( ");
					int i = 0;
					for (Class cls : c.getParameterTypes())
					{
						args.add(cls.getCanonicalName());
						String name = cls.getSimpleName();
						boolean suffix;
						if (name.endsWith("[]"))
						{
							name = name.substring(0, name.length() - 2);
						}
						if (!Character.isLowerCase(name.charAt(0)))
						{
							name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
							suffix = false;
						}
						else
						{
							suffix = true;
						}
						if (!names.contains(name) && !suffix)
						{
							names.add(name);
							body.append(name);
						}
						else
						{
							names.add(name + i);
							body.append(name + i);
							i++;
						}
						body.append(",");
					}
					body.setCharAt(body.length() - 1, ')');
					body.append(";");
					generatedClass.addMethod(GeneratedClass.PUBLIC, "", getClasslessClassNameOnly(generatedClass.className), args.toArray(new String[0]), names.toArray(new String[0]), throwables.toArray(new String[0]), body.toString());
				}
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}

	private void addRaiser(star.annotations.GeneratedClass generatedClass, String raisedClass)
	{
		generatedClass.addInterface(raisedClass);
		if (!generatedClass.hasMethod("raise_" + getClasslessClassNameOnly(getEvent(raisedClass))))
		{
			generatedClass.addMethod(GeneratedClass.PUBLIC, "void", "raise_" + getClasslessClassNameOnly(getEvent(raisedClass)), null, null, "(new " + getEvent(raisedClass) + "(this)).raise();");
		}
	}

	private void generateRaiser(Declaration declaration, star.annotations.GeneratedClass generatedClass, HashSet<MethodDeclaration> methodDeclarations)
	{
		Iterator iterator = getClasses(declaration, RAISES).iterator();
		while (iterator.hasNext())
		{
			addRaiser(generatedClass, getClasslessClass(iterator.next().toString()));
		}
	}

	private boolean generateHandleEventListener(Declaration declaration, String extendsName, star.annotations.GeneratedClass generatedClass, HashSet<MethodDeclaration> methodDeclarations)
	{
		boolean ret = false;
		TreeSet handled = getClasses(declaration, HANDLES);
		if (handled.size() != 0)
		{
			generatedClass.addInterface(star.event.Listener.class.getName());
			generatedClass.addMethod(GeneratedClass.PRIVATE, "void", "eventRaisedEventHandle", new String[] { Event.class.getName() }, new String[] { "event" }, generateEventRaisedBody(declaration, extendsName));
			Iterator iterator = handled.iterator();
			while (iterator.hasNext())
			{
				String handledClass = getClasslessClass(iterator.next().toString());
				generatedClass.addMethod(GeneratedClass.ABSTRACT | GeneratedClass.DEFAULT, "void", "handleEvent", new String[] { getRaiser(handledClass) }, new String[] { "raiser" }, null);
				generatedClass.addMethod(GeneratedClass.DEFAULT, "void", "handleInvalidEvent", new String[] { getRaiser(handledClass) }, new String[] { "raiser" }, "");
			}
			ret = true;
		}
		return ret;
	}

	private boolean generateHandlesListener(Declaration declaration, String extendsName, star.annotations.GeneratedClass generatedClass, HashSet<MethodDeclaration> methodDeclarations)
	{
		boolean ret = false;
		StringBuffer body = new StringBuffer();
		if (methodDeclarations == null)
			return ret;
		for (MethodDeclaration m : methodDeclarations)
		{
			StringBuffer prefixCode = new StringBuffer();
			Collection<ParameterDeclaration> params = m.getParameters();
			Handles handles = m.getAnnotation(Handles.class);
			if (params.size() == 1 && handles != null)
			{
				ret = true;
				for (String raiser : getClasses(m, RAISES, Handles.class))
				{
					prefixCode.append("(new " + getEvent(getClasslessClass(raiser)) + "(this,false)).raise();\n");
					addRaiser(generatedClass, getRaiser(getClasslessClass(raiser)));
				}
				if (handles.handleValid())
				{
					body.append("if( event.getClass().getName().equals( \"" + getEvent(getClasslessClass(params.iterator().next().getType().toString())) + "\" ) && event.isValid() ) " //
					        + "\n{" //
					        + "\n\t" + prefixCode + " utils.Runner.runOnThread(new Runnable() { public void run() { "//
					        + (profile ? "\n\t long start = System.nanoTime();" : "") //
					        + "\n\t" + m.getSimpleName() + "( (" + getClasslessClass(params.iterator().next().getType().toString()) + ")event.getSource());" //
					        + (profile ? "\n\t long end = System.nanoTime();\n\t if( end - start > " + TIME + " ) " //
					                + "{ System.out.println( this.getClass().getName() + \"." + m.getSimpleName() + " \"  + ( end-start )/" + STEP + " ); } " : "") //
					        + "}}" //
					        + ",this," + handles.concurrency() + ");" + "\n}");
				}
				else
				{
					body.append("if( event.getClass().getName().equals( \"" + getEvent(getClasslessClass(params.iterator().next().getType().toString())) + "\" ) && !event.isValid() ) " + "\n{" + "\n\t" + prefixCode + " utils.Runner.runOnThread(new Runnable() { public void run() { "
					        + (profile ? "\n\t long start = System.nanoTime();" : "") + "\n\t" + m.getSimpleName() + "( (" + getClasslessClass(params.iterator().next().getType().toString()) + ")event.getSource());"
					        + (profile ? "\n\t long end = System.nanoTime();\n\t if( end - start > " + TIME + " ) { System.out.println( this.getClass().getName() + \"." + m.getSimpleName() + " \"  + ( end-start )/" + STEP + " ); } " : "") + "}},this," + handles.concurrency() + ");" + "\n}");
				}
				generatedClass.addMethod(GeneratedClass.DEFAULT | GeneratedClass.ABSTRACT, m.getReturnType().toString(), m.getSimpleName(), new String[] { params.iterator().next().getType().toString() }, new String[] { params.iterator().next().getSimpleName() }, null);
			}
		}
		if (ret)
		{
			generatedClass.addMethod(GeneratedClass.PRIVATE, "void", "eventRaisedHandles", new String[] { "final " + Event.class.getName() }, new String[] { "event" }, body.toString());
		}
		return ret;
	}

	long TIME = 1000L * 1000L * 500L; // 500ms
	long STEP = 1000L * 1000L; // 1ms

	private boolean generateAndGateHandlesListener(Declaration declaration, String extendsName, star.annotations.GeneratedClass generatedClass, HashSet<MethodDeclaration> methodDeclarations)
	{
		boolean ret = false;
		StringBuffer body = new StringBuffer();
		body.append("final " + Event.class.getName() + "[] event = in_event;\n");

		if (methodDeclarations == null)
			return ret;
		for (MethodDeclaration m : methodDeclarations)
		{
			StringBuffer prefixCode = new StringBuffer();
			Collection<ParameterDeclaration> params = m.getParameters();
			Handles handles = m.getAnnotation(Handles.class);
			if (params.size() > 1 && handles != null)
			{
				ret = true;
				for (String raiser : getClasses(m, RAISES, Handles.class))
				{
					prefixCode.append("(new " + getEvent(getClasslessClass(raiser)) + "(this,false)).raise();\n");
					addRaiser(generatedClass, getRaiser(getClasslessClass(raiser)));
				}
				StringBuffer p = new StringBuffer();
				StringBuffer instances = new StringBuffer();
				int i = 0;
				for (ParameterDeclaration param : params)
				{
					p.append("(" + param.getType().toString() + ")event[" + i + "].getSource(),");
					instances.append("event[" + i + "].getClass().getName().equals( \"" + getEvent(param.getType().toString()) + "\" ) && ");
					i++;
				}
				instances.append(" true");
				String pr = p.substring(0, p.length() - 1);

				if (handles.handleValid())
				{
					body.append("if( event != null && valid && event.length == " + params.size() + " && " + instances + " )" //
					        + "\n{" //
					        + "\n\t" + prefixCode + "\n\tutils.Runner.runOnThread(new Runnable() { " //
					        + "\n\t\tpublic void run() { " + (profile ? "\n\t\t\tlong start = System.nanoTime();" : "") //
					        + "\n\t\t\t" + m.getSimpleName() + "( " + pr + " );" //
					        + (profile ? "\n\t\t\tlong end = System.nanoTime();\n\t\tif( end - start > " + TIME + " ) { System.out.println( this.getClass().getName() + \"." + m.getSimpleName() + " \"  + ( end-start )/" + STEP + " ); } " : "") + "}},this," + handles.concurrency() + ");" //
					        + "\n}\n");
				}
				else
				{
					body.append("if( !valid && " + instances + " )" //
					        + "\n{" + "\n\t" + prefixCode + "\n\tutils.Runner.runOnThread(new Runnable() { " //
					        + "\n\t public void run() { " //
					        + (profile ? "\n\tlong start = System.nanoTime();" : "") //
					        + m.getSimpleName() + "( " + pr + " );" //
					        + (profile ? "\n\tlong end = System.nanoTime();\n\t\tif( end - start > " + TIME + " ) { System.out.println( this.getClass().getName() + \"." + m.getSimpleName() + " \"  + ( end-start )/" + STEP + " ); } " : "") + "}},this," + handles.concurrency() + ");" //
					        + "\n}\n");
				}
				ArrayList<String> formalParams = new ArrayList<String>();
				ArrayList<String> formalVariables = new ArrayList<String>();
				for (ParameterDeclaration param : params)
				{
					formalParams.add(param.getType().toString());
					formalVariables.add(param.getSimpleName());
				}
				generatedClass.addMethod(GeneratedClass.DEFAULT | GeneratedClass.ABSTRACT, m.getReturnType().toString(), m.getSimpleName(), formalParams.toArray(new String[0]), formalVariables.toArray(new String[0]), null);
			}
		}
		if (ret)
		{
			generatedClass.addMethod(GeneratedClass.PRIVATE, "void", "eventAndGateRaisedHandles", new String[] { "final " + Event.class.getName(), "final boolean" }, new String[] { "in_event[]", "valid" }, body.toString());
		}
		return ret;
	}

	private void generateListener(Declaration declaration, String extendsName, star.annotations.GeneratedClass generatedClass, HashSet<MethodDeclaration> methodDeclarations)
	{
		StringBuffer eventRaiserBody = new StringBuffer();
		StringBuffer eventsRaiserBody = new StringBuffer();
		// environment.getMessager().printNotice(extendsName + " " +
		// hasMethod(extendsName, "eventRaised", new Class[] { Event.class }));
		if (hasMethod(extendsName, "eventRaised", new Class[] { Event.class }))
		{
			eventRaiserBody.append("super.eventRaised( event );\n");
		}

		if (generateHandleEventListener(declaration, extendsName, generatedClass, methodDeclarations))
		{
			eventRaiserBody.append("eventRaisedEventHandle(event);\n");
		}
		if (generateHandlesListener(declaration, extendsName, generatedClass, methodDeclarations))
		{
			eventRaiserBody.append("eventRaisedHandles(event);\n");
		}
		if (generateAndGateHandlesListener(declaration, extendsName, generatedClass, methodDeclarations))
		{
			eventsRaiserBody.append("eventAndGateRaisedHandles(event,valid);");
		}

		if (eventRaiserBody.length() != 0)
		{
			generatedClass.addInterface(star.event.Listener.class.getName());
			generatedClass.addMethod(GeneratedClass.PUBLIC, "void", "eventRaised", new String[] { "final " + Event.class.getName() }, new String[] { "event" }, eventRaiserBody.toString());
		}
		if (eventsRaiserBody.length() != 0)
		{
			generatedClass.addInterface(star.event.GatedListener.class.getName());
			generatedClass.addMethod(GeneratedClass.PUBLIC, "void", "eventsRaised", new String[] { "final " + Event.class.getName(), "final boolean" }, new String[] { "event[]", "valid" }, eventsRaiserBody.toString());
		}

	}

	private String getClassName(Declaration declaration)
	{
		String className = declaration.toString() + "_" + getGeneratedPackageName();
		return className;
	}

	private boolean hasMethod(String className, String methodName, Class[] parameterTypes)
	{
		boolean ret = false;
		TypeDeclaration declaration = environment.getTypeDeclaration(className);
		if (declaration != null)
		{
			ret = hasMethod(declaration, methodName, parameterTypes);
		}
		return ret;
	}

	private boolean hasMethod(TypeDeclaration declaration, String methodName, Class[] parameterTypes)
	{
		boolean ret = false;
		if (declaration != null)
		{
			Collection<? extends MethodDeclaration> methods = declaration.getMethods();
			for (MethodDeclaration d : methods)
			{
				if (d.getSimpleName().equals(methodName))
				{
					ret = true;
					break;
				}
			}
			if (ret == false && declaration instanceof ClassDeclaration)
			{
				ClassType superclassType = ((ClassDeclaration) declaration).getSuperclass();
				if (superclassType != null)
				{
					ClassDeclaration superclass = superclassType.getDeclaration();
					return hasMethod(superclass, methodName, parameterTypes);
				}
			}
		}
		return ret;
	}

	/*
	 * private boolean hasInterface(String className, Class interfaceClass) { boolean ret = false; try { ret =
	 * Class.forName(className).isInstance(interfaceClass); } catch (SecurityException e) { e.printStackTrace();
	 * environment.getMessager().printNotice("hasInterface " + className + " " + interfaceClass + " exc: " + e.toString()); } catch (ClassNotFoundException e) {
	 * e.printStackTrace(); environment.getMessager().printNotice("hasInterface " + className + " " + interfaceClass + " exc: " + e.toString()); } return ret; }
	 */
	private String getClasslessClass(String name)
	{
		String handledClass = name;
		if (handledClass.indexOf(".class") != -1)
		{
			handledClass = handledClass.substring(0, handledClass.indexOf(".class"));
		}
		return handledClass.trim();
	}

	private String getClasslessClassNameOnly(String name)
	{
		String handledClass = name;
		if (handledClass.indexOf(".class") != -1)
		{
			handledClass = handledClass.substring(0, handledClass.indexOf(".class"));
		}
		handledClass = handledClass.substring(handledClass.lastIndexOf('.') + 1);
		return handledClass.trim();
	}

	private String generateAdapterNotify(Declaration declaration, String property, String method)
	{
		StringBuffer ret = new StringBuffer();
		Iterator iterator = getClasses(declaration, property).iterator();
		while (iterator.hasNext())
		{
			String classlessClass = getClasslessClass(iterator.next().toString());
			if (debug)
				environment.getMessager().printNotice("Add " + method + " " + classlessClass + " -> " + getEvent(classlessClass));
			ret.append("getAdapter()." + method + "(" + getEvent(classlessClass) + ".class );\n");
		}
		return ret.toString();
	}

	private String generateAddNotifyHandles(Declaration declaration, HashSet<MethodDeclaration> methodDeclarations)
	{
		StringBuffer body = new StringBuffer();
		if (methodDeclarations == null)
			return "";
		HashSet<String> handled = new HashSet<String>();
		for (MethodDeclaration m : methodDeclarations)
		{
			Collection<ParameterDeclaration> params = m.getParameters();
			Handles handles = m.getAnnotation(Handles.class);
			if (params.size() == 1 && handles != null)
			{
				for (ParameterDeclaration param : params)
				{
					String handledClass = getEvent(param.getType().toString());
					if (!handled.contains(handledClass))
					{
						body.append("\ngetAdapter().addHandled(" + handledClass + ".class);");
						handled.add(handledClass);
					}
				}
			}
			if (params.size() > 1 && handles != null)
			{
				StringBuffer raises = new StringBuffer(" ");
				for (String raiser : getClasses(m, RAISES, Handles.class))
				{
					raises.append(getEvent(getClasslessClass(raiser)) + ".class,");
				}

				StringBuffer events = new StringBuffer(" ");
				for (ParameterDeclaration param : params)
				{
					events.append(getEvent(param.getType().toString()) + ".class,");
				}
				if (!handled.contains(events + "::" + raises))
				{
					body.append("\ngetAdapter().addGatedAnd( new Class[]{" + events.substring(0, events.length() - 1) + "},new Class[]{" + raises.substring(0, raises.length() - 1) + "},true);");
					handled.add(events + "::" + raises);
				}

			}

		}
		return body.toString();
	}

	private String generateRemoveNotifyHandles(Declaration declaration, HashSet<MethodDeclaration> methodDeclarations)
	{
		StringBuffer body = new StringBuffer();
		if (methodDeclarations == null)
			return "";
		HashSet<String> handled = new HashSet<String>();
		for (MethodDeclaration m : methodDeclarations)
		{
			Collection<ParameterDeclaration> params = m.getParameters();
			Handles handles = m.getAnnotation(Handles.class);
			if (params.size() == 1 && handles != null)
			{
				for (ParameterDeclaration param : params)
				{
					String handledClass = getEvent(param.getType().toString());
					if (!handled.contains(handledClass))
					{
						body.append("\ngetAdapter().removeHandled(" + getEvent(param.getType().toString()) + ".class);");
						handled.add(handledClass);
					}
				}
			}
			if (params.size() > 1 && handles != null)
			{
				StringBuffer raises = new StringBuffer(" ");
				for (String raiser : getClasses(m, RAISES, Handles.class))
				{
					raises.append(getEvent(getClasslessClass(raiser)) + ".class,");
				}

				StringBuffer events = new StringBuffer(" ");
				for (ParameterDeclaration param : params)
				{
					events.append(getEvent(param.getType().toString()) + ".class,");
				}
				if (!handled.contains(events + "::" + raises))
				{
					body.append("\ngetAdapter().removeGatedAnd( new Class[]{" + events.substring(0, events.length() - 1) + "},new Class[]{" + raises.substring(0, raises.length() - 1) + "});");
					handled.add(events + "::" + raises);
				}

			}

		}

		return body.toString();
	}

	private String generateAddNotifyBody(Declaration declaration, boolean hasSuper, HashSet<MethodDeclaration> methodDeclarations)
	{
		StringBuffer ret = new StringBuffer();
		if (hasSuper)
		{
			ret.append("super.addNotify();\n");
		}
		ret.append(generateAdapterNotify(declaration, HANDLES, "addHandled"));
		ret.append(generateAdapterNotify(declaration, CONTAINS, "addContained"));
		ret.append(generateAdapterNotify(declaration, EXCLUDEEXTERNAL, "addExcludeExternal"));
		ret.append(generateAdapterNotify(declaration, EXCLUDEINTERNAL, "addExcludeInternal"));
		ret.append(generateAddNotifyHandles(declaration, methodDeclarations));

		ret.append("\n");
		TreeSet components = getClasses(declaration, COMPONENTS);
		Iterator iterator = components.iterator();
		if (components.size() > 0)
		{
			ret.append("starcomponents = new Object[" + components.size() + "];\n");
			int i = 0;
			while (iterator.hasNext())
			{
				ret.append("starcomponents[" + i + "] = new " + getClasslessClass(iterator.next().toString()) + "();\n");
				ret.append("getAdapter().addComponent( starcomponents[" + i + "] );\n");
				i++;
			}
		}
		return ret.toString();
	}

	private void generatePreferences(Declaration declaration, star.annotations.GeneratedClass generatedClass, String className)
	{
		Preferences p = declaration.getAnnotation(Preferences.class);
		String prefix = p.prefix();
		if (prefix.equals(Preferences.DEFAULT))
		{
			prefix = className;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("if( preferences == null )");
		sb.append("\n{");
		sb.append("\n\ttry");
		sb.append("\n\t{");
		sb.append("\n\t\tplugin.preferences.Preferences pref = (plugin.preferences.Preferences) plugin.Loader.getDefaultLoader().getPlugin(plugin.preferences.Preferences.class.getName(), plugin.preferences.PreferencesImplementation.class.getName());");
		sb.append("\n\t\tthis.preferences = pref.getPreferences(\"" + prefix + "\");");
		sb.append("\n\t}");
		sb.append("\n\tcatch( plugin.PluginException ex )");
		sb.append("\n\t{");
		sb.append("\n\t\tex.printStackTrace();\n");
		sb.append("\n\t}\n");
		sb.append("}\n");
		sb.append("return preferences;");
		generatedClass.addMember(GeneratedClass.PRIVATE, java.util.prefs.Preferences.class.getName(), "preferences", "null");
		generatedClass.addMethod(GeneratedClass.PUBLIC, java.util.prefs.Preferences.class.getName(), "getPreferences", null, null, sb.toString());
		sb = new StringBuffer();
		sb.append("try");
		sb.append("\n{");
		sb.append("\n\tplugin.preferences.Preferences pref = (plugin.preferences.Preferences) plugin.Loader.getDefaultLoader().getPlugin(plugin.preferences.Preferences.class.getName(), plugin.preferences.PreferencesImplementation.class.getName());");
		sb.append("\n\tthis.preferences = pref.getPreferences(name);");
		sb.append("\n}");
		sb.append("\ncatch( plugin.PluginException ex )");
		sb.append("\n{");
		sb.append("\n\tex.printStackTrace();\n");
		sb.append("\n}\n");
		sb.append("return preferences;");
		generatedClass.addMethod(GeneratedClass.PUBLIC, java.util.prefs.Preferences.class.getName(), "getPreferences", new String[] { String.class.getName() }, new String[] { "name" }, sb.toString());
	}

	private String generateRemoveNotifyBody(Declaration declaration, boolean hasSuper, HashSet<MethodDeclaration> methodDeclarations)
	{
		StringBuffer ret = new StringBuffer();
		if (hasSuper)
		{
			ret.append("super.removeNotify();\n");
		}
		ret.append(generateAdapterNotify(declaration, HANDLES, "removeHandled"));
		ret.append(generateAdapterNotify(declaration, CONTAINS, "removeContained"));
		ret.append(generateAdapterNotify(declaration, EXCLUDEEXTERNAL, "removeExcludeExternal"));
		ret.append(generateAdapterNotify(declaration, EXCLUDEINTERNAL, "removeExcludeInternal"));
		ret.append(generateRemoveNotifyHandles(declaration, methodDeclarations));
		ret.append("\n");
		TreeSet components = getClasses(declaration, COMPONENTS);
		if (components.size() > 0)
		{
			ret.append("for( int i = 0 ; i < starcomponents.length ; i++ )\n");
			ret.append("{\n");
			ret.append("\tif( starcomponents[i] != null )");
			ret.append("\t{\n");
			ret.append("\t\tgetAdapter().removeComponent(starcomponents[i]);\n");
			ret.append("\t}\n");
			ret.append("}\n");
			ret.append("starcomponents = new Object[0];");
		}
		return ret.toString();
	}

	private String generateEventRaisedBody(Declaration declaration, String extendsName)
	{
		StringBuffer ret = new StringBuffer();
		Iterator iterator = getClasses(declaration, HANDLES).iterator();
		while (iterator.hasNext())
		{
			String handledClass = getClasslessClass(iterator.next().toString());
			ret.append("if( event.getClass().getName().equals( \"" + getEvent(handledClass) + "\" ) ) { \n");
			ret.append("\t" + getRaiser(handledClass) + " r = (" + getRaiser(handledClass) + ")event.getSource();\n");
			ret.append("\tif(event.isValid())\n\t{\n");
			ret.append("\t\thandleEvent( r );\n");
			ret.append("\t}\n");
			ret.append("\telse\n");
			ret.append("\t{\n");
			ret.append("\t\thandleInvalidEvent( r );\n");
			ret.append("\t}\n");
			ret.append("}\n");
		}
		return ret.toString();
	}

	@SuppressWarnings("unchecked")
	private boolean hasAnnotation(Declaration declaration, Class annotation)
	{
		return declaration.getAnnotation(annotation) != null;
	}

	private TreeSet<String> getClasses(Declaration declaration, String property)
	{
		return getClasses(declaration, property, SignalComponent.class);
	}

	@SuppressWarnings("unchecked")
	private TreeSet<String> getClasses(Declaration declaration, String property, Class annotation)
	{
		TreeSet<String> handles = new TreeSet<String>();
		Collection<AnnotationMirror> annotations = declaration.getAnnotationMirrors();
		for (AnnotationMirror mirror : annotations)
		{

			Map<AnnotationTypeElementDeclaration, AnnotationValue> values = mirror.getElementValues();
			if (mirror.getAnnotationType().getDeclaration().getQualifiedName().equals(annotation.getName()))
			{
				for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : values.entrySet())
				{
					AnnotationTypeElementDeclaration elemDecl = entry.getKey();
					if (property.equals(elemDecl.getSimpleName().toString()))
					{
						AnnotationValue value = entry.getValue();
						Object v = value.getValue();
						if (v instanceof ArrayList)
						{
							Iterator<Object> iter = ((ArrayList<Object>) v).iterator();
							while (iter.hasNext())
							{
								handles.add(String.valueOf(iter.next()));
							}
						}
						else
						{
							handles.add(v.toString());
						}
					}
				}
			}
		}
		return handles;
	}

	private String getGeneratedPackageName()
	{
		return "generated";
	}

}
