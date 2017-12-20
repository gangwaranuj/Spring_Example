define([
	'onboarding/main_model',
	'onboarding/onboard_model',
	'onboarding/profile_info_model',
	'onboarding/location_model',
	'onboarding/legal_model',
	'onboarding/industries_model'
], function (MainModel, OnboardModel, ProfileInfoModel, LocationModel, LegalModel, IndustriesModel) {
	'use strict';

	describe('App.Onboarding.Models.MainModel', function () {
		var model;

		beforeEach(function () {
			model = new MainModel({ id: 0 });

			jasmine.addMatchers({
				toHaveError: function () {
					return {
						compare: function (actual, expected) {
							var result = {};
							result.pass = _.contains(_.pluck(actual, 'message'), expected || '');
							if (result.pass) {
								result.message = 'Expected ' + actual + ' to have error ' + expected;
							} else {
								result.message = 'Expected ' + actual + ' not to have error ' + expected;
							}
							return result;
						}
					}
				}
			});
		});

		afterEach(function () {
			model = undefined;
		});

		it('has a global instance', function () {
			expect(MainModel).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(model).toBeDefined();
		});

		it('initializes with a profileInfo submodel', function () {
			expect(model.profileInfo).toBeDefined();
			expect(model.profileInfo instanceof ProfileInfoModel).toBeTruthy();
		});

		it('initializes with a location submodel', function () {
			expect(model.location).toBeDefined();
			expect(model.location instanceof LocationModel).toBeTruthy();
		});

		it('initializes with a industries submodel', function () {
			expect(model.industries).toBeDefined();
			expect(model.industries instanceof IndustriesModel).toBeTruthy();

		});

		it('passes its id to the profileInfo submodel', function () {
			expect(model.profileInfo.id).toBe(model.id);
		});

		it('passes its id to the location submodel', function () {
			expect(model.location.id).toBe(model.id);
		});

		it('passes its id to the industries submodel', function () {
			expect(model.industries.id).toBe(model.id);
		});
	});
});
