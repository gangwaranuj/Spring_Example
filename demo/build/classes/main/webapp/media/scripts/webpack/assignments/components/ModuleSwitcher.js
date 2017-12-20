'use strict';

import React from 'react';
import { WMCheckbox } from '@workmarket/front-end-components';

export default class ModuleSwitcher extends React.Component {
	render() {
		const { modules, style } = this.props;

		return (
			<div
				className="assignment-creation--module-switcher"
				style={style}
			>
				<div className="assignment-creation--module-toggle -toggle-all">
					<WMCheckbox
						className="assignment-creation--checkbox"
						onCheck={ () => this.props.toggleOptionalModules() }
						checked={ this.props.showAllOptionalModules }
						label="All"
					/>
				</div>
				{modules.map(module => {
					if (module.id !== 'moduleSwitcher') {
						return (
							<div
								key={ module.id }
								className="assignment-creation--module-toggle"
							>
								<WMCheckbox
									disabled={ !module.optional }
									checked={ module.isEnabled }
									onCheck={ () => this.props.toggleModule(module.id) }
									label={ module.title }
								/>
							</div>
						);
					} else {
						return '';
					}
				})}
				<div
					className="assignment-creation--module-switcher-close"
					onClick={ () => this.props.toggleSwitcher() }
				>
					<i className="wm-icon-x"></i>
				</div>
			</div>
		);
	}
}
