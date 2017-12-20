// define([
// 	'assignments/message_model',
// 	'user/user_model',
// 	'assignments/users_collection',
// 	'moment'
// ], function (MessageModel, UserModel, UsersCollection, moment) {
// 	'use strict';
//
// 	describe('MessageModel', function () {
// 		var model;
//
// 		beforeEach(function () {
// 			spyOn($, 'ajax');
// 			model = new MessageModel({
// 				id: 0
// 			});
//
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
// 		it('is by default invalid', function () {
// 			expect(model.isValid()).toBeFalsy();
// 		});
//
// 		it('defaults with an creator', function () {
// 			expect(model.has('creator')).toBeTruthy();
// 		});
//
// 		it('defaults with an creator of type UserModel', function () {
// 			expect(model.get('creator') instanceof UserModel).toBeTruthy();
// 		});
//
// 		it('by default is not a question', function () {
// 			expect(model.get('isQuestion')).toBeFalsy();
// 		});
//
// 		describe('parse', function () {
// 			beforeEach(function () {
// 				window.App = {};
// 			});
//
// 			it('removes null values', function () {
// 				expect(model.parse({ createdOn: null }).createdOn).toBeUndefined();
// 			});
//
// 			it('sets an `creator` attribute of type UserModel', function () {
// 				expect(model.parse({ creatorNumber: '1234' }).creator).toBeDefined();
// 				expect(model.parse({ creatorNumber: '1234' }).creator instanceof UserModel).toBeTruthy();
// 			});
//
// 			it('binds a listener onto the creator for `change` events', function () {
// 				spyOn(model, 'trigger');
// 				model.parse({ creatorNumber: '1234' });
// 				UsersCollection.pop().trigger('change');
// 				expect(model.trigger.calls.argsFor(0)).toEqual(['change']);
// 			});
//
// 			it('sets an `onBehalfUser` attribute of type UserModel', function () {
// 				expect(model.parse({ onBehalfUserNumber: '1234' }).onBehalfUser).toBeDefined();
// 				expect(model.parse({ onBehalfUserNumber: '1234' }).onBehalfUser instanceof UserModel).toBeTruthy();
// 			});
//
// 			it('binds a listener onto the onBehalfUser for `change` events', function () {
// 				spyOn(model, 'trigger');
// 				model.parse({ onBehalfUserNumber: '1234' });
// 				UsersCollection.pop().trigger('change');
// 				expect(model.trigger.calls.argsFor(0)).toEqual(['change']);
// 			});
//
// 			it('sets a `createdOnDate` attribute when passed a `createdOn` value', function () {
// 				expect(model.parse({ createdOn: 0 }).createdOnDate).toBeDefined();
// 			});
//
// 			it('sets a `createdOnDate` attribute which is a formatted date string', function () {
// 				expect(model.parse({ createdOn: 0 }).createdOnDate).toBe(moment(0).format('M/DD/YY h:mma'));
// 			});
//
// 			it('loops through the `responses` value and creates a new message for each response', function () {
// 				var responses = model.parse({ responses: [{ createdOn: 0 }] }).responses;
// 				expect(responses.length).toBeGreaterThan(0);
// 				expect(responses[0] instanceof MessageModel).toBeTruthy();
// 			});
// 		});
//
// 		describe('validate', function () {
// 			it('returns an error if no privacy level is set', function () {
// 				expect(model.validate({})).toBeDefined();
// 			});
//
// 			it('returns the error `Message needs a privacy level.` if no privacy level is set', function () {
// 				expect(model.validate({})).toEqual('Message needs a privacy level.');
// 			});
//
// 			it('does not return an error if the message is public', function () {
// 				expect(model.validate({ isPublic: true, content: 'content' })).toBeUndefined();
// 			});
//
// 			it('does not return an error if the message is privileged', function () {
// 				expect(model.validate({ isPrivileged: true, content: 'content' })).toBeUndefined();
// 			});
//
// 			it('does not return an error if the message is private', function () {
// 				expect(model.validate({ isPrivate: true, content: 'content' })).toBeUndefined();
// 			});
//
// 			it('returns an error if the message is an empty string', function () {
// 				expect(model.validate({ content: '', isPublic: true })).toBeDefined();
// 			});
//
// 			it('returns an error if the message is undefined', function () {
// 				expect(model.validate({ isPublic: true })).toBeDefined();
// 			});
//
// 			it('returns an error if the message is a question and the question is an empty string', function () {
// 				expect(model.validate({ question: '', isPublic: true, isQuestion: true })).toBeDefined();
// 			});
//
// 			it('returns an error if the message is a question and the question is undefined', function () {
// 				expect(model.validate({ isPublic: true, isQuestion: true })).toBeDefined();
// 			});
//
// 			it('returns the error `Message cannot be empty.` if the message is empty', function () {
// 				expect(model.validate({ content: '', isPublic: true })).toEqual('Message cannot be empty.');
// 			});
// 		});
// 	});
// });
