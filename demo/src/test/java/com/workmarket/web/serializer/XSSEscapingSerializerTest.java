package com.workmarket.web.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.Lists;
import com.workmarket.web.serializers.XSSEscapingSerializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by nick on 8/8/13 11:53 AM
 */
@RunWith(MockitoJUnitRunner.class)
public class XSSEscapingSerializerTest {

	private static final String XSS_ATTACK = "test')\"onmouseover=\"alert(1)\"";
	private static final String STRIPPED_XSS_ATTACK = "test')&quot;onmouseover=&quot;alert(1)&quot;";
	private XSSEscapingSerializer serializer = new XSSEscapingSerializer();
	private SerializerProvider serializerProvider;
	private JsonGenerator jsonGenerator;

	@Before
	public void setup() throws IOException {
		serializerProvider = mock(SerializerProvider.class);
		jsonGenerator = mock(JsonGenerator.class);
	}

	@Test
	public void serialize_NullList_unchanged() {
		try {
			serializer.serialize(null, jsonGenerator, serializerProvider);
			verify(jsonGenerator, never()).writeString(any(String.class));
			verify(jsonGenerator, never()).writeObject(any(Object.class));
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void serialize_EmptyList_unchanged() {
		try {
			serializer.serialize(Lists.newArrayList(), jsonGenerator, serializerProvider);
			verify(jsonGenerator, never()).writeString(any(String.class));
			verify(jsonGenerator, never()).writeObject(any(Object.class));
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void serialize_ListWithoutHTML_unchanged() {
		try {
			serializer.serialize(Lists.newArrayList("test"), jsonGenerator, serializerProvider);
			verify(jsonGenerator).writeString("test");
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void serialize_ListWithXSS_filtered() {
		try {
			serializer.serialize(Lists.newArrayList(XSS_ATTACK), jsonGenerator, serializerProvider);
			verify(jsonGenerator).writeString(STRIPPED_XSS_ATTACK);
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void serialize_ListWithNestedXSS_filtered() {
		try {
			serializer.serialize(Lists.newArrayList(Lists.newArrayList(XSS_ATTACK)), jsonGenerator, serializerProvider);
			verify(jsonGenerator).writeString(STRIPPED_XSS_ATTACK);
		} catch (IOException e) {
			fail();
		}
	}
}
