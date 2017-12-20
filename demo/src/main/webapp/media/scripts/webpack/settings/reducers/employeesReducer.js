import Immutable from 'immutable';
import {
	EmployeeList as EmployeeListModel
} from './models';
import * as types from '../constants/actionTypes';

const initialEmployeeListState = new EmployeeListModel();

const employeeList = (
	state = initialEmployeeListState,
	{
		type,
		value
	},
) => {
	switch (type) {
	case types.EMPLOYEE_LIST_SUCCESS:
		if (!value) { return state; }
		return new EmployeeListModel(Immutable.fromJS(value));
	default:
		return state;
	}
};

export { initialEmployeeListState };
export default employeeList;
