// define([
// 	'assignments/parts_withtracking_collection',
// 	'assignments/parts_withtracking_model'
// ], function (PartsWithTrackingCollection, PartsModel) {
// 	'use strict';
//
// 	describe('PartsWithTrackingCollection', function () {
// 		var collection;
//
// 		beforeEach(function () {
// 			collection = new PartsWithTrackingCollection();
// 		});
//
// 		it('has a global instance', function () {
// 			expect(PartsWithTrackingCollection).toBeDefined();
// 		});
//
// 		it('can be instantiated', function () {
// 			expect(collection).toBeDefined();
// 		});
//
// 		it('contains models of type PartsModel', function () {
// 			expect(collection.model).toBe(PartsModel);
// 		});
//
//
// 		describe('calculateTotalPrice', function () {
// 			it('returns the `total` price of all models', function () {
// 				collection.add([
// 					{
// 						partValue: '50.00',
// 						isReturn: true
// 					},
// 					{
// 						partValue: '50.00',
// 						isReturn: false
// 					},
// 					{
// 						partValue: '50.00',
// 						isReturn: true
// 					},
// 					{
// 						partValue: '50.00',
// 						isReturn: true
// 					}
// 				]);
// 				expect(collection.calculateTotalPrice(true)).toEqual('150.00');
// 				expect(collection.calculateTotalPrice(false)).toEqual('50.00');
// 			});
// 		});
// 	});
// });
