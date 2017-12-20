import { Map } from 'immutable';
import addEmployee, { initialAddEmployeeState } from '../addEmployeeReducer';
import * as types from '../../constants/actionTypes';

describe('Add Employee Reducer', () => {
	const initialState = Map(initialAddEmployeeState);

	it('should return the initial state', () => {
		expect(addEmployee(undefined, {})).toEqual(initialState);
	});

	it('should handle `CHANGE_ADD_EMPLOYEE_FIELD` action', () => {
		const firstName = 'Vinny';
		const action = {
			type: types.CHANGE_ADD_EMPLOYEE_FIELD,
			name: 'firstName',
			value: firstName
		};
		const state = initialState.set(
			'firstName',
			firstName
		);

		expect(addEmployee(undefined, action)).toEqual(state);
	});

	it('should handle `RESET_ADD_EMPLOYEE_FIELDS` action', () => {
		const firstName = 'Vinny';
		const action = {
			type: types.RESET_ADD_EMPLOYEE_FIELDS
		};
		const modifiedState = initialState.set(
			'firstName',
			firstName
		);

		expect(addEmployee(modifiedState, action)).toEqual(initialState);
	});
});
