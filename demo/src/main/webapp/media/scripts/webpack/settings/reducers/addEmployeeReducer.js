import { Map, Record } from 'immutable';
import * as types from '../constants/actionTypes';

const RolesRecord = Record({
	admin: false,
	manager: false,
	controller: false,
	user: false,
	viewOnly: false,
	staff: false,
	deputy: false,
	dispatcher: false,
	worker: false
});


const PermissionsRecord = Record({
	paymentAccessible: false,
	fundsAccessible: false,
	counterOfferAccessible: false,
	pricingEditable: false,
	workApprovalAllowed: false,
	projectAccessible: false
});

const initialAddEmployeeState = {
	firstName: '',
	lastName: '',
	email: '',
	workPhoneInternationalCode: '',
	workPhone: '',
	workPhoneExtension: '',
	jobTitle: '',
	industryId: null,
	roleSettings: new RolesRecord(),
	permissionSettings: new PermissionsRecord(),
	spendLimit: '',
	errors: [],
	workerRoleCheckboxDisabled: false
};
export { initialAddEmployeeState };

const addEmployee = (state = Map(initialAddEmployeeState), { type, name, value }) => {
	switch (type) {
	case types.CHANGE_ADD_EMPLOYEE_FIELD:
		if (Array.isArray(name)) {
			return state.setIn(name, value);
		}

		return state.set(name, value);

	case types.RESET_ADD_EMPLOYEE_FIELDS:
		return Map(initialAddEmployeeState);

	default:
		return state;
	}
};

export default addEmployee;
