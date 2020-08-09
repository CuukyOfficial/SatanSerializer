package de.almightysatan.satanlib.serializer;

import static de.almightysatan.satanlib.serializer.Serializer.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

class Encoder {


	byte[] serialize(Serializer serializer, Object o) throws Throwable {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		serializeField(serializer, dos, o, ENCODER_VERSION, false); //the annotation id of the root object is the version of the serializer

		dos.close();

		return bos.toByteArray();
	}

	private void serializeField(Serializer serializer, DataOutputStream dos, Object o, int aId, boolean useJavaSerializer) throws Throwable {
		dos.writeInt(aId); //write annotation id

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

			if(o instanceof String) {
				dos.writeByte(INDEX_STRING);
				dos.writeUTF((String) o);
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
			
			if(o instanceof UUID) {
				dos.writeByte(INDEX_UUID);
				
				UUID uuid = (UUID) o;
				
				dos.writeLong(uuid.getMostSignificantBits());
				dos.writeLong(uuid.getLeastSignificantBits());
				return;
			}

			SerializableClazz clazz = serializer.clazzMap.get(o.getClass());
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
				}else{
					dos.writeByte(INDEX_CUSTOM_OBJECT);
					dos.writeInt(clazz.id);


					for (SerializableField sField : clazz.fields.values())
						serializeField(serializer, dos, sField.get(o), sField.id, sField.javaSerializer);

					dos.writeInt(INDEX_BREAK);
				}
			}else
				throw new Error("Unable to serialize object: " + o.getClass().getName());
		}
	}
}
