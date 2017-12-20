import * as types from '../constants/secondaryContactActionTypes';

const updateSecondaryContactId = (value) => {
	return {
		type: types.UPDATE_SECONDARY_CONTACT_ID,
		value
	};
};

const updateSecondaryContactFirstName = (value) => {
	return {
		type: types.UPDATE_SECONDARY_CONTACT_FIRST_NAME,
		value
	};
};

const updateSecondaryContactLastName = (value) => {
	return {
		type: types.UPDATE_SECONDARY_CONTACT_LAST_NAME,
		value
	};
};

const updateSecondaryContactEmail = (value) => {
	return {
		type: types.UPDATE_SECONDARY_CONTACT_EMAIL,
		value
	};
};

const updateSecondaryContactWorkPhone = (value) => {
	return {
		type: types.UPDATE_SECONDARY_CONTACT_WORK_PHONE,
		value
	};
};

const updateSecondaryContactWorkPhoneExtension = (value) => {
	return {
		type: types.UPDATE_SECONDARY_CONTACT_WORK_PHONE_EXTENSION,
		value
	};
};

const updateSecondaryContactMobilePhone = (value) => {
	return {
		type: types.UPDATE_SECONDARY_CONTACT_MOBILE_PHONE,
		value
	};
};

export default {
	updateSecondaryContactId,
	updateSecondaryContactFirstName,
	updateSecondaryContactLastName,
	updateSecondaryContactEmail,
	updateSecondaryContactWorkPhone,
	updateSecondaryContactWorkPhoneExtension,
	updateSecondaryContactMobilePhone
};
