import { Map, List } from 'immutable';
import * as types from '../constants/actionTypes';

const initialapprovalsState = new Map({
	approvalTemplateId: 0,
	approvalSteps: List(),
	isActivated: true,
	isDirtyState: false,
	employeeList: List(),
	errorMessage: null,
	optionsList: [{ id: 1, name: 'all' }]
});

export { initialapprovalsState };

const approvals = (state = initialapprovalsState, { type, value, step }) => {
	switch (type) {
	case types.CREATE_APPROVAL_CONFIG:
		return initialapprovalsState;
		/** Saving and updating resets toggle toa activation mode on the backend */
	case types.SAVE_APPROVAL_CONFIG:
		return state
				.set('approvalTemplateId', value)
				.set('isActivated', true)
				.set('isDirtyState', false);
	case types.TOGGLE_ACTIVATION:
		return state.set('isActivated', value);
	case types.APPROVALS_ERROR:
		return state.set('errorMessage', value);
	case types.EMPLOYEE_LIST_SUCCESS:
		return state.set('employeeList', value);
	case types.ADD_STEP: {
		const newStep = value;
		const newApprovals = state.update('approvalSteps', steps => steps.push(newStep)).set('isDirtyState', true);
		return newApprovals;
	}
	case types.REMOVE_STEP: {
		const stepToRemove = value;
		const indexOfApprovalStepToRemove = state.get('approvalSteps').findIndex((singleStep) => {
			return singleStep === stepToRemove;
		});
		const newApprovals = state
				.update('approvalSteps', steps => steps.delete(indexOfApprovalStepToRemove))
				.set('isDirtyState', true);
		return newApprovals;
	}
	case types.CHANGE_STEP_TITLE: {
		const indexOfApprovalStep = step.orderId;
		const singleStep = state.getIn(['approvalSteps', indexOfApprovalStep]);
		const newSingleStep = { ...singleStep, name: value };
		const newApprovals = state
				.setIn(['approvalSteps', indexOfApprovalStep], newSingleStep)
				.set('isDirtyState', true);
		return newApprovals;
	}
	case types.ADD_APPROVER: {
		const indexOfApprovalStep = value.orderId;
		const singleStep = state.getIn(['approvalSteps', indexOfApprovalStep]);
		const newApprovers = singleStep.selectedApprovers.push(value.approver);
		const newSingleStep = { ...singleStep, selectedApprovers: newApprovers };
		const newApprovals = state
				.setIn(['approvalSteps', indexOfApprovalStep], newSingleStep)
				.set('isDirtyState', true);
		return newApprovals;
	}
	case types.REMOVE_APPROVER: {
		const indexOfApprovalStep = value.orderId;
		const approverIdToRemove = value.approverId;
		const singleStep = state.getIn(['approvalSteps', indexOfApprovalStep]);
		const newApprovers = singleStep.selectedApprovers.filter((approver) => {
			return approver.id !== approverIdToRemove;
		});
		const newSingleStep = { ...singleStep, selectedApprovers: newApprovers };
		const newApprovals = state
				.setIn(['approvalSteps', indexOfApprovalStep], newSingleStep)
				.set('isDirtyState', true);
		return newApprovals;
	}
	case types.CHANGE_OPTION: {
		const indexOfApprovalStep = value.orderId;
		const newOptionId = value.value;
		const singleStep = state.getIn(['approvalSteps', indexOfApprovalStep]);
		const newSingleStep = { ...singleStep, selectedOption: newOptionId };
		const newApprovals = state
				.setIn(['approvalSteps', indexOfApprovalStep], newSingleStep)
				.set('isDirtyState', true);
		return newApprovals;
	}
	default:
		return state;
	}
};

export default approvals;
