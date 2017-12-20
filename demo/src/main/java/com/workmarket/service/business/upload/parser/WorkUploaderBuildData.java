package com.workmarket.service.business.upload.parser;

import com.google.common.collect.BiMap;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.thrift.work.Work;

import java.util.Map;
import java.util.Set;

public class WorkUploaderBuildData {
	private int lineNumber;
	private Work work;
	private Map<String, String> types;
	private BiMap<String, Long> templateLookup;
	private Set<WorkBundle> bundles;

	public int getLineNumber() {
		return lineNumber;
	}

	public WorkUploaderBuildData setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
		return this;
	}

	public Work getWork() {
		return work;
	}

	public WorkUploaderBuildData setWork(Work work) {
		this.work = work;
		return this;
	}

	public Map<String, String> getTypes() {
		return types;
	}

	public WorkUploaderBuildData setTypes(Map<String, String> types) {
		this.types = types;
		return this;
	}

	public BiMap<String, Long> getTemplateLookup() {
		return templateLookup;
	}

	public WorkUploaderBuildData setTemplateLookup(BiMap<String, Long> templateLookup) {
		this.templateLookup = templateLookup;
		return this;
	}

	public Set<WorkBundle> getBundles() {
		return bundles;
	}

	public WorkUploaderBuildData setBundles(Set<WorkBundle> bundles) {
		this.bundles = bundles;
		return this;
	}
}
