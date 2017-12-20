import fetch from 'isomorphic-fetch';
import {
	addLeadingZero,
	getCardType
} from '@workmarket/js-utils';
import Application from '../../core';
import * as types from '../constants/actionTypes';
import { sendAssignmentAnalytics } from '../../assignments/actions/creation';

const makeActionCreator = (type, ...argNames) => (...args) => {
	const action = { type };

	argNames.forEach((arg, index) => {
		action[argNames[index]] = args[index];
	});
	return action;
};

const openModal = async(options) => {
	const loadAssignmentCreationModal = async() => {
		const module = await import(/* webpackChunkName: "newAssignmentCreation" */ '../../assignments/creation_modal');
		return module.default;
	};
	loadAssignmentCreationModal().then(CreationModal => new CreationModal(options));
};

export const changeField = makeActionCreator(types.CHANGE_FIELD, 'name', 'value');
export const changeLocationField = makeActionCreator(types.CHANGE_LOCATION_FIELD, 'name', 'value');
export const changeFundsField = makeActionCreator(types.CHANGE_FUNDS_FIELD, 'name', 'value');
export const blurFundsField = makeActionCreator(types.BLUR_FUNDS_FIELD, 'name', 'value');
export const changeTaxField = makeActionCreator(types.CHANGE_TAX_FIELD, 'name', 'value');
export const blurTaxField = makeActionCreator(types.BLUR_TAX_FIELD, 'name', 'value');
export const changeCreditCardField = makeActionCreator(
	types.CHANGE_CREDIT_CARD_FIELD,
	'name',
	'value'
);

export const changeAddEmployeeField = makeActionCreator(
	types.CHANGE_ADD_EMPLOYEE_FIELD,
	'name',
	'value'
);

export const workerDisabledCheck = () => (dispatch, getState) => {
	const addEmployee = getState().addEmployee.toJS();
	const roleSettings = addEmployee.roleSettings;
	const permissionSettings = addEmployee.permissionSettings;

	const isRoleSettingsTrue = (role) => {
		return roleSettings[role] === true && role !== 'worker';
	};
	const isPermissionsTrue = (permission) => {
		return permissionSettings[permission] === true;
	};

	const trueRoles = Object.keys(roleSettings).filter(isRoleSettingsTrue);
	const truePermissions = Object.keys(permissionSettings).filter(isPermissionsTrue);

	if (trueRoles.length < 1 && truePermissions.length < 1) {
		dispatch(changeAddEmployeeField('workerRoleCheckboxDisabled', false));
	} else {
		dispatch(changeAddEmployeeField('workerRoleCheckboxDisabled', true));
	}
};

export const disabledAssignmentPreferences = makeActionCreator(
	types.GET_ASSIGNMENT_PREFERENCES_REQUEST
);
export const changeAssignmentPreferencesField = makeActionCreator(
	types.CHANGE_ASSIGNMENT_PREFERENCES_FIELD,
	'name',
	'value'
);

export const disabledFirstAssignment = makeActionCreator(
	types.GET_FIRST_ASSIGNMENT_REQUEST
);
export const changeFirstAssignmentField = makeActionCreator(
	types.CHANGE_FIRST_ASSIGNMENT_FIELD,
	'name',
	'value'
);

export const changeFirstAssignmentTemplateField = makeActionCreator(
	types.CHANGE_FIRST_ASSIGNMENT_TEMPLATE_FIELD,
	'name',
	'value'
);

export const removeUpload = makeActionCreator(
	types.CHANGE_FIRST_ASSIGNMENT_TEMPLATE_FIELD,
	'name',
	'value'
);

export const googleAPILoaded = () => (dispatch) => {
	dispatch({
		type: types.GOOGLE_INITIALIZED
	});
};

