package com.workmarket.service.business.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.Template;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.configuration.Constants;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@Ignore
public class TemplateServiceIT extends BaseServiceIT {
	@Autowired TemplateService templateService;

	// assumes working directory is application/backend/
	public static final String TEMPLATE_CLASS_DIR = "src/main/java/com/workmarket/common/template";
	public static final String CLASS_PATH = "src/main/java/";

	private Map<String, Object> types;

	public static final List<String> EXCLUDES = Lists.newArrayList(
			"NotificationTemplate",
			"NotificationTemplateFactoryImpl",
			"EmailTemplateFactoryImpl",
			"NotificationEmailTemplate",
			"PDFTemplateFactoryImpl",
			"FaxTemplateFactoryImpl",
			"SMSTemplateFactoryImpl",
			"VoiceTemplateFactoryImpl"
	);

	@Test
	public void renderAllTemplates_GivenData() {
		// Create a map of types to pass in dummy data
		types = new HashMap<>();

		try {
			// primitives
			types.put("boolean", true);
			types.put("int", 1);
			types.put("long", 1L);
			types.put("double", 1.1);
			types.put("byte", 1);
			types.put("float", 1f);
			types.put("char", 'a');

			// generic stuff
			types.put("java.lang.Long", 5L);
			types.put("java.lang.String", "qwertyuiop");
			types.put("java.lang.Boolean", Boolean.TRUE);
			types.put("java.math.BigDecimal", BigDecimal.TEN);
			types.put("java.lang.Integer", 5);
			types.put("java.lang.Double", 5.0);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(1356134400000L);
			types.put("java.util.Calendar", calendar);
			types.put("java.lang.Throwable", new Throwable());
		} catch (Exception e) {
			Assert.fail("Error building types map");
		}

		// find all template classes
		List<File> classes = new ArrayList<File>();
		findAllClasses(new File(TEMPLATE_CLASS_DIR), classes);

		// run tests
		Set<String> missingTypes = new HashSet<String>();
		Set<String> parseErrors = new HashSet<String>();

		for (File classFile : classes) {
			String formattedPath = classFile.getPath()
					.replace('\\', '/') // windows path fix
					.replaceFirst(CLASS_PATH, "")
					.replace(".java", "")
					.replace('/', '.');

			Class clazz = null;

			try {
				// expects format to be like
				// com.workmarket.common.template.AbstractWorkNotificationTemplate
				clazz = Class.forName(formattedPath);
			} catch (Exception e) {
				Assert.fail("Error finding template classes");
			}

			// only want concrete templates
			if (Modifier.isAbstract(clazz.getModifiers())
					|| Modifier.isInterface(clazz.getModifiers())
					|| EXCLUDES.contains(clazz.getSimpleName()))
				continue;

			// try rendering the template with every constructor
			constructorLoop:
			for (Constructor constructor : clazz.getConstructors()) {
				List<Object> parameters = new ArrayList<Object>();

				for (Type type : constructor.getGenericParameterTypes()) {
					try {
						parameters.add(getInstanceForType(type));
					} catch (MissingTypeException e) {
						missingTypes.add(e.getType());
						break constructorLoop;
					}
				}

				Object templateObject;

				try {
					templateObject = constructor.newInstance(parameters.toArray());
				} catch (Exception e) {
					parseErrors.add("[" + clazz.getSimpleName() + "] appears to have a broken constructor " + constructor.getName());
					break;
				}

				if (templateObject == null)
					continue;

				// render stuff
				Set<Template> templates = new HashSet<Template>();

				if (templateObject instanceof Template) {
					templates.add((Template)templateObject);
				} else if(templateObject instanceof NotificationTemplate) {
					templates.add(((NotificationTemplate) templateObject).getEmailTemplate());
					templates.add(((NotificationTemplate) templateObject).getSMSTemplate());
					templates.add(((NotificationTemplate) templateObject).getPdfTemplate());
					templates.add(((NotificationTemplate) templateObject).getUserNotificationTemplate());
				} else {
					continue;
				}

				for (Template template : templates) {
					if (template == null)
						continue;

					if (template instanceof EmailTemplate) {
						((EmailTemplate) template).setToId(Constants.WORKMARKET_SYSTEM_USER_ID);
						((EmailTemplate) template).setFromId(Constants.WORKMARKET_SYSTEM_USER_ID);
					}

					// populate any null fields on the model
					Object templateModel = template.getModel();
					Map<String, String> properties;

					try {
						properties = BeanUtils.describe(templateModel);
					} catch (Exception e) {
						continue;
					}

					for (Map.Entry<String, String> property : properties.entrySet()) {
						if (property.getValue() == null) {
							try {
								BeanUtils.setProperty(templateModel, property.getKey(),
										getInstanceForType(PropertyUtils.getPropertyType(templateModel, property.getKey())));
							} catch (MissingTypeException e) {
								try {
									missingTypes.add(PropertyUtils.getPropertyType(templateModel, property.getKey()).toString());
								} catch (Exception e1) { }
							} catch (Exception e) { }
						}
					}

					try {
						templateService.render(template);
					} catch (ResourceNotFoundException e) {
						// no template for this type
						continue;
					} catch (Exception e) {
						parseErrors.add(e.getMessage());
					}
				}
			}
		}

		Assert.assertTrue("The following types could not be handled properly, please add them to the test: " + StringUtils.join(missingTypes, "\n"), missingTypes.isEmpty());
		Assert.assertTrue(StringUtils.join(parseErrors, "\n"), parseErrors.isEmpty());
	}

