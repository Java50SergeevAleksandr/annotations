package telran.configuration;

import java.util.*;

import telran.configuration.annotations.Value;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Class injects values from properties configuration file into specified object
 * using annotations {@value}
 */
public class Configuration {
	private static final String DEFAULT_CONFIG_FILE = "application.properties";
	Object configObj;
	Properties properties;

	public Configuration(Object configObj, String configFile) throws Exception {
		this.configObj = configObj;
		try (InputStream ins = new FileInputStream(configFile)) {
			properties = new Properties();
			properties.load(ins);
		}
	}

	public Configuration(Object configObject) throws Exception {
		this(configObject, DEFAULT_CONFIG_FILE);
	}

	public void configInjection() {
		Arrays.stream(configObj.getClass().getDeclaredFields()).filter(f -> f.isAnnotationPresent(Value.class))
				.forEach(this::injection);
	}

	void injection(Field field) {
		// value structure: <property name>:<default value>
		Value valueAnnotation = field.getAnnotation(Value.class);
		Object value = getValue(valueAnnotation.value(), field.getType().getSimpleName().toLowerCase());
		setValue(field, value);
	}

	private Object getValue(String annotationValue, String typeName) {
		String value = null;
		String[] tokens = annotationValue.split(":");
		String propertyName = tokens[0];

		try {
			value = properties.getProperty(propertyName, tokens[1]);
		} catch (Exception e) {
			throw new RuntimeException("No property in configuration file and no default value ");
		}

		try {
			Method method = getClass().getDeclaredMethod(typeName + "Conversion", String.class);
			return method.invoke(this, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	void setValue(Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(configObj, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// assumption: supported data types: int, long, float, double, String
	Integer intConversion(String value) {
		return Integer.valueOf(value);
	}

	Long longConversion(String value) {
		return Long.valueOf(value);
	}

	Float floatConversion(String value) {
		return Float.valueOf(value);
	}

	Double doubleConversion(String value) {
		return Double.valueOf(value);
	}

	String stringConversion(String value) {
		return value;
	}

}