export const getEmployeeList = (
	companyId = Application.UserInfo.companyId,
	url = `/companies/${companyId}/employees`
) => async (dispatch) => {
	try {
		const employeeList = await fetch(url, {
			credentials: 'same-origin'
		})
			.then(res => res.json());
		dispatch({
			type: types.EMPLOYEE_LIST_SUCCESS,
			value: employeeList
		});
	} catch (e) {
		dispatch({
			type: types.EMPLOYEE_LIST_ERROR,
			value: e
		});
	}
};

export const receiveOnboardingProgress = progressInfo => (dispatch) => {
	const { completedActions } = progressInfo;
	const componentsMap = {
		OVERVIEW: types.PROFILE_FORM_SUBMIT_SUCCESS,
		TAX: types.TAX_FORM_SUBMIT_SUCCESS,
		FUNDS: types.FUNDS_FORM_SUBMIT_SUCCESS,
		ASSIGNMENT_SETTINGS: '',
		BANK: ''
	};

	completedActions.forEach((action) => {
		dispatch({
			type: componentsMap[action]
		});
	});
};

export const getOnboardingProgress = (
	completenessUrl = '/employer/v2/settings/completeness_percentage'
) => (dispatch) => {
	return fetch(completenessUrl, {
		credentials: 'same-origin'
	})
		.then(res => res.json())
		.then((res) => {
			const progressInfo = res.results[0];
			dispatch(receiveOnboardingProgress(progressInfo));
		});
};

export const getCompanyProfileInfo = (
	url = '/employer/v2/settings/profile'
) => (dispatch) => {
	dispatch({
		type: types.RETRIEVE_PROFILE_REQUEST
	});
	return fetch(url, {
		credentials: 'same-origin',
		headers: new Headers({
			'Content-Type': 'application/json',
			'X-CSRF-Token': Application.CSRFToken
		}),
		method: 'GET'
	})
		.then(res => res.json())
		.then(res => dispatch({
			type: types.RETRIEVE_PROFILE_SUCCESS,
			value: res.results[0]
		}));
};

export const onSubmitProfileForm = (
	form,
	submitUrl = '/employer/v2/settings/profile'
) => (dispatch, getState) => {
	dispatch({
		type: types.PROFILE_FORM_SUBMIT_REQUEST
	});
	return fetch(submitUrl, {
		method: 'POST',
		credentials: 'same-origin',
		body: JSON.stringify(getState().profile.toJS()),
		headers: new Headers({
			'Content-Type': 'application/json',
			'X-CSRF-Token': Application.CSRFToken,
			'Data-Type': 'json'
		})
	})
	.then((res) => {
		if (!res.ok) {
			return res.json().then((response) => {
				const error = response.results[0].message;
				dispatch({
					type: types.PROFILE_FORM_SUBMIT_ERROR,
					error
				});
			});
		}
		return res.json().then(() => {
			dispatch({
				type: types.PROFILE_FORM_SUBMIT_SUCCESS
			});
		});
	});
};

export const onSubmitFundsForm = (
	form,
	submitUrl = '/employer/v2/settings/funds/accounts'
) => (dispatch) => {
	dispatch({
		type: types.FUNDS_FORM_SUBMIT_REQUEST
	});
	const formData = form.toJS();
	const fundsData = {};
	Object.keys(formData).forEach((key) => {
		fundsData[key] = formData[key].value;
	});

	return fetch(submitUrl, {
		method: 'POST',
		credentials: 'same-origin',
		body: JSON.stringify(fundsData),
		headers: new Headers({
			'Content-Type': 'application/json',
			'X-CSRF-Token': Application.CSRFToken,
			'Data-Type': 'json'
		})
	})
	.then((res) => {
		if (!res.ok) {
			return res.json().then((response) => {
				const errors = response.results.map((err) => {
					return err.message;
				});
				dispatch({
					type: types.FUNDS_FORM_SUBMIT_ERROR,
					error: errors
				});
			});
		}
		return res.json().then(() => {
			dispatch({
				type: types.FUNDS_FORM_SUBMIT_SUCCESS
			});
		});
	});
};

