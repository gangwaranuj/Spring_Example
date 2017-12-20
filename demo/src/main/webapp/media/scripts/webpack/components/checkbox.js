'use strict';

import React from 'react';

export default class Checkbox extends React.Component {
	render() {
		const labelProps = {
			className: `wm-checkbox ${this.props.classList}`,
			id: this.props.id
		};

		const textProps = {
			className: 'wm-checkbox--text'
		};

		const inputProps = Object.assign({
			type: 'checkbox',
			name: this.props.name,
			onChange: this.props.onChange,
			checked: this.props.checked
		}, this.props.attributes);

		if (typeof this.props.badge !== 'undefined') {
			labelProps['data-badge'] = this.props.badge;
			textProps['data-badge-content'] = '';
		}

		if (this.props.disabled) {
			inputProps.disabled = 'disabled';
			labelProps.disabled = 'disabled';
		}

		return (
			<label {...labelProps}>
				<input {...inputProps} />
				<div className="wm-checkbox--skin wm-icon-checkmark"></div>
				<span {...textProps}>{this.props.children}</span>
			</label>
		);
	}
}
