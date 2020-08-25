package de.almightysatan.satanlib.serializer.test;

import java.util.ArrayList;
import java.util.Arrays;
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

	@SerializeId(7)
	private String[] array;

	@SerializeId(8)
	private TestObject2[] customArray;

	@SerializeId(9)
	private byte[] byteArray;

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
		this.array = new String[] {"ArrayObject0", "ArrayObject1"};
		this.customArray = new TestObject2[] {new TestObject2(5), new TestObject2(10)};
		this.byteArray = new byte[] {10, 20};
	}

	public TestObject() {}

	@Override
	public String toString() {
		return a + " : " + b + " : " + c + " : " + list + " : " + customList + " : " + map  + " : "
				+ customMap + " : " + Arrays.toString(array) + " : " + Arrays.toString(customArray) + " : " + Arrays.toString(byteArray);
	}
}
