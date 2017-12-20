'use strict';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { addDeliverable, removeDeliverable, updateDeliverablesInstructions, updateDeliverablesTime } from '../actions/deliverables';
import DeliverableComponent from '../components/add_deliverables';

const mapStateToProps = ({ deliverablesGroup }) => {
	return { deliverablesGroup };
};

const mapDispatchToProps = (dispatch) => {
	return {
		updateDeliverablesInstructions: (value) => dispatch(updateDeliverablesInstructions(value)),
		updateDeliverablesTime: (value) => dispatch(updateDeliverablesTime(value)),
		addDeliverable: (value) => dispatch(addDeliverable(value)),
		removeDeliverable: (index) => dispatch(removeDeliverable(index))
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(DeliverableComponent);
