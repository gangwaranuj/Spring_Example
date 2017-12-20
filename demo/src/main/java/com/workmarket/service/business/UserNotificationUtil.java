package com.workmarket.service.business;

import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.notification.UserNotificationPagination;
import com.workmarket.notification.Direction;
import com.workmarket.notification.OrderField;
import com.workmarket.notification.user.vo.UserNotification;

import java.util.ArrayList;
import java.util.List;

public class UserNotificationUtil {
	public static Direction convertSortDirection(final Pagination.SORT_DIRECTION sortDirection) {
		switch (sortDirection) {
			case ASC:
				return Direction.ASC;
			case DESC:
				return Direction.DESC;
			default:
				return Direction.DESC;
		}
	}

	public static OrderField convertSortOrder(final String sortColumn) {
		if (sortColumn == null) {
			return OrderField.CREATED_ON;
		}

		// unfortunately enums get converted to strings in the monolith
		// so we only deal with strings in this method
		if (UserNotificationPagination.SORTS.CREATED_ON.toString().equals(sortColumn)) {
			return OrderField.CREATED_ON;
		} else if (UserNotificationPagination.SORTS.DESCRIPTION.toString().equals(sortColumn)) {
			return OrderField.DISPLAY_MESSAGE;
		}

		return OrderField.CREATED_ON;
	}

	private static com.workmarket.domains.model.notification.UserNotification convertToMonolith(final UserNotification un) {
		final com.workmarket.domains.model.notification.UserNotification result =
			new com.workmarket.domains.model.notification.UserNotification();

		result.setUuid(un.getUuid());
		result.setDisplayMessage(un.getDisplayMessage());
		result.setSticky(un.isSticky());
		result.setNotificationType(new NotificationType(un.getType()));
		result.setCreatedOn(un.getCreatedOn().toGregorianCalendar());

		return result;
	}

	public static List<com.workmarket.domains.model.notification.UserNotification> convertToMonolitth(final List<UserNotification> from) {
		final List<com.workmarket.domains.model.notification.UserNotification> results = new ArrayList<>();

		for (final UserNotification aFrom : from) {
			results.add(convertToMonolith(aFrom));
		}

		return results;
	}
}
