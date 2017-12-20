package com.workmarket.domains.model.realtime;

import java.util.Comparator;

import com.workmarket.thrift.work.ResourceNote;

public class ResourceNoteComparator implements Comparator<ResourceNote> {

	@Override
	public int compare(ResourceNote o1, ResourceNote o2) {
		if (o1 == null && o2 == null) {
			return 0;
		} else if (o1 != null && o2 == null) {
			return 1;
		} else if (o1 == null && o2 != null) {
			return -1;
		} else if (o1.getDateOfNote() == o2.getDateOfNote()) {
			return 0;
		} else {
			return (o1.getDateOfNote() > o2.getDateOfNote()) ? 1 : -1;
		}
	}


}
