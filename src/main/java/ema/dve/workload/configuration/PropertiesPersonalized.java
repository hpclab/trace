package ema.dve.workload.configuration;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import ema.dve.workload.mobility.AMobilityModel;

public class PropertiesPersonalized extends Properties
{
	public static final String CONFIG_MODEL = "model";

	private static final long serialVersionUID = -1280000968816556745L;

	public PropertiesPersonalized(final String configFile_) throws Exception
	{
		setProperty("ITERATION_NUM", "100");
		setProperty("HOTSPOT_NUM", "3");
		setProperty("HOTSPOT_RADIUS", "10");
		setProperty("OBJECTS_NUM", "0");
		setProperty("RANDOM_SEED", "15555534");
		setProperty("ENABLE_GRAPHIC", "true");
		setProperty("ENABLE_DUMP", "true");

		load(new FileInputStream(configFile_));
		load(new FileInputStream("mobilityModel.conf"));
	}

	public int getPropertyInt(final String val_)
	{
		return Integer.parseInt(getProperty(val_));
	}

	public int getPropertyInt(final String val_, final int defaultValue_)
	{
		return Integer.parseInt(getProperty(val_, Integer.toString(defaultValue_)));
	}

	public long getPropertyLong(final String val_)
	{
		return Long.parseLong(getProperty(val_));
	}

	public double getPropertyDouble(final String val_)
	{
		return Double.parseDouble(getProperty(val_));
	}

	public boolean getPropertyBoolean(final String val_)
	{
		return Boolean.parseBoolean(getProperty(val_));
	}

	public long getRandomSeed()
	{
		return getPropertyLong("RANDOM_SEED");
	}

	public String getObjectPlacement()
	{
		return getProperty("OBJECT_PLACEMENT","random");
	}

	public Class getClass(final String name) throws Exception
	{
		return getClazz(name);
	}

	private Class getClazz(final String name) throws Exception
	{
		final String classname = getProperty(name);
		if (classname == null) {
			throw new Exception(name);
//					"\nPossibly incorrect property: " + getSimilarProperty(name));
		}

		Class c = null;

		try {
			// Maybe classname is just a fully-qualified name
			c = Class.forName(classname);
		} catch (final ClassNotFoundException e) {
		}
		if (c == null) {
			// Maybe classname is a non-qualified name?
			final String fullname = ClassFinder.getQualifiedName(classname);
			if (fullname != null) {
				try {
					c = Class.forName(fullname);
				} catch (final ClassNotFoundException e) {
				}
			}
		}
		if (c == null) {
			// Maybe there are multiple classes with the same
			// non-qualified name.
			final String fullname = ClassFinder.getQualifiedName(classname);
			if (fullname != null) {
				final String[] names = fullname.split(",");
				if (names.length > 1) {
					for (int i = 0; i < names.length; i++) {
						for (int j = i + 1; j < names.length; j++) {
							if (names[i].equals(names[j])) {
								throw new Exception(
										"The class " + names[i]
									+ " appears more than once in the classpath; please check"
									+ " your classpath to avoid duplications.");
							}
						}
					}
					throw new Exception(
							"The non-qualified class name " + classname
									+ "corresponds to multiple fully-qualified classes:" + fullname);
				}
			}
		}
		if (c == null) {
			// Last attempt: maybe the fully classified name is wrong,
			// but the classname is correct.
			final String shortname = ClassFinder.getShortName(classname);
			final String fullname = ClassFinder.getQualifiedName(shortname);
			if (fullname != null) {
				throw new Exception("Class "
						+ classname + " does not exist. Possible candidate(s): " + fullname);
			}
		}
		if (c == null) {
			throw new Exception("Class "
					+ classname + " not found");
		}
		return c;
	}

	public String getModelName()
	{
		return getProperty(CONFIG_MODEL).toLowerCase();
	}

	public AMobilityModel getMobilityModel() throws Exception
	{
		return (AMobilityModel) getInstanceModel(CONFIG_MODEL+"."+getModelName());
	}

	private Object getInstanceModel(final String name) throws Exception
	{
		final Class c = getClass(name);
		if (c == null)
		{
			return null;
		}
		final String classname = c.getSimpleName();

		try {
			final Class pars[] = {PropertiesPersonalized.class};
			final Constructor cons = c.getConstructor(pars);
			final Object objpars[] = {this};
			return cons.newInstance(objpars);
		} catch (final NoSuchMethodException e) {
			e.printStackTrace();
			throw new Exception("Class "
					+ classname + " has no " + classname + "(String) constructor");
		} catch (final InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			} else {
				e.getTargetException().printStackTrace();
				throw new RuntimeException("" + e.getTargetException());
			}
		} catch (final Exception e) {
			throw new Exception(e + "");
		}
	}
}
