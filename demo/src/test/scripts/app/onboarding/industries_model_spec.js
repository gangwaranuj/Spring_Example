define([
	'onboarding/industries_model',
	'onboarding/industry_collection'
], function (IndustriesModel, IndustryCollection) {
	'use strict';

	describe('OnboardingIndustriesModel', function () {
		var model;

		beforeEach(function () {
			model = new IndustriesModel({ id: 0 });
		});

		afterEach(function () {
			model = undefined;
		});

		it('has a global instance', function () {
			expect(IndustriesModel).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(model).toBeDefined();
		});

		it('is the fourth step of Worker Onboarding', function () {
			expect(model.step).toBe(3);
		});

		it('handles the "industries" field', function () {
			expect(model.fields()).toContain('industries');
		});

		it('is valid if its industries collection is valid', function () {
			spyOn(model.industries, 'isValid').and.returnValue(true);
			expect(model.isValid()).toBeTruthy();
		});

		it('is invalid if its industries collection is invalid', function () {
			spyOn(model.industries, 'isValid').and.returnValue(false);
			expect(model.isValid()).toBeFalsy();
		});

		describe('industries', function () {
			it('is created on initialize', function () {
				expect(model.industries).toBeDefined();
			});

			it('is a Backbone collection', function () {
				expect(model.industries instanceof Backbone.Collection).toBeTruthy();
			});

			it('is an instance of IndustryCollection', function () {
				expect(model.industries instanceof IndustryCollection).toBeTruthy();
			});

			it('sets its id to that of the model', function () {
				expect(model.id).toBe(model.industries.id);
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

			it('fetches from /onboarding/profiles/:id?flds=industries', function () {
				expect($.ajax.calls.mostRecent().args[0].url).toBe('/onboarding/profiles/0?flds=industries');
			});

			it('passes the return payload to parse', function () {
				expect(model.parse.calls.mostRecent().args[0]).toBe(payload);
			});
		});

		describe('parse', function () {
			it('adds industries to the industries collection', function () {
				model.parse({ industries: [{}] });
				expect(model.industries.isEmpty()).toBeFalsy();
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

			it('saves a payload containing the industries collection', function () {
				expect(_.isArray(model.save.calls.mostRecent().args[0].industries)).toBeTruthy();
			});

			it('returns true', function () {
				expect(model.update()).toBeTruthy();
			});
		});
	});
});
