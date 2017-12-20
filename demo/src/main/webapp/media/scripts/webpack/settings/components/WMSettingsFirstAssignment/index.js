import React from 'react';
import {
	WMCard,
	WMCardHeader
} from '@workmarket/front-end-components';
import componentStyles from './styles';
import baseStyles from '../styles';

const mediaPrefix = window.mediaPrefix;
const styles = Object.assign({}, baseStyles, componentStyles);

const WMSettingsFirstAssignment = () => (
	<WMCard
		style={ styles.card }
		expandable
		onExpandChange={ () => window.open('/settings/onboarding/first-assignment', '_self') }
	>
		<WMCardHeader
			actAsExpander
			title="Create Your first Assignment"
			subtitle="Create an assignment or assignment template to include specific information about the work you need performed."
			style={ styles.cardHeader }
			textStyle={ styles.cardHeaderText }
			titleStyle={ styles.cardHeaderTitle }
		>
			<img
				id="wm-settings-employees__icon"
				src={ `${mediaPrefix}/images/new.assignment.svg` }
				alt="Add Employees Icon"
				style={ styles.cardIcon }
			/>
		</WMCardHeader>
	</WMCard>
);

export default WMSettingsFirstAssignment;
