import { connect } from 'react-redux';
import Component from './template';
import {
  getMinDate,
  getMaxDate
} from '../selectors';
import {
  updateFrequencyModifier,
  updateEndType,
  updateDaysOfWeek,
  updateEndDate,
  updateStartDate,
  updateRepetitions,
  updateFrequencyType,
  updateRecurrence,
  updateFormValidity,
  updateErrors
} from '../actions';

const errorText = 'Please enter a start date in the scheduling section above to begin configuring the frequency of the assignment.';

const mapStateToProps = ({ recurrence, schedule }) => ({
	foreignStartDate: schedule.from,
	foreignRange: schedule.range,
  initialError: !recurrence.get('startDate') ? errorText : false,
	minDate: getMinDate(recurrence),
	maxDate: getMaxDate(recurrence),
	type: recurrence.get('type'),
	monthlyType: recurrence.get('monthlyType'),
	frequency: recurrence.get('frequency'),
	frequencyModifier: recurrence.get('frequencyModifier'),
	repetitions: recurrence.get('repetitions'),
	days: recurrence.get('days').toArray(),
	startDate: recurrence.get('startDate'),
	endDate: recurrence.get('endDate'),
	weekNumber: recurrence.get('weekNumber'),
	endType: recurrence.get('endType'),
	maxRepeat: recurrence.get('maxRepeat'),
	minRepeat: recurrence.get('minRepeat'),
	validity: recurrence.get('validity').toObject(),
	errors: recurrence.get('errors').toArray()
});

const mapDispatchToProps = dispatch => ({
	handleRecurrence: value => dispatch(updateRecurrence(value)),
	handleFrequency: value => dispatch(updateFrequencyType(value)),
	handleRepeat: value => dispatch(updateRepetitions(value)),
	handleDays: value => dispatch(updateDaysOfWeek(value)),
	handleStartDate: value => dispatch(updateStartDate(value)),
	handleEndDate: value => dispatch(updateEndDate(value)),
	handleEndType: value => dispatch(updateEndType(value)),
	handleFrequencyModifier: value => dispatch(updateFrequencyModifier(value)),
	handleValidity: value => dispatch(updateFormValidity(value)),
	handleErrors: value => dispatch(updateErrors(value))
});

export default connect(mapStateToProps, mapDispatchToProps)(Component);
