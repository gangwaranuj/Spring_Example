// define([
// 	'assignments/message_model',
// 	'assignments/messages_collection'
// ], function (MessageModel, MessagesCollection) {
// 	'use strict';
//
// 	describe('MessagesCollection', function () {
// 		var collection;
//
// 		beforeEach(function () {
// 			spyOn(MessagesCollection.prototype, 'fetch').and.callThrough();
// 			collection = new MessagesCollection([], { id: 0 });
// 		});
//
// 		afterEach(function () {
// 			collection = undefined;
// 		});
//
// 		it('can be instantiated', function () {
// 			expect(collection).toBeDefined();
// 		});
//
// 		it('contains models of type MessageModel', function () {
// 			expect(collection.model).toBe(MessageModel)
// 		});
//
// 		it('defaults the `id` to 0', function () {
// 			expect(collection.id).toBe(0);
// 		});
//
// 		it('has a url of `/assignments/[work_number]/messages` by default', function () {
// 			expect(collection.url()).toBe('/assignments/0/messages');
// 		});
//
// 		it('has a url of `/assignments/[work_number]/questions` if `hasQuestions` is true', function () {
// 			collection = new MessagesCollection([], { id: 0, hasQuestions: true });
// 			expect(collection.url()).toBe('/assignments/0/questions');
// 		});
//
// 		it('fetches on initialize', function () {
// 			expect(collection.fetch).toHaveBeenCalled();
// 		})
//
// 		describe('parse', function () {
// 			it('returns the `results` property of the payload', function () {
// 				var results = [{ id: 1 }];
// 				expect(collection.parse({ results: results })).toEqual(results);
// 			});
// 		});
// 	});
// });
