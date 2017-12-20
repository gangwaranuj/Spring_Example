define([
	'funcs/wm-isValidFileExtension'
], function (wmIsValidFileExtension) {
	'use strict';

	describe('wmIsValidFileExtension', function () {
		it('is defined', function () {
			expect(wmIsValidFileExtension).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(wmIsValidFileExtension()).toBeDefined();
		});

		it('returns .doc as an valid extension', function () {
			expect(wmIsValidFileExtension('doc')).toBeTruthy();
		});

		it('returns .docx as an valid extension', function () {
			expect(wmIsValidFileExtension('docx')).toBeTruthy();
		});

		it('returns .xlsx as an valid extension', function () {
			expect(wmIsValidFileExtension('xlsx')).toBeTruthy();
		});

		it('returns .pdf as an valid extension', function () {
			expect(wmIsValidFileExtension('pdf')).toBeTruthy();
		});

		it('returns .txt as an valid extension', function () {
			expect(wmIsValidFileExtension('txt')).toBeTruthy();
		});

		it('returns .zip as an valid extension', function () {
			expect(wmIsValidFileExtension('zip')).toBeTruthy();
		});

		it('returns .csv as an valid extension', function () {
			expect(wmIsValidFileExtension('csv')).toBeTruthy();
		});

		it('returns .png as a valid extension', function () {
			expect(wmIsValidFileExtension('png')).toBeTruthy();
		});

		it('returns .jpg as a valid extension', function () {
			expect(wmIsValidFileExtension('jpg')).toBeTruthy();
		});

		it('returns .jpeg as a valid extension', function () {
			expect(wmIsValidFileExtension('jpeg')).toBeTruthy();
		});

		it('returns .tiff as a valid extension', function () {
			expect(wmIsValidFileExtension('tiff')).toBeTruthy();
		});

		it('returns .gif as a valid extension', function () {
			expect(wmIsValidFileExtension('gif')).toBeTruthy();
		});

		it('returns .poo as an invalid extension', function () {
			expect(wmIsValidFileExtension('poo')).toBeFalsy();
		});
	});

});