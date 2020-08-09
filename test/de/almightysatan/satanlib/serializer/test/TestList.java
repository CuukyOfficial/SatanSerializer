package de.almightysatan.satanlib.serializer.test;

import java.util.ArrayList;
import java.util.Arrays;

import de.almightysatan.satanlib.serializer.SerializeId;

public class TestList extends ArrayList<String> {

	private static final long serialVersionUID = 1314562316022254435L;
	
	@SerializeId(0)
	private int a;
	
	public TestList() {}
	
	public TestList(int a) {
		this.a = a;
		add("ListObject0");
		add("ListObject1");
	}
	
	@Override
	public String toString() {
		return "{a=" + a + " " + Arrays.toString(toArray()) + "}";
	}
}
