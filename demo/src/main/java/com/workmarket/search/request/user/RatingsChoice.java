package com.workmarket.search.request.user;

public enum RatingsChoice {
	SHOW_ALL(0),
	SHOW_ONE_STAR(1),
	SHOW_TWO_STARS(2),
	SHOW_THREE_STARS(3),
	SHOW_FOUR_STARS(4),
	SHOW_FIVE_STARS(5),
	SHOW_UNRATED(6);

	private final int value;

	private RatingsChoice(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static RatingsChoice findByValue(int value) {
		switch (value) {
			case 0:
				return SHOW_ALL;
			case 1:
				return SHOW_ONE_STAR;
			case 2:
				return SHOW_TWO_STARS;
			case 3:
				return SHOW_THREE_STARS;
			case 4:
				return SHOW_FOUR_STARS;
			case 5:
				return SHOW_FIVE_STARS;
			case 6:
				return SHOW_UNRATED;
			default:
				return null;
		}
	}
}
