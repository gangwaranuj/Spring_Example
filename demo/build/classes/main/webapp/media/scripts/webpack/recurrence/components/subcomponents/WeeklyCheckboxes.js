import PropTypes from 'prop-types';
import React from 'react';
import {
	WMCheckbox
} from '@workmarket/front-end-components';
import { daysOfTheWeek } from '../../utils/visuals';

const verifyDisabled = (arr, elem, pos, startDay) => {
	return (arr.filter(day => day).length === 1 && elem)
	|| (startDay === pos);
};

const WeeklyCheckboxes = ({ days, handleDays, startDate }) => (
	<div style={ { marginLeft: '1em' } }>
		{
      days.map((curDay, i) =>
				<WMCheckbox
					key={ i }
					checked={ curDay }
					label={ daysOfTheWeek[i] }
					disabled={ verifyDisabled(days, curDay, i, startDate.day()) }
					onCheck={ () => { handleDays(i); } }
				/>,
    )}
	</div>
);

WeeklyCheckboxes.propTypes = {
	handleDays: PropTypes.func,
	days: PropTypes.arrayOf(PropTypes.bool),
	startDate: PropTypes.object // eslint-disable-line
};

export default WeeklyCheckboxes;
