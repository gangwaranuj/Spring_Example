import moment from 'moment';

export default (dateToMonth, dateToYear) => {
	if (!dateToYear) {
		return 'Present';
	} else if (!!dateToMonth) {
		return moment('' + dateToMonth).format('MMM') + ' ' + dateToYear;
	} else {
		return dateToYear;
	}
};