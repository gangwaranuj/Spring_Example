package com.workmarket.web.validators;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.CustomFieldGroupSaveRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class CustomFieldGroupSaveRequestValidatorTest extends BaseValidatorTest {

	private CustomFieldGroupSaveRequestValidator validator = new CustomFieldGroupSaveRequestValidator();
	private WorkCustomFieldGroup fieldGroup;

	@Before
	public void setup() {
		fieldGroup = new WorkCustomFieldGroup();
		fieldGroup.setName(ANY_STRING);
		WorkCustomField field1 = generateWorkCustomField(ANY_LONG, ANY_STRING, true, WorkCustomFieldType.RESOURCE);
		WorkCustomField field2 = generateWorkCustomField(ANY_LONG_2, ANY_STRING, false, WorkCustomFieldType.OWNER);
		fieldGroup.setWorkCustomFields(Lists.newArrayList(field1, field2));
	}

	@Test
	public void validate_onComplete_resourceNotEmptyRequiredField_pass() {
		CustomFieldGroup customFieldGroup = new CustomFieldGroup();
		customFieldGroup.setName(ANY_STRING);
		CustomField field1 = generateCustomField(ANY_LONG, ANY_STRING, WorkCustomFieldType.RESOURCE);
		CustomField field2 = generateCustomField(ANY_LONG_2, ANY_STRING, WorkCustomFieldType.OWNER);
		field1.setValue(ANY_STRING);
		field2.setValue(ANY_STRING);

		customFieldGroup.addToFields(field1);
		customFieldGroup.addToFields(field2);

		CustomFieldGroupSaveRequest request = new CustomFieldGroupSaveRequest(customFieldGroup, fieldGroup, true, false, true, false);
		assertFalse(validate(request).hasErrors());
	}

	@Test
	public void validate_onComplete_resourceEmptyRequiredField_fail() {
		CustomFieldGroup customFieldGroup = generateCustomFieldGroup();
		CustomFieldGroupSaveRequest request = new CustomFieldGroupSaveRequest(customFieldGroup, fieldGroup, true, false, true, false);
		assertTrue(hasErrorCode(validate(request), "NotNull"));
	}

	@Test
	public void validate_onComplete_bothEmptyRequiredField_fail() {
		CustomFieldGroup customFieldGroup = generateCustomFieldGroup();
		CustomFieldGroupSaveRequest request = new CustomFieldGroupSaveRequest(customFieldGroup, fieldGroup, true, true, true, false);
		assertTrue(hasErrorCode(validate(request), "NotNull"));
	}

	@Test
	public void validate_notComplete_bothEmptyRequiredField_pass() {
		CustomFieldGroup customFieldGroup = generateCustomFieldGroup();
		CustomFieldGroupSaveRequest request = new CustomFieldGroupSaveRequest(customFieldGroup, fieldGroup, true, true, false, false);
		assertFalse(validate(request).hasErrors());
	}


	@Test
	public void validate_onComplete_adminEmptyRequiredResourceField_fail() {
		CustomFieldGroup customFieldGroup = generateCustomFieldGroup();
		CustomFieldGroupSaveRequest request = new CustomFieldGroupSaveRequest(customFieldGroup, fieldGroup, false, true, true, false);
		assertTrue(hasErrorCode(validate(request), "NotNull"));
	}

	@Test
	public void validate_onComplete_adminEmptyRequiredField_fail() {
		CustomFieldGroup customFieldGroup = generateCustomFieldGroup();
		CustomField field3 = generateCustomField(ANY_LONG, ANY_STRING, WorkCustomFieldType.OWNER);

		customFieldGroup.addToFields(field3);

		WorkCustomField workfield3 = generateWorkCustomField(ANY_LONG, ANY_STRING, true, WorkCustomFieldType.OWNER);
		fieldGroup.setWorkCustomFields(ImmutableList.<WorkCustomField>builder()
				.addAll(fieldGroup.getWorkCustomFields())
				.add(workfield3)
				.build());

		CustomFieldGroupSaveRequest request = new CustomFieldGroupSaveRequest(customFieldGroup, fieldGroup, false, true, true, false);

		assertTrue(hasErrorCode(validate(request), "NotNull"));
	}

	@Test
	public void validate_isSent_adminEmptyRequiredField_fail() {
		CustomFieldGroup customFieldGroup = generateCustomFieldGroup();
		CustomField field3 = generateCustomField(ANY_LONG, ANY_STRING, WorkCustomFieldType.OWNER);

		customFieldGroup.addToFields(field3);

		WorkCustomField workfield3 = generateWorkCustomField(ANY_LONG, ANY_STRING, true, WorkCustomFieldType.OWNER);
		fieldGroup.setWorkCustomFields(ImmutableList.<WorkCustomField>builder()
				.addAll(fieldGroup.getWorkCustomFields())
				.add(workfield3)
				.build());

		CustomFieldGroupSaveRequest request = new CustomFieldGroupSaveRequest(customFieldGroup, fieldGroup, false, true, false, true);

		assertTrue(hasErrorCode(validate(request), "NotNull"));
	}

	@Test
	public void validate_ValidSupports_success() {
		assertTrue(validator.supports(CustomFieldGroupSaveRequest.class));
	}

	@Test
	public void validate_InvalidSupports_fail() {
		assertFalse(validator.supports(String.class));
	}

	private CustomField generateCustomField(long id, String name, String type) {
		CustomField field = new CustomField();
		field.setId(id);
		field.setName(name);
		field.setType(type);
		field.setValue(EMPTY_TOKEN);
		return field;
	}

	private WorkCustomField generateWorkCustomField(long id, String name, boolean required, String type) {
		WorkCustomField field = new WorkCustomField();
		field.setId(id);
		field.setName(name);
		field.setRequiredFlag(required);
		field.setWorkCustomFieldType(new WorkCustomFieldType(type));
		return field;
	}

	private CustomFieldGroup generateCustomFieldGroup() {
		CustomFieldGroup group = new CustomFieldGroup();
		group.setName(ANY_STRING);
		CustomField field1 = generateCustomField(ANY_LONG, ANY_STRING, WorkCustomFieldType.RESOURCE);
		CustomField field2 = generateCustomField(ANY_LONG, ANY_STRING, WorkCustomFieldType.OWNER);

		group.addToFields(field1);
		group.addToFields(field2);
		return group;
	}

	protected Validator getValidator() {
		return validator;
	}
}
