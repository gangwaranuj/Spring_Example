package com.workmarket.thrift.work;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomFieldGroupSet {
	private List<CustomFieldGroup> customFieldGroupSet;

	public CustomFieldGroupSet() {
		this.customFieldGroupSet = new ArrayList<CustomFieldGroup>();
	}

	public List<CustomFieldGroup> getCustomFieldGroupSet() {
		return customFieldGroupSet;
	}

	public void setCustomFieldGroupSet(List<CustomFieldGroup> customFieldGroupSet) {
		this.customFieldGroupSet = customFieldGroupSet;
	}

	public void add(CustomFieldGroup customFieldGroup) {
		this.customFieldGroupSet.add(customFieldGroup);
	}

	public boolean hasRequiredResourceFields() {
		return CollectionUtils.isNotEmpty(customFieldGroupSet) && Iterables.any(customFieldGroupSet, new Predicate<CustomFieldGroup>() {
			@Override public boolean apply(CustomFieldGroup group) {
				return group.hasRequiredResourceFields();
			}
		});
	}

	public boolean hasBuyerFields() {
		return CollectionUtils.isNotEmpty(customFieldGroupSet) && Iterables.any(customFieldGroupSet, new Predicate<CustomFieldGroup>() {
			@Override public boolean apply(CustomFieldGroup group) {
				return group.hasBuyerFields();
			}
		});
	}

	public boolean hasResourceFields() {
		return CollectionUtils.isNotEmpty(customFieldGroupSet) && Iterables.any(customFieldGroupSet, new Predicate<CustomFieldGroup>() {
			@Override public boolean apply(CustomFieldGroup group) {
				return group.hasResourceFields();
			}
		});
	}

	public boolean hasBuyerFieldsVisibleToResourceOnSentStatus() {
		return CollectionUtils.isNotEmpty(customFieldGroupSet) && Iterables.any(customFieldGroupSet, new Predicate<CustomFieldGroup>() {
			@Override public boolean apply(CustomFieldGroup group) {
				return group.hasBuyerFieldsVisibleToResourceOnSentStatus();
			}
		});
	}
}
