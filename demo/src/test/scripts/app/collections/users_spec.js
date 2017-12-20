// define([
// 	'backbone',
// 	'underscore',
// 	'user/user_model',
// 	'assignments/users_collection'
// ], function (Backbone, _, UserModel, UsersCollection) {
// 	'use strict';
//
// 	describe('UserCollection', function () {
// 		beforeEach(function () {
// 			spyOn($, 'ajax');
// 		});
//
// 		it('has a global instance', function () {
// 			expect(UsersCollection).toBeDefined();
// 		});
//
// 		it('contains models of type UserModel', function () {
// 			expect(UsersCollection.model).toBe(UserModel);
// 		});
//
// 		it('has a url of \'/user\'', function () {
// 			expect(UsersCollection.url).toEqual('/user');
// 		});
//
// 		describe('add', function () {
// 			var collectionAddSpy, collectionFetchSpy, modelFetchSpy;
//
// 			beforeEach(function () {
// 				UsersCollection.reset();
// 				collectionAddSpy = spyOn(Backbone.Collection.prototype, 'add').and.callThrough();
// 				collectionFetchSpy = spyOn(UsersCollection, 'fetch').and.callThrough();
// 				modelFetchSpy = spyOn(UserModel.prototype, 'fetch').and.callThrough();
// 			});
//
// 			afterEach(function () {
// 				UsersCollection.reset();
// 			});
//
// 			it('adds a model with Backbone.Collection.add', function () {
// 				var attributes = { id: 1 };
// 				UsersCollection.add(attributes);
// 				expect(collectionAddSpy).toHaveBeenCalledWith(attributes, undefined);
// 			});
//
// 			it('fetches the model if it was not already in the collection', function () {
// 				UsersCollection.add({ id: 1 });
// 				expect(modelFetchSpy).toHaveBeenCalled();
// 			});
//
// 			it('does not fetch the model if it was already in the collection', function () {
// 				UsersCollection.add({ id: 1 });
// 				UsersCollection.add({ id: 1 });
// 				expect(modelFetchSpy.calls.count()).toEqual(1);
// 			});
//
// 			it('returns the model', function () {
// 				expect(UsersCollection.add({}) instanceof UserModel).toBeTruthy();
// 			});
//
// 			it('adds an array of models', function () {
// 				var attributes = [{ id: 1 }, { id: 2 }];
// 				UsersCollection.add(attributes);
// 				expect(collectionAddSpy).toHaveBeenCalledWith(attributes, undefined);
// 			});
//
// 			it('fetches the models if they were not already in the collection', function () {
// 				UsersCollection.add([{ id: 1 }, { id: 2 }]);
// 				expect(collectionFetchSpy).toHaveBeenCalled();
// 			});
//
// 			it('does not fetch the models if they were already in the collection', function () {
// 				UsersCollection.add([{ id: 1 }, { id: 2 }]);
// 				UsersCollection.add([{ id: 1 }, { id: 2 }]);
// 				expect(collectionFetchSpy.calls.count()).toEqual(1);
// 			});
//
// 			it('returns the models', function () {
// 				expect(UsersCollection.add([{}, {}])[0]).toBe(UsersCollection.at(0));
// 			});
//
// 			it('fetches if the `fetch` property was explicitly set to true', function () {
// 				UsersCollection.add({ id: 1 });
// 				UsersCollection.add({ id: 1 }, { fetch: true });
// 				expect(modelFetchSpy.calls.count()).toEqual(2);
// 			});
//
// 			it('fetches multiple models with a traditional style of param serialization', function () {
// 				UsersCollection.add([{ id: 1 }, { id: 2 }]);
// 				expect(collectionFetchSpy.calls.mostRecent().args[0].traditional).toBeTruthy();
// 			});
//
// 			it('sends an array of user IDs during a fetch for multiple models', function () {
// 				var models = UsersCollection.add([{ id: 1 }, { id: 2 }]);
// 				expect(collectionFetchSpy.calls.mostRecent().args[0].data.userNumbers).toEqual(_.pluck(models, 'id'));
// 			});
// 		});
// 	});
// });
