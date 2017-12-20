import React from 'react';
import {
	WMCard,
	WMCardHeader
} from '@workmarket/front-end-components';
import componentStyles from './styles';
import baseStyles from '../styles';

const mediaPrefix = window.mediaPrefix;
const styles = Object.assign({}, baseStyles, componentStyles);

const WMSettingsEmployees = () => (
	<WMCard
		style={ styles.card }
		expandable
		onExpandChange={ () => window.open('/settings/onboarding/employees', '_self') }
	>
		<WMCardHeader
			actAsExpander
			title="Add Your Employees"
			subtitle="Add or upload the employees that will be performing work on behalf of your company."
			style={ styles.cardHeader }
			textStyle={ styles.cardHeaderText }
			titleStyle={ styles.cardHeaderTitle }
		>
			<img
				id="wm-settings-employees__icon"
				src={ `${mediaPrefix}/images/settings/employees.svg` }
				alt="Add Employees Icon"
				style={ styles.cardIcon }
			/>
		</WMCardHeader>
	</WMCard>
);

export default WMSettingsEmployees;
