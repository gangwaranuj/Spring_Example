import moment from 'moment';
export default (date) => {
	return moment(date).format('MMM YYYY');
};