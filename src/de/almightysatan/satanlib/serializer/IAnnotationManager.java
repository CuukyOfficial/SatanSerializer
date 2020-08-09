package de.almightysatan.satanlib.serializer;

import java.lang.reflect.Field;

public interface IAnnotationManager {

	public boolean useJavaSerializer(Field field);
	public boolean isSerializableField(Field field);
	public int getFieldId(Field field);
}
