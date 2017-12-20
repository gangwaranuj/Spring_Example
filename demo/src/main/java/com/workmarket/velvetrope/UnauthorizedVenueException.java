package com.workmarket.velvetrope;

public class UnauthorizedVenueException extends RuntimeException {
	public static final String DEFAULT_MESSAGE = "Unauthorized Venue Access Detected: %s";
	private final VelvetRope velvetRope;

	public UnauthorizedVenueException(VelvetRope velvetRope) {
		super(getMessage(velvetRope));
		this.velvetRope = velvetRope;
	}

	private static String getMessage(VelvetRope velvetRope) {
		if (velvetRope.message() == null) {
			return String.format(DEFAULT_MESSAGE, velvetRope.venue());
		} else {
			return velvetRope.message();
		}
	}

	public String getRedirectPath() {
		return velvetRope.redirectPath();
	}
}
