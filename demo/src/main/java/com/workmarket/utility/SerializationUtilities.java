package com.workmarket.utility;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.SerializationUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public abstract class SerializationUtilities {
	/**
	 * Encode binary data into Base64 encoded string.
	 * @param binaryData binary data to encode
	 * @return encoded Base64 string
	 */
	public static String encodeBase64(byte[] binaryData) {
		return new String(Base64.encodeBase64(binaryData));
	}

	/**
	 * Encode provided input stream into a Base64 encoded string.
	 * @param in input stream
	 * @return Base64 encoded string
	 * @throws IOException if error reading input stream
	 */
	public static String encodeBase64(InputStream in) throws IOException {
		byte[] binaryData = FileCopyUtils.copyToByteArray(in);
		return encodeBase64(binaryData);
	}

	/**
	 * Decode Base64 string to a byte array.
	 * @param base64string Base64 encoded string
	 * @return decoded bytes
	 */
	public static byte[] decodeBase64String(String base64string) {
		return Base64.decodeBase64(base64string.getBytes());
	}

	/**
	 * Decode Base64 string and output to target file.
	 * @param base64string Base64 encoded string
	 * @param target file path
	 * @return bytes copied
	 * @throws IOException if error writing to target file
	 */
	public static int decodeBase64File(String base64string, File target) throws IOException {
		byte[] objectData = decodeBase64String(base64string);
		FileCopyUtils.copy(objectData, target);
		return objectData.length;
	}

	/**
	 * Deep clone object using serialization
	 * @param original object to clone
	 * @return cloned object
	 */
	public static Object clone(Serializable original) {
		return SerializationUtils.deserialize(SerializationUtils.serialize(original));
	}

	public static byte[] serialize(Serializable serializable) {
		return SerializationUtils.serialize(serializable);
	}

	public static Object deserialize(byte[] bytes) {
		return SerializationUtils.deserialize(bytes);
	}
}
