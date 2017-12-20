package com.workmarket.domains.model.realtime;

import com.google.common.collect.Ordering;
import com.workmarket.thrift.services.realtime.RealtimeResource;

import java.util.Comparator;

/**
 * Per WORK-6940: Sort the resources by distance, then last name, then first name.
 *
 * @author krickert
 */
public class RealtimeResourceComparator implements Comparator<RealtimeResource> {
	public static final RealtimeResourceComparator instance = new RealtimeResourceComparator();

	@Override
	public int compare(RealtimeResource o1, RealtimeResource o2) {
		if (!o1.isSetDistance() && !o2.isSetDistance()) {
			return nameCompare(o1, o2);
		} else if (o1.isSetDistance() && !o2.isSetDistance()) {
			return -1;
		} else if (!o1.isSetDistance() && o2.isSetDistance()) {
			return 1;
		} else if (o1.getDistance() == o2.getDistance()) {
			return nameCompare(o1, o2);
		} else if (o1.getDistance() > o2.getDistance()) {
			return 1;
		} else {
			return -1;
		}
	}

	private int nameCompare(RealtimeResource o1, RealtimeResource o2) {
		int lastNameCompare = Ordering.natural().compare(o1.getLastName(), o2.getLastName());
		if (lastNameCompare == 0) {
			return Ordering.natural().compare(o1.getFirstName(), o2.getFirstName());
		} else {
			return lastNameCompare;
		}
	}

}
