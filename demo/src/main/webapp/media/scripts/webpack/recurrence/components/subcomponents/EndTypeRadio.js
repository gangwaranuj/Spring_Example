import PropTypes from 'prop-types';
import React from 'react';
import {
	WMRadioButtonGroup,
  WMRadioButton
} from '@workmarket/front-end-components';
import {
  formatEndText
} from '../../utils/visuals';

const EndTypeRadio = ({ handleEndType, repetitions, frequency, endType }) => (
	<div style={ { marginLeft: '1em' } }>
		<WMRadioButtonGroup
			name="EndType"
			valueSelected={ endType }
			onChange={ ({ target: { value } }) => handleEndType(value) }
		>
			<WMRadioButton label={ 'Ends on a specific date' } value="Date" />
			<WMRadioButton label={ formatEndText(repetitions, frequency) } value="Instances" />
		</WMRadioButtonGroup>
	</div>
);

EndTypeRadio.propTypes = {
	handleEndType: PropTypes.func,
	repetitions: PropTypes.string,
	frequency: PropTypes.string,
	endType: PropTypes.string
};

export default EndTypeRadio;
