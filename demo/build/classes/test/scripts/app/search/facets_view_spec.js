//define([
//	'search/facets_view'
//], function (FacetsView) {
//	'use strict';
//
//	var Sortable = function() {};
//	Sortable.create = function() { return false; };
//	window.Sortable = Sortable;
//
//	describe('FacetsView', function () {
//		var view;
//
//		beforeEach(function () {
//			view = new FacetsView({});
//		});
//
//		afterEach(function () {
//			view = undefined;
//		});
//
//		it('can be instantiated', function () {
//			expect(view).toBeDefined();
//		});
//
//		it('searchType defaults to value in option', function() {
//			var searchFor = 'Spock';
//			var options = {
//				searchType: searchFor
//			};
//			view = new FacetsView(options);
//			expect(view.searchType).toBe(searchFor);
//		});
//
//		it('internal pricing defaults searchType to workers', function() {
//			var options = {
//				pricing_type: 'INTERNAL',
//				searchType: 'vendor'
//			};
//			view = new FacetsView(options);
//			expect(view.searchType).toBe('workers');
//		});
//
//		it('internal pricing restricts lanes 2-4', function() {
//			var mode = 'anyMode';
//			var options = {
//				pricing_type: 'INTERNAL',
//				searchType: 'vendor'
//			};
//			view = new FacetsView(options);
//			expect(view.getRestrictedLanes(mode)).toEqual([2, 3, 4]);
//		});
//
//		it('dispatch search mode restricts lanes 0 and 2-4', function() {
//			var mode = 'dispatch';
//			var options = {
//				searchType: 'workers'
//			};
//			view = new FacetsView(options);
//			expect(view.getRestrictedLanes(mode)).toEqual([0, 2, 3, 4]);
//		});
//
//		it('dispatch search mode restricts lane 1', function() {
//			var mode = 'workers';
//			var options = {
//				searchType: 'workers'
//			};
//			view = new FacetsView(options);
//			expect(view.getRestrictedLanes(mode)).toEqual([1]);
//		});
//	});
//});