export const onSubmitTaxForm = (
	form,
	submitUrl = '/employer/v2/settings/tax'
) => (dispatch) => {
	dispatch({
		type: types.TAX_FORM_SUBMIT_REQUEST
	});
	const formData = form.toJS();
	const taxData = {};
	Object.keys(formData).forEach((key) => {
		taxData[key] = formData[key].value;
	});

	return fetch(submitUrl, {
		method: 'POST',
		credentials: 'same-origin',
		body: JSON.stringify(taxData),
		headers: new Headers({
			'Content-Type': 'application/json',
			'X-CSRF-Token': Application.CSRFToken,
			'Data-Type': 'json'
		})
	})
	.then((res) => {
		if (!res.ok) {
			return res.json().then((response) => {
				const errors = response.results.map((err) => {
					return err.message;
				});
				dispatch({
					type: types.TAX_FORM_SUBMIT_ERROR,
					error: errors
				});
			});
		}
		return res.json().then(() => {
			dispatch({
				type: types.TAX_FORM_SUBMIT_SUCCESS
			});
		});
	});
};

export const onSubmitAddFundsViaCreditCardForm = (
	form,
	submitUrl = '/employer/v2/settings/funds/credit_card'
) => (dispatch) => {
	dispatch({
		type: types.ADD_FUNDS_VIA_CREDIT_CARD_FORM_SUBMIT_REQUEST
	});

	const isUSA = form.get('billingCountry') === 'USA';
	const location = {
		id: 0,
		addressLine1: form.get('billingAddress1'),
		addressLine2: form.get('billingAddress2'),
		city: form.get('billingCity'),
		country: form.get('billingCountry'),
		zip: isUSA ? form.get('billingZip') : form.get('billingPostalCode'),
		state: isUSA ? form.get('billingState') : form.get('billingProvince')
	};

	let cardType = getCardType(form.get('cardNumber')).toLowerCase().replace(/\s/, '');

	if (cardType === 'americanexpress') {
		cardType = 'amex';
	}

	const formData = {
		amount: form.get('fundsToAdd'),
		nameOnCard: form.get('nameOnCard'),
		cardType,
		cardNumber: form.get('cardNumber'),
		cardExpirationMonth: addLeadingZero(form.get('expirationMonth')),
		cardExpirationYear: addLeadingZero(form.get('expirationYear')),
		cardSecurityCode: form.get('securityCode'),
		location
	};

	return fetch(submitUrl, {
		method: 'POST',
		credentials: 'same-origin',
		body: JSON.stringify(formData),
		headers: new Headers({
			'Content-Type': 'application/json',
			'X-CSRF-Token': Application.CSRFToken,
			'Data-Type': 'json'
		})
	})
	.then((res) => {
		const defaultErrorMessage = 'There was a problem submitting your request';

		if (!res.ok) {
			return res.json().then((response) => {
				let errors = [];

				if (response.results) {
					errors = response.results.map(err => err.message);
				} else if (response.errors) {
					errors = response.errors.map(err => err.message);
				}

				if (!errors.length && response.message) {
					errors.push(response.message);
				} else {
					errors.push(defaultErrorMessage);
				}

				dispatch({
					type: types.ADD_FUNDS_VIA_CREDIT_CARD_FORM_SUBMIT_ERROR,
					error: errors
				});
			});
		}
		return res.json().then((response) => {
			if (response.results[0].approved) {
				dispatch({
					type: types.ADD_FUNDS_VIA_CREDIT_CARD_FORM_SUBMIT_SUCCESS
				});
			}

			const error = [response.results[0].responseMessage || defaultErrorMessage];

			dispatch({
				type: types.ADD_FUNDS_VIA_CREDIT_CARD_FORM_SUBMIT_ERROR,
				error
			});
		});
	});
};

