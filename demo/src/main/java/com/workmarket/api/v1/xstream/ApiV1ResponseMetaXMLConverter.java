package com.workmarket.api.v1.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.workmarket.api.v1.ApiV1ResponseMeta;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class ApiV1ResponseMetaXMLConverter implements Converter, ConverterMatcher {
	private static final String ELEMENT_ERRORS = "errors";
	private static final String ELEMENT_STATUS_CODE = "status_code";
	private static final String ELEMENT_VERSION = "version";
	private static final String ELEMENT_EXECUTION_TIME = "execution_time";
	private static final String ELEMENT_TIMESTAMP = "timestamp";

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		ApiV1ResponseMeta meta = (ApiV1ResponseMeta)source;

		writer.startNode(ELEMENT_ERRORS);

		if (CollectionUtils.isNotEmpty(meta.getErrors())) {
			context.convertAnother(meta.getErrors());
		}

		writer.endNode();

		writer.startNode(ELEMENT_STATUS_CODE);
		writer.setValue(String.valueOf(meta.getStatusCode()));
		writer.endNode();

		writer.startNode(ELEMENT_VERSION);
		writer.setValue(String.valueOf(meta.getVersion()));
		writer.endNode();

		writer.startNode(ELEMENT_EXECUTION_TIME);
		writer.setValue(String.valueOf(meta.getExecutionTime()));
		writer.endNode();

		writer.startNode(ELEMENT_TIMESTAMP);
		writer.setValue(String.valueOf(meta.getTimestamp()));
		writer.endNode();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		ApiV1ResponseMeta meta = new ApiV1ResponseMeta();

		while (reader.hasMoreChildren()) {
			reader.moveDown();

			if (ELEMENT_ERRORS.equals(reader.getNodeName())) {
				List<Object> errors = (List)context.convertAnother(meta, List.class);
				meta.setErrors(errors);
			}
			else if (ELEMENT_STATUS_CODE.equals(reader.getNodeName())) {
				meta.setStatusCode(Integer.valueOf(reader.getValue()));
			}
			else if (ELEMENT_VERSION.equals(reader.getNodeName())) {
				meta.setVersion( Integer.valueOf(reader.getValue()) );
			}
			else if (ELEMENT_EXECUTION_TIME.equals(reader.getNodeName())) {
				meta.setExecutionTime(Double.valueOf(reader.getValue()));
			}
			else if (ELEMENT_TIMESTAMP.equals(reader.getNodeName())) {
				meta.setTimestamp( Long.valueOf(reader.getValue()) );
			}

			reader.moveUp();
		}

		return meta;
	}

	@Override
	public boolean canConvert(Class type) {
		return ApiV1ResponseMeta.class.equals(type);
	}
}
