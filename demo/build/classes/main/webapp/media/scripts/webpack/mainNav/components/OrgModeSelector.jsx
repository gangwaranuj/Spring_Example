import React from 'react';
import {
	MenuItem,
	SelectField
} from 'material-ui';
import { commonStyles } from '@workmarket/front-end-components';
import t from '@workmarket/translation';
import Application from '../../core';
import wmAlert from '../../funcs/wmAlert';

class OrgModeSelector extends React.Component {

	constructor (props) {
		super(props);
		this.state = {
			selected: props.value,
		};
	}

	componentWillMount() {
		// If we only have 1 org, and its not selected, select it
		if (this.props.orgModes.length === 1 && !this.props.value) {
			this.handleFieldChange(null, null, this.props.orgModes[0].uuid);
		}
	}

	handleFieldChange = (event, key, value) => {
		this.setState({
			selected: value
		});

		var selectedOrgMode = this.props.orgModes.find(it => it.uuid === value);
		Application.Events.trigger('org:modechange', selectedOrgMode);

		this.updateUserOrgMode(value);
	};

	updateUserOrgMode = (orgUnitUuid) => {
		fetch(`/v2/orgStructure/orgMode?orgModeUuid=${orgUnitUuid}`, {
			credentials: 'same-origin',
			method: 'POST'
		}).then((resp) => {
			if (!resp.ok) {
				throw new Error('Failed to update org mode');
			}
			location.reload(true);
		}).catch(() => {
			wmAlert({
				type: 'danger',
				message: t('mainNav.orgModeUpdateFail')
			});
		});
	};

	render () {
		return (
			<SelectField
				id="orgmodeselect"
				name="orgmode"
				onChange={ this.handleFieldChange }
				labelStyle={ { color: commonStyles.colors.baseColors.white } }
				style={ { verticalAlign: 'middle' } }
				value={ this.state.selected }
			>
				{
					this.props.orgModes.map((orgMode) => {
						return <MenuItem
							key={ `orgmode-${orgMode.uuid}` }
							primaryText={ orgMode.name }
							value={ orgMode.uuid }
						/>;
					})
				}
			</SelectField>
		);
	}

}

export default OrgModeSelector;
