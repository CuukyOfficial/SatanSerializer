package de.almightysatan.satanlib.serializer.test;

import java.util.ArrayList;
import java.util.HashMap;

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
	
	@SerializeId(3)
	private ArrayList<String> list;
	
	@SerializeId(4)
	private TestList customList;
	
	@SerializeId(5)
	private HashMap<String, String> map;
	
	@SerializeId(6)
	private TestMap customMap;
	
	public TestObject(int a, String b, String c) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.list = new ArrayList<>();
		this.list.add("listObject0");
		this.list.add("listObject1");
		this.customList = new TestList(10015);
		this.map = new HashMap<>();
		this.map.put("test0", "abc");
		this.map.put("test1", "def");
		this.customMap = new TestMap(420);
	}
	
	public TestObject() {}
	
	@Override
	public String toString() {
		return a + " : " + b + " : " + c + " : " + list + " : " + customList + " : " + map  + " : " + customMap;
	}
}
