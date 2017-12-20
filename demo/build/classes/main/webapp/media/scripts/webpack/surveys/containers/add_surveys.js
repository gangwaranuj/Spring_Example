'use strict';

import PropTypes from 'prop-types';

import React, { Component } from 'react';
import { connect } from 'react-redux';
import { addSurvey, removeSurvey, toggleRequired } from '../actions/surveys';
import SurveyComponent from '../components/add_surveys';

const mapStateToProps = ({ surveys }) => {
	return { surveys }
};

const mapDispatchToProps = (dispatch) => {
	return {
		addSurvey: (value) => dispatch(addSurvey(value)),
		removeSurvey: (index) => dispatch(removeSurvey(index)),
		toggleRequired: (index) => dispatch(toggleRequired(index))
	}
};

SurveyComponent.PropTypes = {
	surveys: PropTypes.arrayOf(PropTypes.shape({
		id: PropTypes.string,
		name: PropTypes.string.isRequired,
		required: PropTypes.boolean
	})).isRequired,
	addSurvey: PropTypes.func.isRequired,
	removeSurvey: PropTypes.func.isRequired
};

export default connect(mapStateToProps, mapDispatchToProps)(SurveyComponent);
