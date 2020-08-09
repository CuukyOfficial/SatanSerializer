package de.almightysatan.satanlib.serializer.test;

import java.util.Arrays;

import de.almightysatan.satanlib.serializer.Serializer;

public class Main {

	public static void main(String[] args) throws Throwable {
		Serializer serializer = new Serializer();
		
		serializer.setProtected(false);
		serializer.loadClass(TestObject.class, 50);
		
		byte[] data = serializer.serialize(new TestObject(5, "Hello World", "test"));
		
		System.out.println(Arrays.toString(data));
		
		TestObject deserialized = (TestObject) serializer.deserialize(data);
		System.out.println(deserialized.a + " : " + deserialized.b + " : " + deserialized.c);
		
	}
}
