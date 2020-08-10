package de.almightysatan.satanlib.serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class Serializer {
	
	public static final String VERSION = "1.0";
	public static final int ENCODER_VERSION = 1;
	
	static final byte INDEX_BREAK = -1;
	static final byte INDEX_JAVA_OBJECT = 1;
	static final byte INDEX_CUSTOM_OBJECT = 2;
	static final byte INDEX_CUSTOM_ENUM = 3;
	static final byte INDEX_BYTE = 4;
	static final byte INDEX_SHORT = 5;
	static final byte INDEX_INT = 6;
	static final byte INDEX_LONG = 7;
	static final byte INDEX_FLOAT = 8;
	static final byte INDEX_DOUBLE = 9;
	static final byte INDEX_BOOL = 10;
	static final byte INDEX_CHAR = 11;
	static final byte INDEX_STRING = 12;
	static final byte INDEX_UUID = 13;
	static final byte INDEX_LIST = 14;
	static final byte INDEX_CUSTOM_LIST = 15;
	static final byte INDEX_ARRAY = 16;
	static final byte INDEX_CUSTOM_ARRAY = 17;
	static final byte INDEX_MAP = 18;
	static final byte INDEX_CUSTOM_MAP = 19;
	
	static final List<Class<?>> GLOBAL_WHITELIST = new ArrayList<>();
	
	static final Encoder ENCODER = new Encoder();
	static final Decoder DECODER = new Decoder();
	
	Map<Class<?>, SerializableClazz> clazzMap = new HashMap<>();
	Map<Integer, SerializableClazz> clazzIdMap = new HashMap<>();
	IAnnotationManager annotationManager;
	ArrayList<Class<?>> whitelist = new ArrayList<>();
	boolean protect = true;
	boolean allowNegativeId = true;
	
	public Serializer() {
		this.annotationManager = new DefaultAnnotationManager();
	}
	
	public Serializer(IAnnotationManager annotionManager) {
		this.annotationManager = annotionManager;
	}
	
	public void loadClass(Class<?> clazz, int id) throws IllegalAccessException {
		if(this.clazzIdMap.containsKey(id))
			throw new Error("Duplicate id " + id + " for classes: " + clazz.getName() + " and " + this.clazzIdMap.get(id).clazz.getName());
		
		SerializableClazz serializableClazz = new SerializableClazz(this.annotationManager, clazz, id);
		this.clazzMap.put(clazz, serializableClazz);
		this.clazzIdMap.put(id, serializableClazz);
	}
	
	public void addToWhitelist(Class<?> clazz) {
		this.whitelist.add(clazz);
	}
	
	public void addToWhitelist(ArrayList<Class<?>> clazzes) {
		this.whitelist.addAll(clazzes);
	}
	
	public void addToWhitelist(Class<?>... clazzes) {
		for(Class<?> clazz : clazzes)
			this.addToWhitelist(clazz);
	}
	
	public byte[] serialize(Object object) throws Throwable {
		return ENCODER.serialize(this, object);
	}
	
	public Object deserialize(byte[] data) throws Throwable {
		return DECODER.deserialize(this, data);
	}
	
	public Object clone(Object in) throws Throwable {
		return deserialize(serialize(in));
	}
	
	public boolean isProtected() {
		return protect;
	}

	public void setProtected(boolean proteced) {
		this.protect = proteced;
	}

	public boolean isAllowNegativeId() {
		return allowNegativeId;
	}

	public void setAllowNegativeId(boolean allowNegativeId) {
		this.allowNegativeId = allowNegativeId;
	}

	static {
		GLOBAL_WHITELIST.add(Number.class);
		GLOBAL_WHITELIST.add(Boolean.class);
		GLOBAL_WHITELIST.add(Byte.class);
		GLOBAL_WHITELIST.add(Character.class);
		GLOBAL_WHITELIST.add(Short.class);
		GLOBAL_WHITELIST.add(Integer.class);
		GLOBAL_WHITELIST.add(Long.class);
		GLOBAL_WHITELIST.add(Float.class);
		GLOBAL_WHITELIST.add(Double.class);
		GLOBAL_WHITELIST.add(Boolean[].class);
		GLOBAL_WHITELIST.add(Byte[].class);
		GLOBAL_WHITELIST.add(Character[].class);
		GLOBAL_WHITELIST.add(Short[].class);
		GLOBAL_WHITELIST.add(Integer[].class);
		GLOBAL_WHITELIST.add(Long[].class);
		GLOBAL_WHITELIST.add(Float[].class);
		GLOBAL_WHITELIST.add(Double[].class);
		GLOBAL_WHITELIST.add(boolean[].class);
		GLOBAL_WHITELIST.add(byte[].class);
		GLOBAL_WHITELIST.add(char[].class);
		GLOBAL_WHITELIST.add(short[].class);
		GLOBAL_WHITELIST.add(int[].class);
		GLOBAL_WHITELIST.add(long[].class);
		GLOBAL_WHITELIST.add(float[].class);
		GLOBAL_WHITELIST.add(double[].class);
		GLOBAL_WHITELIST.add(String[].class);
		GLOBAL_WHITELIST.add(List.class);
		GLOBAL_WHITELIST.add(ArrayList.class);
		GLOBAL_WHITELIST.add(CopyOnWriteArrayList.class);
		GLOBAL_WHITELIST.add(Vector.class);
		GLOBAL_WHITELIST.add(Map.class);
		GLOBAL_WHITELIST.add(HashMap.class);
		GLOBAL_WHITELIST.add(TreeMap.class);
		GLOBAL_WHITELIST.add(UUID.class);
	}
}
