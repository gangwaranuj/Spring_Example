import ReactDOM from 'react-dom';
import $ from 'jquery';
import Application from '../../core';
import wmNotify from '../../funcs/wmNotify';
import recurInitialState from '../../recurrence/reducers/initialState';
import * as types from '../constants/creationActionTypes';

class Assignment {
	constructor (state) {
		if (state.id !== 0) {
			this.id = state.id;
		}
		this.numberOfCopies = state.numberOfCopies;
		this.errors = state.errors;
		this.saveMode = state.saveMode;
		this.assignmentStatus = state.assignmentStatus;
		// Basics
		this.title = state.title;
		if (state.description && state.description.length) {
			this.description = state.description;
		}
		if (state.skills && state.skills.length) {
			this.skills = state.skills;
		}

		this.industryId = state.industryId;

		if (state.ownerId && state.ownerId.length) {
			this.ownerId = state.ownerId;
		}
		if (state.projectId) {
			this.projectId = state.projectId;
		}
		if (state.supportContactId) {
			this.supportContactId = state.supportContactId.toString();
		}
		if (state.instructions && state.instructions.length) {
			this.instructions = state.instructions;
		}
		this.instructionsPrivate = state.instructionsPrivate;
		if (state.uniqueExternalId && state.uniqueExternalId.length) {
			this.uniqueExternalId = state.uniqueExternalId;
		}

		// Location
		if ((!state.location || !state.location.addressLine1) && !state.location.id) {
			this.location = { id: 0 };
			if (state.location.clientCompanyId) {
				this.location.clientCompanyId = state.location.clientCompanyId;
			}
		} else if (state.location.id) {
			this.location = { id: state.location.id };
			Object.keys(state.location).forEach((key) => {
				if (key !== 'contact' && key !== 'secondaryContact') {
					this.location[key] = state.location[key];
				} else if (state.location[key].firstName) {
					this.location[key] = state.location[key];
				}
			});
			if (state.location.contact.id) {
				this.location.contact = {};
				this.location.contact.id = state.location.contact.id;
			} else if (state.location.contact.firstName && state.location.contact.firstName.length > 0) {
				this.location.contact = state.location.contact;
			}
			if (state.location.secondaryContact.id) {
				this.location.secondaryContact = {};
				this.location.secondaryContact.id = state.location.secondaryContact.id;
			} else if (
				state.location.secondaryContact.firstName
				&& state.location.secondaryContact.firstName.length > 0
			) {
				this.location.secondaryContact = state.location.secondaryContact;
			}
			if (state.location.clientCompanyId) {
				this.location.clientCompanyId = state.location.clientCompanyId;
			}
		} else {
			this.location = {};
			Object.keys(state.location).forEach((key) => {
				if (key !== 'contact' && key !== 'secondaryContact') {
					this.location[key] = state.location[key];
				} else if (state.location[key].firstName) {
					this.location[key] = state.location[key];
				}
			});
		}

		// Pricing
		// TODO: once API is updated to not require default values for this,
		// update how price is checked and sent
		const priceConfig = this.getPriceConfig(state);
		if (priceConfig.isPriceSet) {
			this.pricing = priceConfig;
		}

		// Scheduling
		const schedule = state.schedule;
		this.schedule = {};
		if (schedule.from) {
			this.schedule.range = schedule.range;
			this.schedule.from = schedule.from;
		}
		if (schedule.range) {
			this.schedule.through = schedule.through;
		}
		this.schedule.confirmationRequired = schedule.confirmationRequired;
		if (schedule.confirmationRequired) {
			this.schedule.confirmationLeadTime = schedule.confirmationLeadTime;
		}
		this.schedule.checkinRequired = schedule.checkinRequired;
		this.schedule.checkinCallRequired = schedule.checkinCallRequired;
		if (schedule.checkinCallRequired) {
			if (schedule.checkinContactName && schedule.checkinContactName.length) {
				this.schedule.checkinContactName = schedule.checkinContactName;
			}
			if (schedule.checkinContactPhone && schedule.checkinContactPhone.length) {
				this.schedule.checkinContactPhone = schedule.checkinContactPhone;
			}
		}
		if (schedule.checkoutNoteDisplayed) {
			this.schedule.checkoutNoteDisplayed = schedule.checkoutNoteDisplayed;
			this.schedule.checkoutNote = schedule.checkoutNote;
		}

		if (state.followerIds && state.followerIds.length) {
			this.followerIds = state.followerIds;
		}

		// Recurrence
		if (state.recurrence && state.recurrence.get('type') === 'Recur') {
			const {
				frequency,
				endDate,
				repetitions,
				days,
				validity,
				endType,
				frequencyModifier
			} = state.recurrence.toJS();
			if (endType === 'Date') {
				this.recurrence = {
					type: frequency.toUpperCase(),
					endDate: endDate.format('MM/DD/YYYY'),
					frequencyModifier,
					description: validity.reason,
					weekdays: days
				};
			} else {
				this.recurrence = {
					type: frequency.toUpperCase(),
					frequencyModifier,
					repetitions,
					description: validity.reason,
					weekdays: days
				};
			}
		}

		// Deliverables
		if (state.deliverablesGroup.deliverables.length) {
			this.deliverablesGroup = Object.assign({}, state.deliverablesGroup);
		}

		// Documents
		if (state.documents.length) {
			this.documents = state.documents;
		}

		// Surveys
		if (state.surveys && state.surveys.length) {
			this.surveys = state.surveys;
		}

		// Requirment Sets
		if (state.requirementSetIds && state.requirementSetIds.length) {
			this.requirementSetIds = state.requirementSetIds;
		}

		// Custom Fields
		if (state.customFieldGroups && state.customFieldGroups.length) {
			this.customFieldGroups = state.customFieldGroups;
		}

		// Shipments
		this.shipmentGroup = state.shipmentGroup;

		// Routing
		this.routing = state.routing;

		// Configuration
		this.configuration = state.configuration;
	}

