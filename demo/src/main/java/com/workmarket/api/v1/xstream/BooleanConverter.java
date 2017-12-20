package com.workmarket.api.v1.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class BooleanConverter implements Converter, ConverterMatcher {
	public static final BooleanConverter INSTANCE = new BooleanConverter();

	public static BooleanConverter getInstance() {
		return INSTANCE;
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Boolean value = (Boolean) source;
		writer.setValue(Boolean.TRUE.equals(value) ? "1" : "0");
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String value = reader.getValue();
		return "1".equals(value) || "true".equalsIgnoreCase(value);
	}

	@Override
	public boolean canConvert(Class type) {
		return boolean.class == type || Boolean.class == type;
	}
}
