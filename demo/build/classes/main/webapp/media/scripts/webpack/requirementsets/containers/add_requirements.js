'use strict';

import PropTypes from 'prop-types';

import React from 'react';
import { connect } from 'react-redux';
import { addRequirementSet, removeRequirementSet } from '../actions/requirements';
import RequirementsComponent from '../components/add_requirements';

const mapStateToProps = ({ requirementSetIds }) => {
	return { requirementSetIds };
};

const mapDispatchToProps = (dispatch) => {
	return {
		addRequirementSet: (id) => dispatch(addRequirementSet(id)),
		removeRequirementSet: (id) => dispatch(removeRequirementSet(id))
	};
};

RequirementsComponent.PropTypes = {
	requirementSetIds: PropTypes.arrayOf(PropTypes.number),
	addRequirementSet: PropTypes.func.isRequired,
	removeRequirementSet: PropTypes.func.isRequired
};

export default connect(mapStateToProps, mapDispatchToProps)(RequirementsComponent);
