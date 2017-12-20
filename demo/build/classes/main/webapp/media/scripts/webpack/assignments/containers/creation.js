'use strict';

import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { updateAssignmentStatus, updateErrors, updateUserConfig, toggleTemplateModal, updateTemplateId, updateTemplateName, updateTemplateDescription, updateNumberOfCopies, updateSaveMode, saveTemplate, fetchTemplate } from '../actions/creation';
import CreationComponent from '../components/creation';

const mapStateToProps = (state) => {
	return state;
};

const mapDispatchToProps = (dispatch) => {
	return {
		updateErrors: (value) => dispatch(updateErrors(value)),
		updateAssignmentStatus: (value) => dispatch(updateAssignmentStatus(value)),
		updateUserConfig: (value) => dispatch(updateUserConfig(value)),
		toggleTemplateModal: () => dispatch(toggleTemplateModal()),
		updateTemplateId: (value) => dispatch(updateTemplateId(value)),
		updateNumberOfCopies: (value) => dispatch(updateNumberOfCopies(value)),
		updateSaveMode: (value) => dispatch(updateSaveMode(value)),
		updateTemplateName: (value) => dispatch(updateTemplateName(value)),
		updateTemplateDescription: (value) => dispatch(updateTemplateDescription(value)),
		fetchTemplate: (templateId, templateUrl) => dispatch(fetchTemplate(templateId, templateUrl)),
		saveTemplate: (modal) => dispatch(saveTemplate(modal))
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(CreationComponent);
