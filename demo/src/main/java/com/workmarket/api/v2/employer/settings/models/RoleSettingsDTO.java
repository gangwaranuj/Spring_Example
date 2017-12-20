package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModel;

@ApiModel("RoleSettings")
@JsonDeserialize(builder = RoleSettingsDTO.Builder.class)
public class RoleSettingsDTO {

	private final boolean admin;
	private final boolean manager;
	private final boolean controller;
	private final boolean user;
	private final boolean viewOnly;
	private final boolean staff;
	private final boolean deputy;
	private final boolean dispatcher;
	private final boolean employeeWorker;

	private RoleSettingsDTO(RoleSettingsDTO.Builder builder) {
		this.admin = builder.admin;
		this.manager = builder.manager;
		this.controller = builder.controller;
		this.user = builder.user;
		this.viewOnly = builder.viewOnly;
		this.staff = builder.staff;
		this.deputy = builder.deputy;
		this.dispatcher = builder.dispatcher;
		this.employeeWorker = builder.employeeWorker;
	}

	public boolean isAdmin() {
		return admin;
	}

	public boolean isManager() {
		return manager;
	}

	public boolean isController() {
		return controller;
	}

	public boolean isUser() {
		return user;
	}

	public boolean isViewOnly() {
		return viewOnly;
	}

	public boolean isStaff() {
		return staff;
	}

	public boolean isDeputy() {
		return deputy;
	}

	public boolean isDispatcher() {
		return dispatcher;
	}

	public boolean isEmployeeWorker() {
		return employeeWorker;
	}

	public static class Builder implements AbstractBuilder<RoleSettingsDTO> {

		private boolean admin;
		private boolean manager;
		private boolean controller;
		private boolean user;
		private boolean viewOnly;
		private boolean staff;
		private boolean deputy;
		private boolean dispatcher;
		private boolean employeeWorker;

		public Builder() {}

		public Builder(RoleSettingsDTO roleSettingsDTO) {
			this.admin = roleSettingsDTO.admin;
			this.manager = roleSettingsDTO.manager;
			this.controller = roleSettingsDTO.controller;
			this.user = roleSettingsDTO.user;
			this.viewOnly = roleSettingsDTO.viewOnly;
			this.staff = roleSettingsDTO.staff;
			this.deputy = roleSettingsDTO.deputy;
			this.dispatcher = roleSettingsDTO.dispatcher;
			this.employeeWorker = roleSettingsDTO.employeeWorker;
		}

		@JsonProperty("admin") public Builder setAdmin(final boolean admin) {
			this.admin = admin;
			return this;
		}

		@JsonProperty("manager") public Builder setManager(final boolean manager) {
			this.manager = manager;
			return this;
		}

		@JsonProperty("controller") public Builder setController(final boolean controller) {
			this.controller = controller;
			return this;
		}

		@JsonProperty("user") public Builder setUser(final boolean user) {
			this.user = user;
			return this;
		}

		@JsonProperty("viewOnly") public Builder setViewOnly(final boolean viewOnly) {
			this.viewOnly = viewOnly;
			return this;
		}

		@JsonProperty("staff") public Builder setStaff(final boolean staff) {
			this.staff = staff;
			return this;
		}

		@JsonProperty("deputy") public Builder setDeputy(final boolean deputy) {
			this.deputy = deputy;
			return this;
		}

		@JsonProperty("dispatcher") public Builder setDispatcher(final boolean dispatcher) {
			this.dispatcher = dispatcher;
			return this;
		}

		@JsonProperty("employeeWorker") public Builder setEmployeeWorker(final boolean employeeWorker) {
			this.employeeWorker = employeeWorker;
			return this;
		}

		@Override
		public RoleSettingsDTO build() {
			return new RoleSettingsDTO(this);
		}
	}
}
