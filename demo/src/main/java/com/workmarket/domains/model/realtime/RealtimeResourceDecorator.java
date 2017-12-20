package com.workmarket.domains.model.realtime;

import static com.workmarket.utility.GeoUtilities.distanceInMiles;
import static org.apache.commons.math.util.MathUtils.round;

import com.workmarket.thrift.services.realtime.RealtimeResource;
import com.workmarket.thrift.services.realtime.ResourceIconType;

public class RealtimeResourceDecorator extends RealtimeResource implements IRealtimeResource {
	private static final long serialVersionUID = 5536128880169739273L;

	private Double latitude;
	private Double longitude;
	private Long workId;
	private Long workResourceId;

	@Override
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Override
	public void setRelativeDistance(Double rlatitude, Double rlongitude) {

		if (rlatitude == null || rlongitude == null) {
			return;
		}
		// we're too nice - exception should be thrown for 0/0 lat/lon
		// but we don't want to hurt the database row's feelings
		if (rlatitude == 0.0 && rlongitude == 0.0) {
			return;
		}
		if (latitude == null && longitude == null) {
			return;
		}
		if (latitude == 0.0 && longitude == 0.0) {
			return;
		}

		Double distance = distanceInMiles(rlatitude, rlongitude, latitude, longitude);
		if (distance != null) {
			setDistance(round(distance, 2));
			if (distance > 50.0) {
				this.addToIcons(ResourceIconType.LONG_DISTANCE_RESOURCE);
			}
		}
	}

	@Override
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Override
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public Double getLatitude() {
		return latitude;
	}

	@Override
	public Double getLongitude() {
		return longitude;
	}

	@Override
	public Long getWorkResourceId() {
		return workResourceId;
	}

	public void setWorkResourceId(Long workResourceId) {
		this.workResourceId = workResourceId;
	}

}
