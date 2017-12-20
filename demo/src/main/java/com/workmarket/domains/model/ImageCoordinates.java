package com.workmarket.domains.model;

import javax.validation.constraints.Min;

/**
 * Created by ianha on 7/14/14
 */
public class ImageCoordinates {
	// crop top left point of crop rectangle, (0,0) is top left corner
	@Min(0) private Integer x;
	@Min(0) private Integer y;

	// crop bottom right point of crop rectangle, (0,0) is the top left corner
	@Min(1) private Integer x2;
	@Min(1) private Integer y2;

	public ImageCoordinates() {
	}

	public ImageCoordinates(Integer x, Integer y, Integer x2, Integer y2) {
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getX2() {
		return x2;
	}

	public void setX2(Integer x2) {
		this.x2 = x2;
	}

	public Integer getY2() {
		return y2;
	}

	public void setY2(Integer y2) {
		this.y2 = y2;
	}
}
