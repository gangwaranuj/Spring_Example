import $ from 'jquery';
import _ from 'underscore';
import 'jquery-ui';
import fetch from 'isomorphic-fetch';
import React from 'react';
import ReactDOM from 'react-dom';
import { combineReducers, applyMiddleware, createStore } from 'redux';
import { createLogger } from 'redux-logger';
import { Provider } from 'react-redux';
import ReduxThunk from 'redux-thunk';
import '../config/datepicker';
import BasicContainer from './containers/basic';
import BasicReducer from './reducers/basic';
import CreationContainer from './containers/creation';
import * as CreationActions from './actions/creation';
import * as CreationTypes from './constants/creationActionTypes';
import {
	creationUserConfig,
	isSavingTemplate,
	templateInfoReducer,
	saveMode,
	numberOfCopies,
	errors,
	assignmentStatus
} from './reducers/creation';
import AddClientLocationContainer from '../addressbook/containers/add_client_location';
import AddClientLocationReducer from '../addressbook/reducers/add_client_location';
import ProjectIdReducer from '../addressbook/reducers/project';
import Schedule from '../schedule/main';
import Recurrence from '../recurrence';
import initialRecurState from '../recurrence/reducers/initialState';
import Surveys from '../surveys/main';
import AddPricingContainer from '../payments/containers/add_pricing';
import PricingReducer from '../payments/reducers/pricing';
import Routing from '../routing/main';
import RequirementSets from '../requirementsets/main';
import Deliverables from '../deliverables/main';
import Shipments from '../shipments/main';
import Followers from '../followers/main';
import CustomFields from '../customfields/main';
import Documents from '../documents/main';
import wmModal from '../funcs/wmModal';
import locationActions from '../addressbook/actions/location';

const { updateClientCompanyId, updateProjectId } = locationActions;

const fetchEntitlements = () => {
	return fetch('/featureEntitlements', { credentials: 'same-origin' })
		.then(res => res.json())
		.then(data => data)
		.catch((err) => {
			console.error('There was an error retrieving entitlements in the creation modal.', err);
			return false;
		});
};

const modules = [
	{
		id: 'basic',
		title: 'The Basics',
		Component: BasicContainer,
		reducer: BasicReducer,
		isEnabled: true,
		optional: false
	},
	{
		id: 'location',
		title: 'Location',
		Component: AddClientLocationContainer,
		reducer: AddClientLocationReducer,
		isEnabled: true,
		optional: false
	},
	{
		id: 'schedule',
		title: 'Scheduling',
		Component: Schedule.AddScheduleContainer,
		reducer: Schedule.ScheduleReducer,
		isEnabled: true,
		optional: false
	},
	{
		id: 'recurrence',
		title: 'Assignment Frequency',
		Component: Recurrence.RecurrenceContainer,
		reducer: Recurrence.RecurrenceReducer,
		isEnabled: false,
		optional: true
	},
	{
		id: 'pricing',
		title: 'Pricing',
		Component: AddPricingContainer,
		reducer: PricingReducer,
		isEnabled: true,
		optional: false
	},
	{
		id: 'followerIds',
		title: 'Followers',
		Component: Followers.AddFollowersContainer,
		reducer: Followers.FollowersReducer,
		isEnabled: false,
		optional: true
	},
	{
		id: 'deliverablesGroup',
		title: 'Deliverables',
		Component: Deliverables.AddDeliverablesContainer,
		reducer: Deliverables.DeliverablesReducer,
		isEnabled: true,
		optional: true
	},
	{
		id: 'documents',
		title: 'Documents',
		Component: Documents.AddDocumentsContainer,
		reducer: Documents.DocumentsReducer,
		isEnabled: true,
		optional: true
	},
	{
		id: 'surveys',
		title: 'Surveys',
		Component: Surveys.AddSurveysContainer,
		reducer: Surveys.SurveysReducer,
		isEnabled: false,
		optional: true
	},
	{
		id: 'requirementSetIds',
		title: 'Requirement Sets',
		Component: RequirementSets.AddRequirementsContainer,
		reducer: RequirementSets.RequirementsReducer,
		isEnabled: false,
		optional: true
	},
	{
		id: 'customFieldGroups',
		title: 'Custom Fields',
		Component: CustomFields.AddCustomFieldsContainer,
		reducer: CustomFields.CustomFieldsReducer,
		isEnabled: false,
		optional: true
	},
	{
		id: 'shipmentGroup',
		title: 'Shipments',
		Component: Shipments.AddShipmentsContainer,
		reducer: Shipments.ShipmentsReducer,
		isEnabled: false,
		optional: true
	},
	{
		id: 'routing',
		title: 'Routing',
		Component: Routing.AddRoutingContainer,
		reducer: Routing.RoutingReducer,
		isEnabled: true,
		optional: false
	},
	{
		id: 'moduleSwitcher',
		title: '+ Add Modules',
		isEnabled: true,
		Component: null,
		optional: false
	}
];

