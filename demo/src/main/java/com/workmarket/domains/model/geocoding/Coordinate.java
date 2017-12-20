package com.workmarket.domains.model.geocoding;

import com.vividsolutions.jts.geom.Point;
import com.workmarket.utility.GeoUtilities;

import static org.springframework.util.Assert.notNull;

public class Coordinate {
	private Double longitude;
	private Double latitude;

	public Coordinate() {
	}

	public Coordinate(Double longitude, Double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public Coordinate(Point p) {
		this.longitude = p.getX();
		this.latitude = p.getY();
	}

	public double distanceInMiles(Coordinate b) {
		notNull(b);
		notNull(getLongitude());
		notNull(getLatitude());
		notNull(b.getLongitude());
		notNull(b.getLatitude());
		return GeoUtilities.distanceInMiles(getLatitude(), getLongitude(), b.getLatitude(), b.getLongitude());
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
}
