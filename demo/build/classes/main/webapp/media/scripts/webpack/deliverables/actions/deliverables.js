'use strict';
import * as types from '../constants/actionTypes.js';

export const updateDeliverablesInstructions = (value) => {
	return {
		type: types.UPDATE_DELIVERABLES_INSTRUCTIONS,
		value
	};
};

export const updateDeliverablesTime = (value) => {
	return {
		type: types.UPDATE_DELIVERABLES_TIME,
		value
	};
};

export const addDeliverable = ({ description, numberOfFiles, deliverableType }) => {
	return { type: types.ADD_DELIVERABLE, description, numberOfFiles, deliverableType };
};

export const removeDeliverable = (index) => {
	return { type: types.REMOVE_DELIVERABLE, index };
};