	// TODO - this will be used once API fixes pricing
	getPriceConfig = (state) => {
		let isPriceSet = false;
		const priceInfo = state.pricing;

		const priceConfig = {};

		switch (priceInfo.type) {
		case 'FLAT':
			isPriceSet = priceInfo.flatPrice > 0;
			if (isPriceSet) {
				priceConfig.type = 'FLAT';
				priceConfig.flatPrice = priceInfo.flatPrice;
			}
			break;
		case 'PER_HOUR':
			isPriceSet = priceInfo.perHourPrice > 0 && priceInfo.maxNumberOfHours;
			if (isPriceSet) {
				priceConfig.perHourPrice = priceInfo.perHourPrice;
				priceConfig.maxNumberOfHours = priceInfo.maxNumberOfHours;
			}
			break;
		case 'PER_UNIT':
			isPriceSet = priceInfo.perUnitPrice > 0 && priceInfo.maxNumberOfUnits;
			if (isPriceSet) {
				priceConfig.perUnitPrice = priceInfo.perUnitPrice;
				priceConfig.maxNumberOfUnits = priceInfo.maxNumberOfUnits;
			}
			break;
		case 'BLENDED_PER_HOUR':
			isPriceSet = priceInfo.initialPerHourPrice > 0
				&& priceInfo.initialNumberOfHours > 0
				&& priceInfo.additionalPerHourPrice > 0
				&& priceInfo.maxBlendedNumberOfHours > 0;
			if (isPriceSet) {
				priceConfig.initialPerHourPrice = priceInfo.initialPerHourPrice;
				priceConfig.initialNumberOfHours = priceInfo.initialNumberOfHours;
				priceConfig.additionalPerHourPrice = priceInfo.additionalPerHourPrice;
				priceConfig.maxBlendedNumberOfHours = priceInfo.maxBlendedNumberOfHours;
			}
			break;
		case 'INTERNAL':
			isPriceSet = true;
			break;
		default:
			break;
		}

		priceConfig.isPriceSet = isPriceSet;
		priceConfig.mode = priceInfo.mode;
		priceConfig.type = priceInfo.type;
		priceConfig.offlinePayment = priceInfo.offlinePayment;
		priceConfig.disablePriceNegotiation = priceInfo.disablePriceNegotiation;
		priceConfig.paymentTermsDays = priceInfo.paymentTermsDays;

		return priceConfig;
	}
}

class Template {
	constructor (assignment, state) {
		this.assignment = assignment;
		this.id = state.templateInfo.id;
		this.name = state.templateInfo.name;
		this.description = state.templateInfo.description;
	}
}

