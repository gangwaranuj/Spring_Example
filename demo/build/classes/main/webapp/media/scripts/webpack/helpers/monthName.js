import moment from 'moment';

export default (month) => {
	return moment('' + month).format('MMM');
};