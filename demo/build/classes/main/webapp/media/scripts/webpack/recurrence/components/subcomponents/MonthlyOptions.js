import PropTypes from 'prop-types';
import React from 'react';
import StartingDate from './StartingDate';
import EndingDate from './EndingDate';
import EndingDropDown from './EndingDropDown';

const MonthlyOptions = ({
  frequencyModifier,
  frequency,
  startDate,
  handleEndDate,
  repetitions,
  endDate,
  handleRepeat,
  maxRepeat,
  minRepeat,
  handleStartDate,
  minDate,
  maxDate
}) => (
	<div>
		<StartingDate startDate={ startDate } handleStartDate={ handleStartDate } disabled />
		<EndingDate handleEndDate={ handleEndDate } disabled endDate={ endDate } startDate={ startDate } frequency={ frequency } minRepeat={ minRepeat + 1 } />
		<EndingDropDown
			repetitions={ repetitions }
			maxRepeat={ maxRepeat }
			handleRepeat={ handleRepeat }
			frequency={ frequency }
			startDate={ startDate }
			frequencyModifier={ frequencyModifier }
			minDate={ minDate }
			maxDate={ maxDate }
		/>
	</div>
);

MonthlyOptions.propTypes = {
  minDate: PropTypes.object, //eslint-disable-line
  maxDate: PropTypes.object, //eslint-disable-line
	frequencyModifier: PropTypes.string,
	frequency: PropTypes.string,
  startDate: PropTypes.object, // eslint-disable-line
	handleEndDate: PropTypes.func,
	repetitions: PropTypes.string,
  endDate: PropTypes.object, // eslint-disable-line
	handleRepeat: PropTypes.func,
	maxRepeat: PropTypes.string,
	minRepeat: PropTypes.number,
	handleStartDate: PropTypes.func
};

export default MonthlyOptions;
