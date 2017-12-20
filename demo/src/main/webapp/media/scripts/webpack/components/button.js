'use strict';
import PropTypes from 'prop-types';
import React, { Component } from 'react';

const Button = (props) => {
	const buttonText = (props.label) ? `${props.label}` : 'Add';
	const className = (props.class) ? `${props.class} button` : 'button';

	return (
		<button disabled={props.isDisabled} className={className} data-action='add' data-kind={props.label} {...props} onClick={props.handleClick}>{buttonText}</button>
	);
};

Button.propTypes = {
	handleClick: PropTypes.func.isRequired,
	label: PropTypes.string,
	isDisabled: PropTypes.bool
};

export default Button;