	private void findAllClasses(File directory, List<File> classes) {
		File[] contents = directory.listFiles();
		List<File> directories = new ArrayList<File>();

		if (contents == null)
			Assert.fail("Failed to read templates... is your working directory correct? This test requires it to be /application/backend");

		for (int i = 0; i < contents.length; i++) {
			if (contents[i].isDirectory()) {
				directories.add(contents[i]);
			} else if (contents[i].getName().endsWith(".java")) {
				classes.add(contents[i]);
			}
		}

		for (File subDirectory : directories) {
			findAllClasses(subDirectory, classes);
		}
	}

	private Object getInstanceForType(Type type) throws MissingTypeException{
		if (type instanceof ParameterizedType) {
			String typeName = ((ParameterizedTypeImpl) type).getRawType().getName();

			if (typeName == "java.util.Set") {
				return Sets.newHashSet(getInstanceForType(((ParameterizedTypeImpl) type).getActualTypeArguments()[0]));
			} else if (typeName == "java.util.List") {
				return Lists.newArrayList(getInstanceForType(((ParameterizedTypeImpl) type).getActualTypeArguments()[0]));
			} else if (typeName == "java.util.Map") {
				Map mapMock = mock(Map.class);
				when(mapMock.get(any(Object.class))).thenReturn(getInstanceForType(((ParameterizedTypeImpl) type).getActualTypeArguments()[1]));
				return mapMock;
			}

			throw new MissingTypeException(((ParameterizedTypeImpl) type).getRawType().getName());
		} else if (type instanceof Class) {
			String typeName = ((Class) type).getName();

			// Java primitives / generic stuff
			if (types.containsKey(typeName)) {
				return types.get(typeName);
			}

			Class clazz;

			try {
				clazz = Class.forName(typeName);
			} catch (ClassNotFoundException e) {
				throw new MissingTypeException(typeName);
			}

			// enumerations
			if (clazz.isEnum()) {
				return clazz.getEnumConstants()[0];
			}

			// arrays
			if (clazz.isArray()) {
				if (clazz.getComponentType().getName().equals("java.lang.String"))
					return new String[]{"hello", "world"};

				throw new MissingTypeException(typeName);
			}

			// and everything else
			try {
				return mock(clazz, Mockito.RETURNS_MOCKS);
			} catch (Exception e) {
				throw new MissingTypeException(typeName);
			}
		} else {
			throw new MissingTypeException(type.toString());
		}
	}

	private class MissingTypeException extends Exception {
		private String type;

		public MissingTypeException(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}
}

