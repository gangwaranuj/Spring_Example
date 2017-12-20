import { Map } from 'immutable';
import * as types from '../constants/actionTypes';

const initialFirstAssignmentState = Map({
	customFieldsEnabled: false,
	shipmentsEnabled: false,
	requirementSetsEnabled: false,
	deliverablesEnabled: false
});
export { initialFirstAssignmentState };

const firstAssignment = (
	state = Map(initialFirstAssignmentState),
	{ type, name, value }
) => {
	switch (type) {
	case types.CHANGE_FIRST_ASSIGNMENT_FIELD:
		return state.set(name, value);

	case types.GET_FIRST_ASSIGNMENT_SUCCESS:
		return Map(value);

	default:
		return state;
	}
};

export default firstAssignment;
