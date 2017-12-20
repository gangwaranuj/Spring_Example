define([
	'onboarding/onboard_model'
], function (OnboardModel) {
	'use strict';

	describe('OnboardingOnboardModel', function () {
		var model;

		beforeEach(function () {
			model = new OnboardModel({ id: 0 });
		});

		afterEach(function () {
			model = undefined;
		});

		it('has a global instance', function () {
			expect(OnboardModel).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(model).toBeDefined();
		});

		it('is initialized as a valid model', function () {
			expect(model.isValid()).toBeTruthy();
		});

		it('sets attributes passed into the constructor', function () {
			model = new OnboardModel({ name: 'John' });
			expect(model.get('name')).toBe('John');
		});

		it('sets the id passed into the constructor', function () {
			expect(model.id).toBe(0);
		});

		it('has a url of "/onboarding/profiles/[id]?flds=', function () {
			expect(model.url()).toBe('/onboarding/profiles/0?flds=');
		});

		it('has no fields', function () {
			expect(model.fields()).toEqual([]);
		});

		it('is always valid', function () {
			expect(model.isValid()).toBeTruthy();
		});

		describe('set', function () {
			it('takes an object', function () {
				model.set({ name: 'John', age: 24 });
				expect(model.get('name')).toBeDefined();
				expect(model.get('age')).toBeDefined();
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

			it('fetches from /onboarding/profiles/:id?flds=', function () {
				expect($.ajax.calls.mostRecent().args[0].url).toBe('/onboarding/profiles/0?flds=');
			});

			it('passes the return payload to parse', function () {
				expect(model.parse.calls.mostRecent().args[0]).toBe(payload);
			});
		});

		describe('update', function () {
			beforeEach(function () {
				spyOn($, 'ajax').and.callFake(function () { return {}; });
				spyOn(model, 'sync').and.callThrough();
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
				expect(_.extend(payload, model.attributes)).toBe(payload);
			});

			it('returns true', function () {
				expect(model.update()).toBeTruthy();
			});
		});
	});
});
