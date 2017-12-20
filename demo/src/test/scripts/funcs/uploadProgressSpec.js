define([
	'jquery',
	'funcs/uploadProgress'
], function ($, uploadProgress) {
	'use strict';

	describe('uploadProgress', function () {
		var $progressBar;

		beforeEach(function () {
			appendSetFixtures('<div id="progress_bar"></div><div class="header--notifications"></div>');
			$progressBar = $('#progress_bar');
		});

		it('is defined', function () {
			spyOn($, 'ajax');
			expect(uploadProgress).toBeDefined();
		});

		it('can be instantiated', function () {
			spyOn($, 'ajax');
			expect(uploadProgress()).toBeDefined();
		});

		it('returns a promise object', function () {
			spyOn($, 'ajax');
			var progress = uploadProgress();
			expect(['pending','resolved','rejected']).toContain(progress.state());
		});

		it('is in progress while the upload is not finished', function () {
			spyOn($, 'ajax').and.callFake(function (options) { options.success({ data: { uploadProgress: 0.7 } }); });
			expect(uploadProgress().state()).toBe('pending');
		});

		it('resolves when the upload has finished', function () {
			spyOn($, 'ajax').and.callFake(function (options) { options.success({ data: { uploadProgress: 1 } }); });
			expect(uploadProgress().state()).toBe('resolved');
		});

		it('is rejected if the response is undefined', function () {
			spyOn($, 'ajax').and.callFake(function (options) { options.success(); });
			expect(uploadProgress().state()).toBe('rejected');
		});

		it('is rejected if the response does not have a data property', function () {
			spyOn($, 'ajax').and.callFake(function (options) { options.success({}); });
			expect(uploadProgress().state()).toBe('rejected');
		});

		it('is rejected if the response does not have a data.uploadProgress property', function () {
			spyOn($, 'ajax').and.callFake(function (options) { options.success({ data: {} }); });
			expect(uploadProgress().state()).toBe('rejected');
		});
	});
});
