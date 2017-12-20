define([
	'onboarding/onboard_view',
	'onboarding/onboard_model'
], function (OnboardView, OnboardModel) {
	'use strict';

	describe('App.Onboarding.Views.OnboardView', function () {
		var view, minimumRequiredParameters;

		beforeEach(function () {
			minimumRequiredParameters = {
				model: new OnboardModel()
			};
			view = new OnboardView(minimumRequiredParameters);
		});

		afterEach(function () {
			minimumRequiredParameters = view = undefined;
		});

		it('has a global instance', function () {
			expect(OnboardView).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(view).toBeDefined();
		});

		it('initializes with an element', function () {
			expect(view.el).toBeDefined();
		});

		it('initializes with events', function () {
			expect(view.events).toBeDefined();
		});

		it('calls setData when onboarding information is changed', function () {
			expect(view.events['change [data-onboarding]']).toBe('setData');
		});

		describe('setData', function () {
			var event;

			beforeEach(function () {
				event = {
					target: {
						name: 'Joe',
						value: 2
					}
				};
			});

			afterEach(function () {
				event = undefined;
			});

			it('can be called', function () {
				expect(view.setData).toBeDefined();
			});

			it('sets the value in the model to undefined without a target.name property', function () {
				delete event.target.name;
				view.setData(event);
				expect(view.model.values()).not.toContain(event.target.value);
			});

			it('does not set the value in the model without a target.value property', function () {
				delete event.target.value;
				view.setData(event);
				expect(view.model.get(event.target.name)).toBeUndefined();
			});

			it('sets the attribute called target.name in the model to target.value', function () {
				view.setData(event);
				expect(view.model.get(event.target.name)).toBe(event.target.value);
			});
		});
	});
});