export const onSubmitAddEmployeeForm = (
	form,
	submitUrl = '/employer/v2/settings/users'
) => (dispatch, getState) => {
	dispatch({
		type: types.ADD_EMPLOYEE_FORM_SUBMIT_REQUEST
	});

	const formData = getState().addEmployee.toJS();
	delete formData.errors;

	return fetch(submitUrl, {
		method: 'POST',
		credentials: 'same-origin',
		body: JSON.stringify(formData),
		headers: new Headers({
			'Content-Type': 'application/json',
			'X-CSRF-Token': Application.CSRFToken,
			'Data-Type': 'json'
		})
	})
	.then((res) => {
		const defaultErrorMessage = 'There was a problem submitting your request';

		if (!res.ok) {
			return res.json().then((response) => {
				let errors = [];

				if (response.results) {
					errors = response.results.map(err => err.message);
				} else if (response.errors) {
					errors = response.errors.map(err => err.message);
				}

				if (!errors.length && response.message) {
					errors.push(response.message);
				} else if (!errors.length) {
					errors.push(defaultErrorMessage);
				}

				dispatch({
					type: types.ADD_EMPLOYEE_FORM_SUBMIT_ERROR,
					error: errors
				});
			});
		}

		return res.json().then(() => {
			dispatch({
				type: types.ADD_EMPLOYEE_FORM_SUBMIT_SUCCESS
			});
			dispatch({
				type: types.RESET_ADD_EMPLOYEE_FIELDS
			});
		});
	});
};

export const processBulkUpload = (
	id,
	uuid,
	submitUrl = '/users/import'
) => (dispatch) => {
	dispatch({
		type: types.BULK_PROCESS_EMPLOYEES_START,
		id,
		uuid
	});

	return fetch(`${submitUrl}/${uuid}`, {
		method: 'POST',
		credentials: 'same-origin',
		headers: new Headers({
			'Content-Type': 'application/json',
			'X-CSRF-Token': Application.CSRFToken,
			'Data-Type': 'json'
		})
	})
	.then((res) => {
		const defaultErrorMessage = 'There was an error in processing the file. Please upload the file again.';

		if (!res.ok) {
			return res.json().then((response) => {
				let errors = [];

				if (response.results) {
					errors = response.results.map(err => err.message);
				} else if (response.errors) {
					errors = response.errors.map(err => err.message);
				}

				if (!errors.length && response.message) {
					errors.push(response.message);
				} else if (!errors.length) {
					errors.push(defaultErrorMessage);
				}

				dispatch({
					type: types.BULK_UPLOAD_EMPLOYEES_ERROR,
					id,
					error: errors
				});
			});
		}

		return res.json().then(() => {
			dispatch({
				type: types.BULK_PROCESS_EMPLOYEES_SUCCESS,
				id
			});
		});
	});
};

export const onBulkEmployeeUpload = file => (dispatch) => {
	const upload = new FormData();
	const xhr = new XMLHttpRequest();
	const id = `${file.name}_${Date.now()}`;

	upload.append('file', file);

	xhr.upload.addEventListener('progress', (event) => {
		if (event.lengthComputable) {
			const progress = Math.round((event.loaded * 100) / event.total);

			dispatch({
				type: types.BULK_UPLOAD_EMPLOYEES_PROGRESS,
				file,
				id,
				progress
			});
		} else {
			dispatch({
				type: types.BULK_UPLOAD_EMPLOYEES_PROGRESS,
				file,
				id,
				progress: false
			});
		}
	}, false);

	xhr.upload.addEventListener('error', () => {
		dispatch({
			type: types.BULK_UPLOAD_EMPLOYEES_ERROR,
			file,
			id
		});
	}, false);

	xhr.upload.addEventListener('load', () => {
		dispatch({
			type: types.BULK_UPLOAD_EMPLOYEES_SUCCESS,
			id
		});
	}, false);

	xhr.open('POST', '/employer/v2/uploads');
	xhr.responseType = 'json';
	xhr.setRequestHeader('X-CSRF-Token', Application.CSRFToken);
	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			dispatch({
				type: types.BULK_UPLOAD_EMPLOYEES_SUCCESS,
				id
			});

			const uuid = xhr.response.results[0].uuid;
			if (uuid) {
				dispatch(processBulkUpload(id, xhr.response.results[0].uuid));
			} else {
				dispatch({
					type: types.BULK_UPLOAD_EMPLOYEES_ERROR,
					id
				});
			}
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status !== 200) {
			dispatch({
				type: types.BULK_UPLOAD_EMPLOYEES_ERROR,
				id
			});
		}
	};

	xhr.send(upload);

	dispatch({
		type: types.BULK_UPLOAD_EMPLOYEES_START,
		file,
		id,
		abort: () => xhr.abort()
	});
};

