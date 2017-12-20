define([
	'onboarding/industry_model',
	'onboarding/industry_collection'
], function (IndustryModel, IndustryCollection) {
	'use strict';

	describe('App.Onboarding.Collections.IndustryCollection', function () {
		var collection;

		beforeEach(function () {
			collection = new IndustryCollection();
		});

		afterEach(function () {
			collection = undefined;
		});

		it('has a global instance', function () {
			expect(IndustryCollection).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(collection).toBeDefined();
		});

		it('collects IndustryModel objects', function () {
			expect(collection.model).toBe(IndustryModel);
		});

		it('is invalid by default', function () {
			expect(collection.isValid()).toBeFalsy();
		});

		it('is valid if some are checked', function () {
			collection.add([
				{ checked: true },
				{ checked: false }
			]);
			expect(collection.isValid()).toBeTruthy();
		});

		it('is invalid if none of the industries are checked', function () {
			collection.add([
				{ checked: false },
				{ checked: false }
			]);
			expect(collection.isValid()).toBeFalsy();
		});
	});
});
