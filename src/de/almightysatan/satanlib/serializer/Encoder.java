package de.almightysatan.satanlib.serializer;

import static de.almightysatan.satanlib.serializer.Serializer.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

class Encoder {

	byte[] serialize(Serializer serializer, Object instance) throws Throwable {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		dos.writeInt(ENCODER_VERSION);
		
		serializeField(serializer, dos, instance, false);

		dos.close();

		return bos.toByteArray();
	}

	private void serializeField(Serializer serializer, DataOutputStream dos, Object instance, boolean useJavaSerializer) throws Throwable {
		if (instance != null) {
			if(useJavaSerializer) {
				dos.writeByte(INDEX_JAVA_OBJECT);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(instance);
				oos.close();

				byte[] data = bos.toByteArray();
				dos.writeInt(data.length);
				dos.write(data);
				bos.close();
				return;
			}
			
			if(instance instanceof Byte) {
				dos.writeByte(INDEX_BYTE);
				dos.writeByte((byte) instance);
				return;
			}
			
			if(instance instanceof Short) {
				dos.writeByte(INDEX_SHORT);
				dos.writeShort((short) instance);
				return;
			}

			if(instance instanceof Integer) {
				dos.writeByte(INDEX_INT);
				dos.writeInt((int) instance);
				return;
			}

			if(instance instanceof Long) {
				dos.writeByte(INDEX_LONG);
				dos.writeLong((long) instance);
				return;
			}

			if(instance instanceof Float) {
				dos.writeByte(INDEX_FLOAT);
				dos.writeFloat((float) instance);
				return;
			}

			if(instance instanceof Double) {
				dos.writeByte(INDEX_DOUBLE);
				dos.writeDouble((double) instance);
				return;
			}

			if(instance instanceof Boolean) {
				dos.writeByte(INDEX_BOOL);
				dos.writeBoolean((boolean) instance);
				return;
			}
			
			if(instance instanceof Character) {
				dos.writeByte(INDEX_CHAR);
				dos.writeChar((char) instance);
				return;
			}
			
			if(instance instanceof String) {
				dos.writeByte(INDEX_STRING);
				dos.writeUTF((String) instance);
				return;
			}

			if(instance instanceof UUID) {
				dos.writeByte(INDEX_UUID);

				UUID uuid = (UUID) instance;

				dos.writeLong(uuid.getMostSignificantBits());
				dos.writeLong(uuid.getLeastSignificantBits());
				return;
			}
			
			if(instance instanceof Object[]) {
				SerializableClazz clazz = serializer.clazzMap.get(instance.getClass().getComponentType());
				
				if(clazz == null) {
					dos.writeByte(INDEX_ARRAY);
					dos.writeUTF(instance.getClass().getComponentType().getName());
				}else {
					dos.writeByte(INDEX_CUSTOM_ARRAY);
					dos.writeInt(clazz.id);
				}
				
				writeArrayData(serializer, dos, instance);
				return;
			}
			
			if(instance.getClass().isArray()) {
				dos.writeByte(INDEX_PRIMITIVE_ARRAY);
				dos.writeUTF(instance.getClass().getComponentType().getName());
				
				writePrimitiveArrayData(serializer, dos, instance);
				return;
			}

			SerializableClazz clazz = serializer.clazzMap.get(instance.getClass());

			if(instance instanceof List<?>) {
				if(clazz == null) {
					dos.writeByte(INDEX_LIST);
					dos.writeUTF(instance.getClass().getName());
					
					writeListData(serializer, dos, instance);
					return;
				}else {
					dos.writeByte(INDEX_CUSTOM_LIST);
					dos.writeInt(clazz.id);
					
					writeListData(serializer, dos, instance);
					
					writeCustomFields(serializer, dos, clazz, instance);
					return;
				}

			}
			
			if(instance instanceof Map<?, ?>) {
				if(clazz == null) {
					dos.writeByte(INDEX_MAP);
					dos.writeUTF(instance.getClass().getName());
					
					writeMapData(serializer, dos, instance);
					return;
				}else {
					dos.writeByte(INDEX_CUSTOM_MAP);
					dos.writeInt(clazz.id);
					
					writeMapData(serializer, dos, instance);
					
					writeCustomFields(serializer, dos, clazz, instance);
					return;
				}
			}

			if(clazz != null) {
				if(instance instanceof Enum<?>) {
					dos.writeByte(INDEX_CUSTOM_ENUM);
					dos.writeInt(clazz.id);

					//TODO this is slow and should be optimized
					for (SerializableField sField : clazz.fields.values()) {
						Object fieldObject = sField.get(instance);

						if(instance == fieldObject) {
							dos.writeInt(sField.id);
							break;
						}
					}
					
					return;
				}else{
					dos.writeByte(INDEX_CUSTOM_OBJECT);
					dos.writeInt(clazz.id);
					
					writeCustomFields(serializer, dos, clazz, instance);
					
					return;
				}
			}
			
			throw new Error("Unable to serialize object: " + instance.getClass().getName());
		}
	}
	
	private void writeCustomFields(Serializer serializer, DataOutputStream dos, SerializableClazz clazz, Object instance) throws IOException, Throwable {
		for (SerializableField sField : clazz.fields.values()) {
			Object value = sField.get(instance);
			
			if(value != null) {
				dos.writeInt(sField.id); //write annotation id
				serializeField(serializer, dos, value, sField.javaSerializer);
			}
		}

		dos.writeInt(INDEX_BREAK);
	}
	
	private void writeListData(Serializer serializer, DataOutputStream dos, Object instance) throws Throwable {
		List<?> list = (List<?>) instance;
		
		dos.writeInt(list.size());
		
		for(Object element : list)
			serializeField(serializer, dos, element, false); //never use java serializer
	}
	
	private void writeArrayData(Serializer serializer, DataOutputStream dos, Object instance) throws Throwable {
		Object[] array = (Object[]) instance;
		
		dos.writeInt(array.length);
		
		for(Object element : array)
			serializeField(serializer, dos, element, false);
	}
	
	private void writePrimitiveArrayData(Serializer serializer, DataOutputStream dos, Object instance) throws Throwable {
		int length = Array.getLength(instance);
		
		dos.writeInt(length);
		
		for(int i = 0; i < length; i++)
			serializeField(serializer, dos, Array.get(instance, i), false);
	}
	
	private void writeMapData(Serializer serializer, DataOutputStream dos, Object instance) throws Throwable {
		Map<?, ?> map = (Map<?, ?>) instance;
		
		dos.writeInt(map.size());
		
		for(Entry<?, ?> entry : map.entrySet()) {
			serializeField(serializer, dos, entry.getKey(), false);
			serializeField(serializer, dos, entry.getValue(), false);
		}
	}
}
