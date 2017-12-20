define([
	'onboarding/location_model'
], function (LocationModel) {
	'use strict';

	describe('OnboardingLocationModel', function () {
		var model;

		beforeEach(function () {
			model = new LocationModel({ id: 0 });

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
			expect(LocationModel).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(model).toBeDefined();
		});

		it('is the second step of Worker Onboarding', function () {
			expect(model.step).toBe(2);
		});

		it('handles the "address" field', function () {
			expect(model.fields()).toContain('address');
		});

		it('handles the "address1" field', function () {
			expect(model.fields()).toContain('address1');
		});

		it('handles the "city" field', function () {
			expect(model.fields()).toContain('city');
		});

		it('handles the "postalCode" field', function () {
			expect(model.fields()).toContain('postalCode');
		});

		it('handles the "maxTravelDistance" field', function () {
			expect(model.fields()).toContain('maxTravelDistance');
		});

		it('handles the "numberOfNearbyAssignments" field', function () {
			expect(model.fields()).toContain('numberOfNearbyAssignments');
		});

		it('handles the "stateShortName" field', function () {
			expect(model.fields()).toContain('stateShortName');
		});

		it('handles the "countryIso" field', function () {
			expect(model.fields()).toContain('countryIso');
		});

		it('defaults "maxTravelDistance" to 60 miles', function () {
			expect(model.get('maxTravelDistance')).toBe(150);
		});

		it('defaults "numberOfNearbyAssignments" to 0', function () {
			expect(model.get('numberOfNearbyAssignments')).toBe(0);
		});

		it('is invalid by default', function () {
			expect(model.isValid()).toBeFalsy();
		});

		it('is invalid if address1 is empty', function () {
			expect(model.validate({ address1: '' })).toHaveError('Cannot find a valid Street Address.');
		});

		it('is invalid if countryIso is empty', function () {
			expect(model.validate({ countryIso: '' })).toHaveError('Cannot find a valid Country Code.');
		});

		it('is invalid if address1 is null', function () {
			expect(model.validate({ address1: null })).toHaveError('Cannot find a valid Street Address.');
		});

		it('is invalid if countryIso is null', function () {
			expect(model.validate({ countryIso: null })).toHaveError('Cannot find a valid Country Code.');
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

			it('fetches from /onboarding/profiles/:id?flds=maxTravelDistance,numberOfNearbyAssignments,address1,city,stateShortName,countryIso,postalCode,address', function () {
				expect($.ajax.calls.mostRecent().args[0].url).toBe('/onboarding/profiles/0?flds=maxTravelDistance,numberOfNearbyAssignments,address1,city,stateShortName,countryIso,postalCode,address');
			});

			it('passes the return payload to parse', function () {
				expect(model.parse.calls.mostRecent().args[0]).toBe(payload);
			});
		});

		describe('parse', function () {
			it('builds the address field out of location fields', function () {
				model.set(model.parse({
					city: 'New York',
					stateShortName: 'NY',
					countryIso: 'US',
					postalCode: '10018'
				}));
				expect(model.get('address')).toBe('New York, NY, US, 10018');
			});

			it('does not insert null values into address', function () {
				model.set(model.parse({
					city: 'New York',
					stateShortName: null,
					countryIso: 'US',
					postalCode: '10018'
				}));
				expect(model.get('address')).toBe('New York, US, 10018');
			});

			it('does not set null values', function () {
				model.set(model.parse({ foo: null }));
				expect(model.get('foo')).toBeUndefined();
			});
		});

		describe('update', function () {
			beforeEach(function () {
				spyOn($, 'ajax').and.callFake(function () { return {}; });
				spyOn(model, 'save').and.callThrough();
				spyOn(model, 'isValid').and.returnValue(true);
				spyOn(model, 'validate').and.returnValue(undefined);
				model.update();
			});

			it('fires Backbone\'s save function', function () {
				expect(model.save).toHaveBeenCalled();
			});

			it('saves with a payload', function () {
				expect(model.save.calls.mostRecent().args[0]).toEqual(jasmine.any(Object));
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
				expect(_.extend(payload, model.attributes)).toBe(payload);
			});

			it('returns true', function () {
				expect(model.update()).toBeTruthy();
			});
		});
	});
});
