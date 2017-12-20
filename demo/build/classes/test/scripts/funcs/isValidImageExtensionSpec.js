define([
	'funcs/wm-isValidImageExtension'
], function (wmIsValidImageExtension) {
	'use strict';

	describe('wmIsValidImageExtension', function () {
		it('is defined', function () {
			expect(wmIsValidImageExtension).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(wmIsValidImageExtension()).toBeDefined();
		});

		it('returns .png as a valid extension', function () {
			expect(wmIsValidImageExtension('png')).toBeTruthy();
		});

		it('returns .jpg as a valid extension', function () {
			expect(wmIsValidImageExtension('jpg')).toBeTruthy();
		});

		it('returns .jpeg as a valid extension', function () {
			expect(wmIsValidImageExtension('jpeg')).toBeTruthy();
		});

		it('returns .tiff as a valid extension', function () {
			expect(wmIsValidImageExtension('tiff')).toBeTruthy();
		});

		it('returns .gif as a valid extension', function () {
			expect(wmIsValidImageExtension('gif')).toBeTruthy();
		});

		it('returns .poo as an invalid extension', function () {
			expect(wmIsValidImageExtension('poo')).toBeFalsy();
		});

		it('returns .doc as an invalid extension', function () {
			expect(wmIsValidImageExtension('doc')).toBeFalsy();
		});

		it('returns .pdf as an invalid extension', function () {
			expect(wmIsValidImageExtension('pdf')).toBeFalsy();
		});

		it('returns .csv as an invalid extension', function () {
			expect(wmIsValidImageExtension('csv')).toBeFalsy();
		});
	});
});