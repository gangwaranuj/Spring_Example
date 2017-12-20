'use strict';

import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { addCustomFields, updateCustomFields, fetchCustomFields, removeCustomFieldGroup } from '../actions/custom_fields';
import CustomFieldsComponent from '../components/add_custom_fields';

const mapStateToProps = ({ customFieldGroups }) => {
	return { customFieldGroups };
};

const mapDispatchToProps = (dispatch) => {
	return {
		addCustomFields: (value) => dispatch(addCustomFields(value)),
		updateCustomFields: (value) => dispatch(updateCustomFields(value)),
		fetchCustomFields: (value) => dispatch(fetchCustomFields(value)),
		removeCustomFieldGroup: (value) => dispatch(removeCustomFieldGroup(value))
	};
};

CustomFieldsComponent.PropTypes = {
	customFieldGroups: PropTypes.arrayOf(PropTypes.shape({
		id: PropTypes.string.isRequired,
		name: PropTypes.string.isRequired,
		required: PropTypes.string.isRequired,
		position: PropTypes.number.isRequired,
		workCustomFields: PropTypes.arrayOf(PropTypes.shape({
			id: PropTypes.number.isRequired,
			name: PropTypes.string.isRequired,
			value: PropTypes.string,
			defaults: PropTypes.string,
			is_dropdown: PropTypes.bool.isRequired,
			required: PropTypes.bool.isRequired,
			type: PropTypes.string.isRequired
		}))
	})),
	addCustomFields: PropTypes.func.isRequired
};

export default connect(mapStateToProps, mapDispatchToProps)(CustomFieldsComponent);
