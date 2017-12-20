import PropTypes from 'prop-types';
import React from 'react';

const Root = ({ children }) => <div>{ children }</div>;

export default Root;

Root.propTypes = {
	children: PropTypes.element.isRequired
};
