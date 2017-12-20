import { Map, List } from 'immutable';
import moment from 'moment';

const initialWeek = List([
	false,
	false,
	false,
	false,
	false,
	false,
	false
]).set(moment().day(), true);

export default Map({
	type: 'Single',
	frequency: 'Daily',
	frequencyModifier: '1',
	repetitions: '1',
	maxRepeat: '370',
	days: initialWeek,
	startDate: null,
	endDate: moment().add(1, 'days').set('hour', 12),
	endType: 'Date',
	minRepeat: 1,
	validity: Map({
		valid: true,
		reason: 'This module has not been engaged yet, so I am valid.'
	}),
	errors: List([])
});
