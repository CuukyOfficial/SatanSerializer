package de.almightysatan.satanlib.serializer.test;

import java.util.HashMap;

import de.almightysatan.satanlib.serializer.SerializeId;

public class TestMap extends HashMap<String, String> {

	private static final long serialVersionUID = 1314562316022254436L;
	
	@SerializeId(0)
	private int a;
	
	public TestMap() {}
	
	public TestMap(int a) {
		this.a = a;
		put("MapObject0", "a");
		put("MapObject1", "b");
	}
	
	@Override
	public String toString() {
		return "{a=" + a + " " + super.toString() + "}";
	}
}
