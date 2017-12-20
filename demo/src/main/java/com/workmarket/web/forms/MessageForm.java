package com.workmarket.web.forms;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class MessageForm implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String title;
	@NotEmpty
	private String message;
	private List<Long> userIds;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public List<Long> getUserIds() {
		return userIds;
	}
	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	@Override
	public String toString() {
		return String.format("%s: %s", title, message);
	}
}
