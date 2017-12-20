package com.workmarket.service.business.wrapper;

public class PushResponseAndDeviceType {
    private final PushResponse pushResponse;
    private final String deviceType;

    public PushResponseAndDeviceType(final PushResponse pushResponse, final String deviceType) {
        this.pushResponse = pushResponse;
        this.deviceType = deviceType;
    }

    public PushResponse getPushResponse() {
        return pushResponse;
    }

    public String getDeviceType() {
        return deviceType;
    }
}
