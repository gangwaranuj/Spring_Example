'use strict';

import * as types from '../constants/actionTypes';

export const addDocument = ({ id, uuid, name, mime_type, description, visibilityType, uploaded }) => {
	return { type: types.ADD_DOCUMENT, id, uuid, name, mime_type, description, visibilityType, uploaded };
};

export const updateDocument = ({uuid, description, visibilityType}) => {
	return {type: types.UPDATE_DOCUMENT, uuid, description, visibilityType};
};

export const removeDocument = (uuid) => {
	return { type: types.REMOVE_DOCUMENT, uuid };
};
