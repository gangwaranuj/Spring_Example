import moment from 'moment';
import { parseNotifications } from './utils';

describe('wmNotifications', () => {
	describe('parseNotifications', () => {
		let createdOn = '2016-08-09T16:47:17.000Z',
			notifications = [{ created_on: createdOn }];

		it('should return an array', () => {
			expect(Array.isArray(parseNotifications())).toBeTruthy();
		});

		it('returns an empty array by default', () => {
			expect(parseNotifications()).toHaveLength(0);
		});

		it('adds a `displayTime` property to each notification', () => {
			const returnedNotifications = parseNotifications(notifications).every(({ displayTime }) => displayTime);
			expect(returnedNotifications).toBeTruthy();
		});

		describe('the `displayTime` property', () => {
			it('displays "time ago" for the notification', () => {
				const [{ displayTime }] = parseNotifications(notifications);
				expect(displayTime).toEqual(moment(createdOn).fromNow());
			});
		});
	});
});