export const cancelBulkUpload = id => (dispatch, getState) => {
	const state = getState().files;
	state.getIn([id, 'abort'])();

	dispatch({
		type: types.BULK_UPLOAD_EMPLOYEES_ABORT,
		id
	});
};

export const getEmployees = () => (dispatch) => {
	fetch(`/employer/v2/settings/users?companyId=${Application.UserInfo.companyNumber}&fields=fullName,rolesString,latestActivityOn`, { credentials: 'same-origin' })
		.then(res => res.json())
		.then((employees) => {
			dispatch({
				type: types.REFRESH_EMPLOYEE_LIST,
				value: employees.results
			});
		});
};

export const getAssignmentPreferences = () => (dispatch) => {
	fetch('/v2/employer/assignments/configuration', { credentials: 'same-origin' })
		.then(res => res.json())
		.then((configuration) => {
			dispatch({
				type: types.GET_ASSIGNMENT_PREFERENCES_SUCCESS,
				value: configuration.results[0]
			});
		});
};

export const getFirstAssignment = () => (dispatch) => {
	fetch('/v2/employer/assignments/configuration', { credentials: 'same-origin' })
		.then(res => res.json())
		.then((configuration) => {
			dispatch({
				type: types.GET_FIRST_ASSIGNMENT_SUCCESS,
				value: configuration.results[0]
			});
		});
};

export const onSubmitAssignmentPreferences = (
	submitUrl = '/v2/employer/assignments/configuration'
) => (dispatch, getState) => {
	dispatch({
		type: types.ASSIGNMENT_PREFERENCES_FORM_SUBMIT_REQUEST
	});

	const formData = getState().assignmentPreferences.toJS();

	return fetch(submitUrl, {
		method: 'POST',
		credentials: 'same-origin',
		body: JSON.stringify(formData),
		headers: new Headers({
			'Content-Type': 'application/json',
			'X-CSRF-Token': Application.CSRFToken,
			'Data-Type': 'json'
		})
	})
	.then((res) => {
		const defaultErrorMessage = 'There was a problem submitting your request';

		if (!res.ok) {
			return res.json().then((response) => {
				let errors = [];

				if (response.results) {
					errors = response.results.map(err => err.message);
				} else if (response.errors) {
					errors = response.errors.map(err => err.message);
				}

				if (!errors.length && response.message) {
					errors.push(response.message);
				} else if (!errors.length) {
					errors.push(defaultErrorMessage);
				}

				dispatch({
					type: types.ASSIGNMENT_PREFERENCES_FORM_SUBMIT_ERROR,
					error: errors
				});
			});
		}

		return res.json().then(() => {
			dispatch({
				type: types.ASSIGNMENT_PREFERENCES_FORM_SUBMIT_SUCCESS
			});
		});
	});
};

