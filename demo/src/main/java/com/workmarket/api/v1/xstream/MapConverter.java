package com.workmarket.api.v1.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.AbstractPullReader;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapConverter implements Converter {

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Map map = (Map)source;

		for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry)i.next();
			String key = String.valueOf(entry.getKey());
			Object value = entry.getValue();

			writer.startNode(key);

			if (value != null) {
				context.convertAnother(value);
			}

			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader streamReader, UnmarshallingContext context) {
		AbstractPullReader reader = (AbstractPullReader)streamReader;
		Map<String,Object> map = new LinkedHashMap<String,Object>();

		while (reader.hasMoreChildren()) {
			reader.moveDown();

			if (reader.hasMoreChildren()) {
				String parentName = reader.getNodeName();
				reader.mark();
				reader.moveDown();

				String nodeName = reader.getNodeName();
				reader.reset();

				if (ListConverter.ELEMENT_ITEM.equals(nodeName)) {
					map.put(parentName, context.convertAnother(map, List.class));
				}
				else {
					map.put(parentName, context.convertAnother(map, Map.class));
				}
			}
			else {
				String value = reader.getValue();

				if ("successful".equals(reader.getNodeName())) {
					Boolean boolValue = (Boolean)context.convertAnother(map, Boolean.class, BooleanConverter.INSTANCE);
					map.put(reader.getNodeName(), boolValue);
				}
				else {
					map.put(reader.getNodeName(), StringUtils.hasText(value) ? value : null);
				}
			}

			reader.moveUp();
		}

		return map;
	}

	@Override
	public boolean canConvert(Class type) {
		return Map.class.isAssignableFrom(type);
	}
}
