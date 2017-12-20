package com.workmarket.service.business.upload.parser;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.utility.SerializationUtilities;

import java.util.List;

// TODO Micah - need WorkBundleRowParseError?

public class WorkUploaderBuildResponse {

	private final Work work;
	private WorkBundle workBundle;
	private final List<WorkRowParseError> errors;
	private final Multimap<WorkUploadLocation, Integer> newLocations = LinkedListMultimap.create();
	private final Multimap<WorkUploadLocationContact, Integer> newContacts = LinkedListMultimap.create();
	private final Multimap<WorkBundle, Work> newBundles = LinkedListMultimap.create();

	public WorkUploaderBuildResponse(Work work) {
		if (work == null) { work = new Work(); }
		// cloning ensures that values set in a template don't get clobbered
		// as this work object is altered during the upload process
		this.work = (Work) SerializationUtilities.clone(work);
		errors = Lists.newLinkedList();
	}

	public Work getWork() {
		return work;
	}

	public WorkBundle getWorkBundle() {
		return workBundle;
	}

	public void setWorkBundle(WorkBundle workBundle) {
		this.workBundle = workBundle;
	}

	public List<WorkRowParseError> getErrors() {
		return errors;
	}

	public Multimap<WorkUploadLocation, Integer> getNewLocations() {
		return newLocations;
	}

	public Multimap<WorkUploadLocationContact, Integer> getNewContacts() {
		return newContacts;
	}

	public Multimap<WorkBundle, Work> getNewBundles() {
		return newBundles;
	}

	public WorkUploaderBuildResponse addNewLocation(WorkUploadLocation location, int lineNum) {
		newLocations.put(location, lineNum);
		return this;
	}

	public WorkUploaderBuildResponse addNewContact(WorkUploadLocationContact contact, int lineNum) {
		newContacts.put(contact, lineNum);
		return this;
	}

	public WorkUploaderBuildResponse addNewBundle(WorkBundle bundle, Work work) {
		newBundles.put(bundle, work);
		return this;
	}

	public WorkUploaderBuildResponse addNewBundle(WorkBundle bundle, List<Work> workList) {
		newBundles.putAll(bundle, Lists.newArrayList(workList));
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + ((work == null) ? 0 : work.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		WorkUploaderBuildResponse other = (WorkUploaderBuildResponse) obj;
		if (errors == null) {
			if (other.errors != null) {
				return false;
			}
		} else if (!errors.equals(other.errors)) {
			return false;
		}
		if (work == null) {
			if (other.work != null) {
				return false;
			}
		} else if (!work.equals(other.work)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "WorkUploaderBuildResponse [work=" + work + ", errors=" + errors + "]";
	}

	public void addToRowParseErrors(WorkRowParseError error) {
		errors.add(error);
	}

	public void addToRowParseErrors(List<WorkRowParseError> errors) {
		for (WorkRowParseError error : errors) {
			addToRowParseErrors(error);
		}
	}
}
