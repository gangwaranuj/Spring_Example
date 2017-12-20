package com.workmarket.service.business;

/**
 * Created by drew on 26/06/17.
 */
public class UserNotificationPreferencePojo {
  private final String notificationType;
  private final Boolean emailFlag;
  private final Boolean followFlag;
  private final Boolean bullhornFlag;
  private final Boolean smsFlag;
  private final Boolean voiceFlag;
  private final Boolean pushFlag;
  private final Boolean dispatchEmailFlag;
  private final Boolean dispatchBullhornFlag;
  private final Boolean dispatchSmsFlag;
  private final Boolean dispatchVoiceFlag;
  private final Boolean dispatchPushFlag;

  public UserNotificationPreferencePojo(
      final String notificationType,
      final Boolean emailFlag,
      final Boolean followFlag,
      final Boolean bullhornFlag,
      final Boolean smsFlag,
      final Boolean voiceFlag,
      final Boolean pushFlag,
      final Boolean dispatchEmailFlag,
      final Boolean dispatchBullhornFlag,
      final Boolean dispatchSmsFlag,
      final Boolean dispatchVoiceFlag,
      final Boolean dispatchPushFlag) {
    this.notificationType = notificationType;
    this.emailFlag = emailFlag;
    this.followFlag = followFlag;
    this.bullhornFlag = bullhornFlag;
    this.smsFlag = smsFlag;
    this.voiceFlag = voiceFlag;
    this.pushFlag = pushFlag;
    this.dispatchEmailFlag = dispatchEmailFlag;
    this.dispatchBullhornFlag = dispatchBullhornFlag;
    this.dispatchSmsFlag = dispatchSmsFlag;
    this.dispatchVoiceFlag = dispatchVoiceFlag;
    this.dispatchPushFlag = dispatchPushFlag;
  }

  public String getNotificationType() {
    return notificationType;
  }

  public Boolean getEmailFlag() {
    return emailFlag;
  }

  public Boolean getFollowFlag() {
    return followFlag;
  }

  public Boolean getBullhornFlag() {
    return bullhornFlag;
  }

  public Boolean getSmsFlag() {
    return smsFlag;
  }

  public Boolean getVoiceFlag() {
    return voiceFlag;
  }

  public Boolean getPushFlag() {
    return pushFlag;
  }

  public Boolean getDispatchEmailFlag() {
    return dispatchEmailFlag;
  }

  public Boolean getDispatchBullhornFlag() {
    return dispatchBullhornFlag;
  }

  public Boolean getDispatchSmsFlag() {
    return dispatchSmsFlag;
  }

  public Boolean getDispatchVoiceFlag() {
    return dispatchVoiceFlag;
  }

  public Boolean getDispatchPushFlag() {
    return dispatchPushFlag;
  }

  public boolean isCacheable() { return getBullhornFlag(); }

  @Override
	public String toString() {
		return "UserNotificationPreference{" +
			", notificationType=" + notificationType +
			", emailFlag=" + emailFlag +
			", followFlag=" + followFlag +
			", bullhornFlag=" + bullhornFlag +
			", smsFlag=" + smsFlag +
			", voiceFlag=" + voiceFlag +
			", pushFlag=" + pushFlag +
			", dispatchEmailFlag=" + dispatchEmailFlag +
			", dispatchBullhornFlag=" + dispatchBullhornFlag +
			", dispatchSmsFlag=" + dispatchSmsFlag +
			", dispatchVoiceFlag=" + dispatchVoiceFlag +
			", dispatchPushFlag=" + dispatchPushFlag +
			'}';
	}
}
