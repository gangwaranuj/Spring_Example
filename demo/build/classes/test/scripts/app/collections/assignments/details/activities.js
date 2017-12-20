// define([
// 	'assignments/activity_model',
// 	'assignments/activities_collection'
// ], function (ActivityModel, ActivitiesCollection) {
// 	'use strict';
//
// 	describe('ActivitiesCollection', function () {
// 		var collection;
//
// 		beforeEach(function () {
// 			spyOn(ActivitiesCollection.prototype, 'fetch').and.callThrough();
// 			collection = new ActivitiesCollection([], { id: 0 });
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
// 		it('contains models of type ActivityModel', function () {
// 			expect(collection.model).toBe(ActivityModel)
// 		});
//
// 		it('defaults the `id` to 0', function () {
// 			expect(collection.id).toBe(0);
// 		});
//
// 		it('has a url of `/assignments/[work_number]/activities`', function () {
// 			expect(collection.url()).toBe('/assignments/0/activities');
// 		});
// 	});
// });
