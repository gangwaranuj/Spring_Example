package com.workmarket.domains.model.requirementset;

import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.requirementset.backgroundcheck.BackgroundCheckRequirement;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirement;
import com.workmarket.domains.model.requirementset.profilevideo.ProfileVideoRequirement;

public class RequirementType {
	private static final ImmutableMap DEFAULT_NAMES = ImmutableMap.of(
		"BackgroundCheckRequirement", BackgroundCheckRequirement.DEFAULT_NAME,
		"DrugTestRequirement", DrugTestRequirement.DEFAULT_NAME,
		"ProfileVideoRequirement", ProfileVideoRequirement.DEFAULT_NAME
	);

	private final String name;
	private final String humanName;
	private final String defaultRequirableName;
	private final boolean allowMultiple;
	private final String[] filters;

	public RequirementType(String name, String humanName, boolean allowMultiple, String[] filters) {
		this.name = name;
		this.humanName = humanName;
		this.defaultRequirableName = String.valueOf(DEFAULT_NAMES.get(name));
		this.allowMultiple = allowMultiple;
		this.filters = filters;
	}

	public String getName() {
		return name;
	}

	public String getHumanName() {
		return humanName;
	}

	public String getDefaultRequirableName() {
		return defaultRequirableName;
	}

	public boolean getAllowMultiple() {
		return allowMultiple;
	}

	public String[] getFilters() {
		return filters;
	}
}
