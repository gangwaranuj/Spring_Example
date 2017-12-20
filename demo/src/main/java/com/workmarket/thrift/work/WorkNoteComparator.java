package com.workmarket.thrift.work;

import java.util.Comparator;

import com.workmarket.thrift.core.Note;

public class WorkNoteComparator implements Comparator<Note>{

	@Override
	public int compare(Note arg0, Note arg1) {
		if (arg0.getCreatedOn() > arg1.getCreatedOn()) {
			return 1;
		} else if (arg0.getCreatedOn() < arg1.getCreatedOn()) {
			return -1;
		} else {
			return 0;
		}
	}

}
