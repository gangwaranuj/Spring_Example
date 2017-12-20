define([
	'onboarding/phone_model',
	'wmPatterns'
], function (PhoneModel, wmPatterns) {
	'use strict';

	describe('App.Onboarding.Models.PhoneModel', function () {
		var model;

		beforeEach(function () {
			model = new PhoneModel();
			model.collection = { indexOf: {} };
			wmPatterns = { phone: { test: {} } };
			spyOn(wmPatterns.phone, 'test').and.callFake(function (phone) { return phone === '2125555555'; });
			spyOn(model.collection, 'indexOf').and.returnValue(0);

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
			wmPatterns = undefined;
		});

		it('has a global instance', function () {
			expect(PhoneModel).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(model).toBeDefined();
		});

		it('defaults type to work', function () {
			expect(model.get('type')).toBe('work');
		});

		it('is invalid by default', function () {
			expect(model.isValid()).toBeFalsy();
		});

		it('is invalid if type is empty', function () {
			expect(model.validate({ type: '' })).toHaveError('Type is a required field.');
		});

		it('is invalid if code is empty', function () {
			expect(model.validate({ code: '' })).toHaveError('Code is a required field.');
		});

		it('is invalid if number is empty', function () {
			expect(model.validate({ number: '' })).toHaveError('Number is a required field.');
		});

		it('is invalid if number is an invalid phone number', function () {
			expect(model.validate({ number: '1234' })).toHaveError('Please enter a valid phone number.');
		});
	});
});
