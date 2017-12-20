import PropTypes from 'prop-types';

export default {
	type: PropTypes.string,
	frequency: PropTypes.string,
	handleFrequency: PropTypes.func,
	handleDays: PropTypes.func,
	handleRepeat: PropTypes.func,
	handleStartDate: PropTypes.func,
	handleEndDate: PropTypes.func,
	repetitions: PropTypes.string,
	days: PropTypes.arrayOf(PropTypes.bool),
  startDate: PropTypes.object, // eslint-disable-line
  endDate: PropTypes.object, // eslint-disable-line
	monthlyType: PropTypes.string,
	handleMonthlyType: PropTypes.func,
	day: PropTypes.string,
	weekNumber: PropTypes.string,
	handleDay: PropTypes.func,
	handleWeekNumber: PropTypes.func,
	endType: PropTypes.string,
	handleEndType: PropTypes.func,
	handleErrors: PropTypes.func,
	maxRepeat: PropTypes.string,
	dirty: PropTypes.bool,
	validity: PropTypes.shape({
		valid: PropTypes.bool,
		reason: PropTypes.string
	}),
	frequencyModifier: PropTypes.string,
	handleFrequencyModifier: PropTypes.func,
	handleRecurrence: PropTypes.func,
	id: PropTypes.string,
	setModuleValidation: PropTypes.func,
	minRepeat: PropTypes.number,
	foreignRange: PropTypes.bool,
	errors: PropTypes.arrayOf(PropTypes.string),
	minDate: PropTypes.object, //eslint-disable-line
	maxDate: PropTypes.object, //eslint-disable-line
	initialError: PropTypes.oneOfType([
		PropTypes.string,
		PropTypes.bool
	])
};
