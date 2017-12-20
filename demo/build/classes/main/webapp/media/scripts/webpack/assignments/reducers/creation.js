'use strict';

import { combineReducers } from 'redux';
import * as types from '../constants/creationActionTypes';

export const errors = (state = {}, { type, value }) => {
	switch (type) {
		case types.UPDATE_ERRORS:
			return value;
		default:
			return state;
	}
};

export const creationUserConfig = (state = {}, { type, userConfig }) => {
	switch (type) {
		case types.UPDATE_USER_CONFIG:
			return userConfig || {};
		default:
			return state;
	}
};

export const isSavingTemplate = (state = false, { type }) => {
	switch (type) {
		case types.TOGGLE_TEMPLATE_MODAL:
			return !state;
		default:
			return state;
	}
};

export const assignmentStatus = (state = '', { type, value }) => {
	switch (type) {
		case types.UPDATE_ASSIGNMENT_STATUS:
			return value;
		default:
			return state;
	}
};

const id = (state = 0, { type, value }) => {
	switch (type) {
		case types.UPDATE_TEMPLATE_ID:
			return value;
		default:
			return state;
	}
};

const name = (state = '', { type, value }) => {
	switch (type) {
		case types.UPDATE_TEMPLATE_NAME:
			return value;
		default:
			return state;
	}
};

const description = (state = '', { type, value }) => {
	switch (type) {
		case types.UPDATE_TEMPLATE_DESCRIPTION:
			return value;
		default:
			return state;
	}
};

export const saveMode = (state = 'new', { type, value }) => {
	switch (type) {
		case types.UPDATE_SAVE_MODE:
			return value;
		default:
			return state;
	}
};

export const numberOfCopies = (state = 1, { type, value }) => {
	switch (type) {
		case types.UPDATE_NUMBER_OF_COPIES:
			return value;
		default:
			return state;
	}
};

export const templateInfoReducer = combineReducers({
	id,
	name,
	description
});
