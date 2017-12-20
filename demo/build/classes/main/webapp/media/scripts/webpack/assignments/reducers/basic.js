'use strict';
import * as types from '../constants/basicActionTypes';

const id = (state = 0, action) => {
	switch (action.type) {
		case types.UPDATE_ID:
			return action.value;
		default:
			return state;
	}
};

const title = (state = '', action) => {
	switch (action.type) {
		case types.UPDATE_TITLE:
			return action.value;
		default:
			return state;
	}
};

const description = (state = '', action) => {
	switch (action.type) {
		case types.UPDATE_DESCRIPTION:
			return action.value;
		default:
			return state;
	}
};

const skills = (state = '', action) => {
	switch (action.type) {
		case types.UPDATE_SKILLS:
			return action.value;
		default:
			return state;
	}
};

const industryId = (state = 0, action) => {
	switch (action.type) {
		case types.UPDATE_INDUSTRY_ID:
			return action.value;
		default:
			return state;
	}
};

const projectId = (state = '', action) => {
	switch (action.type) {
		case types.UPDATE_PROJECT_ID:
			return action.value;
		default:
			return state;
	}
};

const ownerId = (state = '', action) => {
	switch (action.type) {
		case types.UPDATE_OWNER:
			return action.value;
		default:
			return state;
	}
};

const supportContactId = (state = '', action) => {
	switch (action.type) {
		case types.UPDATE_SUPPORT_CONTACT_ID:
			return action.value;
		default:
			return state;
	}
};

const instructions = (state = '', action) => {
	switch (action.type) {
		case types.UPDATE_INSTRUCTIONS:
			return action.value;
		default:
			return state;
	}
};

const instructionsPrivate = (state = true, action) => {
	switch (action.type) {
		case types.TOGGLE_PRIVATE_INSTRUCTIONS:
			return !state;
		default:
			return state;
	}
};

const uniqueExternalId = (state = null, action) => {
	switch (action.type) {
		case types.UPDATE_UNIQUE_EXTERNAL_ID:
			return action.value;
		default:
			return state;
	}
};

export default {
	id,
	title,
	description,
	skills,
	industryId,
	projectId,
	ownerId,
	supportContactId,
	instructions,
	instructionsPrivate,
	uniqueExternalId
};
