package de.almightysatan.satanlib.serializer.test;

import de.almightysatan.satanlib.serializer.SerializeId;

public class TestObject2 {

	@SerializeId(0)
	public int a;
	
	public TestObject2(int a) {
		this.a = a;
	}
	
	public TestObject2() {}
	
	@Override
	public String toString() {
		return a + "";
	}
}
