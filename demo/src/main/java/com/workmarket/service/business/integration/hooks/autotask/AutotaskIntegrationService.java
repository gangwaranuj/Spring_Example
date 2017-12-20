package com.workmarket.service.business.integration.hooks.autotask;

import com.autotask.ws.*;
import com.google.common.base.Optional;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.integration.autotask.AutotaskTicket;
import com.workmarket.domains.model.integration.autotask.AutotaskUser;
import com.workmarket.domains.model.integration.autotask.AutotaskUserCustomFieldsPreference;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.business.dto.integration.AutotaskUserCustomFieldsPreferenceDTO;
import com.workmarket.service.business.dto.integration.AutotaskUserDTO;
import com.workmarket.service.exception.integration.AutotaskAuthenticationException;
import com.workmarket.service.exception.integration.AutotaskCustomFieldsException;
import com.workmarket.web.forms.mmw.AutotaskUserForm;

import java.util.List;
import java.util.Map;

/**
 * Created by nick on 2012-12-21 10:45 AM
 */
public interface AutotaskIntegrationService {

	public Optional<AutotaskUser> findAutotaskUserByUserId(Long userId);

	public void saveOrUpdateAutotaskUser(AutotaskUserDTO autotaskUserDto, boolean hasNotesEnabled, boolean isNotesInternal, boolean isAttachmentsInternal,
	                                        Map<String, AutotaskUserCustomFieldsPreferenceDTO> preferenceMap) throws AutotaskAuthenticationException, AutotaskCustomFieldsException;
	public void updateAutotaskTicket(AutotaskTicket autotaskTicket, Long workId);

	/**
	 * Accepts an AT userName and optionally returns the valid zone URL for that user
	 * @param
	 * @return
	 */
	public Optional<String> findZoneUrl(String userName);

	public Optional<String> getThresholdAndUsageInfo(String userName, String password, String zoneUrl);
	public Optional<String> getThresholdAndUsageInfo(Long autotaskId);
	public Map<String, String> getAutotaskProps();
	public List<Entity> processATWSResponse(ATWSResponse response);

	public void createExternalWorkNumberForWork(Long workId, Long externalWorkNumber);
	public boolean validateCredentials(String userName, String password, String zoneUrl);
	public boolean updateTicketOnWorkCreated(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, AbstractWork work, Map<String, AutotaskUserCustomFieldsPreference> preferenceMap);
	public boolean updateTicketOnWorkAccepted(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, AbstractWork work, WorkResource resource, Map<String, AutotaskUserCustomFieldsPreference> preferenceMap);
	public boolean updateTicketToCheckInOut(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, WorkResource resource, Map<String, AutotaskUserCustomFieldsPreference> preferenceMap);
	public boolean updateTicketOnWorkComplete(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, AbstractWork work, PaymentSummaryDTO payment, Map<String, AutotaskUserCustomFieldsPreference> preferenceMap);
	public boolean updateTicketOnWorkApproved(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, AbstractWork work, PaymentSummaryDTO payment, Map<String, AutotaskUserCustomFieldsPreference> preferenceMap);
	public boolean updateTicketOnNoteAdded(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, Note note);
	public boolean updateTicketOnAttachmentData(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, Asset asset);

	public boolean findAutotaskUserPreference(Long autotaskUserId, String notification_code);
	public void saveOrUpdateAutotaskUserPreference(AutotaskUser autotaskUser, NotificationType notificationType, boolean flag);

	public boolean isCreatedByAutotask(Long workId);
	public Optional<AutotaskTicket> findAutotaskTicketByWorkId(AutotaskUser autotaskUser, Long workId);
	public boolean updateTicketUserDefinedFields(AutotaskUser autotaskUser, Long ticketId, ArrayOfUserDefinedField fieldsArray);

	public boolean updateTicketUserDefinedFields(AutotaskUser autotaskUser, Ticket ticket, ArrayOfUserDefinedField fieldsArray);

	public List<AutotaskUserCustomFieldsPreference> findAutotaskCustomFieldPreferencesByAutotaskUser(Long companyId);

	public AutotaskUserForm populateAutotaskUserForm(Long companyId, Long userId);

	public AutotaskUser findAutotaskUserByCompanyId(Long companyId);
}
