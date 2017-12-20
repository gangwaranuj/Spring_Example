import PropTypes from 'prop-types';
import React from 'react';
import {
  WMRadioButtonGroup,
  WMRadioButton
} from '@workmarket/front-end-components';

const RecurrenceRadio = ({
	handleRecurrence,
	type,
	foreignRange,
	startDate
}) => (
	<div
		style={ { marginLeft: '1em', paddingBottom: '1em' } }
	>
		<WMRadioButtonGroup
			name="Assignment Type"
			valueSelected={ type }
			onChange={ ({ target: { value } }) => handleRecurrence(value) }
		>
			<WMRadioButton label="One Time" value="Single" />
			<WMRadioButton label="Repeating" value="Recur" disabled={ foreignRange || !startDate } />
		</WMRadioButtonGroup>
	</div>
);

RecurrenceRadio.propTypes = {
	handleRecurrence: PropTypes.func,
	type: PropTypes.string,
	startDate: PropTypes.object // eslint-disable-line
};

export default RecurrenceRadio;
