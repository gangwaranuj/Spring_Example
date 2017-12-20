import PropTypes from 'prop-types';
import React from 'react';
import WeeklyCheckboxes from './WeeklyCheckboxes';
import StartingDate from './StartingDate';
import EndingDate from './EndingDate';

const WeeklyOptions = ({
  startDate,
  handleEndDate,
	endDate,
  days,
  handleDays,
  handleStartDate,
  minDate,
  maxDate
}) => (
	<div>
		<WeeklyCheckboxes days={ days } handleDays={ handleDays } startDate={ startDate } />
		<StartingDate startDate={ startDate } handleStartDate={ handleStartDate } disabled />
		<EndingDate
    	handleEndDate={ handleEndDate }
    	endDate={ endDate }
    	minDate={ minDate }
    	maxDate={ maxDate }
 		/>
	</div>
);

WeeklyOptions.propTypes = {
  minDate: PropTypes.object, //eslint-disable-line
  maxDate: PropTypes.object, //eslint-disable-line
	handleDays: PropTypes.func,
	days: PropTypes.arrayOf(PropTypes.bool),
	frequencyModifier: PropTypes.string,
	frequency: PropTypes.string,
  startDate: PropTypes.object, // eslint-disable-line
	handleEndType: PropTypes.func,
	handleEndDate: PropTypes.func,
	repetitions: PropTypes.string,
  endDate: PropTypes.object, // eslint-disable-line
	endType: PropTypes.string,
	handleRepeat: PropTypes.func,
	maxRepeat: PropTypes.string,
	minRepeat: PropTypes.number,
	handleStartDate: PropTypes.func
};

export default WeeklyOptions;
