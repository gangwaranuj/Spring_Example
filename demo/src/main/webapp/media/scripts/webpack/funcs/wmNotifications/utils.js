import moment from 'moment';

export const parseNotifications = (response = []) => {
	return response.map(notification => {
		let { created_on } = notification;
		return Object.assign({ displayTime: moment(created_on).fromNow() }, notification);
	});
};
