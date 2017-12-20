'use strict';

import React from 'react';
import ReactDOM from 'react-dom';

export default class SettingsSwitch extends React.Component {
	render() {
		const labelProps = Object.assign({}, {
			className: `settings-switch ${this.props.classList}`,
			htmlFor: this.props.id
		}, this.props.attributes);

		const inputProps = {
			type: 'checkbox',
			id: this.props.id,
			name: this.props.name,
			value: this.props.value,
			onChange: this.props.onChange,
			checked: this.props.checked
		};

		return (
			<label {...labelProps}>
				<input {...inputProps} />
				<div
					className="settings-switch--skin"
					data-on={this.props.on || 'On'}
					data-off={this.props.off || 'Off'}
				>
					<div className="settings-switch--slider"></div>
				</div>
			</label>
		);
	}
}
