package com.workmarket.helpers;

import java.math.BigDecimal;

/**
 * User: micah
 * Date: 10/3/13
 * Time: 7:49 AM
 */
public class GeoMathHelper {
	public static final int X = 0;
	public static final int Y = 1;
	public static final int Z = 2;

	public static final int LAT = 0;
	public static final int LON = 1;

	public static double toRadians(BigDecimal degrees) {
		return Math.toRadians(degrees.doubleValue());
	}

	public static double[] toCartesian(BigDecimal lat, BigDecimal lon) {
		double[] ret = new double[3];
		double _lat = toRadians(lat);
		double _lon = toRadians(lon);

		ret[X] = Math.cos(_lat)*Math.cos(_lon);
		ret[Y] = Math.cos(_lat)*Math.sin(_lon);
		ret[Z] = Math.sin(_lat);

		return ret;
	}

	public static double[] toGeo(double[] cartesianLocation) {
		double[] ret = new double[2];

		ret[LON] = Math.toDegrees(Math.atan2(cartesianLocation[Y], cartesianLocation[X]));
		double hyp = Math.sqrt(cartesianLocation[X]*cartesianLocation[X] + cartesianLocation[Y]*cartesianLocation[Y]);
		ret[LAT] = Math.toDegrees(Math.atan2(cartesianLocation[Z], hyp));

		return ret;
	}

	public static double[] findCartesianMidpoint(double[][] locations) {
		double[] ret = new double[3];
		for (double[] location : locations) {
			ret[X] += location[X];
			ret[Y] += location[Y];
			ret[Z] += location[Z];
		}
		ret[X] = ret[X]/locations.length;
		ret[Y] = ret[Y]/locations.length;
		ret[Z] = ret[Z]/locations.length;
		return ret;
	}

	public static double[] findGeoMidpoint(BigDecimal[][] locations) {
		double[][] cartesianLocations = new double[locations.length][3];
		for (int i = 0; i < locations.length; i++) {
			cartesianLocations[i] = toCartesian(locations[i][LAT], locations[i][LON]);
		}
		double[] cartesianMidpoint = findCartesianMidpoint(cartesianLocations);
		return toGeo(cartesianMidpoint);
	}
}