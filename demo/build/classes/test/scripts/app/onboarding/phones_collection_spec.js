define([
	'onboarding/phone_model',
	'onboarding/phones_collection'
], function (PhoneModel, PhonesCollection) {
	'use strict';

	describe('App.Onboarding.Collections.PhonesCollection', function () {
		var collection;

		beforeEach(function () {
			collection = new PhonesCollection();
		});

		afterEach(function () {
			collection = undefined;
		});

		it('has a global instance', function () {
			expect(PhonesCollection).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(collection).toBeDefined();
		});

		it('collects PhoneModel objects', function () {
			expect(collection.model).toBe(PhoneModel);
		});

		it('defaults max number of phones to 3', function () {
			expect(collection.maxPhones).toBe(3);
		});

		describe('canAddMorePhones', function () {
			it('returns true by default', function () {
				expect(collection.canAddMorePhones()).toBeTruthy();
			});

			it('returns true when the number of phones is fewer than the maximum', function () {
				collection.maxPhones = 3;
				_.times(collection.maxPhones - 1, function () { collection.add({}); });
				expect(collection.canAddMorePhones()).toBeTruthy();
			});

			it('returns false when the number of phones is at the maximum', function () {
				collection.maxPhones = 3;
				_.times(collection.maxPhones, function () { collection.add({}); });
				expect(collection.canAddMorePhones()).toBeFalsy();
			});

			it('returns false when the number of phones is greater than the maximum', function () {
				collection.maxPhones = 3;
				_.times(collection.maxPhones + 1, function () { collection.add({}); });
				expect(collection.canAddMorePhones()).toBeFalsy();
			});

			it('runs each time a phone is added', function () {
				collection.maxPhones = 1;
				var before = collection.canAddMorePhones();
				collection.add({});
				var after = collection.canAddMorePhones();
				expect(before).not.toBe(after);
			});
		});

		describe('isLast', function () {
			it('returns false if not passed a model', function () {
				expect(collection.isLast()).toBeFalsy();
			});

			it('returns true if the model is last in the collection', function () {
				collection.add({});
				var model = collection.at(0);
				expect(collection.isLast(model)).toBeTruthy();
			});

			it('returns false if the model is not last in the collection', function () {
				collection.add([{}, {}]);
				var model = collection.at(0);
				expect(collection.isLast(model)).toBeFalsy();
			});
		});
	});
});