export const toggleTemplateModal = () => {
	return {
		type: types.TOGGLE_TEMPLATE_MODAL
	};
};

export const updateTemplateId = (value) => {
	return {
		type: types.UPDATE_TEMPLATE_ID,
		value
	};
};

export const updateTemplateName = (value) => {
	return {
		type: types.UPDATE_TEMPLATE_NAME,
		value
	};
};

export const updateTemplateDescription = (value) => {
	return {
		type: types.UPDATE_TEMPLATE_DESCRIPTION,
		value
	};
};

export const updateUserConfig = (userConfig) => {
	return {
		type: types.UPDATE_USER_CONFIG,
		userConfig
	};
};

export const updateAssignmentStatus = (value) => {
	return {
		type: types.UPDATE_ASSIGNMENT_STATUS,
		value
	};
};

export const updateNumberOfCopies = (value) => {
	return {
		type: types.UPDATE_NUMBER_OF_COPIES,
		value
	};
};

export const updateSaveMode = (value) => {
	return {
		type: types.UPDATE_SAVE_MODE,
		value
	};
};

export const updateErrors = (value) => {
	return {
		type: types.UPDATE_ERRORS,
		value
	};
};

export const sendAssignmentAnalytics = (props) => {
	const message = 'Assignment Creation';
	window.analytics.track(message, props);
};

export const fetchTemplate = (templateId, templateUrl = '/employer/v2/assignments/templates/') => {
	return (dispatch) => {
		return fetch(`${templateUrl}/${templateId}`, {
			credentials: 'same-origin'
		})
			.then(res => res.json())
			.then((res) => {
				const templateResponse = res.results[0].assignment;
				const checkStateConfig = (newState) => {
					const validState = newState;
					// Recurrence Should not Save State to template at present
					// so I default it to its intial state.
					validState.recurrence = recurInitialState;

					// if this has an ID, but config is turned off, need to force it back on to display
					// TODO[tim-mc]: move this to draft/save+route
					// validState.configuration.uniqueExternalIdEnabled = newState.uniqueExternalId;

					// these fields can't be null when passed to redux state, change to empty objs if so
					// TODO[tim-mc]: move this to its own func
					validState.location = newState.location || {};
					validState.location.id = newState.location.id || '';
					validState.location.name = newState.location.name || '';
					validState.location.number = newState.location.number || '';
					validState.location.addressLine1 = newState.location.addressLine1 || '';
					validState.location.addressLine2 = newState.location.addressLine2 || '';
					validState.location.city = newState.location.city || '';
					validState.location.state = newState.location.state || '';
					validState.location.country = newState.location.country || '';
					validState.location.zip = newState.location.zip || '';
					validState.location.contact = newState.location.contact || {};
					validState.location.secondaryContact = newState.location.secondaryContact || {};
					validState.configuration.customFieldsEnabled = newState.customFieldGroups.length > 0;
					validState.configuration.documentsEnabled = newState.configuration.documentsEnabled;
					validState.configuration.shipmentsEnabled = newState.configuration.shipmentsEnabled;
					validState.shipmentGroup.returnShipment = newState.shipmentGroup.returnShipment || false;
					validState.shipmentGroup.returnAddress = newState.shipmentGroup.returnAddress || {};
					validState.shipmentGroup.shipToAddress = newState.shipmentGroup.shipToAddress || {};
					validState.shipmentGroup.returnAddress.name
						= newState.shipmentGroup.returnAddress.name || null;
					validState.shipmentGroup.returnAddress.addressLine1
						= newState.shipmentGroup.returnAddress.addressLine1 || null;
					validState.shipmentGroup.returnAddress.addressLine2
						= newState.shipmentGroup.returnAddress.addressLine2 || null;
					validState.shipmentGroup.returnAddress.city
						= newState.shipmentGroup.returnAddress.city || null;
					validState.shipmentGroup.returnAddress.state
						= newState.shipmentGroup.returnAddress.state || null;
					validState.shipmentGroup.returnAddress.country
						= newState.shipmentGroup.returnAddress.country || null;
					validState.shipmentGroup.returnAddress.zip
						= newState.shipmentGroup.returnAddress.zip || null;
					validState.shipmentGroup.shipToAddress.name
						= newState.shipmentGroup.shipToAddress.name || null;
					validState.shipmentGroup.shipToAddress.addressLine1
						= newState.shipmentGroup.shipToAddress.addressLine1 || null;
					validState.shipmentGroup.shipToAddress.addressLine2
						= newState.shipmentGroup.shipToAddress.addressLine2 || null;
					validState.shipmentGroup.shipToAddress.city
						= newState.shipmentGroup.shipToAddress.city || null;
					validState.shipmentGroup.shipToAddress.state
						= newState.shipmentGroup.shipToAddress.state || null;
					validState.shipmentGroup.shipToAddress.country
						= newState.shipmentGroup.shipToAddress.country || null;
					validState.shipmentGroup.shipToAddress.zip
						= newState.shipmentGroup.shipToAddress.zip || null;
					return validState;
				};

				const formatCustomFields = (rawFieldGroups = []) => {
					return rawFieldGroups.map((fieldGroup) => {
						fieldGroup.fields.forEach((field) => {
							// eslint-disable-next-line no-param-reassign
							field.value = field.value.length > 0 ? field.value : field.defaultValue;
						});
						return fieldGroup;
					});
				};

				const formatDeliverableFields = (group = {}) => {
					const formatted = Object.assign({}, group, { id: null });

					formatted.deliverables = formatted.deliverables.map((deliverable) => {
						return Object.assign({}, deliverable, { id: null });
					});

					return formatted;
				};

				const formatShipmentFields = (group = {}) => {
					const formatted = Object.assign({}, group, { uuid: null });

					formatted.shipments = formatted.shipments.map((shipment) => {
						return Object.assign({}, shipment, { uuid: null });
					});

					return formatted;
				};

				const templateState = checkStateConfig(templateResponse);
				templateState.customFieldGroups = formatCustomFields(templateState.customFieldGroups);
				templateState.deliverablesGroup = formatDeliverableFields(templateState.deliverablesGroup);
				templateState.shipmentGroup = formatShipmentFields(templateState.shipmentGroup);

				dispatch({ type: 'HYDRATE_STATE', state: templateState });

				sendAssignmentAnalytics({
					version: '2',
					action: 'Template loaded',
					templateId: res.results[0].id,
					success: true
				});
			});
	};
};

