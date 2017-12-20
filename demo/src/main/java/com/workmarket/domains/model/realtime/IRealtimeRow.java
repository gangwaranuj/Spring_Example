package com.workmarket.domains.model.realtime;

public interface IRealtimeRow {

	Long getWorkId();

	Double getLatitude();

	Double getLongitude();

	void addToInvitedResources(IRealtimeResource resource);

	IRealtimeUser getRealtimeOwner();

}
