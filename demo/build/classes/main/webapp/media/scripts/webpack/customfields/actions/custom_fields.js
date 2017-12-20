import fetch from 'isomorphic-fetch';
import * as types from '../constants/actionTypes';

const receiveCustomFields = (customFieldGroup, customFields) => {
	return {
		type: types.RECEIVE_CUSTOM_FIELDS,
		customFieldGroup,
		customFields
	};
};

export function receiveCustomFieldGroup (customFieldGroup) {
	return {
		type: types.RECEIVE_CUSTOM_FIELD_GROUP,
		customFieldGroup
	};
}

// v2 will change to passing through an array of group ids to cut down on requests
export function fetchCustomFields (customFieldGroup) {
	const url = `/employer/v2/custom_field_groups/${customFieldGroup.id}/custom_fields?fields=id,name,defaultValue,required,type`;

	return (dispatch) => {
		return fetch(url, { credentials: 'same-origin' })
			.then(res => res.json())
			.then((res) => {
				dispatch(receiveCustomFields(customFieldGroup, res.results));
			});
	};
}

export const addCustomFields = ({ id, name, required, workCustomFields }) => {
	return { type: types.ADD_CUSTOM_FIELDS, id, name, required, workCustomFields };
};

export const updateCustomFields = ({ value, fieldId, groupId, isDefault }) => {
	const type = isDefault ? types.UPDATE_CUSTOM_FIELD_DEFAULT : types.UPDATE_CUSTOM_FIELD_VALUE;
	return { type, value, fieldId, groupId };
};

export const removeCustomFieldGroup = (groupId) => {
	return { type: types.REMOVE_CUSTOM_FIELD_GROUP, groupId };
};

export const toggleCustomFieldRequired = (groupId) => {
	return { type: types.TOGGLE_CUSTOM_FIELD_REQUIRED, groupId };
};
