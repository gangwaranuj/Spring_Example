'use strict';
import PropTypes from 'prop-types';
import React, { Component } from 'react';

export default class Select extends Component {
	render() {
		return (
				<div>
					<label className="assignment-creation--label" htmlFor={this.props.name}>{this.props.label}</label>
					<div className="assignment-creation--field">
						<select id={this.props.name} className="wm-select" name={this.props.name}></select>
					</div>
				</div>
		);
	}
}

Select.PropTypes = {
	name: PropTypes.string.isRequired,
	label: PropTypes.string.isRequired,
}
