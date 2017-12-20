import moment from 'moment';

export default (date) => {
	const invoiceDueDate = moment(date).startOf('day');
	const currentDate = moment().startOf('day');
	const diff = invoiceDueDate.diff(currentDate, 'days');
	if (diff === 0) {
		return 'Due Today';
	}
	else if (diff < 0) {
		return 'Past Due';
	}
	else if (diff <= 7) {
		return 'Coming Due';
	}
	else {
		return '';
	}
};

