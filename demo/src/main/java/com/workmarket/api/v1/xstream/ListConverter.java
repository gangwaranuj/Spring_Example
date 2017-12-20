package com.workmarket.api.v1.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.collections.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListConverter implements Converter {
	public static final String ELEMENT_ITEM = "item";

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		List list = (List)source;

		if (CollectionUtils.isNotEmpty(list)) {
			for (Object item : list) {
				writer.startNode(ELEMENT_ITEM);

				if (item != null) {
					context.convertAnother(item);
				}

				writer.endNode();
			}
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		List<Object> list = new LinkedList<Object>();

		while (reader.hasMoreChildren()) {
			reader.moveDown();

			if (ELEMENT_ITEM.equals(reader.getNodeName())) {
				list.add(context.convertAnother(list, Map.class));
			}

			reader.moveUp();
		}

		return list;
	}

	@Override
	public boolean canConvert(Class type) {
		return List.class.isAssignableFrom(type);
	}
}
