package com.workmarket.web.models;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class MessageBundle implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> success = Lists.newArrayList();
	private List<String> errors = Lists.newArrayList();
	private List<String> notices = Lists.newArrayList();
	private List<String> warnings = Lists.newArrayList();

	public static MessageBundle newInstance() {
		return new MessageBundle();
	}

	public List<String> getSuccess() {
		return success;
	}
	public void setSuccess(List<String> success) {
		this.success = success;
	}
	public void addSuccess(String message) {
		this.success.add(message);
	}

	public List<String> getErrors() {
		return errors;
	}
	public Boolean hasErrors() {
		return !errors.isEmpty();
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public void addError(String message) {
		this.errors.add(message);
	}

	public List<String> getNotices() {
		return notices;
	}
	public Boolean hasNotices() {
		return !notices.isEmpty();
	}
	public void setNotices(List<String> notices) {
		this.notices = notices;
	}
	public void addNotice(String message) {
		this.notices.add(message);
	}

	public List<String> getWarnings() {
		return warnings;
	}
	public Boolean hasWarnings() {
		return !warnings.isEmpty();
	}
	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
	public void addWarning(String message) {
		this.warnings.add(message);
	}
	
	public void addMessage(MessageBundleType messageType, String message) {
		switch (messageType) {
			case SUCCESS: addSuccess(message); return;
			case ERROR:   addError(message); return;
			case WARN:    addWarning(message); return;
			case NOTICE:  addNotice(message); return;
		}
	}
	
	public List<String> getAllMessages() {
		List<String> messages = Lists.newArrayList();
		messages.addAll(success);
		messages.addAll(errors);
		messages.addAll(notices);
		messages.addAll(warnings);
		return messages;
	}
}
