// define([
// 	'assignments/activity_model'
// ], function (ActivityModel) {
// 	'use strict';
//
// 	describe('ActivityModel', function () {
// 		var model;
//
// 		beforeEach(function () {
// 			spyOn($, 'ajax');
// 			model = new ActivityModel();
// 		});
//
// 		afterEach(function () {
// 			model = undefined;
// 		});
//
// 		it('can be instantiated', function () {
// 			expect(model).toBeDefined();
// 		});
//
// 		it('defaults isWorkStatusChange to false', function () {
// 			expect(model.get('isWorkStatusChange')).toBeFalsy();
// 		});
//
// 		it('defaults isWorkResourceStatusChange to false', function () {
// 			expect(model.get('isWorkResourceStatusChange')).toBeFalsy();
// 		});
//
// 		it('defaults isWorkProperty to false', function () {
// 			expect(model.get('isWorkProperty')).toBeFalsy();
// 		});
//
// 		it('defaults isWorkCreated to false', function () {
// 			expect(model.get('isWorkCreated')).toBeFalsy();
// 		});
//
// 		it('defaults isWorkNoteCreated to false', function () {
// 			expect(model.get('isWorkNoteCreated')).toBeFalsy();
// 		});
//
// 		it('defaults isWorkNegotiationRequested to false', function () {
// 			expect(model.get('isWorkNegotiationRequested')).toBeFalsy();
// 		});
//
// 		it('defaults isWorkNegotiationExpired to false', function () {
// 			expect(model.get('isWorkNegotiationExpired')).toBeFalsy();
// 		});
//
// 		it('defaults isWorkRescheduleRequested to false', function () {
// 			expect(model.get('isWorkRescheduleRequested')).toBeFalsy();
// 		});
//
// 		it('defaults isWorkQuestionAsked to false', function () {
// 			expect(model.get('isWorkQuestionAsked')).toBeFalsy();
// 		});
//
// 		it('defaults isAlert to false', function () {
// 			expect(model.get('isAlert')).toBeFalsy();
// 		});
//
// 		it('defaults status to false', function () {
// 			expect(model.get('status')).toBeFalsy();
// 		});
//
// 		it('defaults with a subStatus', function () {
// 			expect(model.has('subStatus')).toBeTruthy();
// 		});
//
// 		it('defaults with a subStatus of type Backbone.Model', function () {
// 			expect(model.get('subStatus') instanceof Backbone.Model).toBeTruthy();
// 		});
//
// 		it('defaults with a rejectionAction', function () {
// 			expect(model.get('rejectionAction')).toBeFalsy();
// 		});
//
// 		it('defaults with a null onBehalfOfUser', function () {
// 			expect(model.get('onBehalfOfUser')).toBeNull();
// 		});
//
// 		describe('parse', function () {
// 			it('removes null values', function () {
// 				expect(model.parse({ timestamp: null }).timestamp).toBeUndefined();
// 			});
//
// 			it('sets one boolean type attribute to true', function () {
// 				expect(model.parse({ type: 'WORK_RESOURCE_STATUS_CHANGE' }).isWorkResourceStatusChange).toBeTruthy();
// 			});
//
// 			it('sets isAlert to true if the type is `WORK_STATUS_CHANGE` and the status is `Cancelled`', function () {
// 				expect(model.parse({ type: 'WORK_STATUS_CHANGE', status: 'Cancelled' }).isAlert).toBeTruthy();
// 			});
//
// 			it('sets isAlert to true if the type is `WORK_STATUS_CHANGE` and the status is `Void`', function () {
// 				expect(model.parse({ type: 'WORK_STATUS_CHANGE', status: 'Void' }).isAlert).toBeTruthy();
// 			});
//
// 			it('sets isAlert to true if the type is `WORK_STATUS_CHANGE` and the status is `Deleted`', function () {
// 				expect(model.parse({ type: 'WORK_STATUS_CHANGE', status: 'Deleted' }).isAlert).toBeTruthy();
// 			});
//
// 			it('sets isAlert to true if the type is `WORK_STATUS_CHANGE` and the status is `Cancelled - Payment Pending`', function () {
// 				expect(model.parse({ type: 'WORK_STATUS_CHANGE', status: 'Cancelled - Payment Pending' }).isAlert).toBeTruthy();
// 			});
//
// 			it('sets isAlert to true if the type is `WORK_STATUS_CHANGE` and the status is `Cancelled and Paid`', function () {
// 				expect(model.parse({ type: 'WORK_STATUS_CHANGE', status: 'Cancelled and Paid' }).isAlert).toBeTruthy();
// 			});
//
// 			it('sets isAlert to true if the type is `WORK_RESOURCE_STATUS_CHANGE` and the status is `cancelled`', function () {
// 				expect(model.parse({ type: 'WORK_RESOURCE_STATUS_CHANGE', status: 'cancelled' }).isAlert).toBeTruthy();
// 			});
//
// 			it('sets a `timestampDate` attribute when passed a `timestamp` value', function () {
// 				expect(model.parse({ timestamp: 0 }).timestampDate).toBeDefined();
// 			});
//
// 			it('sets a `timestampDate` attribute which is a formatted date string', function () {
// 				expect(model.parse({ timestamp: 0 }).timestampDate).toBe(moment(0).format('M/DD/YY h:mma'));
// 			});
//
// 			it('sets a `subStatus` attribute of type Backbone.Model', function () {
// 				expect(model.parse({ subStatus: { foo: 'bar' } }).subStatus).toBeDefined();
// 				expect(model.parse({ subStatus: { foo: 'bar' } }).subStatus instanceof Backbone.Model).toBeTruthy();
// 			});
// 		});
//
// 		describe('toJSON', function () {
// 			it('calls the Backbone.Model toJSON prototype function', function () {
// 				spyOn(Backbone.Model.prototype, 'toJSON').and.callThrough();
// 				model.toJSON();
// 				expect(Backbone.Model.prototype.toJSON).toHaveBeenCalled();
// 			});
//
// 			it('calls the toJSON method on subStatus', function () {
// 				spyOn(model.get('subStatus'), 'toJSON').and.callThrough();
// 				model.toJSON();
// 				expect(model.get('subStatus').toJSON).toHaveBeenCalled();
// 			});
// 		});
// 	});
// });