const HYDRATE_STATE = 'HYDRATE_STATE';
function makeHydratable (reducer, hydrateActionType) {
	return (state, action) => {
		switch (action.type) {
		case hydrateActionType:
			return reducer(action.state, action);
		default:
			return reducer(state, action);
		}
	};
}

const assignmentReducers = modules
	.filter(module => module.id !== 'basic' && module.id !== 'moduleSwitcher')
	.reduce((memo, { id, reducer }) => {
		memo[id] = reducer;
		return memo;
	}, modules.find(module => module.id === 'basic').reducer);
assignmentReducers.configuration = creationUserConfig;
assignmentReducers.isSavingTemplate = isSavingTemplate;
assignmentReducers.projectId = ProjectIdReducer;
assignmentReducers.templateInfo = templateInfoReducer;
assignmentReducers.saveMode = saveMode;
assignmentReducers.numberOfCopies = numberOfCopies;
assignmentReducers.assignmentStatus = assignmentStatus;
assignmentReducers.errors = errors;
const creationReducers = combineReducers(assignmentReducers);
const hydratableReducer = makeHydratable(creationReducers, HYDRATE_STATE);
const middlewares = [ReduxThunk];
if (process.env.NODE_ENV === 'development') {
	const logger = createLogger();
	middlewares.push(logger);
}
const store = createStore(hydratableReducer,
	window.devToolsExtension ? window.devToolsExtension() : f => f,
	applyMiddleware(...middlewares));
let creationComponentContainer;

// API currently returns wrong assignment configuration
// TODO: get rid of this once the API returns the correct values
const validateStateConfig = (newState) => {
	const validState = newState;
	// TODO: enable this once deliverables are returned by the API
	// validState.configuration.deliverablesEnabled = newState.deliverables.length ? true : false;
	validState.configuration.deliverablesEnabled = newState.deliverablesGroup.deliverables.length > 0;
	validState.configuration.followersEnabled = newState.followerIds.length > 0;
	validState.configuration.customFieldsEnabled = newState.customFieldGroups.length > 0;
	validState.configuration.surveysEnabled = newState.surveys.length > 0;
	validState.configuration.documentsEnabled = newState.configuration.documentsEnabled;
	validState.configuration.shipmentsEnabled = newState.configuration.shipmentsEnabled;
	validState.configuration.requirementSetsEnabled = newState.requirementSetIds.length > 0;
	validState.configuration.uniqueExternalIdEnabled = Boolean(newState.uniqueExternalId);

	return validState;
};

const constrainStatePricing = (state) => {
	const pricing = state.pricing;

	switch (pricing.type) {
	case 'FLAT':
		pricing.flatPrice = pricing.flatPrice;
		break;
	case 'PER_HOUR':
		pricing.perHourPrice = pricing.perHourPrice;
		break;
	case 'PER_UNIT':
		pricing.perUnitPrice = pricing.perUnitPrice;
		break;
	case 'BLENDED_PER_HOUR':
		pricing.additionalPerHourPrice = pricing.additionalPerHourPrice;
		pricing.initialPerHourPrice = pricing.initialPerHourPrice;
		break;
	default:
		break;
	}

	return state;
};

