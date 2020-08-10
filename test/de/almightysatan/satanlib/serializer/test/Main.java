package de.almightysatan.satanlib.serializer.test;

import java.util.Arrays;

import de.almightysatan.satanlib.serializer.Serializer;

public class Main {

	public static void main(String[] args) throws Throwable {
		Serializer serializer = new Serializer();
		
		serializer.setProtected(false);
		serializer.loadClass(TestObject.class, 50);
		serializer.loadClass(TestList.class, 1);
		serializer.loadClass(TestMap.class, 2);
		serializer.loadClass(TestObject2.class, 3);
		
		TestObject original = new TestObject(5, "Hello World", "test");
		byte[] data = serializer.serialize(original);
		
		System.out.println(Arrays.toString(data));
		
		TestObject deserialized = (TestObject) serializer.deserialize(data);
		System.out.println(deserialized.toString());
		System.out.println();
		System.out.println(original.toString().equals(deserialized.toString()));
	}
}
