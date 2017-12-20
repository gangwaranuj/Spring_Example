import fetch from 'isomorphic-fetch';
import { List } from 'immutable';
import Application from '../../core';
import * as types from '../constants/actionTypes';

const NEW_APPROVAL_TEMPLATE_ID = 0;
const requestHeader = {
	credentials: 'same-origin',
	headers: new Headers({
		'Content-Type': 'application/json',
		'X-CSRF-Token': Application.CSRFToken,
		'Data-Type': 'json'
	})
};

const approvalsRequestRoot = '/v2/decision_flow';

const makeActionCreator = (type, ...argNames) => (...args) => {
	const action = { type };

	argNames.forEach((arg, index) => {
		action[argNames[index]] = args[index];
	});

	return action;
};

const defaultNewStep = {
	id: 0,
	name: 'New Step',
	orderId: 0,
	selectedApprovers: List(),
	selectedOption: 1
};
export const createApprovalConfig = makeActionCreator(types.CREATE_APPROVAL_CONFIG);
export const onAddStep = makeActionCreator(types.ADD_STEP, 'value');
export const onRemoveStep = makeActionCreator(types.REMOVE_STEP, 'value');
export const onAddApprover = makeActionCreator(types.ADD_APPROVER, 'value');
export const onRemoveApprover = makeActionCreator(types.REMOVE_APPROVER, 'value');
export const onChangeStepTitle = makeActionCreator(types.CHANGE_STEP_TITLE, 'value', 'step');
export const onChangeOption = makeActionCreator(types.CHANGE_OPTION, 'value');
export const getEmployeeList = (
	companyId = Number.parseInt(Application.UserInfo.companyId, 10),
	url = `/companies/${companyId}/employees`
) => async (dispatch) => {
	try {
		const employeeList = await fetch(url, requestHeader).then(res => res.json());
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

export const fetchApprovals = (
	approvalsTemplateId,
	url = `${approvalsRequestRoot}/approvalsTemplateId`
) => async (dispatch) => {
	try {
		const approvalSteps = await fetch(url, requestHeader)
			.then(res => res.json())
			.then(res => res.results);
		if (approvalSteps.size > 0) {
			dispatch({
				type: types.FETCH_APPROVALS_SUCCESS,
				value: approvalSteps
			});
		} else {
			dispatch(onAddStep(defaultNewStep));
		}
	} catch (e) {
		dispatch({
			type: types.APPROVALS_ERROR,
			error: e
		});
	}
};

export const fetchApprovalConfiguration = (
	companyNumber = Number.parseInt(Application.UserInfo.companyNumber, 10),
	url = `${approvalsRequestRoot}/list/companyNumber/${companyNumber}`
) => async (dispatch) => {
	try {
		const approvalsTemplateId = await fetch(url, requestHeader)
			.then(res => res.json())
			.then(res => res.results);
		if (approvalsTemplateId !== NEW_APPROVAL_TEMPLATE_ID) {
			fetchApprovals(approvalsTemplateId);
		}
	} catch (e) {
		dispatch({
			type: types.APPROVALS_ERROR,
			error: e
		});
	}
};

export const createStep = (step = defaultNewStep) => async (dispatch, getState) => {
	try {
		const store = getState();
		const approvalSteps = store.approvals.get('approvalSteps');
		if (approvalSteps.size === 0) {
			dispatch(onAddStep(step));
		}
	} catch (e) {
		dispatch({
			type: types.APPROVALS_ERROR,
			error: e
		});
	}
};

// post example
// {
// 	"name": "approval flow name",
// 	"namespace": "maybe the company UUID?",
// 	"description": "might be optional, I forgot",
// 	"decisionSteps": [{
// 	  "name": "step 1",
// 	  "sequence": 1,
// 	  "quorumType": "UNANIMOUS",
// 	  "decisions": [{
// 		"uuid": "USER UUID"
// 	  }]
// 	}]
//   }

/** Workflow defaults to activate when configuration is saved or udpated. With no workflow set, toggle is diabled */
export const onToggleActivation = () => async (dispatch, getState) => {
	const store = getState();
	const approvalTemplateId = store.get('approvalTemplateId');
	if (approvalTemplateId === 0) {
		console.log('Toggle should be disabled. Wierd state.');
	}
	const activateUrl = `${approvalsRequestRoot}/activate/${approvalTemplateId}`;
	const deactivateUrl = `${approvalsRequestRoot}/deactivate/${approvalTemplateId}`;
	const isActivated = store.approvals.get('isActivated');
	if (isActivated) {
		try {
			const response = await fetch(activateUrl, requestHeader)
				.then(res => res.json())
				.then(res => res.results);
			if (response.successful) {
				dispatch({
					type: types.TOGGLE_ACTIVATION,
					value: true
				});
			}
		} catch (e) {
			dispatch({
				type: types.APPROVALS_ERROR,
				value: e
			});
		}
	} else {
		try {
			const response = await fetch(deactivateUrl, requestHeader)
				.then(res => res.json())
				.then(res => res.results);
			if (response.successful) {
				dispatch({
					type: types.TOGGLE_ACTIVATION,
					value: false
				});
			}
		} catch (e) {
			dispatch({
				type: types.APPROVALS_ERROR,
				value: e
			});
		}
	}
};

export const onSaveConfig = (
	approvalTemplateId = NEW_APPROVAL_TEMPLATE_ID,
	createUrl = `${approvalsRequestRoot}/create`,
	updateUrl = `${approvalsRequestRoot}/update/${approvalTemplateId}`
) => async (dispatch, getState) => {
	try {
		const approvals = getState().approvals.toJS();
		// if _.undefined(approvals.approvalSteps) then save button is disabled
		const decisionSteps = approvals.approvalSteps.map(step => ({
			name: step.name,
			sequence: step.orderId,
			quorumType: 'UNANIMOUS',
			decisions: step.selectedApprovers.toJS()
		}));
		const renameProp = (obj, toKey, fromKey) => {
			obj[toKey] = obj[fromKey];
			delete obj[fromKey];
			return obj;
		};
		decisionSteps.map((decisionStep) => {
			const transformedDecisions = [];
			decisionStep.decisions.map((decider) => {
				const newdecider = { decider: { uuid: decider.id } };
				transformedDecisions.push(newdecider);
			});
			decisionStep.decisions = transformedDecisions; // eslint-ignore-line
		});
		const approvalsPostBody = {
			name: 'Assignment Approval',
			description: 'Assignment Approval',
			namespace: 'assignments',
			decisionSteps
		};
		const postUrl = approvalTemplateId === NEW_APPROVAL_TEMPLATE_ID ? createUrl : updateUrl;
		// const postBody = Object.assign({}, requestHeader, { method: 'POST', body: approvalsPostBody });
		const response = await fetch(postUrl, {
			method: 'POST',
			credentials: 'same-origin',
			headers: requestHeader,
			body: JSON.stringify(approvalsPostBody)
		}).then(res => res.json());
		/**  // approvalTemplateId is captured to associate all relative actions to existing flow */
		if (response.successful) {
			const { templateId } = response;
			dispatch({
				type: types.CREATE_APPROVAL_CONFIG,
				value: templateId
			});
		}
	} catch (e) {
		dispatch({
			type: types.APPROVALS_ERROR,
			value: e
		});
	}
};

export const createApprovalFlow = () => (dispatch) => {
	dispatch(createApprovalConfig());
	dispatch(fetchApprovalConfiguration());
	dispatch(createStep(defaultNewStep));
	dispatch(getEmployeeList());
};
