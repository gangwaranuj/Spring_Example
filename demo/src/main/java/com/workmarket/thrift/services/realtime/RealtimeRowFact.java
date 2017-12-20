package com.workmarket.thrift.services.realtime;

import com.workmarket.thrift.EnumValue;

public enum RealtimeRowFact implements EnumValue {
  EXPIRES_IN_3_HOURS(0),
  ALL_RESOURCES_DECLINED(1),
  ALL_RESOURCES_OFFERED(2),
  TIME_ELAPSED(3),
  WORK_DIALER_DISABLED(4),
  WORK_DIALER_USED(5);

  private final int value;

  private RealtimeRowFact(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static RealtimeRowFact findByValue(int value) { 
    switch (value) {
      case 0:
        return EXPIRES_IN_3_HOURS;
      case 1:
        return ALL_RESOURCES_DECLINED;
      case 2:
        return ALL_RESOURCES_OFFERED;
      case 3:
        return TIME_ELAPSED;
      case 4:
        return WORK_DIALER_DISABLED;
      case 5:
        return WORK_DIALER_USED;
      default:
        return null;
    }
  }
}
