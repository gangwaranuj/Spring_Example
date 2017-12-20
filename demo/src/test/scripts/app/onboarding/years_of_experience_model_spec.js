define([
	'onboarding/years_of_experience_model'
], function (YearsOfExperienceModel) {
	'use strict';

	describe('App.Onboarding.Models.YearsOfExperienceModel', function () {
		var model;

		beforeEach(function () {
			model = new YearsOfExperienceModel();
		});

		afterEach(function () {
			model = undefined;
		});

		it('has a global instance', function () {
			expect(YearsOfExperienceModel).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(model).toBeDefined();
		});

		it('defaults checked to false', function () {
			expect(model.get('checked')).toBeFalsy();
		});
	});
});
