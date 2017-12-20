'use strict';
import * as types from '../constants/actionTypes';

export const addSurvey = ({ id, name, required }) => {
	return { type: types.ADD_SURVEY, id, name, required };
};

export const removeSurvey = (index) => {
	return { type: types.REMOVE_SURVEY, index };
};

export const toggleRequired = (index) => {
	return { type: types.TOGGLE_REQUIRED, index };
};
