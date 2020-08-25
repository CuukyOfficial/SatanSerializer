package de.almightysatan.satanlib.serializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

class SerializableClazz {
	
	Class<?> clazz;
	int id;
	Constructor<?> constructor;
	Map<Integer, SerializableField> fields = new HashMap<>();
	
	SerializableClazz(IAnnotationManager annotationManager, Class<?> clazz, int id) throws IllegalAccessException {
		this.clazz = clazz;
		this.id = id;
		
		try {
			this.constructor = clazz.getDeclaredConstructor();
			this.constructor.setAccessible(true);
		}catch(NoSuchMethodException | SecurityException e) {}
		
		loadFields(clazz, annotationManager);
	}
	
	private void loadFields(Class<?> clazz, IAnnotationManager annotationManager) throws IllegalAccessException {
		for (Field field : clazz.getDeclaredFields())
			if(annotationManager.isSerializableField(field)) {
				int id = annotationManager.getFieldId(field);
				
				if(id < 0)
					throw new Error("Field id can not be < 0 in class " + clazz.getName());
				
				if(this.fields.containsKey(id))
					throw new Error("Duplicate field id " + id + " i class "+clazz.getName());
				
				this.fields.put(id, new SerializableField(field, id, annotationManager.useJavaSerializer(field)));
			}
		
		Class<?> superclazz = clazz.getSuperclass(); 
		
		if(superclazz != null && superclazz != Object.class)
			loadFields(superclazz, annotationManager);
	}

	Object newInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(this.constructor == null)
			throw new Error("Class has no empty constructor " + clazz.getName());
		
		return this.constructor.newInstance();
	}
}
