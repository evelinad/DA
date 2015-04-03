package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeMessage {

	public static byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteArray);
			objectStream.writeObject(obj);
			return byteArray.toByteArray();
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
		
	}
	
		public static Object deserialize(byte[] bytes) {
			try {
				ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
				ObjectInputStream objectStream = new ObjectInputStream(byteArray);
				return objectStream.readObject();
			} catch (Exception exc) {
				exc.printStackTrace();
				return null;
			}
		}
}
