package com.workmarket.domains.model.realtime;

import com.workmarket.thrift.services.realtime.RealtimeResource;
import com.workmarket.thrift.services.realtime.RealtimeRow;

/**
 * Row decorator because we have to keep track of the rows in the realtime but
 * we're not going to return the database work id (or any other transient data)
 *
 * @author kristian
 */
public class RealtimeRowDecorator extends RealtimeRow implements IRealtimeRow {
	private static final long serialVersionUID = 8223627330537443849L;

	private Long workId;
	private Double longitude;
	private Double latitude;

	@Override
	public Long getWorkId() {
		return workId;
	}

	public IRealtimeRow setWorkId(Long workId) {
		this.workId = workId;
		return this;
	}

	@Override
	public void addToInvitedResources(IRealtimeResource resource) {
		this.addToInvitedResources((RealtimeResource) resource);
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Override
	public Double getLatitude() {
		return latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public Double getLongitude() {
		return longitude;
	}

	@Override
	public IRealtimeUser getRealtimeOwner() {
		return new RealtimeOwnerDecorator(this.getOwner());
	}

}
