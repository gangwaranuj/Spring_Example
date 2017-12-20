// define([
// 	'user/user_model'
// ], function (UserModel) {
// 	'use strict';
//
// 	describe('UserModel', function () {
// 		var model;
//
// 		beforeEach(function () {
// 			model = new UserModel({id: 0});
// 		});
//
// 		afterEach(function () {
// 			model = undefined;
// 		});
//
// 		it('has a global instance', function () {
// 			expect(UserModel).toBeDefined();
// 		});
//
// 		it('can be instantiated', function () {
// 			expect(model).toBeDefined();
// 		});
//
// 		it('is always valid', function () {
// 			expect(model.isValid()).toBeTruthy();
// 		});
//
// 		it('has a urlRoot of \'/user\'', function () {
// 			expect(model.urlRoot).toBe('/user');
// 		});
//
// 		it('defaults `fullName` to \'A User\'', function () {
// 			expect(model.get('fullName')).toBe('A User');
// 		});
//
// 		it('defaults `isCurrentUser` to false', function () {
// 			expect(model.get('isCurrentUser')).toBeFalsy();
// 		});
//
// 		describe('parse', function () {
// 			var parameters = {
// 				firstName: 'John',
// 				lastName: 'Doe'
// 			};
//
// 			it('combines `firstName` and `lastName` into `fullName`', function () {
// 				expect(model.parse(parameters).fullName).toBeDefined();
// 				expect(model.parse(parameters).fullName).toBe('John Doe');
// 			});
// 		});
// 	});
//
// });
