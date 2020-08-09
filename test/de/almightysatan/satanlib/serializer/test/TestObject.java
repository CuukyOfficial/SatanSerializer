package de.almightysatan.satanlib.serializer.test;

import de.almightysatan.satanlib.serializer.JavaSerializer;
import de.almightysatan.satanlib.serializer.SerializeId;

public class TestObject {

	@SerializeId(0)
	public int a;
	
	@JavaSerializer
	@SerializeId(1)
	public String b;
	
	@SerializeId(2)
	public String c;
	
	public TestObject(int a, String b, String c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public TestObject() {}
}
