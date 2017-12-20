define([
	'underscore',
	'wmMaskInput'
], function (_, wmMaskInput) {
	'use strict';

	describe('wmMaskInput', function () {
		var $inputElement, $selectElement, $textareaElement, $noLabelElement, $element;

		var patterns = {
			ssn: '000-00-0000',
			ein: '00-0000000',
			sin: '000-000-000',
			bin: '000000000-SS-0000',
			postalCAN: 'S0S 0S0'
		};

		beforeEach(function () {
			appendSetFixtures('<select class="select"></select>');
			appendSetFixtures('<textarea class="textarea"></textarea>');
			appendSetFixtures('<input class="no-label" type="tel" value="0000000000" />');
			appendSetFixtures('<input type="ssn" value="000000000" data-mask />');
			appendSetFixtures('<input type="ein" value="000000000" data-mask />');
			appendSetFixtures('<input type="sin" value="000000000" data-mask />');
			appendSetFixtures('<input type="bin" value="000000000SS0000" data-mask />');
			appendSetFixtures('<input type="postalCAN" value="S0S0S0" data-mask />');

			$inputElement = $('.input');
			$selectElement = $('.select');
			$textareaElement = $('.textarea');
			$noLabelElement = $('.no-label');
			$element = $inputElement;

			jasmine.addMatchers({
				toHaveMaskedInput: function () {
					return {
						compare: function (actual) {
							var result = {},
								options,
								event;

							if (_.isString(arguments[1])) {
								options = arguments[2];
								event = arguments[1];
							} else {
								options = arguments[1];
								event = arguments[2];
							}

							var originalValue = actual.val();
							wmMaskInput(options);
							if (!_.isUndefined(event)) {
								actual.trigger(event);
							}
							var newValue = actual.val();

							result.pass = originalValue !== newValue;
							result.message = 'Expected ' + actual.selector + (result.pass ? ' not' : '') + ' to have masked input' + (_.isUndefined(event) ? '' : ' with ' + event) + (_.isUndefined(event) || _.isUndefined(options) ? '' : ' and') + (_.isUndefined(options) ? '' : ' with options ' + options);
							return result;
						}
					};
				}
			});
		});

		afterEach(function () {
			$(document).off('keyup');
			$inputElement = $selectElement = $noLabelElement = $element = null;
		});

		it('is defined', function () {
			expect(wmMaskInput).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(wmMaskInput()).toBeDefined();
		});

		it('returns an array on a successful run', function () {
			expect(wmMaskInput()).toEqual(jasmine.any(Array));
		});

		it('returns false when not run immediately', function () {
			expect(wmMaskInput({ autorun: false })).toBeFalsy();
		});

		it('selects elements with the "mask" data attribute by default', function () {
			expect($('[data-mask]')).toHaveMaskedInput();
		});

		it('fires on change by default', function () {
			expect($element).toHaveMaskedInput('change', { autorun: false });
		});

		it('fires on keyup by default', function () {
			expect($element).toHaveMaskedInput('keyup', { autorun: false });
		});

		it('does not fire on keydown by default', function () {
			expect($element).not.toHaveMaskedInput('keydown', { autorun: false });
		});

		it('does not fire on keypress by default', function () {
			expect($element).not.toHaveMaskedInput('keypress', { autorun: false });
		});

		it('does not fire on focus by default', function () {
			expect($element).not.toHaveMaskedInput('focus', { autorun: false });
		});

		it('does not fire on blur by default', function () {
			expect($element).not.toHaveMaskedInput('blur', { autorun: false });
		});

		it('does not fire on mousedown by default', function () {
			expect($element).not.toHaveMaskedInput('mousedown', { autorun: false });
		});

		it('does not fire on mouseup by default', function () {
			expect($element).not.toHaveMaskedInput('mouseup', { autorun: false });
		});

		it('does not fire on click by default', function () {
			expect($element).not.toHaveMaskedInput('click', { autorun: false });
		});

		it('runs on input elements by default', function () {
			expect($inputElement).toHaveMaskedInput();
		});

		it('does not run on select elements by default', function () {
			expect($selectElement).not.toHaveMaskedInput();
		});

		it('does not run on textarea elements by default', function () {
			expect($textareaElement).not.toHaveMaskedInput();
		});

		it('binds automatically to targeted elements by default ', function () {
			expect($element).toHaveMaskedInput('keyup', {
				events: 'keyup',
				autorun: false
			});
		});

		it('does not bind automatically to targeted elements without "autobind"', function () {
			expect($element).not.toHaveMaskedInput('keyup', {
				events: 'keyup',
				autobind: false,
				autorun: false
			});
		});

		it('runs instantly on targeted elements by default', function () {
			expect($element).toHaveMaskedInput();
		});

		it('does not run instantly on targeted elements without "autorun"', function () {
			expect($element).not.toHaveMaskedInput({ autorun: false });
		});

		_.each(patterns, function (value, key) {
			it('masks ' + key + ' numbers as "' + value + '"', function () {
				wmMaskInput();
				expect($('[type="' + key + '"]')).toHaveValue(value);
			});
		});

		_.each(patterns, function (value, key) {
			it('accepts ' + key + ' as a pattern', function () {
				wmMaskInput({ selector: '[type="' + key + '"]' }, key);
				expect($('[type="' + key + '"]')).toHaveValue(value);
			});
		});
	});
});
