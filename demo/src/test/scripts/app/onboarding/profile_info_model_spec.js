define([
	'onboarding/profile_info_model',
	'onboarding/phones_collection',
	'onboarding/image_model',
	'onboarding/legal_model',
	'wmPatterns'
], function (ProfileInfoModel, PhonesCollection, ImageModel, LegalModel, wmPatterns) {
	'use strict';

	describe('ProfileInfoModel', function () {
		var validParameters = {
				email: 'foo@workmarket.com',
				firstName: 'Foo',
				lastName: 'Bar'
			},
			model;

		beforeEach(function () {
			model = new ProfileInfoModel({ id: 0 });
			wmPatterns = { email: { test: {} } };
			spyOn(wmPatterns.email, 'test').and.callFake(function (param) { return param === 'foo@workmarket.com'; });

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
			expect(ProfileInfoModel).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(model).toBeDefined();
		});

		it('is the first step of Worker Onboarding', function () {
			expect(model.step).toBe(1);
		});

		it('handles the "email" field', function () {
			expect(model.fields()).toContain('email');
		});

		it('handles the "firstName" field', function () {
			expect(model.fields()).toContain('firstName');
		});

		it('handles the "lastName" field', function () {
			expect(model.fields()).toContain('lastName');
		});

		it('handles the "gender" field', function () {
			expect(model.fields()).toContain('gender');
		});

		it('handles the "jobTitle" field', function () {
			expect(model.fields()).toContain('jobTitle');
		});

		it('is valid if all attributes are valid and all its submodels are valid', function () {
			spyOn(model.phones, 'isValid').and.returnValue(true);
			spyOn(model.avatar, 'isValid').and.returnValue(true);
			model.set(validParameters);
			expect(model.isValid()).toBeTruthy();
		});

		it('is invalid by default', function () {
			expect(model.isValid()).toBeFalsy();
		});

		it('is invalid if some of its submodels are invalid', function () {
			spyOn(model.phones, 'isValid').and.returnValue(false);
			spyOn(model.avatar, 'isValid').and.returnValue(true);
			model.set(validParameters);
			expect(model.isValid()).toBeFalsy();
		});

		it('is invalid if email is empty', function () {
			expect(model.validate({ email: '' })).toHaveError('Email is a required field.');
		});

		it('is invalid if firstName is empty', function () {
			expect(model.validate({ firstName: '' })).toHaveError('First Name is a required field.');
		});

		it('is invalid if lastName is empty', function () {
			expect(model.validate({ lastName: '' })).toHaveError('Last Name is a required field.');
		});

		it('is invalid if firstName is more than 50 characters', function () {
			expect(model.validate({ firstName: 'Commodo cillum dolor exercitation laborum velit ipsum duis.' })).toHaveError('First Name must be 50 characters or less.');
		});

		it('is invalid if lastName is more than 50 characters', function () {
			expect(model.validate({ lastName: 'Commodo cillum dolor exercitation laborum velit ipsum duis.' })).toHaveError('Last Name must be 50 characters or less.');
		});

		describe('phones', function () {
			it('is created on initialize', function () {
				expect(model.phones).toBeDefined();
			});

			it('is a Backbone collection', function () {
				expect(model.phones instanceof Backbone.Collection).toBeTruthy();
			});

			it('is an instance of PhonesCollection', function () {
				expect(model.phones instanceof PhonesCollection).toBeTruthy();
			});
		});

		describe('avatar', function () {
			it('is created on initialize', function () {
				expect(model.avatar).toBeDefined();
			});

			it('is a Backbone model', function () {
				expect(model.avatar instanceof Backbone.Model).toBeTruthy();
			});

			it('is an instance of ImageModel', function () {
				expect(model.avatar instanceof ImageModel).toBeTruthy();
			});
		});

		describe('legal', function () {
			it('is created on initialize', function () {
				expect(model.legal).toBeDefined();
				expect(model.legal instanceof LegalModel).toBeTruthy();
			});

			it('passes its id to the legal submodel', function () {
				expect(model.legal.id).toBe(model.id);
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

			it('fetches from /onboarding/profiles/:id?flds=email,firstName,lastName,phones,avatar,gender,jobTitle', function () {
				expect($.ajax.calls.mostRecent().args[0].url).toBe('/onboarding/profiles/0?flds=email,firstName,lastName,phones,avatar,legal,gender,jobTitle,countryCodes');
			});

			it('passes the return payload to parse', function () {
				expect(model.parse.calls.mostRecent().args[0]).toBe(payload);
			});
		});

		describe('parse', function () {
			var validPhones = [
				{ type: 'foo', code: '1', number: '2125555555' },
				{ type: 'bar', code: '1', number: '2124444444' },
				{ type: 'baz', code: '1', number: '2123333333' }
			];

			beforeEach(function () {
				wmPatterns.phone = { test: {} };
				spyOn(wmPatterns.phone, 'test').and.returnValue(true);
			});

			afterEach(function () {
				wmPatterns.phone = undefined;
			});

			it('builds the phones\' phoneTypes array', function () {
				model.parse({ phones: validPhones });
				expect(model.phones.phoneTypes).toEqual(['foo', 'bar', 'baz']);
			});

			it('adds phones to the phones collection', function () {
				model.parse({ phones: validPhones });
				expect(model.phones.at(0).attributes).toEqual(validPhones[0]);
			});

			it('ignores phones with empty numbers', function () {
				validPhones[0].number = '';
				model.parse({ phones: validPhones });
				expect(model.phones.length).toBeLessThan(validPhones.length);
			});

			it('removes any phone number formatting', function () {
				validPhones[0].number = '(212) 555-5555';
				model.parse({ phones: validPhones });
				expect(model.phones.at(0).get('number')).toBe('2125555555');
			});

			it('adds an empty phone if no phones are passed', function () {
				model.parse({ phones: [] });
				expect(model.phones.length).toBe(1);
			});

			it('sets the avatar url', function () {
				model.parse({ avatar: { url: '/home' } });
				expect(model.avatar.get('url')).toBe('/home');
			});

			it('does not set null values', function () {
				expect(model.parse({ foo: null }).foo).toBeUndefined();
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

			it('sends the legal attributes', function () {
				var payload = JSON.parse($.ajax.calls.mostRecent().args[0].data);
				expect(payload.logo).not.toBeUndefined();
			});

			it('saves a payload containing the phones collection', function () {
				expect(_.isArray(model.save.calls.mostRecent().args[0].phones)).toBeTruthy();
			});

			it('saves a payload containing the avatar model', function () {
				expect(_.isObject(model.save.calls.mostRecent().args[0].avatar)).toBeTruthy();
			});

			it('returns true', function () {
				expect(model.update()).toBeTruthy();
			});
		});
	});
});
