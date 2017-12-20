import PropTypes from 'prop-types';
import React from 'react';
import { Map } from 'immutable';
import { connect } from 'react-redux';
import { WMZeroState, WMApprovalConfiguration } from '@workmarket/front-end-patterns';
import { WMMessageBanner } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const mediaPrefix = window.mediaPrefix;

export const mapStateToProps = ({ approvals, showConfig }) => ({
	approvals,
	showConfig
});

export const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		createApprovalFlow: () => {
			dispatch(actions.createApprovalFlow());
		},
		onSaveConfig: () => {
			dispatch(actions.onSaveConfig(ownProps.approvalTemplateId));
		},
		onToggleActivation: () => {
			dispatch(actions.onToggleActivation());
		},
		onAddStep: (value) => {
			dispatch(actions.onAddStep(value));
		},
		onRemoveStep: (value) => {
			dispatch(actions.onRemoveStep(value));
		},
		onAddApprover: (value) => {
			dispatch(actions.onAddApprover(value));
		},
		onRemoveApprover: (value) => {
			dispatch(actions.onRemoveApprover(value));
		},
		onChangeStepTitle: (value, step) => {
			dispatch(actions.onChangeStepTitle(value, step));
		},
		onChangeOption: (value) => {
			dispatch(actions.onChangeOption(value));
		}
	};
};

// Note to self: how do activation and saving work out?
const WMApprovalsAppTemplate = ({
	approvals,
	showConfig,
	createApprovalFlow,
	onSaveConfig,
	onToggleActivation,
	onAddStep,
	onRemoveStep,
	onAddApprover,
	onRemoveApprover,
	onChangeStepTitle,
	onChangeOption
}) => {
	return (
		<div>
			{approvals.get('errorMessage') ? (
				<WMMessageBanner hideDismiss status="error">
					{approvals.get('errorMessage')}
				</WMMessageBanner>
			) : null}
			{approvals.get('approvalSteps').size > 0 || showConfig ? (
				<WMApprovalConfiguration
					disabledTemplateName
					approvalTemplateId={ approvals.get('approvalTemplateId') }
					approvalSteps={ approvals.get('approvalSteps') }
					name="Assignment Approval WorkFlow"
					approverList={ approvals.get('employeeList') }
					approverListIdKey="id"
					approverListLabelKey="fullName"
					disableEditing={ !approvals.get('isActivated') }
					dirtyState={ approvals.get('isDirtyState') }
					optionsList={ approvals.get('optionsList') }
					optionsListIdKey={ 'id' }
					optionsListLabelKey={ 'name' }
					onSave={ () => {
						onSaveConfig();
					} }
					active={ approvals.get('isActivated') }
					onChangeActivation={ () => {
						onToggleActivation();
					} }
					onAddStep={ (step) => {
						onAddStep(step);
					} }
					onRemoveStep={ (step) => {
						onRemoveStep(step);
					} }
					onAddApprover={ (value) => {
						onAddApprover(value);
					} }
					onRemoveApprover={ (value) => {
						onRemoveApprover(value);
					} }
					onChangeStepTitle={ (value, step) => {
						onChangeStepTitle(value, step);
					} }
					onChangeOption={ (value) => {
						onChangeOption(value);
					} }
				/>
			) : (
				<WMZeroState
					headerImageSrc={ `${mediaPrefix}/images/settings/approvals.default.svg` }
					headerImageAlt="Approval Flows"
					headerText="Approval Flows"
					textContentStyle={ { width: 'auto' } }
					buttonLabel="Create Approval Flow"
					onButtonClick={ () => {
						createApprovalFlow();
					} }
					textContent={ [
						'Approval flows allow you to configure multi-step approval flows with custom groups of employees.',
						'Approval flows can be applied to submitted work, payments, and talentpool membership.'
					] }
				/>
			)}
		</div>
	);
};

const WMApprovalsApp = connect(mapStateToProps, mapDispatchToProps)(WMApprovalsAppTemplate);

WMApprovalsAppTemplate.propTypes = {
	approvals: PropTypes.instanceOf(Map).isRequired,
	createApprovalFlow: PropTypes.func.isRequired,
	onSaveConfig: PropTypes.func.isRequired,
	onToggleActivation: PropTypes.func.isRequired,
	onAddStep: PropTypes.func.isRequired,
	showConfig: PropTypes.bool.isRequired,
	onAddApprover: PropTypes.func.isRequired,
	onRemoveApprover: PropTypes.func.isRequired,
	onRemoveStep: PropTypes.func.isRequired,
	onChangeStepTitle: PropTypes.func.isRequired,
	onChangeOption: PropTypes.func.isRequired
};

export default WMApprovalsApp;
