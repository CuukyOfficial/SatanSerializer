package de.almightysatan.satanlib.serializer.test;

import de.almightysatan.satanlib.serializer.SerializeId;
import de.almightysatan.satanlib.serializer.Serializer;

public class Benchmark {
	
	public static int ITERATIONS = 10000000;

	public static void main(String[] args) throws Throwable {
		System.out.println("--- Serializer Benchmark ---");
		
		long time = System.currentTimeMillis();

		Serializer serializer = new Serializer();
		serializer.setProtected(false);
		serializer.loadClass(BenchTest0.class, 100);

		System.out.println("init: " + (System.currentTimeMillis() - time) + "ms");
		System.out.println();
		System.out.println("iterations: " + ITERATIONS);

		time = System.currentTimeMillis();

		for(int i = 0; i < ITERATIONS; i++)
			serializer.serialize(new BenchTest0(100, "Hello World"));

		System.out.println("serialisation: " + (System.currentTimeMillis() - time) + "ms");
		
		BenchTest0 obj = new BenchTest0(100, "Hello World");
		byte[] serialized = serializer.serialize(obj);
		
		time = System.currentTimeMillis();
		
		for(int i = 0; i < ITERATIONS; i++)
			serializer.deserialize(serialized);
		
		System.out.println("deserialisation: " + (System.currentTimeMillis() - time) + "ms");
		System.out.println();
		System.out.println("size: " + serialized.length);
		System.out.println();
		
		BenchTest0 deserialized = (BenchTest0) serializer.deserialize(serialized);
		if(deserialized.field0 == obj.field0 && deserialized.field1.equals(obj.field1))
			System.out.println("PASSED");
		else
			System.out.println("FAILED");
	}
	
	public static class BenchTest0 {
		
		@SerializeId(0)
		public int field0;
		@SerializeId(1)
		public String field1;
		
		public BenchTest0() {}
		
		public BenchTest0(int field0, String field1) {
			this.field0 = field0;
			this.field1 = field1;
		}
	}
}