export function sendAssignment () {
	return (dispatch, getState) => {
		const rawState = getState();
		const assignment = new Assignment(Object.assign({}, rawState));
		const showBrowse = rawState.routing.browseMarketplace;
		const draftOverride = showBrowse && !rawState.showInFeed;
		const isUpdate = rawState.saveMode === 'update';
		const isCopy = rawState.saveMode === 'copy';
		let url;
		if (isUpdate) {
			url = `/employer/v2/assignments${draftOverride ? '/drafts' : ''}/${rawState.id}`;
		} else if (isCopy) {
			url = `/employer/v2/assignments${draftOverride ? '/drafts' : `/multiple?numberOfCopies=${rawState.numberOfCopies}`}`;
		} else {
			url = `/employer/v2/assignments${draftOverride ? '/drafts' : ''}`;
		}
		$.ajax({
			type: 'POST',
			url,
			data: JSON.stringify(assignment),
			contentType: 'application/json',
			dataType: 'json',
			headers: {
				'X-CSRF-Token': Application.CSRFToken
			},
			beforeSend: (xhr) => { if (!rawState.routing.isValid) xhr.abort(); }
		})
		.success((response) => {
			const id = response.results[0].id;
			const newUrl = `/assignments/${showBrowse ? 'contact' : 'details'}/${id}`;
			const analyticsProps = {
				version: 'v2',
				assignmentId: id,
				action: 'Save + Route',
				success: true
			};

			$('.wm-modal--control.-route').addClass('-disabled');

			sendAssignmentAnalytics(analyticsProps);

			/*
			So this is a weird point in time - either I receive a dashboard link with
			preset filters here that point to only this recurrence, or I do it on the front
			end. If the back DOES send that link I need to pop a success banner here.
			*/
			window.location = newUrl;
		})
		.fail((response) => {
			let errors = [];

			if (response.responseJSON) {
				errors.push({ message: response.responseJSON.meta.message });
				if (response.responseJSON.results.length > 0) {
					response.responseJSON.results.forEach((result) => {
						errors.push({ message: result.message });
					});
				}
			} else {
				// TODO - better message here?
				errors.push({ message: 'Something went wrong while saving the assignment' });
			}

			const analyticsProps = {
				version: '2',
				action: 'Save + Route',
				success: false,
				errors
			};
			sendAssignmentAnalytics(analyticsProps);
			dispatch(updateAssignmentStatus('sent'));
			dispatch(updateErrors(errors));
			errors.forEach((error) => {
				wmNotify({
					message: error.message,
					type: 'danger'
				});
			});
		});
	};
}

