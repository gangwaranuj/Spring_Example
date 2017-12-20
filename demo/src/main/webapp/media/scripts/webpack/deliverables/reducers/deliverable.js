'use strict';

import { ADD_DELIVERABLE, REMOVE_DELIVERABLE } from '../constants/actionTypes';

const deliverables = (state = [], action) => {
	switch (action.type) {
		case ADD_DELIVERABLE:
			return [
				...state,
				{
					id: null,
					description: action.description,
					numberOfFiles: action.numberOfFiles,
					type: action.deliverableType
				}
			];
		case REMOVE_DELIVERABLE:
			return state.filter((deliverable, index) => index !== action.index);
		default:
			return state;
	}
};

export default deliverables;