export const loadAssignment = (state, mode, templateId) => {
	let validState;
	const validateState = _.compose(validateStateConfig, constrainStatePricing);
	const initialState = {
		name: null,
		addressLine1: null,
		addressLine2: null,
		city: null,
		state: null,
		country: null,
		zip: null
	};
	if (state) {
		state.recurrence = initialRecurState;
		state.shipmentGroup.shipToAddress = state.shipmentGroup.shipToAddress === null
			? Object.assign({}, initialState, state.shipmentGroup.shipToAddress)
			: state.shipmentGroup.shipToAddress;
		state.shipmentGroup.returnAddress = state.shipmentGroup.returnAddress === null
			? Object.assign({}, initialState, state.shipmentGroup.returnAddress)
			: state.shipmentGroup.returnAddress;
		state.location.contact = !state.location.contact ? {} : state.location.contact;
		state.location.secondaryContact = !state.location.secondaryContact
			? {}
			: state.location.secondaryContact;
		state.templateInfo = { id: templateId };
		state.numberOfCopies = !state.numberOfCopies ? 1 : state.numberOfCopies;
		state.routing.isValid = !state.routing.isValid;
		state.errors = !state.errors ? {} : state.errors;
		state.saveMode = mode;
		validState = validateState(state);
	} else {
		validState = state;
	}

	CreationActions.updateTemplateId(templateId);
	store.dispatch({
		type: HYDRATE_STATE,
		state: validState
	});
};

const sendAssignmentModalAnalytics = (message, props = {}) => {
	window.analytics.track(message, props);
};

export const initialize = () => {
	$('body').datepicker({ dateFormat: 'mm/dd/yy' });

	this.initModal();
};

class CreationModal {
	saveButtonText = 'Save as Draft';
	routeButtonText = 'Save + Route';
	/**
	 * @param options {Object}
	 * @param options.assignmentId {String} The ID of an assignment to load (i.e., edit flow)
	 * @param options.title {String} The title of the assignment modal -- Warning the title also controls some behaviors like loading an assignment, or template
	 * @param options.clientCompanyId {String} The ID of the client company to select when the modal loads
	 * @param options.projectId {String} The ID of the project to select when the modal loads
	 */
	constructor (options = {}) {
		this.config = Object.assign({}, {
			assignmentId: '',
			title: 'Add Assignment'
		}, options);

		// Hydrate the store with the provided clientCompanyId -- This will select the correct ID within the dropdown
		if (this.config.clientCompanyId) {
			store.dispatch(updateClientCompanyId(this.config.clientCompanyId));
		}

		// Hydrate the store with the provided projectId -- This will select the correct ID within the dropdown
		if (this.config.projectId) {
			store.dispatch(updateProjectId(this.config.projectId));
		}

		if (this.config.title === 'Edit Assignment') {
			this.saveButtonText = 'Update Assignment';
			this.routeButtonText = 'Update + Route';
		}

		this.config.assignmentId = this.config.assignmentId.toString();
		this.initialize();

		if (this.config.title === 'Edit Assignment') {
			fetch(`/employer/v2/assignments/${this.config.assignmentId}`, {
				credentials: 'same-origin'
			})
				.then(res => res.json())
				.then((res) => {
					loadAssignment(res.results[0], 'update');
				});
		} else if (this.config.title === 'Copy Assignment') {
			fetch(`/employer/v2/assignments/${this.config.assignmentId}`, {
				credentials: 'same-origin'
			})
				.then(res => res.json())
				.then((res) => {
					loadAssignment(res.results[0], 'copy');
				});
		} else if (this.config.title === 'Edit Template') {
			loadAssignment(this.config.assignment, 'new', this.config.templateId);
		} else {
			fetch('/employer/v2/assignments/', {
				credentials: 'same-origin'
			})
				.then(res => res.json())
				.then((res) => {
					loadAssignment(res.results[0], 'new');
				});
		}
	}

