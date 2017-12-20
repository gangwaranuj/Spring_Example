define([
	'onboarding/industry_model'
], function (IndustryModel) {
	'use strict';

	describe('App.Onboarding.Models.IndustryModel', function () {
		var model;

		beforeEach(function () {
			model = new IndustryModel();

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
			expect(IndustryModel).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(model).toBeDefined();
		});

		it('is initialized as a valid model', function () {
			expect(model.isValid()).toBeTruthy();
		});

		it('does not accept an empty name', function () {
			expect(model.validate({ name: '' })).toHaveError('Industry name is a required field.');
		});

		it('does not accept name as a boolean', function () {
			expect(model.validate({ name: true })).toHaveError('Industry name needs to be a string.');
		});

		it('does not accept name as an object', function () {
			expect(model.validate({ name: {} })).toHaveError('Industry name needs to be a string.');
		});

		it('does not accept name as an array', function () {
			expect(model.validate({ name: [] })).toHaveError('Industry name needs to be a string.');
		});

		it('does not accept name as a number', function () {
			expect(model.validate({ name: 1 })).toHaveError('Industry name needs to be a string.');
		});

		it('does not accept name as null', function () {
			expect(model.validate({ name: null })).toHaveError('Industry name needs to be a string.');
		});

		it('does not accept an empty id', function () {
			expect(model.validate({ id: '' })).toHaveError('Industry id is a required field.');
		});

		it('does not accept id as a boolean', function () {
			expect(model.validate({ id: true })).toHaveError('Industry id needs to be a number.');
		});

		it('does not accept id as an object', function () {
			expect(model.validate({ id: {} })).toHaveError('Industry id needs to be a number.');
		});

		it('does not accept id as an array', function () {
			expect(model.validate({ id: [] })).toHaveError('Industry id needs to be a number.');
		});

		it('does not accept id as a negative number', function () {
			expect(model.validate({ id: -1 })).toHaveError('Industry id cannot be negative.');
		});

		it('does not accept id as null', function () {
			expect(model.validate({ id: null })).toHaveError('Industry id needs to be a number.');
		});

		it('does not accept an empty checked value', function () {
			expect(model.validate({ checked: '' })).toHaveError('Industry checked is a required field.');
		});

		it('does not accept checked as a number', function () {
			expect(model.validate({ checked: 0 })).toHaveError('Industry checked needs to be a boolean.');
		});

		it('does not accept checked as an object', function () {
			expect(model.validate({ checked: {} })).toHaveError('Industry checked needs to be a boolean.');
		});

		it('does not accept checked as an array', function () {
			expect(model.validate({ checked: [] })).toHaveError('Industry checked needs to be a boolean.');
		});

		it('does not accept checked as null', function () {
			expect(model.validate({ checked: null })).toHaveError('Industry checked needs to be a boolean.');
		});

		it('defaults checked to false', function () {
			expect(model.get('checked')).toBeFalsy();
		});
	});
});
