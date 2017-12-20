define([
	'onboarding/image_model'
], function (ImageModel) {
	'use strict';

	describe('App.Onboarding.Models.ImageModel', function () {
		var model;

		beforeEach(function () {
			model = new ImageModel();
		});

		afterEach(function () {
			model = undefined;
		});

		it('has a global instance', function () {
			expect(ImageModel).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(model).toBeDefined();
		});

		it('defaults image to null', function () {
			expect(model.get('image')).toBeNull();
		});

		it('defaults filename to null', function () {
			expect(model.get('filename')).toBeNull();
		});

		it('defaults coordinates to null', function () {
			expect(model.get('coordinates')).toBeNull();
		});

		it('defaults url to null', function () {
			expect(model.get('url')).toBeNull();
		});
	});
});
