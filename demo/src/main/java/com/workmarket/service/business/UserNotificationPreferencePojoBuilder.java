package com.workmarket.service.business;

public class UserNotificationPreferencePojoBuilder {
  private String notificationType;
  private Boolean emailFlag = false;
  private Boolean followFlag = false;
  private Boolean bullhornFlag = false;
  private Boolean smsFlag = false;
  private Boolean voiceFlag = false;
  private Boolean pushFlag = false;
  private Boolean dispatchEmailFlag = false;
  private Boolean dispatchBullhornFlag = false;
  private Boolean dispatchSmsFlag = false;
  private Boolean dispatchVoiceFlag = false;
  private Boolean dispatchPushFlag = false;

  public UserNotificationPreferencePojoBuilder setNotificationType(final String notificationType) {
    this.notificationType = notificationType;
    return this;
  }

  public UserNotificationPreferencePojoBuilder setEmailFlag(final Boolean emailFlag) {
    this.emailFlag = emailFlag;
    return this;
  }

  public UserNotificationPreferencePojoBuilder setFollowFlag(final Boolean followFlag) {
    this.followFlag = followFlag;
    return this;
  }

  public UserNotificationPreferencePojoBuilder setBullhornFlag(final Boolean bullhornFlag) {
    this.bullhornFlag = bullhornFlag;
    return this;
  }

  public UserNotificationPreferencePojoBuilder setSmsFlag(final Boolean smsFlag) {
    this.smsFlag = smsFlag;
    return this;
  }

  public UserNotificationPreferencePojoBuilder setVoiceFlag(final Boolean voiceFlag) {
    this.voiceFlag = voiceFlag;
    return this;
  }

  public UserNotificationPreferencePojoBuilder setPushFlag(final Boolean pushFlag) {
    this.pushFlag = pushFlag;
    return this;
  }

  public UserNotificationPreferencePojoBuilder setDispatchEmailFlag(final Boolean dispatchEmailFlag) {
    this.dispatchEmailFlag = dispatchEmailFlag;
    return this;
  }

  public UserNotificationPreferencePojoBuilder setDispatchBullhornFlag(final Boolean dispatchBullhornFlag) {
    this.dispatchBullhornFlag = dispatchBullhornFlag;
    return this;
  }

  public UserNotificationPreferencePojoBuilder setDispatchSmsFlag(final Boolean dispatchSmsFlag) {
    this.dispatchSmsFlag = dispatchSmsFlag;
    return this;
  }

  public UserNotificationPreferencePojoBuilder setDispatchVoiceFlag(final Boolean dispatchVoiceFlag) {
    this.dispatchVoiceFlag = dispatchVoiceFlag;
    return this;
  }

  public UserNotificationPreferencePojoBuilder setDispatchPushFlag(final Boolean dispatchPushFlag) {
    this.dispatchPushFlag = dispatchPushFlag;
    return this;
  }

  public UserNotificationPreferencePojo build() {
    return new UserNotificationPreferencePojo(notificationType, emailFlag, followFlag, bullhornFlag, smsFlag, voiceFlag, pushFlag, dispatchEmailFlag, dispatchBullhornFlag, dispatchSmsFlag, dispatchVoiceFlag, dispatchPushFlag);
  }
}