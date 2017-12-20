define([
	'funcs/wm-scrollBottom'
], function (wmScrollBottom) {

	describe('wmScrollBottom', function () {
		'use strict';

		beforeEach(function () {
			appendSetFixtures('<div class="scroll" style="overflow: auto; height: 100px;"><div style="height: 400px;"></div></div>');
		});

		it('is defined', function () {
			expect(wmScrollBottom).toBeDefined();
		});

		it('returns null when not passed in any parameters', function () {
			expect(wmScrollBottom()).toBeNull();
		});

		it('returns null when the parameter does not match any DOM element', function () {
			expect(wmScrollBottom('')).toBeNull();
		});

		it('returns the DOM element queried using the parameter', function () {
			var element = wmScrollBottom('.scroll');
			expect(element).toExist();
			expect(element).toBeInDOM();
			expect(element).toBeMatchedBy('.scroll');
		});

		it('scrolls the contents of the DOM element to the bottom of their container', function () {
			var element = wmScrollBottom('.scroll');
			expect(element.scrollTop).toEqual(element.scrollHeight - element.clientHeight);
		});
	});

});