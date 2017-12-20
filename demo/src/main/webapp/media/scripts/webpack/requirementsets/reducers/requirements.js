'use strict';

import { ADD_REQUIREMENTSET, REMOVE_REQUIREMENTSET } from '../constants/actionTypes';

const requirementSetIds = (state = [], action) => {
	switch (action.type) {
		case ADD_REQUIREMENTSET:
			if (!state.includes(action.id)) {
				return [...state, action.id];
			}
			else {
				return state;
			}
		case REMOVE_REQUIREMENTSET:
			return state.filter(id => id !== action.id);

		default:
			return state;
	}
};

export default requirementSetIds;
