import PropTypes from 'prop-types';
import React from 'react';
import DateSelection from './DateSelection';

const DailyOptions = ({
	startDate,
	handleEndType,
	handleEndDate,
	repetitions,
	frequency,
	endDate,
	endType,
	handleRepeat,
	maxRepeat,
	minRepeat,
	handleStartDate,
	frequencyModifier,
	minDate,
	maxDate
}) => (
	<div>
		<DateSelection
			startDate={ startDate }
			handleEndType={ handleEndType }
			handleEndDate={ handleEndDate }
			handleStartDate={ handleStartDate }
			repetitions={ repetitions }
			frequency={ frequency }
			endDate={ endDate }
			endType={ endType }
			handleRepeat={ handleRepeat }
			maxRepeat={ maxRepeat }
			minRepeat={ minRepeat }
			frequencyModifier={ frequencyModifier }
			minDate={ minDate }
			maxDate={ maxDate }
		/>
	</div>
);

DailyOptions.propTypes = {
	minDate: PropTypes.object, //eslint-disable-line
	maxDate: PropTypes.object, //eslint-disable-line
	startDate: PropTypes.object, // eslint-disable-line
	handleEndType: PropTypes.func,
	handleEndDate: PropTypes.func,
	repetitions: PropTypes.string,
	frequency: PropTypes.string,
	endDate: PropTypes.object, // eslint-disable-line
	endType: PropTypes.string,
	handleRepeat: PropTypes.func,
	maxRepeat: PropTypes.string,
	minRepeat: PropTypes.number,
	handleStartDate: PropTypes.func,
	frequencyModifier: PropTypes.string
};

export default DailyOptions;