export const onSubmitFirstAssignmentTemplate = (
	submitUrl = '/employer/v2/assignments/templates'
) => (dispatch, getState) => {
	dispatch({
		type: types.FIRST_ASSIGNMENT_TEMPLATE_FORM_SUBMIT_REQUEST
	});

	const formData = {
		assignment: {
			industryId: '1000',
			instructionsPrivate: false,
			location: {
				id: 0
			},
			ownerId: Application.UserInfo.userNumber,
			routing: {
				browseMarketplace: false,
				firstToAcceptCandidates: {
					groupIds: [],
					resourceNumbers: [],
					vendorCompanyNumbers: []
				},
				needToApplyCandidates: {
					groupIds: [],
					resourceNumbers: [],
					vendorCompanyNumbers: []
				},
				shownInFeed: false,
				smartRoute: false
			},
			schedule: {
				checkinCallRequired: false,
				checkinRequired: false,
				confirmationRequired: false
			},
			supportContactId: Application.UserInfo.userNumber,
			title: getState().firstAssignmentTemplate.get('title')
		},
		description: '',
		id: 0,
		name: getState().firstAssignmentTemplate.get('name')
	};

	return fetch(submitUrl, {
		method: 'POST',
		credentials: 'same-origin',
		body: JSON.stringify(formData),
		headers: new Headers({
			'Content-Type': 'application/json',
			'X-CSRF-Token': Application.CSRFToken,
			'Data-Type': 'json'
		})
	})
	.then((res) => {
		const defaultErrorMessage = 'There was a problem submitting your request';

		if (!res.ok) {
			return res.json().then((response) => {
				let errors = [];

				if (response.results) {
					errors = response.results.map(err => err.message);
				} else if (response.errors) {
					errors = response.errors.map(err => err.message);
				}

				if (!errors.length && response.message) {
					errors.push(response.message);
				} else if (!errors.length) {
					errors.push(defaultErrorMessage);
				}

				dispatch({
					type: types.FIRST_ASSIGNMENT_TEMPLATE_FORM_SUBMIT_ERROR,
					error: errors
				});

				const analyticsProps = {
					version: '2',
					action: 'Save Route',
					success: false,
					errors
				};
				sendAssignmentAnalytics(analyticsProps);
			});
		}

		return res.json().then((response) => {
			const templateId = response.results[0].id;
			const title = 'Edit Template';

			fetch(`/employer/v2/assignments/templates/${templateId}`, {
				credentials: 'same-origin'
			})
			.then(templateResponse => templateResponse.json())
			.then((templateResponse) => {
				dispatch({
					type: types.FIRST_ASSIGNMENT_TEMPLATE_FORM_SUBMIT_SUCCESS
				});

				const assignment = templateResponse.results[0].assignment;
				assignment.id = null;
				const analyticsProps = {
					version: '2',
					action: 'Template saved',
					success: true,
					templateId
				};
				sendAssignmentAnalytics(analyticsProps);
				return openModal({
					title,
					assignment
				});
			});
		});
	});
};

export const onSubmitFirstAssignment = (
	submitUrl = '/v2/employer/assignments/configuration'
) => (dispatch, getState) => {
	dispatch({
		type: types.FIRST_ASSIGNMENT_FORM_SUBMIT_REQUEST
	});

	const formData = getState().firstAssignment.toJS();

	return fetch(submitUrl, {
		method: 'POST',
		credentials: 'same-origin',
		body: JSON.stringify(formData),
		headers: new Headers({
			'Content-Type': 'application/json',
			'X-CSRF-Token': Application.CSRFToken,
			'Data-Type': 'json'
		})
	})
	.then((res) => {
		const defaultErrorMessage = 'There was a problem submitting your request';

		if (!res.ok) {
			return res.json().then((response) => {
				let errors = [];

				if (response.results) {
					errors = response.results.map(err => err.message);
				} else if (response.errors) {
					errors = response.errors.map(err => err.message);
				}

				if (!errors.length && response.message) {
					errors.push(response.message);
				} else if (!errors.length) {
					errors.push(defaultErrorMessage);
				}

				dispatch({
					type: types.FIRST_ASSIGNMENT_FORM_SUBMIT_ERROR,
					error: errors
				});
			});
		}

		return res.json().then(() => {
			dispatch({
				type: types.FIRST_ASSIGNMENT_FORM_SUBMIT_SUCCESS
			});
		});
	});
};

