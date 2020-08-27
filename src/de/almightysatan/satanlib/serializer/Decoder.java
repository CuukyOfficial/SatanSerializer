package de.almightysatan.satanlib.serializer;

import static de.almightysatan.satanlib.serializer.Serializer.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

class Decoder {

	Object deserialize(Serializer serializer, byte[] data) throws Throwable {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

		@SuppressWarnings("unused")
		int version = dis.readInt();

		return deserializeField(serializer, dis);
	}

	private Object deserializeField(Serializer serializer, DataInputStream dis) throws Throwable {
		byte type = dis.readByte();

		switch(type) {
		case INDEX_JAVA_OBJECT:
			byte[] data = new byte[dis.readInt()];
			dis.read(data);
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bis);

			Object deserialized = ois.readObject();

			ois.close();
			bis.close();

			return deserialized;
		case INDEX_CUSTOM_OBJECT:
			int clazzId = dis.readInt();
			
			SerializableClazz clazz = serializer.clazzIdMap.get(clazzId);
			verifyClass(clazz, clazzId);

			Object object = clazz.newInstance();

			readCustomFields(serializer, dis, clazz, object);
			
			return object;
		case INDEX_CUSTOM_ENUM:
			clazzId = dis.readInt();
			clazz = serializer.clazzIdMap.get(clazzId);
			verifyClass(clazz, clazzId);

			SerializableField field = clazz.fields.get(dis.readInt());
			return field.get(null);
		case INDEX_BYTE:
			return dis.readByte();
		case INDEX_SHORT:
			return dis.readShort();
		case INDEX_INT:
			return dis.readInt();
		case INDEX_LONG:
			return dis.readLong();
		case INDEX_FLOAT:
			return dis.readFloat();
		case INDEX_DOUBLE:
			return dis.readDouble();
		case INDEX_BOOL:
			return dis.readBoolean();
		case INDEX_CHAR:
			return dis.readChar();
		case INDEX_STRING:
			return dis.readUTF();
		case INDEX_UUID:
			return new UUID(dis.readLong(), dis.readLong());
		case INDEX_LIST:
			@SuppressWarnings("unchecked")
			Class<? extends List<Object>> listClass = (Class<? extends List<Object>>) Class.forName(dis.readUTF());
			
			List<Object> javaList = listClass.getDeclaredConstructor().newInstance();
			readListData(serializer, dis, javaList);
			return javaList;
		case INDEX_CUSTOM_LIST:
			clazzId = dis.readInt();
			clazz = serializer.clazzIdMap.get(clazzId);
			verifyClass(clazz, clazzId);
			
			@SuppressWarnings("unchecked")
			List<Object> customList = (List<Object>) clazz.newInstance();
			readListData(serializer, dis, customList);
			
			readCustomFields(serializer, dis, clazz, customList);
			
			return customList;
		case INDEX_PRIMITIVE_ARRAY:
			Class<?> primitiveArrayClass = (Class<?>) getPrimitiveClass(dis.readUTF());
			
			int size = dis.readInt();
			
			Object primitiveArray = Array.newInstance(primitiveArrayClass, size);
			readPrimitiveArrayData(serializer, dis, primitiveArray, size);
			return primitiveArray;
		case INDEX_ARRAY:
			@SuppressWarnings("unchecked")
			Class<? extends Object[]> arrayClass = (Class<? extends Object[]>) Class.forName(dis.readUTF());
			
			size = dis.readInt();
			
			Object[] array = (Object[]) Array.newInstance(arrayClass, size);
			readArrayData(serializer, dis, array, size);
			return array;
		case INDEX_CUSTOM_ARRAY:
			clazzId = dis.readInt();
			clazz = serializer.clazzIdMap.get(clazzId);
			verifyClass(clazz, clazzId);
			
			size = dis.readInt();
			
			array = (Object[]) Array.newInstance(clazz.clazz, size);
			readArrayData(serializer, dis, array, size);
			return array;
		case INDEX_MAP:
			@SuppressWarnings("unchecked")
			Class<? extends Map<Object, Object>> mapClass = (Class<? extends Map<Object, Object>>) Class.forName(dis.readUTF());
			
			Map<Object, Object> javaMap = mapClass.getDeclaredConstructor().newInstance();
			readMapData(serializer, dis, javaMap);
			return javaMap;
		case INDEX_CUSTOM_MAP:
			clazzId = dis.readInt();
			clazz = serializer.clazzIdMap.get(clazzId);
			verifyClass(clazz, clazzId);
			
			@SuppressWarnings("unchecked")
			Map<Object, Object> customMap = (Map<Object, Object>) clazz.newInstance();
			readMapData(serializer, dis, customMap);
			
			readCustomFields(serializer, dis, clazz, customMap);
			
			return customMap;
		default:
			throw new Error("Invalid type: " + type);
		}
	}
	
	private void verifyClass(SerializableClazz clazz, int clazzId) {
		if(clazz == null)
			throw new Error("Serializable class not found: " + clazzId);
	}
	
	private void readCustomFields(Serializer serializer, DataInputStream dis, SerializableClazz clazz, Object instance) throws Throwable {
		Map<Integer, Object> fields = new HashMap<>();

		int fieldAId; 
		while((fieldAId = dis.readInt()) != INDEX_BREAK)
			fields.put(fieldAId, deserializeField(serializer, dis));

		for(Entry<Integer, Object> field : fields.entrySet())
			clazz.fields.get(field.getKey()).set(instance, field.getValue());
	}
	
	private Class<?> getPrimitiveClass(String name) throws ClassNotFoundException {
		switch (name) {
		case "byte":
			return byte.class;
		default:
			throw new Error();
		}
	}
	
	private void readListData(Serializer serializer, DataInputStream dis, List<Object> list) throws Throwable {
		int size = dis.readInt();
		
		for(int index = 0; index < size; index++)
			list.add(deserializeField(serializer, dis));
	}
	
	private void readPrimitiveArrayData(Serializer serializer, DataInputStream dis, Object array, int size) throws Throwable {
		for(int index = 0; index < size; index++)
			Array.set(array, index, deserializeField(serializer, dis));
	}
	
	private void readArrayData(Serializer serializer, DataInputStream dis, Object[] array, int size) throws Throwable {
		for(int index = 0; index < size; index++)
			array[index] = deserializeField(serializer, dis);
	}
	
	private void readMapData(Serializer serializer, DataInputStream dis, Map<Object, Object> map) throws Throwable {
		int size = dis.readInt();
		
		for(int index = 0; index < size; index++)
			map.put(deserializeField(serializer, dis), deserializeField(serializer, dis));
	}
}
