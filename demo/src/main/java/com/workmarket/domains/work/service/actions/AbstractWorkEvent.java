package com.workmarket.domains.work.service.actions;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.event.Event;
import com.workmarket.domains.work.service.actions.handlers.WorkEventHandler;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.web.helpers.AjaxResponseBuilder;

import java.util.List;
import java.util.Set;

public abstract class AbstractWorkEvent extends Event {
	private static final long serialVersionUID = 2322791649260251841L;

	@Deprecated
	private List<Work> works;
	private List<String> workNumbers;
	private Set<Long> workIds;
	private Set<WorkContext> workContexts;
	private String actionName;
	private String messageKey;
	private WorkEventHandler workEventHandler;
	private AjaxResponseBuilder response;
	private boolean queue;

	public abstract static class Builder {

		@Deprecated
		List<Work> works;
		List<String> workNumbers;
		Set<Long> workIds;
		User user;
		String actionName;
		String messageKey;
		boolean queue = false;
		WorkEventHandler workEventHandler;
		Set<WorkContext> workContexts;
		AjaxResponseBuilder response;

		public Builder() {
		}

		public Builder(List<String> workNumbers,
					   User user,
					   String actionName,
					   String messageKey) {
			this.workNumbers = workNumbers;
			this.user = user;
			this.actionName = actionName;
			this.messageKey = messageKey;
		}

		public Builder(Set<Long> workIds, User user, String actionName, String messageKey) {
			this.workIds = workIds;
			this.user = user;
			this.actionName = actionName;
			this.messageKey = messageKey;
		}

		@Deprecated
		public Builder work(List<Work> val) {
			works = val;
			return this;
		}

		public Builder queue() {
			queue = true;
			return this;
		}

		public Builder workEventHandler(WorkEventHandler val) {
			workEventHandler = val;
			return this;
		}

		public Builder contexts(Set<WorkContext> val) {
			workContexts = val;
			return this;
		}

		public Builder response(AjaxResponseBuilder val) {
			response = val;
			return this;
		}

		public abstract AbstractWorkEvent build();
	}

	protected AbstractWorkEvent() {

	}

	protected AbstractWorkEvent(Builder builder) {
		super();
		this.setUser(builder.user);

		this.works = builder.works;
		this.workNumbers = builder.workNumbers;
		this.workIds = builder.workIds;
		this.actionName = builder.actionName;
		this.messageKey = builder.messageKey;
		this.queue = builder.queue;
		this.workEventHandler = builder.workEventHandler;
		this.workContexts = builder.workContexts;
		this.response = builder.response;
	}

	public boolean isValid() {
		if (this.getUser() == null) {
			return false;
		}

		if (this.getWorkNumbers() == null && this.getWorkIds() == null) {
			return false;
		}
		if (this.getResponse() == null) {
			return false;
		}
		if (!this.getResponse().isSuccessful()) {
			return false;
		}

		if (this.getMessageKey() == null) {
			return false;
		}
		return true;
	}

	public AjaxResponseBuilder handleEvent() {
		return workEventHandler.handleEvent(this);
	}

	public List<String> getWorkNumbers() {
		return workNumbers;
	}

	public Set<Long> getWorkIds() {
		return workIds;
	}

	public Set<WorkContext> getWorkContexts() {
		return workContexts;
	}

	public void addContexts(List<WorkContext> contexts) {
		workContexts.addAll(contexts);
	}

	public String getActionName() {
		return actionName;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public WorkEventHandler getWorkEventHandler() {
		return workEventHandler;
	}

	public AbstractWorkEvent setWorkEventHandler(WorkEventHandler workEventHandler) {
		this.workEventHandler = workEventHandler;
		return this;
	}

	public AjaxResponseBuilder getResponse() {
		return response;
	}

	public AjaxResponseBuilder setSuccessful(boolean successful) {
		if (this.response == null) return this.response;
		this.response.setSuccessful(successful);
		return this.getResponse();
	}

	public List<Work> getWorks() {
		return works;
	}

	public boolean isQueue() {
		return queue;
	}
}
