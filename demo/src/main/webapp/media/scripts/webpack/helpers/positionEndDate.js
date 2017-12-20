import moment from 'moment';

export default (current, dateToMonth, dateToYear) => {
	if (!!current) {
		return 'Present';
	} else if (!!dateToMonth) {
		return moment('' + dateToMonth).format('MMM') + ' ' + dateToYear;
	} else {
		return dateToYear;
	}
};