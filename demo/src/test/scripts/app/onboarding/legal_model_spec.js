define([
	'jquery',
	'backbone',
	'underscore',
	'onboarding/legal_model',
	'onboarding/image_model',
	'wmPatterns'
], function ($, Backbone, _, LegalModel, ImageModel, wmPatterns) {
	'use strict';

	describe('OnboardingLegalModel', function () {
		var validParameters = {
				id: 0,
				companyName: 'Work Market',
				companyOverview: 'Test',
				companyWebsite: 'workmarket.com',
				companyYearFounded: '2000',
				individual: false
			},
			model;

		beforeEach(function () {
			model = new LegalModel({ id: 0 });
			wmPatterns = { url: { test: {} } };
			spyOn(wmPatterns.url, 'test').and.callFake(function (param) { return param === 'workmarket.com'; });

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
			expect(LegalModel).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(model).toBeDefined();
		});

		it('handles the "companyName" field', function () {
			expect(model.fields()).toContain('companyName');
		});

		it('handles the "companyOverview" field', function () {
			expect(model.fields()).toContain('companyOverview');
		});

		it('handles the "companyWebsite" field', function () {
			expect(model.fields()).toContain('companyWebsite');
		});

		it('handles the "companyYearFounded" field', function () {
			expect(model.fields()).toContain('companyYearFounded');
		});

		it('handles the "individual" field', function () {
			expect(model.fields()).toContain('individual');
		});

		it('handles the "logo" field', function () {
			expect(model.fields()).toContain('logo');
		});

		it('defaults "individual" to true', function () {
			expect(model.get('individual')).toBeTruthy();
		});

		it('is valid if individual is false and all attributes are valid', function () {
			model.set(validParameters);
			expect(model.isValid()).toBeTruthy();
		});

		it('is valid if individual is true', function () {
			model.set('individual', true);
			expect(model.isValid()).toBeTruthy();
		});

		it('is valid by default', function () {
			expect(model.isValid()).toBeTruthy();
		});

		it('is invalid if some of its submodels are invalid', function () {
			spyOn(model.logo, 'isValid').and.returnValue(false);
			model.set(validParameters);
			expect(model.isValid()).toBeFalsy();
		});

		it('is invalid if individual is false and companyName is empty', function () {
			model.set('individual', false);
			expect(model.validate({ companyName: '' })).toHaveError('Name is a required field.');
		});

		it('is invalid if individual is false and companyOverview is empty', function () {
			model.set('individual', false);
			expect(model.validate({ companyOverview: '' })).toHaveError('Overview is a required field.');
		});

		it('is invalid if individual is false and companyWebsite is empty', function () {
			model.set('individual', false);
			expect(model.validate({ companyWebsite: '' })).toHaveError('Website is a required field.');
		});

		it('is invalid if individual is false and companyYearFounded is empty', function () {
			model.set('individual', false);
			expect(model.validate({ companyYearFounded: '' })).toHaveError('Year Founded is a required field.');
		});

		it('is invalid if individual is false and companyName is null', function () {
			model.set('individual', false);
			expect(model.validate({ companyName: null })).toHaveError('Name is a required field.');
		});

		it('is invalid if individual is false and companyOverview is null', function () {
			model.set('individual', false);
			expect(model.validate({ companyOverview: null })).toHaveError('Overview is a required field.');
		});

		it('is invalid if individual is false and companyWebsite is null', function () {
			model.set('individual', false);
			expect(model.validate({ companyWebsite: null })).toHaveError('Website is a required field.');
		});

		it('is invalid if individual is false and companyYearFounded is null', function () {
			model.set('individual', false);
			expect(model.validate({ companyYearFounded: null })).toHaveError('Year Founded is a required field.');
		});

		it('is invalid if individual is false and companyWebsite is an invalid URI', function () {
			model.set('individual', false);
			expect(model.validate({ companyWebsite: 'work@market' })).toHaveError('Website is not a valid URI.');
		});

		it('is invalid if companyOverview is over 1000 characters', function () {
			expect(model.validate({ companyOverview: 'Irure anim adipisicing mollit exercitation do elit ea. Labore cillum labore incididunt eu magna qui anim anim. Qui dolore cillum eu eiusmod non ad irure ad. Nisi mollit culpa cupidatat excepteur ut proident cupidatat et dolore Lorem ea laboris aliquip. Labore esse deserunt ex cupidatat ad proident velit. Proident amet eu et proident nostrud aute labore officia aliqua anim nulla esse ut id. Occaecat id cillum nisi Lorem cupidatat est. Proident minim nulla ut laborum adipisicing. Dolore ullamco ex mollit consequat ipsum excepteur sit incididunt magna. Irure anim adipisicing mollit exercitation do elit ea. Labore cillum labore incididunt eu magna qui anim anim. Qui dolore cillum eu eiusmod non ad irure ad. Nisi mollit culpa cupidatat excepteur ut proident cupidatat et dolore Lorem ea laboris aliquip. Labore esse deserunt ex cupidatat ad proident velit. Proident amet eu et proident nostrud aute labore officia aliqua anim nulla esse ut id. Occaecat id cillum nisi Lorem cupidatat est. Proident minim nulla ut laborum adipisicing. Dolore ullamco ex mollit consequat ipsum excepteur sit incididunt magna.' })).toHaveError('Company Overview must be 1000 characters or less.');
		});

		describe('logo', function () {
			it('is created on initialize', function () {
				expect(model.logo).toBeDefined();
			});

			it('is a Backbone model', function () {
				expect(model.logo instanceof Backbone.Model).toBeTruthy();
			});

			it('is an instance of ImageModel', function () {
				expect(model.logo instanceof ImageModel).toBeTruthy();
			});
		});

		describe('fetch', function () {
			var payload = {};

			beforeEach(function () {
				spyOn($, 'ajax').and.callFake(function (options) { options.success(payload); });
				spyOn(model, 'parse');
				model.fetch();
			});

			it('fetches asynchronously', function () {
				expect($.ajax).toHaveBeenCalled();
			});

			it('makes only one asynchronous call', function () {
				expect($.ajax.calls.count()).toBe(1);
			});

			it('fetches via HTTP GET', function () {
				expect($.ajax.calls.mostRecent().args[0].type).toBe('GET');
			});

			it('fetches from /onboarding/profiles/:id?flds=individual,companyName,companyOverview,companyWebsite,companyYearFounded,logo', function () {
				expect($.ajax.calls.mostRecent().args[0].url).toBe('/onboarding/profiles/0?flds=individual,companyName,companyOverview,companyWebsite,companyYearFounded,logo');
			});

			it('passes the return payload to parse', function () {
				expect(model.parse.calls.mostRecent().args[0]).toBe(payload);
			});
		});

		describe('parse', function () {
			it('sets the logo url', function () {
				model.parse({ logo: { url: '/home' } });
				expect(model.logo.get('url')).toBe('/home');
			});

			it('does not set null values', function () {
				model.parse({ foo: null });
				expect(model.get('foo')).toBeUndefined();
			});
		});

		describe('update', function () {
			beforeEach(function () {
				spyOn($, 'ajax').and.callFake(function () { return {}; });
				spyOn(model, 'sync').and.callThrough();
				spyOn(model, 'unset').and.callThrough();
				model.update();
			});

			it('fires Backbone\'s sync function', function () {
				expect(model.sync).toHaveBeenCalled();
			});

			it('syncs with a method', function () {
				expect(model.sync.calls.mostRecent().args[0]).toEqual(jasmine.any(String));
			});

			it('syncs with a payload', function () {
				expect(model.sync.calls.mostRecent().args[1]).toEqual(jasmine.any(Object));
			});

			it('sends an async request', function () {
				expect($.ajax).toHaveBeenCalled();
			});

			it('makes only one asynchronous call', function () {
				expect($.ajax.calls.count()).toBe(1);
			});

			it('sends via HTTP PUT', function () {
				expect($.ajax.calls.mostRecent().args[0].type).toBe('PUT');
			});

			it('sends the model attributes', function () {
				var payload = JSON.parse($.ajax.calls.mostRecent().args[0].data);
				expect(model.toJSON()).toEqual(payload);
			});

			it('sends only the model id and `individual` attribute if `individual` is true', function () {
				model.set('individual', true);
				model.update();
				var payload = JSON.parse($.ajax.calls.mostRecent().args[0].data);
				expect(model.toJSON()).toEqual(payload);
			});

			it('syncs a payload containing the logo model', function () {
				expect(model.sync.calls.mostRecent().args[1].logo instanceof ImageModel).toBeTruthy();
			});

			it('returns true', function () {
				expect(model.update()).toBeTruthy();
			});
		});
	});
});
