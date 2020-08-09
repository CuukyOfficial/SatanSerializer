package de.almightysatan.satanlib.serializer;

import java.lang.reflect.Field;

class DefaultAnnotationManager implements IAnnotationManager{

	@Override
	public int getFieldId(Field field) {
		return field.getAnnotation(SerializeId.class).value();
	}

	@Override
	public boolean isSerializableField(Field field) {
		return field.getAnnotation(SerializeId.class) != null;
	}

	@Override
	public boolean useJavaSerializer(Field field) {
		return field.getAnnotation(JavaSerializer.class) != null;
	}
}
