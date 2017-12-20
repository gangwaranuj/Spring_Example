define([
	'onboarding/onboard_model',
	'onboarding/onboard_collection'
], function (OnboardModel, OnboardCollection) {
	'use strict';

	describe('App.Onboarding.Collections.OnboardCollection', function () {
		var collection;

		beforeEach(function () {
			collection = new OnboardCollection([{}, {}]);
		});

		afterEach(function () {
			collection = undefined;
		});

		it('has a global instance', function () {
			expect(OnboardCollection).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(collection).toBeDefined();
		});

		it('collects OnboardModel objects', function () {
			expect(collection.model).toBe(OnboardModel);
		});

		it('is valid if any its models are valid', function () {
			spyOn(collection.at(0), 'isValid').and.returnValue(false);
			spyOn(collection.at(1), 'isValid').and.returnValue(true);
			expect(collection.isValid()).toBeTruthy();
		});

		it('is valid if the collection is optional', function () {
			spyOn(collection.at(0), 'isValid').and.returnValue(false);
			spyOn(collection.at(1), 'isValid').and.returnValue(false);
			collection.isOptional = true;
			expect(collection.isValid()).toBeTruthy();
		});

		it('is invalid if all of its models are invalid', function () {
			spyOn(collection.at(0), 'isValid').and.returnValue(false);
			spyOn(collection.at(1), 'isValid').and.returnValue(false);
			expect(collection.isValid()).toBeFalsy();
		});
	});
});
