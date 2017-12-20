package com.workmarket.api;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.SimpleType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.jackson.ModelResolver;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.util.Json;

/**
 * Created by joshlevine on 12/29/16.
 */
public class ApiSwaggerModelConverter extends ModelResolver {
	private static final Log logger = LogFactory.getLog(ApiSwaggerModelConverter.class);

	public static final String REFERENCE_GENERIC_LIST = "__GenericList__";
	public static final String REFERENCE_GENERIC_MAP = "__GenericMap__";
	public static final String REFERENCE_GENERIC = "__Generic__";

	public ApiSwaggerModelConverter() {
		super(Json.mapper());
	}

	public ApiSwaggerModelConverter(ObjectMapper mapper) {
		super(mapper);
	}

	@Override
	protected JavaType getInnerType(String innerType) {
		return super.getInnerType(innerType);
	}


	@Override
	public Property resolveProperty(Type type,
																	ModelConverterContext context,
																	Annotation[] annotations,
																	Iterator<ModelConverter> next) {
		return super.resolveProperty(type, context, annotations, next);
	}

	@Override
	public Property resolveProperty(JavaType propType,
																	ModelConverterContext context,
																	Annotation[] annotations,
																	Iterator<ModelConverter> next) {
		logger.debug("API SWAGGER - property[" + propType.toString() + "]");
		if (propType instanceof SimpleType) {
			SimpleType simpleType = (SimpleType) propType;

			if (simpleType.containedTypeCount() == 1) {
				Property wrapper;
				if (simpleType.isContainerType()) {
					logger.debug("API SWAGGER - property[" + propType.toString() + "] - resolving as container type");
					return next.next().resolveProperty(simpleType, context, annotations, next);
				}
				JavaType containedType = propType.containedType(0);
				logger.debug("API SWAGGER - property["
										 + propType.toString()
										 + "] - contained type["
										 + containedType.toString()
										 + "]");
				wrapper = new ObjectProperty();
				wrapper.setName(simpleType.toCanonical());
				addProperties(propType, context, containedType, new MutableModelOrProperty<>(wrapper), next);
				return wrapper;
			}
		}

		logger.debug("API SWAGGER - property[" + propType.toString() + "] - normal property");
		return super.resolveProperty(propType, context, annotations, next);
	}

	@Override
	public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> next) {
		logger.debug("API SWAGGER - model[" + type.toString() + "]");
		JavaType propType = Json.mapper().constructType(type);
		if (propType instanceof SimpleType) {
			SimpleType simpleType = (SimpleType) propType;
			logger.debug("API SWAGGER - model[" + type.toString() + "] - simple type");

			if (simpleType.containedTypeCount() == 1) {
				logger.debug("API SWAGGER - model[" + type.toString() + "] - generic");
				if (simpleType.containedType(0).isContainerType()) {
					logger.debug("API SWAGGER - model[" + type.toString() + "] - container type");
					return next.next().resolve(simpleType.containedType(0), context, next);
				}
				logger.debug("API SWAGGER - model[" + type.toString() + "] - non-container type");
				ModelImpl wrapper = new ModelImpl();
				Type dtoType = propType.containedType(0);
				addProperties(propType, context, dtoType, new MutableModelOrProperty<>(wrapper), next);
				logger.debug("API SWAGGER - model[" + type.toString() + "] - dto type[" + dtoType.toString() + "]");
				return null;
			}
		}
		return super.resolve(type, context, next);
	}

	@Override
	public Model resolve(JavaType type, ModelConverterContext context, Iterator<ModelConverter> next) {
		return next.next().resolve(type, context, next);
	}

	private void addProperties(JavaType propType,
														 ModelConverterContext context,
														 Type dtoType,
														 MutableModelOrProperty wrapper,
														 Iterator<ModelConverter> next) {
		logger.debug("API SWAGGER - addProperties: " + propType);
		logger.debug("API SWAGGER - addProperties - wrapper: " + wrapper);
		for (Field field : propType.getRawClass().getDeclaredFields()) {
			logger.debug("API SWAGGER - addProperties - field[" + field.getName() + "]");

			// Only process fields that have ApiModelProperty annotations
			if (!field.isAnnotationPresent(ApiModelProperty.class)) {
				continue;
			}

			ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
			logger.debug("API SWAGGER - addProperties - field["
									 + field.getName()
									 + "] - ApiModelProperty: "
									 + apiModelProperty);
			if (REFERENCE_GENERIC_LIST.equals(apiModelProperty.reference())) {
				ArrayProperty results = new ArrayProperty();
				results.setItems(resolveProperty(dtoType, context, field.getDeclaredAnnotations(), next));
				wrapper.addProperty(field.getName(), results);
			} else if (REFERENCE_GENERIC_MAP.equals(apiModelProperty.reference())) {
				MapProperty results = new MapProperty();
				results.setAdditionalProperties(resolveProperty(dtoType, context, field.getDeclaredAnnotations(), next));
				wrapper.addProperty(field.getName(), results);
			} else if (REFERENCE_GENERIC.equals(apiModelProperty.reference())) {
				wrapper.addProperty(field.getName(), resolveProperty(dtoType, context, field.getDeclaredAnnotations(), next));
			} else {
				// get dataType from ApiModelProperty
				String dataType = apiModelProperty.dataType();
				logger.debug("API SWAGGER - addProperties - field[" + field.getName() + "] - Data Type: " + dataType);
				if (StringUtils.isEmpty(dataType)) {
					logger.debug("API SWAGGER - addProperties - field[" + field.getName() + "] - Normal property");
					wrapper.addProperty(field.getName(),
															resolveProperty(field.getGenericType(), context, field.getDeclaredAnnotations(), next));
				} else {
					try {
						// resolve dataType
						Type resolvedDataType = Json.mapper().constructType(Class.forName(dataType));
						// add dataType to wrapper
						wrapper.addProperty(field.getName(),
																resolveProperty(resolvedDataType, context, field.getDeclaredAnnotations(), next));
					}
					catch (ClassNotFoundException e) {
						logger.debug("API SWAGGER - addProperties - field[" + field.getName() + "] - Data Type Failed.");
					}
				}
			}
		}
	}


	private class MutableModelOrProperty<T> {
		private T modelOrProperty;

		public MutableModelOrProperty(T modelOrProperty) {
			this.modelOrProperty = modelOrProperty;
		}

		public void addProperty(String name, Property value) {
			logger.debug("API SWAGGER - addProperties - field[" + name + "] - property: " + value);
			if (modelOrProperty instanceof ModelImpl) {
				logger.debug("API SWAGGER - addProperties - adding");
				((ModelImpl) modelOrProperty).property(name, value);
			} else if (modelOrProperty instanceof ObjectProperty) {
				logger.debug("API SWAGGER - addProperties - adding");
				((ObjectProperty) modelOrProperty).property(name, value);
			} else {
				logger.debug("API SWAGGER - addProperties - skipping");
			}
		}
	}
}
