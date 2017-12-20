package com.workmarket.domains.work.service.actions;


import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.project.Project;

import java.util.List;

public class BulkEditClientProjectEvent extends AbstractWorkEvent {

	private ClientCompany client;
	private Project project;


	public static class Builder extends AbstractWorkEvent.Builder {
		private ClientCompany client;
		private Project project;

		public Builder(List<String> workNumbers, User user, String actionName, String messageKey, ClientCompany client, Project project) {
			super(workNumbers, user, actionName, messageKey);
			this.client = client;
			this.project = project;
		}

		@Override
		public BulkEditClientProjectEvent build() {
			return new BulkEditClientProjectEvent(this);
		}
	}

	private BulkEditClientProjectEvent(Builder builder){
		super(builder);
		this.client = builder.client;
		this.project = builder.project;

	}

	public ClientCompany getClient() {
		return client;
	}

	public void setClient(ClientCompany client) {
		this.client = client;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
