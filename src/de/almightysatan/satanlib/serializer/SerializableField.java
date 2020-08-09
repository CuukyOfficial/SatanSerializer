package de.almightysatan.satanlib.serializer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

class SerializableField {
	
	/**
	 * there is almost no difference in performance
	 */
	private static final boolean USE_METHODHANDLES = true;

	private Field field;
	private MethodHandle getter;
	private MethodHandle setter;
	
	int id;
	boolean javaSerializer;

	SerializableField(Field field, int id, boolean javaSerializer) throws IllegalAccessException {
		this.field = field;
		this.id = id;
		this.javaSerializer = javaSerializer;

		this.field.setAccessible(true);
		
		if(USE_METHODHANDLES) {
			this.getter = MethodHandles.lookup().unreflectGetter(field);
			this.setter = MethodHandles.lookup().unreflectSetter(field);
		}
	}
	
	Object get(Object instance) throws Throwable {
		if(USE_METHODHANDLES)
			return getter.invoke(instance);
		else
			return field.get(instance);
	}
	
	void set(Object instance, Object value) throws Throwable {
		if(USE_METHODHANDLES)
			setter.invoke(instance, value);
		else
			field.set(instance, value);
	}
}
