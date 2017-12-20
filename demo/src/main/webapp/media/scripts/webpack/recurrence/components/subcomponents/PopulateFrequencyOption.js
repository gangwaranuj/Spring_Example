import PropTypes from 'prop-types';
import React from 'react';
import DailyOptions from './DailyOptions';
import WeeklyOptions from './WeeklyOptions';
import MonthlyOptions from './MonthlyOptions';

const PopulateFrequencyDropdown = ({
  frequency,
  startDate,
  handleEndType,
  handleEndDate,
  repetitions,
  endDate,
  endType,
  handleRepeat,
  maxRepeat,
  minRepeat,
  handleStartDate,
  frequencyModifier,
  minDate,
  maxDate,
  days,
  handleDays
}) => {
  switch (frequency) {
  case 'Daily':
    return (
      <DailyOptions
        startDate={ startDate }
        handleEndType={ handleEndType }
        handleEndDate={ handleEndDate }
        repetitions={ repetitions }
        frequency={ frequency }
        endDate={ endDate }
        endType={ endType }
        handleRepeat={ handleRepeat }
        maxRepeat={ maxRepeat }
        minRepeat={ minRepeat }
        handleStartDate={ handleStartDate }
        frequencyModifier={ frequencyModifier }
        minDate={ minDate }
        maxDate={ maxDate }
      />
      );
    break;
  case 'Weekly':
    return (
      <WeeklyOptions
        frequencyModifier={ frequencyModifier }
        frequency={ frequency }
        startDate={ startDate }
        handleEndType={ handleEndType }
        handleEndDate={ handleEndDate }
        repetitions={ repetitions }
        endDate={ endDate }
        endType={ endType }
        handleRepeat={ handleRepeat }
        maxRepeat={ maxRepeat }
        days={ days }
        handleDays={ handleDays }
        minRepeat={ minRepeat }
        handleStartDate={ handleStartDate }
        minDate={ minDate }
        maxDate={ maxDate }
      />
      );
    break;
  case 'Monthly':
    return (
      <MonthlyOptions
        frequencyModifier={ frequencyModifier }
        frequency={ frequency }
        startDate={ startDate }
        handleEndType={ handleEndType }
        handleEndDate={ handleEndDate }
        repetitions={ repetitions }
        endDate={ endDate }
        endType={ endType }
        handleRepeat={ handleRepeat }
        maxRepeat={ maxRepeat }
        minRepeat={ minRepeat }
        handleStartDate={ handleStartDate }
        minDate={ minDate }
        maxDate={ maxDate }
      />
      );
    break;
  default:
    console.error('Error rendering recurrence module.');
    return <div>Error While Rendering.</div>;
  }
};

PopulateFrequencyDropdown.propTypes = {
  frequency: PropTypes.string,
  startDate: PropTypes.object, //eslint-disable-line
  handleEndType: PropTypes.func,
  handleEndDate: PropTypes.func,
  repetitions: PropTypes.string,
  endDate: PropTypes.object, //eslint-disable-line
  endType: PropTypes.string,
  handleRepeat: PropTypes.func,
  minRepeat: PropTypes.number,
  maxRepeat: PropTypes.string,
  handleStartDate: PropTypes.func,
  frequencyModifier: PropTypes.string,
  minDate: PropTypes.object, //eslint-disable-line
  maxDate: PropTypes.object,
  handleDays: PropTypes.func,
  days: PropTypes.arrayOf(PropTypes.bool)
};

export default PopulateFrequencyDropdown;

