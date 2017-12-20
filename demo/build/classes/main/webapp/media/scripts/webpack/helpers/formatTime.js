import moment from 'moment';
export default (time) => {
	return moment(time).format('h:mm A');
};