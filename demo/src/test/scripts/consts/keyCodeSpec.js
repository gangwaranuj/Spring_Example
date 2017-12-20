define([
	'keyCodes'
], function (keyCodes) {

	describe('wm.consts.keyCode', function () {
		'use strict';

		it('is defined', function () {
			expect(keyCodes).toBeDefined();
		});

		it('returns an object', function () {
			expect(keyCodes).toEqual(jasmine.any(Object));
		});

		it('defines `BACKSPACE`', function () {
			expect(keyCodes.BACKSPACE).toEqual(8);
		});

		it('defines `COMMA`', function () {
			expect(keyCodes.COMMA).toEqual(188);
		});

		it('defines `CONTROL`', function () {
			expect(keyCodes.CONTROL).toEqual(17);
		});

		it('defines `DELETE`', function () {
			expect(keyCodes.DELETE).toEqual(46);
		});

		it('defines `DOWN`', function () {
			expect(keyCodes.DOWN).toEqual(40);
		});

		it('defines `END`', function () {
			expect(keyCodes.END).toEqual(35);
		});

		it('defines `ENTER`', function () {
			expect(keyCodes.ENTER).toEqual(13);
		});

		it('defines `ESCAPE`', function () {
			expect(keyCodes.ESCAPE).toEqual(27);
		});

		it('defines `HOME`', function () {
			expect(keyCodes.HOME).toEqual(36);
		});

		it('defines `LEFT`', function () {
			expect(keyCodes.LEFT).toEqual(37);
		});

		it('defines `NUMPAD_ADD`', function () {
			expect(keyCodes.NUMPAD_ADD).toEqual(107);
		});

		it('defines `NUMPAD_DECIMAL`', function () {
			expect(keyCodes.NUMPAD_DECIMAL).toEqual(110);
		});

		it('defines `NUMPAD_DIVIDE`', function () {
			expect(keyCodes.NUMPAD_DIVIDE).toEqual(111);
		});

		it('defines `NUMPAD_ENTER`', function () {
			expect(keyCodes.NUMPAD_ENTER).toEqual(108);
		});

		it('defines `NUMPAD_MULTIPLY`', function () {
			expect(keyCodes.NUMPAD_MULTIPLY).toEqual(106);
		});

		it('defines `NUMPAD_SUBTRACT`', function () {
			expect(keyCodes.NUMPAD_SUBTRACT).toEqual(109);
		});

		it('defines `PAGE_DOWN`', function () {
			expect(keyCodes.PAGE_DOWN).toEqual(34);
		});

		it('defines `PAGE_UP`', function () {
			expect(keyCodes.PAGE_UP).toEqual(33);
		});

		it('defines `PERIOD`', function () {
			expect(keyCodes.PERIOD).toEqual(190);
		});

		it('defines `RIGHT`', function () {
			expect(keyCodes.RIGHT).toEqual(39);
		});

		it('defines `SPACE`', function () {
			expect(keyCodes.SPACE).toEqual(32);
		});

		it('defines `TAB`', function () {
			expect(keyCodes.TAB).toEqual(9);
		});

		it('defines `UP`', function () {
			expect(keyCodes.UP).toEqual(38);
		});

		it('defines `V`', function () {
			expect(keyCodes.V).toEqual(86);
		});
	});

});