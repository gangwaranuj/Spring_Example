'use strict';

import * as types from '../constants/basicActionTypes';

const updateId = (value) => {
	return {
		type: types.UPDATE_ID,
		value
	};
};

const updateTitle = (value) => {
	return {
		type: types.UPDATE_TITLE,
		value
	};
};

const updateDescription = (value) => {
	return {
		type: types.UPDATE_DESCRIPTION,
		value
	};
};

const updateSkills = (value) => {
	return {
		type: types.UPDATE_SKILLS,
		value
	};
};

const updateIndustryId = (value) => {
	return {
		type: types.UPDATE_INDUSTRY_ID,
		value
	};
};

const updateProjectId = (value) => {
	return {
		type: types.UPDATE_PROJECT_ID,
		value
	};
};

const updateOwner = (value) => {
	return {
		type: types.UPDATE_OWNER,
		value
	};
};

const updateSupportContactId = (value) => {
	return {
		type: types.UPDATE_SUPPORT_CONTACT_ID,
		value
	};
};

const updateInstructions = (value) => {
	return {
		type: types.UPDATE_INSTRUCTIONS,
		value
	};
};

const togglePrivateInstructions = () => {
	return {
		type: types.TOGGLE_PRIVATE_INSTRUCTIONS
	};
};

const updateUniqueExternalId = (value) => {
	return {
		type: types.UPDATE_UNIQUE_EXTERNAL_ID,
		value
	};
};

export default {
	updateId,
	updateTitle,
	updateDescription,
	updateSkills,
	updateIndustryId,
	updateProjectId,
	updateOwner,
	updateSupportContactId,
	updateInstructions,
	togglePrivateInstructions,
	updateUniqueExternalId
};
