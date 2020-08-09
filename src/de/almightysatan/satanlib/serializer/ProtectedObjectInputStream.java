package de.almightysatan.satanlib.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

class ProtectedObjectInputStream extends ObjectInputStream{
	
	private Serializer serializer;

	ProtectedObjectInputStream(InputStream in, Serializer serializer) throws IOException {
		super(in);
		this.serializer = serializer;
	}
	
	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		Class<?> clazz = super.resolveClass(desc);
		
		if(serializer.isProtected())
			if(!Serializer.GLOBAL_WHITELIST.contains(clazz) && !serializer.whitelist.contains(clazz))
				throw new Error("class not whitelisted: " + clazz.getName());
		
		return clazz;
	}
}
