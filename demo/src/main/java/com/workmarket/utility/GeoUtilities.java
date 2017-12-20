package com.workmarket.utility;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.workmarket.configuration.Constants;
import org.h2.util.StringUtils;

import static org.springframework.util.Assert.isTrue;

public class GeoUtilities {

	private GeoUtilities() {
	}

	/**
	 * Uses apache's well supported and heavily tests lat/lon distance tool
	 * instead of the homegrown stuff
	 *
	 * @param lat1
	 *            latitude1 value
	 * @param lon1
	 *            longitude1 value
	 * @param lat2
	 *            latitude2 value
	 * @param lon2
	 *            longitude2 value
	 * @return distance in miles of the lat/lon type
	 * @throws Exception
	 *             if lat/lon are both 0.
	 */
	public static double distanceInMiles(double lat1, double lon1, double lat2, double lon2) {
		isTrue(lat1 != 0.0 && lon1 != 0.0);
		isTrue(lat2 != 0.0 && lon2 != 0.0);

		LatLng point1 = new LatLng(lat1, lon1);
		LatLng point2 = new LatLng(lat2, lon2);
		return LatLngTool.distance(point1, point2, LengthUnit.MILE);
	}

	/* Gets JUST the URL to view an address in Google Maps site.  Use when you want to provide user with link to view in Google Maps. */
	public static String getMapUrlFromAddress(String addressQuery) {
		String urlEncodedQuery = StringUtils.urlEncode(addressQuery);
		return "//maps.google.com/maps?hl=en&q=" + urlEncodedQuery;
	}

	/* Gets JUST the URL of a static Google Maps map */
	public static String getStaticMapImageUrlFromAddress(Integer width, Integer height, String addressQuery, Boolean showMarker, Integer zoomLevel, Integer scale) {
		String urlEncodedQuery = StringUtils.urlEncode(addressQuery);
		String markerParams = "";

		if(showMarker) {
			markerParams = "&markers=size:mid%7Ccolor:orang%%7Clabel:WM%7C" + urlEncodedQuery;
		}

		String htmlResponse = String.format("//maps.googleapis.com/maps/api/staticmap?"
				+ "center=%s"
				+ "&zoom=%d"
				+ "&size=%dx%d"
				+ "&maptype=roadmap&scale=%d&sensor=false%s"
				+ "&key=%s"
				, urlEncodedQuery, zoomLevel, width, height, scale, markerParams, Constants.GOOGLE_API_KEY_RESTRICTED_REFERRERS
		);

		return htmlResponse;
	}

	/* Gets iframe embedded Google Map which has zoom/pan controls, map types, etc */
	public static String getEmbeddedMapFromAddress(Integer width, Integer height, Boolean scrolling, String addressQuery, Integer zoomLevel, Boolean makeLink) {
		String urlEncodedQuery = StringUtils.urlEncode(addressQuery);

		String htmlResponse = String.format("<iframe width=\"%d\" "
				+ "height=\"%d\" "
				+ "frameborder=\"0\" "
				+ "scrolling=\"%s\" "
				+ "marginheight=\"0\" marginwidth=\"0\" "
				+ "src=\"//maps.google.com/maps?f=q&amp;source=s_q&amp;hl=en&amp;q=%s&amp;geocode=&amp;aq=&amp;ie=UTF8&amp;hq=&amp;t=m&amp;z=%d&amp;key=%s&amp;output=embed\"></iframe>"
				, width, height, scrolling, urlEncodedQuery, zoomLevel, Constants.GOOGLE_API_KEY_RESTRICTED_REFERRERS
		);

		// ToDo: this doesn't seem to work wrapped around <iframe>.. why not?
		if(makeLink) {
			htmlResponse = String.format("<a href=\"//maps.google.com/maps?hl=en&q=%s\">%s</a>", urlEncodedQuery, htmlResponse);
		}

		return htmlResponse;
	}

}
