package com.workmarket.service.infra.voice;

public interface VoiceApplicationFactory {
	
	/**
	 * Get the named call application.
	 * @param name
	 * @return
	 */
	VoiceApplication getApplication(String name);
	
	/**
	 * Builds the outgoing call application for resource invitation to an assignment.
	 * @return
	 */
	VoiceApplication buildResourceInvitationApplication();
	
	/**
	 * Builds the outgoing call application for resource confirmation of an assignment.
	 * @return
	 */
	VoiceApplication buildResourceConfirmationApplication();
	
	/**
	 * Builds the assignment IVR application allowing for resources to dial in and
	 * update statuses and trigger events for an in progress assignment.
	 * @return
	 */
	VoiceApplication buildResourceCheckinApplication();
}
