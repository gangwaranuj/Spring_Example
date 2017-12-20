import { Map } from 'immutable';
import * as types from '../constants/actionTypes';

const initialFirstAssignmentTemplateState = Map({
	title: '',
	name: ''
});
export { initialFirstAssignmentTemplateState };

const firstAssignmentTemplate = (
	state = Map(initialFirstAssignmentTemplateState),
	{ type, name, value }
) => {
	switch (type) {
	case types.CHANGE_FIRST_ASSIGNMENT_TEMPLATE_FIELD:
		return state.set(name, value);

	default:
		return state;
	}
};

export default firstAssignmentTemplate;
