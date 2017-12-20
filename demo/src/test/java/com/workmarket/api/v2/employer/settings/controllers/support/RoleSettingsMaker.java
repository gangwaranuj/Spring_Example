package com.workmarket.api.v2.employer.settings.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.settings.models.RoleSettingsDTO;

import static com.natpryce.makeiteasy.Property.newProperty;

public class RoleSettingsMaker {

	public static final Property<RoleSettingsDTO, Boolean>
		admin = newProperty(),
		manager = newProperty(),
		controller = newProperty(),
		user = newProperty(),
		viewOnly = newProperty(),
		staff = newProperty(),
		deputy = newProperty(),
		dispatcher = newProperty(),
		employeeWorker = newProperty();

	public static final Instantiator<RoleSettingsDTO> DefaultRoleSettings = new Instantiator<RoleSettingsDTO>() {

		@Override
		public RoleSettingsDTO instantiate(PropertyLookup<RoleSettingsDTO> lookup) {
			return new RoleSettingsDTO.Builder()
				.setAdmin(lookup.valueOf(admin, true))
				.setManager(lookup.valueOf(manager, true))
				.setController(lookup.valueOf(controller, false))
				.setViewOnly(lookup.valueOf(viewOnly, false))
				.setUser(lookup.valueOf(user, false))
				.setStaff(lookup.valueOf(staff, false))
				.setDeputy(lookup.valueOf(deputy, false))
				.setDispatcher(lookup.valueOf(dispatcher, false))
				.setEmployeeWorker(lookup.valueOf(employeeWorker, false))
				.build();
		}
	};

	public static final Instantiator<RoleSettingsDTO> EmptyRoleSettings = new Instantiator<RoleSettingsDTO>() {

		@Override
		public RoleSettingsDTO instantiate(PropertyLookup<RoleSettingsDTO> lookup) {
			return new RoleSettingsDTO.Builder()
				.setAdmin(lookup.valueOf(admin, false))
				.setManager(lookup.valueOf(manager, false))
				.setController(lookup.valueOf(controller, false))
				.setViewOnly(lookup.valueOf(viewOnly, false))
				.setUser(lookup.valueOf(user, false))
				.setStaff(lookup.valueOf(staff, false))
				.setDeputy(lookup.valueOf(deputy, false))
				.setDispatcher(lookup.valueOf(dispatcher, false))
				.setEmployeeWorker(lookup.valueOf(employeeWorker, false))
				.build();
		}
	};

	public static final Instantiator<RoleSettingsDTO> EmployeeWorkerRoleSettings = new Instantiator<RoleSettingsDTO>() {

		@Override
		public RoleSettingsDTO instantiate(PropertyLookup<RoleSettingsDTO> lookup) {
			return new RoleSettingsDTO.Builder()
				.setAdmin(lookup.valueOf(admin, false))
				.setManager(lookup.valueOf(manager, false))
				.setController(lookup.valueOf(controller, false))
				.setViewOnly(lookup.valueOf(viewOnly, false))
				.setUser(lookup.valueOf(user, false))
				.setStaff(lookup.valueOf(staff, false))
				.setDeputy(lookup.valueOf(deputy, false))
				.setDispatcher(lookup.valueOf(dispatcher, false))
				.setEmployeeWorker(lookup.valueOf(employeeWorker, true))
				.build();
		}
	};
}
