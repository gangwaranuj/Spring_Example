package com.workmarket.thrift;

import com.google.common.collect.Lists;
import com.workmarket.json.JsonAdapter;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.SerializationUtilities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class ThriftUtilities {
	private static final JsonAdapter jsonAdapter = new ThriftJsonAdapter();

	public static String serializeToJson(Object object) {
		return jsonAdapter.toJson(object);
	}

	public static String serializeToJson(Collection<? extends Serializable> object) {
		List<String> serialized = Lists.newArrayList();
		if (object != null)
			for (Serializable o : object)
				serialized.add(serializeToJson(o));
		return String.format("[%s]", CollectionUtilities.join(serialized, ","));
	}

	public static String serializeToJson(Collection<? extends Serializable> object, String... ignore) {
		List<String> serialized = Lists.newArrayList();
		if (object != null)
			for (Serializable o : object)
				serialized.add(jsonAdapter.toJson(o, ignore));
		return String.format("[%s]", CollectionUtilities.join(serialized, ","));
	}

	public static String serializeToString(Serializable object) throws Exception {
		byte[] bytes = SerializationUtilities.serialize(object);
		return SerializationUtilities.encodeBase64(bytes);
	}

	public static byte[] serialize(Serializable object) throws Exception {
		return SerializationUtilities.serialize(object);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deserialize(String data, Class<T> clazz) throws Exception {
		byte[] bytes = SerializationUtilities.decodeBase64String(data);
		return (T) SerializationUtilities.deserialize(bytes);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deserialize(byte[] data, Class<T> clazz) throws Exception {
		return (T) SerializationUtilities.deserialize(data);
	}
}
