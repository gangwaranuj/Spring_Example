package com.workmarket.domains.model.realtime;

public interface IRealtimeResource {

	void setLatitude(Double latitude);

	void setLongitude(Double longitude);

	Long getWorkId();

	Long getWorkResourceId();//not needed on the front end, but needed for 

	void setWorkResourceId(Long workResourceId);

	Double getLatitude();

	Double getLongitude();

	void setRelativeDistance(Double rlatitude, Double rlongitude);


}
