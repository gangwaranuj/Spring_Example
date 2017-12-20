import React from 'react';
import {
	WMCard,
	WMCardHeader
} from '@workmarket/front-end-components';
import componentStyles from './styles';
import baseStyles from '../styles';

const mediaPrefix = window.mediaPrefix;
const styles = Object.assign({}, baseStyles, componentStyles);

const WMSettingsAssignment = () => (
	<WMCard
		style={ styles.card }
		expandable
		onExpandChange={ () => window.open('/settings/onboarding/assignment-preferences', '_self') }
	>
		<WMCardHeader
			actAsExpander
			title="Set Your Company's Assignment Preferences"
			subtitle="Configure payment terms, Code of Conduct, and more."
			style={ styles.cardHeader }
			textStyle={ styles.cardHeaderText }
			titleStyle={ styles.cardHeaderTitle }
		>
			<img
				id="wm-settings-employees__icon"
				src={ `${mediaPrefix}/images/settings/compliance.svg` }
				alt="Add Employees Icon"
				style={ styles.cardIcon }
			/>
		</WMCardHeader>
	</WMCard>
);

export default WMSettingsAssignment;
