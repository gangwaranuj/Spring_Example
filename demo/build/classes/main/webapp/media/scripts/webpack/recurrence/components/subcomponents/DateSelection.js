import PropTypes from 'prop-types';
import React from 'react';
import StartingDate from './StartingDate';
import EndingDate from './EndingDate';
import EndingDropDown from './EndingDropDown';
import EndTypeRadio from './EndTypeRadio';

const DateSelection = ({
  startDate,
  handleEndType,
  handleEndDate,
  handleStartDate,
  repetitions,
  frequency,
  endDate,
  endType,
  handleRepeat,
  maxRepeat,
  frequencyModifier,
  minDate,
  maxDate
}) => (
	<div>
		<StartingDate startDate={ startDate } handleStartDate={ handleStartDate } disabled />
		{
      endType === 'Date' 
      	? (<EndingDate
          	handleEndDate={ handleEndDate }
          	endDate={ endDate }
          	minDate={ minDate }
          	maxDate={ maxDate }
       		/>) 
        : (<EndingDropDown
          	repetitions={ repetitions }
          	maxRepeat={ maxRepeat }
          	handleRepeat={ handleRepeat }
          	frequency={ frequency }
          	startDate={ startDate }
          	frequencyModifier={ frequencyModifier }
       		/>)
     }
		<EndTypeRadio
			handleEndType={ handleEndType }
			repetitions={ repetitions }
			frequency={ frequency }
			endDate={ endDate }
			endType={ endType }
		/>
	</div>
);

DateSelection.propTypes = {
  minDate: PropTypes.object, //eslint-disable-line
  maxDate: PropTypes.object, //eslint-disable-line
  startDate: PropTypes.object, // eslint-disable-line
	handleEndType: PropTypes.func,
	handleEndDate: PropTypes.func,
	handleStartDate: PropTypes.func,
	repetitions: PropTypes.string,
	frequency: PropTypes.string,
  endDate: PropTypes.object, // eslint-disable-line
	endType: PropTypes.string,
	handleRepeat: PropTypes.func,
	maxRepeat: PropTypes.string,
	frequencyModifier: PropTypes.string
};

export default DateSelection;