	initialize () {
		// pop in the datepicker in case the page doesn't already have it
		$('body').datepicker({ dateFormat: 'mm/dd/yy' });

		this.initModal();
		if (this.config.title === 'Copy Assignment') {
			$('.assignment-creation--templates').addClass('-hide');
		} else {
			$('.assignment-creation--copies').addClass('-hide');
		}
	}

	initModal () {
		const assignmentId = this.config.assignmentId;
		const analyticsMessage = 'Assignment Creation Modal';
		const modalTitle = this.config.title;
		// This needs to be removed later. Used to conditionally render recurrence.
		// PR by @Qeezus in R18 should fix.
		fetchEntitlements()
			.then((myEntitlements) => {
				if ((modalTitle === 'Add Assignment' || modalTitle === 'Edit Template') && myEntitlements && myEntitlements['recurrence.feature.toggle'] && myEntitlements['recurrence.feature.toggle'] !== 'false') {
					modules.forEach((module) => {
						if (module.id === 'recurrence') {
							module.isEnabled = true; // eslint-disable-line
						}
					});
				} else {
					modules.forEach((module) => {
						if (module.id === 'recurrence') {
							module.isEnabled = false; //eslint-disable-line
						}
					});
				}
			});

		const self = this;
		self.modal = wmModal({
			title: modalTitle,
			autorun: true,
			fixedScroll: true,
			sidebar: true,
			fullHeight: true,
			destroyOnClose: true,
			classList: 'assignment-creation-modal',
			customHandlers: [
				{
					event: 'click',
					selector: '.wm-modal--control.-route',
					callback: () => {
						const analyticsProps = {
							assignmentId,
							action: 'Save + Route button clicked'
						};
						sendAssignmentModalAnalytics(analyticsMessage, analyticsProps);

						store.dispatch(CreationActions.updateAssignmentStatus('sent'));
						store.dispatch(CreationActions.sendAssignment(self.modal));
					}
				},
				{
					event: 'click',
					selector: '.wm-modal--control.-save-draft',
					callback: () => {
						const analyticsProps = {
							assignmentId,
							action: 'Save As Draft button clicked'
						};
						sendAssignmentModalAnalytics(analyticsMessage, analyticsProps);
						store.dispatch(CreationActions.updateAssignmentStatus('draft'));
						store.dispatch(CreationActions.saveDraftAssignment(self.modal));
					}
				},
				{
					event: 'click',
					selector: '.wm-modal--control.-save-template',
					callback: () => {
						sendAssignmentModalAnalytics(analyticsMessage, {
							action: 'Save as Template clicked'
						});

						store.dispatch(CreationActions.toggleTemplateModal());
					}
				},
				{
					event: 'modal-destroy',
					callback: () => {
						sendAssignmentModalAnalytics(analyticsMessage, {
							action: 'Modal closed'
						});
						ReactDOM.unmountComponentAtNode(creationComponentContainer);
						let blankState;
						loadAssignment(blankState);
					}
				}
			],
			controls: [
				{
					text: 'Save as Template',
					sidebar: true,
					classList: '-save-template'

				},
				{
					text: this.saveButtonText,
					close: false,
					classList: '-save-draft',
					primary: true
				},
				{
					text: this.routeButtonText,
					primary: true,
					classList: '-route'
				}
			]
		});

		creationComponentContainer = document.querySelector('.wm-modal--content');
		ReactDOM.render((
			<Provider store={ store }>
				<CreationContainer
					modules={ modules }
					loadTemplateData={ loadAssignment }
					modal={ self.modal }
					scrollToTab={ this.config.scrollToTab }
				/>
			</Provider>
		), creationComponentContainer);
	}
}

export default CreationModal;
