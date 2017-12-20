package com.workmarket.utility;

import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class SerializationUtilitiesTest {
	private final String sampleText = "Work Market is a platform for the management of labor and human resource services.";
	private final String base64sampleText = "V29yayBNYXJrZXQgaXMgYSBwbGF0Zm9ybSBmb3IgdGhlIG1hbmFnZW1lbnQgb2YgbGFib3IgYW5kIGh1bWFuIHJlc291cmNlIHNlcnZpY2VzLg==";

	@Test
	public void encodeBase64Bytes() throws Exception {
		String base64string = SerializationUtilities.encodeBase64(sampleText.getBytes("UTF-8"));
		assertEquals(base64sampleText, base64string);
	}

	@Test
	public void encodeBase64InputStream() throws Exception {
		InputStream in = new ByteArrayInputStream(sampleText.getBytes("UTF-8"));
		String base64string;

		try {
			base64string = SerializationUtilities.encodeBase64(in);
		} finally {
			in.close();
		}

		assertEquals(base64sampleText, base64string);
	}

	@Test
	public void decodeBase64String() throws Exception {
		byte[] binaryData = SerializationUtilities.decodeBase64String(base64sampleText);
		assertArrayEquals(sampleText.getBytes(), binaryData);
	}

	@Test
	public void decodeBase64File() throws Exception {
		File target = File.createTempFile("test", "dat");
		int bytesCopied = SerializationUtilities.decodeBase64File(base64sampleText, target);
		assertEquals(sampleText.getBytes("UTF-8").length, bytesCopied);
		String targetContent = new String(FileCopyUtils.copyToByteArray(target));
		assertEquals(sampleText, targetContent);
	}
}
