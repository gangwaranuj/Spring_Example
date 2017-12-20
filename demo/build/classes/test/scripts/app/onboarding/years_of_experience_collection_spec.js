define([
	'onboarding/years_of_experience_model',
	'onboarding/years_of_experience_collection'
], function (YearsOfExperienceModel, YearsOfExperienceCollection) {
	'use strict';

	describe('App.Onboarding.Collections.YearsOfExperienceCollection', function () {
		var collection;

		beforeEach(function () {
			collection = new YearsOfExperienceCollection();
		});

		afterEach(function () {
			collection = undefined;
		});

		it('has a global instance', function () {
			expect(YearsOfExperienceCollection).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(collection).toBeDefined();
		});

		it('collects YearsOfExperienceModel objects', function () {
			expect(collection.model).toBe(YearsOfExperienceModel);
		});

		it('has a default option of "Years of Work Experience"', function () {
			expect(collection.defaultOption).toBe('Years of Work Experience');
		});

		it('is optional', function () {
			expect(collection.isOptional).toBeTruthy();
		});
	});
});
