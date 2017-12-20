package com.workmarket.web.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.workmarket.utility.StringUtilities;

import java.io.IOException;
import java.util.Collection;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Created by nick on 8/8/13 10:45 AM
 * <p/>
 * This does an object graph traversal and XSS escapes any strings
 */
public class XSSEscapingSerializer extends JsonSerializer<Collection> {

	@Override
	public void serialize(
			Collection collection,
			JsonGenerator generator,
			SerializerProvider serializerProvider) throws IOException {

		serializeCollection(collection, generator);
	}

	private void serializeCollection(Collection collection, JsonGenerator generator) throws IOException {
		generator.writeStartArray();
		if (isNotEmpty(collection)) {
			for (Object o : collection) {
				if (o instanceof Collection)
					serializeCollection((Collection) o, generator);
				else if (o instanceof String) {
					generator.writeString(StringUtilities.stripXSSAndEscapeHtml((String) o));
				} else {
					// TODO: need to use reflection here and XSS escape strings
					// this only covers Collections right now
					generator.writeObject(o);
				}
			}
		}
		generator.writeEndArray();
	}
}
