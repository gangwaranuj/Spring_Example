import moment from 'moment';

export default (through, from, range) => {
	if (range) {
		if (through - from < 24 * 60 * 60 * 1000) {
			return moment(from).format('dddd, MMMM D, YYYY h:mma') + ' to ' + moment('' + through).format('h:mma');
		} else {
			return moment(from).format('dddd, MMMM D, YYYY h:mma') + ' to ' + moment('' + through).format('dddd, MMMM D, YYYY h:mma z');
		}
	} else {
		return moment(from).format('dddd, MMMM D, YYYY h:mma z');
	}
};