export function saveDraftAssignment () {
	return (dispatch, getState) => {
		const rawState = getState();
		const assignment = new Assignment(Object.assign({}, rawState));
		const isUpdate = rawState.saveMode === 'update';
		const isCopy = rawState.saveMode === 'copy';
		let url;
		if (isUpdate) {
			url = `/employer/v2/assignments/drafts/${rawState.id}`;
		} else if (isCopy) {
			url = `/employer/v2/assignments/drafts/multiple?numberOfCopies=${rawState.numberOfCopies}`;
		} else {
			url = '/employer/v2/assignments/drafts/';
		}

		$.ajax({
			type: 'POST',
			url,
			data: JSON.stringify(assignment),
			contentType: 'application/json',
			dataType: 'json',
			headers: {
				'X-CSRF-Token': Application.CSRFToken
			}
		})
			.success((response) => {
				const id = response.results[0].id;
				const analyticsProps = {
					version: '2',
					assignmentId: id,
					action: 'Save as draft',
					success: true
				};
				const newUrl = `/assignments/details/${id}`;

				$('.wm-modal--control.-save-draft').addClass('-disabled');

				sendAssignmentAnalytics(analyticsProps);

				window.location = newUrl;
			})
			.fail((response) => {
				const results = response.responseJSON.results;
				let errors = [];

				if (response.responseJSON.results) {
					errors = results.map((error) => {
						return error;
					});
				} else {
					// TODO - better message here?
					errors.push({ message: 'Something went wrong while saving the assignment' });
				}
				$('.wm-modal--control.-save-draft').removeClass('-disabled');

				const analyticsProps = {
					version: '2',
					action: 'Save as draft',
					success: false,
					errors
				};

				sendAssignmentAnalytics(analyticsProps);
				dispatch(updateAssignmentStatus('draft'));
				dispatch(updateErrors(errors));
				errors.forEach((error) => {
					wmNotify({
						message: error.message,
						type: 'danger'
					});
				});
			});
	};
}

export function saveTemplate (creationModal) {
	return (dispatch, getState) => {
		const rawState = getState();
		const assignment = new Assignment(Object.assign({}, rawState));
		const template = new Template(assignment, Object.assign({}, rawState));

		$.ajax({
			type: 'POST',
			url: '/employer/v2/assignments/templates',
			data: JSON.stringify(template),
			contentType: 'application/json',
			dataType: 'json',
			headers: {
				'X-CSRF-Token': Application.CSRFToken
			}
		})
			.success((response) => {
				const templateId = response.results[0].id;
				const analyticsProps = {
					version: '2',
					action: 'Template saved',
					success: true,
					templateId
				};

				sendAssignmentAnalytics(analyticsProps);

				const creationComponentNode = document.querySelector('.wm-modal--content');
				dispatch(toggleTemplateModal());
				ReactDOM.unmountComponentAtNode(creationComponentNode);
				creationModal.destroy();

				wmNotify({
					message: 'Assignment template saved successfully'
				});
			})
			.fail((response) => {
				const results = response.responseJSON.results;
				const errors = [];

				if (results && Array.isArray(results) && results.length > 0) {
					results.forEach((messageObj) => {
						errors.push(messageObj.message);
					});
				} else {
					// TODO - better message here?
					errors.push('Something went wrong while saving the template - please check for missing required fields');
				}

				const analyticsProps = {
					version: '2',
					action: 'Save as Template',
					success: false,
					errors
				};

				sendAssignmentAnalytics(analyticsProps);

				dispatch(updateErrors(errors));
				errors.forEach((error) => {
					wmNotify({
						message: error,
						type: 'danger'
					});
				});
			});
	};
}
