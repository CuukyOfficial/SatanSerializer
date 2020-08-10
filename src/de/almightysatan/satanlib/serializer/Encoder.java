package de.almightysatan.satanlib.serializer;

import static de.almightysatan.satanlib.serializer.Serializer.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

class Encoder {


	byte[] serialize(Serializer serializer, Object o) throws Throwable {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		dos.writeInt(ENCODER_VERSION);
		
		serializeField(serializer, dos, o, false); //the annotation id of the root object is the version of the serializer

		dos.close();

		return bos.toByteArray();
	}

	private void serializeField(Serializer serializer, DataOutputStream dos, Object o, boolean useJavaSerializer) throws Throwable {
		if (o != null) {
			if(useJavaSerializer) {
				dos.writeByte(INDEX_JAVA_OBJECT);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(o);
				oos.close();

				byte[] data = bos.toByteArray();
				dos.writeInt(data.length);
				dos.write(data);
				bos.close();
				return;
			}
			
			if(o instanceof Byte) {
				dos.writeByte(INDEX_BYTE);
				dos.writeByte((byte) o);
				return;
			}
			
			if(o instanceof Short) {
				dos.writeByte(INDEX_SHORT);
				dos.writeShort((short) o);
				return;
			}

			if(o instanceof Integer) {
				dos.writeByte(INDEX_INT);
				dos.writeInt((int) o);
				return;
			}

			if(o instanceof Long) {
				dos.writeByte(INDEX_LONG);
				dos.writeLong((long) o);
				return;
			}

			if(o instanceof Float) {
				dos.writeByte(INDEX_FLOAT);
				dos.writeFloat((float) o);
				return;
			}

			if(o instanceof Double) {
				dos.writeByte(INDEX_DOUBLE);
				dos.writeDouble((double) o);
				return;
			}

			if(o instanceof Boolean) {
				dos.writeByte(INDEX_BOOL);
				dos.writeBoolean((boolean) o);
				return;
			}
			
			if(o instanceof Character) {
				dos.writeByte(INDEX_CHAR);
				dos.writeChar((char) o);
				return;
			}
			
			if(o instanceof String) {
				dos.writeByte(INDEX_STRING);
				dos.writeUTF((String) o);
				return;
			}

			if(o instanceof UUID) {
				dos.writeByte(INDEX_UUID);

				UUID uuid = (UUID) o;

				dos.writeLong(uuid.getMostSignificantBits());
				dos.writeLong(uuid.getLeastSignificantBits());
				return;
			}
			
			if(o instanceof Object[]) {
				SerializableClazz clazz = serializer.clazzMap.get(o.getClass().getComponentType());
				
				if(clazz == null) {
					dos.writeByte(INDEX_ARRAY);
					dos.writeUTF(o.getClass().getComponentType().getName());
				}else {
					dos.writeByte(INDEX_CUSTOM_ARRAY);
					dos.writeInt(clazz.id);
				}
				
				writeArrayData(serializer, dos, o);
				return;
			}

			SerializableClazz clazz = serializer.clazzMap.get(o.getClass());

			if(o instanceof List<?>) {
				if(clazz == null) {
					dos.writeByte(INDEX_LIST);
					dos.writeUTF(o.getClass().getName());
					
					writeListData(serializer, dos, o);
					return;
				}else {
					dos.writeByte(INDEX_CUSTOM_LIST);
					dos.writeInt(clazz.id);
					
					writeListData(serializer, dos, o);
					
					writeCustomFields(serializer, dos, clazz, o);
					return;
				}

			}
			
			if(o instanceof Map<?, ?>) {
				if(clazz == null) {
					dos.writeByte(INDEX_MAP);
					dos.writeUTF(o.getClass().getName());
					
					writeMapData(serializer, dos, o);
					return;
				}else {
					dos.writeByte(INDEX_CUSTOM_MAP);
					dos.writeInt(clazz.id);
					
					writeMapData(serializer, dos, o);
					
					writeCustomFields(serializer, dos, clazz, o);
					return;
				}
			}

			if(clazz != null) {
				if(o instanceof Enum<?>) {
					dos.writeByte(INDEX_CUSTOM_ENUM);
					dos.writeInt(clazz.id);

					for (SerializableField sField : clazz.fields.values()) {
						Object fieldObject = sField.get(o);

						if(o == fieldObject) {
							dos.writeInt(sField.id);
							break;
						}
					}
					
					return;
				}else{
					dos.writeByte(INDEX_CUSTOM_OBJECT);
					dos.writeInt(clazz.id);
					
					writeCustomFields(serializer, dos, clazz, o);
					
					return;
				}
			}
			
			throw new Error("Unable to serialize object: " + o.getClass().getName());
		}
	}
	
	private void writeCustomFields(Serializer serializer, DataOutputStream dos, SerializableClazz clazz, Object instance) throws IOException, Throwable {
		for (SerializableField sField : clazz.fields.values()) {
			dos.writeInt(sField.id); //write annotation id
			serializeField(serializer, dos, sField.get(instance), sField.javaSerializer);
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
	
	private void writeMapData(Serializer serializer, DataOutputStream dos, Object instance) throws Throwable {
		Map<?, ?> map = (Map<?, ?>) instance;
		
		dos.writeInt(map.size());
		
		for(Entry<?, ?> entry : map.entrySet()) {
			serializeField(serializer, dos, entry.getKey(), false);
			serializeField(serializer, dos, entry.getValue(), false);
		}
	}
}
