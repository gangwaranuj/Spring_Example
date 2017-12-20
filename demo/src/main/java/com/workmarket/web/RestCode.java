package com.workmarket.web;

/**
 * Created by ianha on 6/2/14
 * This class represents
 */
public enum RestCode {
	INTERNAL_SERVER_ERROR(1, "An internal error was encountered"),
	BAD_REQUEST(2, "Bad request"),
	NOT_FOUND(3, "Requested resource we not found"),
	PROFILE_NOT_FOUND(4, "Profile not found"),
	INVALID_DISTANCE(5, "Distance must be greater than 0"),
	WRONG_IMAGE_FORMAT(6, "Wrong image format"),
	UNREADEABLE_IMAGE(7, "Could not read image"),
	ASSIGNMENT_NOT_FOUND(8,"Assignment not found");

	private final int value;
	private final String description;

	private RestCode(int value, String description) {
		this.value = value;
		this.description = description;
	}

	public int getValue() { return this.value; }
	public String getDescription() { return this.description; }
}
