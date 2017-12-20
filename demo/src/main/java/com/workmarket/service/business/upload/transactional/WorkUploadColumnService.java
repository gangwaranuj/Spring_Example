package com.workmarket.service.business.upload.transactional;

import com.workmarket.thrift.work.uploader.FieldCategory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkUploadColumnService {

	List<FieldCategory> getFieldCategories();
	List<FieldCategory> getFieldCategoriesForTemplate(long templateId);
	List<FieldCategory> getFieldCategoriesForTemplates(Set<Long> templateIds);

	String getColumnTypeCode(String key, String defaultValue);

	Map<String, Integer> getColumnOrder();

	String getColumnTypeCode(String key);

	List<String> getColumnTypeCodeKeys();
}
