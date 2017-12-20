import * as types from '../constants/contactActionTypes';

const updateContactId = (value) => {
	return {
		type: types.UPDATE_CONTACT_ID,
		value
	};
};

const updateContactFirstName = (value) => {
	return {
		type: types.UPDATE_CONTACT_FIRST_NAME,
		value
	};
};

const updateContactLastName = (value) => {
	return {
		type: types.UPDATE_CONTACT_LAST_NAME,
		value
	};
};

const updateContactEmail = (value) => {
	return {
		type: types.UPDATE_CONTACT_EMAIL,
		value
	};
};

const updateContactWorkPhone = (value) => {
	return {
		type: types.UPDATE_CONTACT_WORK_PHONE,
		value
	};
};

const updateContactWorkPhoneExtension = (value) => {
	return {
		type: types.UPDATE_CONTACT_WORK_PHONE_EXTENSION,
		value
	};
};

const updateContactMobilePhone = (value) => {
	return {
		type: types.UPDATE_CONTACT_MOBILE_PHONE,
		value
	};
};

export default {
	updateContactId,
	updateContactFirstName,
	updateContactLastName,
	updateContactEmail,
	updateContactWorkPhone,
	updateContactWorkPhoneExtension,
	updateContactMobilePhone
};
