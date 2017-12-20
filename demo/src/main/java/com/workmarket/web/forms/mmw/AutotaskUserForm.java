package com.workmarket.web.forms.mmw;

import com.workmarket.service.business.dto.integration.AutotaskUserCustomFieldsPreferenceDTO;
import com.workmarket.service.business.dto.integration.AutotaskUserDTO;

import java.io.Serializable;
import java.util.Map;

public class AutotaskUserForm implements Serializable {
	private Map<String, AutotaskUserCustomFieldsPreferenceDTO> preferenceMap;
	private AutotaskUserDTO autotaskUser;
	private boolean hasApiUser;
	private boolean notesEnabled;
	private boolean notesInternal;
	private boolean attachmentsInternal;
	public Map<String, AutotaskUserCustomFieldsPreferenceDTO> getPreferenceMap() {
		return preferenceMap;
	}

	public void setPreferenceMap(Map<String, AutotaskUserCustomFieldsPreferenceDTO> preferenceMap) {
		this.preferenceMap = preferenceMap;
	}

	public AutotaskUserDTO getAutotaskUser() {
		return autotaskUser;
	}

	public void setAutotaskUser(AutotaskUserDTO autotaskUser) {
		this.autotaskUser = autotaskUser;
	}

	public boolean isNotesEnabled() {
		return notesEnabled;
	}

	public void setNotesEnabled(boolean notesEnabled) {
		this.notesEnabled = notesEnabled;
	}

	public boolean isNotesInternal() {
		return notesInternal;
	}

	public void setNotesInternal(boolean notesInternal) {
		this.notesInternal = notesInternal;
	}

	public boolean isAttachmentsInternal() {
		return attachmentsInternal;
	}

	public void setAttachmentsInternal(boolean attachmentsInternal) {
		this.attachmentsInternal = attachmentsInternal;
	}

	public boolean isHasApiUser() {
		return hasApiUser;
	}

	public void setHasApiUser(boolean hasApiUser) {
		this.hasApiUser = hasApiUser;
	}

}
