'use strict';

import * as types from '../constants/actionTypes';

export const addRequirementSet = id => {
	return { type: types.ADD_REQUIREMENTSET, id };
};

export const removeRequirementSet = id => {
	return { type: types.REMOVE_REQUIREMENTSET, id };
};
