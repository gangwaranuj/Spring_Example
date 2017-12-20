'use strict';

import PropTypes from 'prop-types';

import React, { Component } from 'react';
import {connect} from 'react-redux';
import {addDocument, updateDocument, removeDocument} from '../actions/documents';
import DocumentComponent from '../components/add_documents';

const mapStateToProps = ({ documents }) => {
	return {documents};
};

const mapDispatchToProps = (dispatch) => {
	return {
		addDocument: (value) => dispatch(addDocument(value)),
		updateDocument: (value) => dispatch(updateDocument(value)),
		removeDocument: (index) => dispatch(removeDocument(index))
	};
};

DocumentComponent.PropTypes = {
	documents: PropTypes.arrayOf(PropTypes.shape({
		id: PropTypes.number.isRequired,
		uuid: PropTypes.string.isRequired,
		description: PropTypes.string,
		visibilityType: PropTypes.string.isRequired
	})).isRequired,
	addDocument: PropTypes.func.isRequired,
	updateDocument: PropTypes.func.isRequired,
	removeDocument: PropTypes.func.isRequired
};

export default connect(mapStateToProps, mapDispatchToProps)(DocumentComponent);
