package de.almightysatan.satanlib.serializer;

import static de.almightysatan.satanlib.serializer.Serializer.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
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
			ByteArrayInputStream bis = new ByteArrayInputStream(dis.readNBytes(dis.readInt()));
			ObjectInputStream ois = new ObjectInputStream(bis);
			
			Object deserialized = ois.readObject();
			
			ois.close();
			bis.close();
			
			return deserialized;
		case INDEX_CUSTOM_OBJECT:
			int clazzId = dis.readInt();
			
			Map<Integer, Object> fields = new HashMap<>();
			
			int fieldAId; 
			while((fieldAId = dis.readInt()) != INDEX_BREAK)
				fields.put(fieldAId, deserializeField(serializer, dis));
			
			SerializableClazz clazz = serializer.clazzIdMap.get(clazzId);
			
			if(clazz == null)
				throw new Error("Serializable class not found: " + clazzId);
			
			Object object = clazz.newInstance();
			
			for(Entry<Integer, Object> field : fields.entrySet())
				clazz.fields.get(field.getKey()).set(object, field.getValue());
			
			return object;
		case INDEX_CUSTOM_ENUM:
			clazzId = dis.readInt();
			clazz = serializer.clazzIdMap.get(clazzId);
			
			if(clazz == null)
				throw new Error("Serializable class not found: " + clazzId);
			
			SerializableField field = clazz.fields.get(dis.readInt());
			return field.get(null);
		case INDEX_STRING:
			return dis.readUTF();
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
		case INDEX_UUID:
			return new UUID(dis.readLong(), dis.readLong());
		default:
			throw new Error("Corrupted data (Invalid type)");
		}
	}
}
