package com.workmarket.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * User: micah
 * Date: 10/2/13
 * Time: 3:02 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class GeoMathHelperTest {
	static final int NEW_YORK = 0;
	static final int CHICAGO  = 1;
	static final int ATLANTA  = 2;

	static final BigDecimal[][] FIXTURE_LAT_LONG_PAIRS = {
		{ new BigDecimal(40.7143528), new BigDecimal(-74.0059731) }, // New York
		{ new BigDecimal(41.8781136), new BigDecimal(-87.6297982) }, // Chicago
		{ new BigDecimal(33.7489954), new BigDecimal(-84.3879824) }  // Atlanta
	};

	static final double[] FIXTURE_CARTESIAN_MIDPOINT = { .106984397, -.766684605, .625130707 };

	@Test
	public void toRadians_WithBigDecimal() {
		double radians = GeoMathHelper.toRadians(FIXTURE_LAT_LONG_PAIRS[NEW_YORK][GeoMathHelper.LAT]);
		assertEquals(0.710599509, radians, 0.0001);
	}

	@Test
	public void toCartesian_WithBigDecimal() {
		double[] nyCartesian = GeoMathHelper.toCartesian(FIXTURE_LAT_LONG_PAIRS[NEW_YORK][GeoMathHelper.LAT], FIXTURE_LAT_LONG_PAIRS[NEW_YORK][GeoMathHelper.LON]);
		assertEquals(0.20884915, nyCartesian[GeoMathHelper.X], 0.0001);
		assertEquals(-0.728630226, nyCartesian[GeoMathHelper.Y], 0.0001);
		assertEquals(0.65228829, nyCartesian[GeoMathHelper.Z], 0.0001);
	}

	@Test
	public void findCartesianMidPoint() {
		double[] nyCartesian = GeoMathHelper.toCartesian(FIXTURE_LAT_LONG_PAIRS[NEW_YORK][GeoMathHelper.LAT], FIXTURE_LAT_LONG_PAIRS[NEW_YORK][GeoMathHelper.LON]);
		double[] chicagoCartesian = GeoMathHelper.toCartesian(FIXTURE_LAT_LONG_PAIRS[CHICAGO][GeoMathHelper.LAT], FIXTURE_LAT_LONG_PAIRS[CHICAGO][GeoMathHelper.LON]);
		double[] atlantaCartesian = GeoMathHelper.toCartesian(FIXTURE_LAT_LONG_PAIRS[ATLANTA][GeoMathHelper.LAT], FIXTURE_LAT_LONG_PAIRS[ATLANTA][GeoMathHelper.LON]);
		double[][] locations = { nyCartesian, chicagoCartesian, atlantaCartesian };

		double[] midpoint = GeoMathHelper.findCartesianMidpoint(locations);
		assertEquals(FIXTURE_CARTESIAN_MIDPOINT[GeoMathHelper.X], midpoint[GeoMathHelper.X], 0.0001);
		assertEquals(FIXTURE_CARTESIAN_MIDPOINT[GeoMathHelper.Y], midpoint[GeoMathHelper.Y], 0.0001);
		assertEquals(FIXTURE_CARTESIAN_MIDPOINT[GeoMathHelper.Z], midpoint[GeoMathHelper.Z], 0.0001);
	}

	@Test
	public void toGeo_WithCartesian() {
		double[] midpointGeo = GeoMathHelper.toGeo(FIXTURE_CARTESIAN_MIDPOINT);
		assertEquals(38.922418321995075, midpointGeo[GeoMathHelper.LAT], 0.0001);
		assertEquals(-82.05615068621998, midpointGeo[GeoMathHelper.LON], 0.0001);
	}

	@Test
	public void findGeoMidPoint() {
		double[] midpointGeo = GeoMathHelper.findGeoMidpoint(FIXTURE_LAT_LONG_PAIRS);
		assertEquals(38.922418321995075, midpointGeo[GeoMathHelper.LAT], 0.0001);
		assertEquals(-82.05615068621998, midpointGeo[GeoMathHelper.LON], 0.0001);
	}
}