export const processAssignmentPrintoutUpload = (
	id,
	uuid
) => (dispatch) => {
	dispatch({
		type: types.BULK_PROCESS_EMPLOYEES_START,
		id,
		uuid
	});

	const body = new FormData();
	const description = `Assignment Printout (${id})`;

	body.append('data', `upload_uuid=${uuid}&description=${description}`);

	return fetch('/filemanager/add', {
		method: 'POST',
		credentials: 'same-origin',
		body,
		headers: new Headers({
			'X-CSRF-Token': Application.CSRFToken
		})
	})
	.then((res) => {
		const defaultErrorMessage = 'There was an error in processing the file. Please upload the file again.';

		if (!res.ok) {
			return res.json().then((response) => {
				let errors = [];

				if (response.errors) {
					errors = response.errors;
				}

				if (!errors.length) {
					errors.push(defaultErrorMessage);
				}

				dispatch({
					type: types.ASSIGNMENT_PRINTOUT_UPLOAD_ERROR,
					id,
					error: errors
				});
			});
		}

		return res.json().then(() => {
			dispatch({
				type: types.ASSIGNMENT_PRINTOUT_PROCESS_SUCCESS,
				id
			});
		});
	});
};

// TODO: Make generic
export const onAssignmentPrintoutUpload = file => (dispatch) => {
	const upload = new FormData();
	const xhr = new XMLHttpRequest();
	const id = `${file.name}_${Date.now()}`;

	upload.append('qqfile', file);

	xhr.upload.addEventListener('progress', (event) => {
		if (event.lengthComputable) {
			const progress = Math.round((event.loaded * 100) / event.total);

			dispatch({
				type: types.ASSIGNMENT_PRINTOUT_UPLOAD_PROGRESS,
				file,
				id,
				progress
			});
		} else {
			dispatch({
				type: types.ASSIGNMENT_PRINTOUT_UPLOAD_PROGRESS,
				file,
				id,
				progress: false
			});
		}
	}, false);

	xhr.upload.addEventListener('error', () => {
		dispatch({
			type: types.ASSIGNMENT_PRINTOUT_UPLOAD_ERROR,
			file,
			id
		});
	}, false);

	xhr.upload.addEventListener('load', () => {
		dispatch({
			type: types.ASSIGNMENT_PRINTOUT_UPLOAD_SUCCESS,
			id
		});
	}, false);

	xhr.open('POST', '/upload/uploadqq');
	xhr.responseType = 'json';
	xhr.setRequestHeader('X-CSRF-Token', Application.CSRFToken);
	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			dispatch({
				type: types.ASSIGNMENT_PRINTOUT_UPLOAD_SUCCESS,
				id
			});

			const uuid = xhr.response.uuid;
			if (uuid) {
				dispatch(processAssignmentPrintoutUpload(id, uuid));
			} else {
				dispatch({
					type: types.ASSIGNMENT_PRINTOUT_UPLOAD_ERROR,
					id
				});
			}
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status !== 200) {
			dispatch({
				type: types.ASSIGNMENT_PRINTOUT_UPLOAD_ERROR,
				id
			});
		}
	};

	xhr.send(upload);

	dispatch({
		type: types.ASSIGNMENT_PRINTOUT_UPLOAD_START,
		file,
		id,
		abort: () => xhr.abort()
	});
};

export const cancelAssignmentPrintoutUpload = id => (dispatch, getState) => {
	const state = getState().files;
	state.getIn([id, 'abort'])();

	dispatch({
		type: types.ASSIGNMENT_PRINTOUT_UPLOAD_ABORT,
		id
	});
};

export const removeAssignmentPrintoutUpload = id => (dispatch) => {
	dispatch({
		type: types.ASSIGNMENT_PRINTOUT_UPLOAD_REMOVE,
		id
	});
};
