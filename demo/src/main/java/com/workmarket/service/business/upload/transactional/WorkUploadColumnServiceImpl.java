package com.workmarket.service.business.upload.transactional;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.dao.customfield.WorkCustomFieldGroupDAO;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.work.dao.WorkUploadColumnCategoryDAO;
import com.workmarket.domains.work.model.WorkUploadColumnCategory;
import com.workmarket.domains.work.model.WorkUploadColumnType;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.work.uploader.FieldCategory;
import com.workmarket.thrift.work.uploader.FieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class WorkUploadColumnServiceImpl implements WorkUploadColumnService {

	private static final String CUSTOM_FIELD_GROUP_LABEL_FORMAT = "Custom - %s";

	@Autowired private WorkUploadColumnCategoryDAO categoryDAO;
	@Autowired private LookupEntityDAO lookupEntityDAO;
	@Autowired private WorkCustomFieldGroupDAO customFieldGroupDAO;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkCustomFieldDAO workCustomFieldDAO;
	@Autowired private CompanyService companyService;

	private Map<String, String> columnTypeCodes = Maps.newHashMapWithExpectedSize(82);
	private final Map<String,Integer> columnOrder = Maps.newHashMapWithExpectedSize(82);

	@Override
	public List<FieldCategory> getFieldCategories() {
		List<FieldCategory> fieldCategories = Lists.newLinkedList();

		List<WorkUploadColumnCategory> fieldColumnCategories = categoryDAO.findAllColumnCategories();
		fieldCategories.addAll(buildFieldCategories(fieldColumnCategories));

		/* if company has marked a specific custom column field set as "Required" (in Settings),
		only load that one field set for mapping. */
		User currentUser = authenticationService.getCurrentUser();
		WorkCustomFieldGroup customFieldGroup = customFieldGroupDAO.findRequiredWorkCustomFieldGroup(currentUser.getCompany().getId());
		if (customFieldGroup != null) {
			Optional<FieldCategory> c = buildCustomFieldCategory(customFieldGroup);
			if (c.isPresent()) {
				fieldCategories.add(c.get());
			}
		}

		return fieldCategories;
	}

	@Override
	public List<FieldCategory>getFieldCategoriesForTemplate(long templateId) {
		return getFieldCategoriesForTemplates(Sets.newHashSet(templateId));
	}

	@Override
	public List<FieldCategory> getFieldCategoriesForTemplates(Set<Long> templateIds) {
		List<FieldCategory> categories = Lists.newLinkedList();
		categories.addAll(getFieldCategories());

		List<WorkCustomFieldGroup> groups = customFieldGroupDAO.findByWork(templateIds.toArray(new Long[templateIds.size()]));
		for (WorkCustomFieldGroup g : groups) {
			Optional<FieldCategory> toAdd = buildCustomFieldCategory(g);
			if (!toAdd.isPresent()) continue;
			categories.add(toAdd.get());
		}

		return categories;
	}

	private Optional<FieldCategory> buildCustomFieldCategory(WorkCustomFieldGroup group) {
		List<WorkCustomField> workCustomFields = workCustomFieldDAO.findAllFieldsForCustomFieldGroup(group.getId());
		if ((group == null) || workCustomFields.isEmpty()) {
			return Optional.absent();
		}

		WorkUploadColumnType type = lookupEntityDAO.findByCode(WorkUploadColumnType.class, WorkUploadColumnType.CUSTOM_FIELD_TYPE);

		FieldCategory toAdd = new FieldCategory()
				.setCode(type.getCategory().getCode())
				.setDescription(String.format(CUSTOM_FIELD_GROUP_LABEL_FORMAT, group.getName()))
				.setOrder(type.getCategory().getOrder());

		for (WorkCustomField f : workCustomFields) {
			FieldType aType = new FieldType()
					.setCode(type.getDerivedCode(f.getId()))
					.setDescription(f.getName())
					.setOrder(type.getOrder());
			toAdd.addToFieldTypes(aType);
		}

		return Optional.of(toAdd);
	}

	private List<FieldCategory> buildFieldCategories(List<WorkUploadColumnCategory> fieldCategories) {
		List<FieldCategory> categories = Lists.newLinkedList();

		User currentUser = authenticationService.getCurrentUser();
		CompanyPreference companyPreference = companyService.getCompanyPreference(currentUser.getCompany().getId());

		for (WorkUploadColumnCategory category : fieldCategories) {
			List<FieldType> fieldTypes = Lists.newLinkedList();

			for (WorkUploadColumnType item : category.getFieldTypes()) {
				if (!item.isVisible()) continue;

				if (WorkUploadColumn.UNIQUE_EXTERNAL_ID.getUploadColumnName().equals(item.getCode())) {
					if (!companyPreference.isExternalIdActive()) {
						continue;
					}
					item.setDescription(companyPreference.getExternalIdDisplayName());
				}

				FieldType type = new FieldType()
					.setCode(item.getCode())
					.setDescription(item.getDescription())
					.setOrder(item.getOrder());
				fieldTypes.add(type);
			}

			if (fieldTypes.isEmpty()) continue;

			FieldCategory toAdd = new FieldCategory()
				.setCode(category.getCode())
				.setDescription(category.getDescription())
				.setOrder(category.getOrder())
				.setFieldTypes(fieldTypes);
			categories.add(toAdd);
		}

		return categories;
	}

	private void initColumnTypeCodes() {
		List<WorkUploadColumnCategory> fieldCategories = categoryDAO.findAllColumnCategories();
		for (WorkUploadColumnCategory c : fieldCategories) {
			for (WorkUploadColumnType t : c.getFieldTypes()) {
				this.columnTypeCodes.put(t.getDescription(), t.getCode());
			}
		}
	}

	@Override
	public String getColumnTypeCode(String key) {
		return getColumnTypeCode(key, "ignore");
	}

	@Override
	public String getColumnTypeCode(String key, String defaultValue) {
		if (this.columnTypeCodes.isEmpty()) {
			initColumnTypeCodes();
		}
		String val = this.columnTypeCodes.get(key);
		return (val != null) ? val : defaultValue;
	}

	private void initColumnOrder() {
		List<WorkUploadColumnCategory> fieldCategories = categoryDAO.findAllColumnCategories();
		for (WorkUploadColumnCategory c : fieldCategories) {
			for (WorkUploadColumnType t : c.getFieldTypes()) {
				this.columnOrder.put(t.getCode(), c.getOrder() * 100 + t.getOrder());
			}
		}
	}

	@Override
	public Map<String, Integer> getColumnOrder() {
		if (this.columnOrder.isEmpty())
			initColumnOrder();
		return this.columnOrder;
	}

	@Override
	public List<String> getColumnTypeCodeKeys() {
		if (this.columnTypeCodes.isEmpty()) {
			initColumnTypeCodes();
		}
		return new ArrayList(this.columnTypeCodes.keySet());
	}
}