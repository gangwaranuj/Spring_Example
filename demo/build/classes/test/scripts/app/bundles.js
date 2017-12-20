// define([
// 	'bundles/create_assignment_bundle_model',
// 	'bundles/view_assignment_bundle_collection'
// ], function (BundleModel, BundleCollection) {
// 	'use strict';
//
// 	describe('Models', function () {
// 		describe('assignment bundle', function () {
// 			it('should exhibit attributes', function () {
// 				spyOn($, 'ajax');
// 				var bundle = new BundleModel();
// 				bundle.save({ title: 'Hello' });
// 				expect(bundle.get('title')).toEqual('Hello');
// 				expect($.ajax.calls.count()).toEqual(1);
// 				expect($.ajax.calls.mostRecent().args[0].type).toEqual('POST');
// 				expect($.ajax.calls.mostRecent().args[0].url).toEqual('/assignments/create_bundle');
// 			});
// 		});
//
// 		describe('bundle with overview', function () {
// 			it('should show buyer data', function () {
// 				spyOn($, 'ajax').and.callFake(function (options) {
// 					options.success({
// 						data: {
// 							overview: {
// 								assignments: 2,
// 								dates: {
// 									from: '01/01/2013',
// 									to: '01/31/2013'
// 								},
// 								budget: '2160.01',
// 								owner: 'Doug Bay'
// 							},
// 							assignments: [
// 								{
// 									title: 'abc',
// 									location: 'def',
// 									due: '01/01/2013',
// 									budget: '2000',
// 									status: 'draft',
// 									workNumber: '1234'
// 								},
// 								{
// 									title: 'ghi',
// 									location: 'jkl',
// 									due: '01/31/2013',
// 									budget: '160.01',
// 									status: 'draft',
// 									workNumber: '5678'
// 								}
// 							]
// 						}
// 					});
// 				});
// 				var bundle = new BundleCollection([], { parentId: 123 });
// 				bundle.fetch();
// 				expect($.ajax.calls.count()).toBe(1);
// 				expect($.ajax.calls.mostRecent().args[0].type).toBe('GET');
// 				expect($.ajax.calls.mostRecent().args[0].url).toBe('/assignments/bundle_overview/123');
// 				expect(bundle.models.length).toEqual(2);
// 				expect(bundle.overview.assignments).toBe(2);
// 			});
// 		});
// 	});
// });
