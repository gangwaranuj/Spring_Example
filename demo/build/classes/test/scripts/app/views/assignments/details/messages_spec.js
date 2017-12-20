// define([
// 	'assignments/messages_view'
// ], function (MessagesView) {
// 	'use strict';
//
// 	describe('MessagesView', function () {
// 		var view;
//
// 		beforeEach(function () {
// 			spyOn(MessagesView.prototype, 'resetCollection');
// 			spyOn(MessagesView.prototype, 'render');
// 			spyOn($.fn, 'html').and.returnValue('');
// 			spyOn($, 'trim').and.returnValue('');
// 			wm.templates = jasmine.createSpyObj('templates', ['assignments/details/messages']);
// 			view = new MessagesView({
// 				questions: new Backbone.Collection(),
// 				messages: new Backbone.Collection()
// 			});
// 		});
//
// 		describe('collection', function () {
// 			var question = {
// 					creator: 1,
// 					createdOn: 1,
// 					isQuestion: true,
// 					responses: []
// 				},
// 				message = {
// 					creator: 1,
// 					createdOn: 2,
// 					responses: []
// 				},
// 				answeredQuestion = {
// 					creator: 1,
// 					createdOn: 1,
// 					isQuestion: true,
// 					responses: [ new Backbone.Model({ creator: 2, createdOn: 3 }) ]
// 				};
//
// 			it('is defined', function () {
// 				expect(view.collection).toBeDefined();
// 			});
//
// 			it('is a Backbone Collection', function () {
// 				expect(view.collection instanceof Backbone.Collection).toBeTruthy();
// 			});
//
// 			it('sorts mixed questions/messages chronologically descending', function () {
// 				view.collection.set([ message, question ]);
// 				expect(view.collection.last().attributes).toEqual(message);
// 			});
//
// 			it('sorts messages behind questions with newer answers', function () {
// 				view.collection.set([ message, answeredQuestion ]);
// 				expect(view.collection.last().attributes).toEqual(answeredQuestion);
// 			});
// 		});
// 	});
// });
