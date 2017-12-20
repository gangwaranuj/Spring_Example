// define(['wmFloatLabel'], function (wmFloatLabel) {
// 	'use strict';
//
// 	describe('wmFloatLabel', function () {
// 		var floatLabel, $inputElement, $selectElement, $textareaElement, $noLabelElement, $element, $nestedElement, originalClassList, newClassList;
//
// 		beforeEach(function () {
// 			appendSetFixtures('<div class="input" data-float-label="Test"><input type="text" value="Test Label" /></div>');
// 			appendSetFixtures('<div class="select" data-float-label="Test"><select><option value="Test Label" selected></option></select></div>');
// 			appendSetFixtures('<div class="textarea" data-float-label="Test"><textarea>Test Label</textarea></div>');
// 			appendSetFixtures('<div class="no-label"></div>');
// 			appendSetFixtures('<div class="nested"><div class="nested-input" data-float-label="Test"><input type="text" value="Test Label" /></div></div>');
//
// 			$inputElement = $('.input');
// 			$selectElement = $('.select');
// 			$textareaElement = $('.textarea');
// 			$noLabelElement = $('.no-label');
// 			$nestedElement = $('.nested');
// 			$element = $inputElement;
//
// 			jasmine.addMatchers({
// 				toHaveFloatedLabel: function (util, customEqualityTesters) {
// 					return {
// 						compare: function (actual, param1, param2) {
// 							var result = {},
// 								options = undefined,
// 								event = undefined;
//
// 							if (param1 !== undefined) {
// 								if (_.isString(param1)) {
// 									event = param1;
// 									if (param2 !== undefined) {
// 										options = param2;
// 									}
// 								} else {
// 									options = param1;
// 									if (param2 !== undefined) {
// 										event = param2;
// 									}
// 								}
// 							}
//
// 							var originalClassList = actual.attr('class');
// 							if (options !== undefined) {
// 								var floatLabel = wmFloatLabel(_.extend({ autorun: false }, options));
// 							} else if (event !== undefined) {
// 								var floatLabel = wmFloatLabel({ autorun: false });
// 							} else {
// 								var floatLabel = wmFloatLabel();
// 							}
// 							if (event !== undefined) {
// 								actual.trigger(event);
// 							}
// 							var newClassList = actual.attr('class');
//
// 							result.pass = !util.equals(originalClassList, newClassList, customEqualityTesters);
// 							if (result.pass) {
// 								if (event !== undefined) {
// 									result.message = 'Expected ' + actual + ' not to have floated label with ' + event;
// 									if (options !== undefined) {
// 										result.message += ' and options ' + JSON.stringify(options);
// 									}
// 								} else if (options !== undefined) {
// 									result.message = 'Expected ' + actual + ' not to have floated label with options ' + JSON.stringify(options);
// 								} else {
// 									result.message = 'Expected ' + actual + ' not to have floated label';
// 								}
// 							} else {
// 								if (event !== undefined) {
// 									result.message = 'Expected ' + actual + ' to have floated label with ' + event;
// 									if (options !== undefined) {
// 										result.message += ' and options ' + JSON.stringify(options);
// 									}
// 								} else if (options !== undefined) {
// 									result.message = 'Expected ' + actual + ' to have floated label with options ' + JSON.stringify(options);
// 								} else {
// 									result.message = 'Expected ' + actual + ' to have floated label';
// 								}
// 							}
//
// 							return result;
// 						}
// 					}
// 				}
// 			});
// 		});
//
// 		afterEach(function () {
// 			$(document).off();
// 			$inputElement = $selectElement = $textareaElement = $noLabelElement = $element = $nestedElement = undefined;
// 		});
//
// 		it('is defined', function () {
// 			expect(wmFloatLabel).toBeDefined();
// 		});
//
// 		it('can be instantiated', function () {
// 			floatLabel = wmFloatLabel();
// 			expect(floatLabel).toBeDefined();
// 		});
//
// 		it('returns true on a successful run', function () {
// 			expect(wmFloatLabel()).toBeTruthy();
// 		});
//
// 		it('returns false when not run immediately', function () {
// 			expect(wmFloatLabel({ autorun: false })).toBeFalsy();
// 		});
//
// 		it('is triggered from elements with the "selector" parameter', function () {
// 			expect($('.input')).toHaveFloatedLabel('foo', { events: 'foo', selector: '.input' });
// 		});
//
// 		it('is triggered from elements with the "float-label" data attribute by default', function () {
// 			expect($('[data-float-label]')).toHaveFloatedLabel();
// 		});
//
// 		it('is triggered from events in the "events" parameter', function () {
// 			expect($element).toHaveFloatedLabel('foo', { events: 'foo' });
// 		});
//
// 		it('is triggered from the keyup event by default', function () {
// 			expect($element).toHaveFloatedLabel('keyup');
// 		});
//
// 		it('is triggered from the change event by default', function () {
// 			expect($element).toHaveFloatedLabel('change');
// 		});
//
// 		it('is not triggered from the keydown event by default', function () {
// 			expect($element).not.toHaveFloatedLabel('keydown');
// 		});
//
// 		it('is not triggered from the keypress event by default', function () {
// 			expect($element).not.toHaveFloatedLabel('keypress');
// 		});
//
// 		it('is not triggered from the focus event by default', function () {
// 			expect($element).not.toHaveFloatedLabel('focus');
// 		});
//
// 		it('is not triggered from the blur event by default', function () {
// 			expect($element).not.toHaveFloatedLabel('blur');
// 		});
//
// 		it('is not triggered from the mousedown event by default', function () {
// 			expect($element).not.toHaveFloatedLabel('mousedown');
// 		});
//
// 		it('is not triggered from the mouseup event by default', function () {
// 			expect($element).not.toHaveFloatedLabel('mouseup');
// 		});
//
// 		it('is not triggered from the click event by default', function () {
// 			expect($element).not.toHaveFloatedLabel('click');
// 		});
//
// 		it('toggles the class determined by the "classname" parameter', function () {
// 			floatLabel = wmFloatLabel({ classname: 'foo' });
// 			expect($element).toHaveClass('foo');
// 		});
//
// 		it('toggles the class "-label" by default', function () {
// 			floatLabel = wmFloatLabel();
// 			expect($element).toHaveClass('-label');
// 		});
//
// 		it('does not float while the value is empty', function () {
// 			$element.find('input').val('');
// 			expect($element).not.toHaveFloatedLabel('foo', { events: 'foo' });
// 		});
//
// 		it('does not float while the value is null', function () {
// 			$element.find('input').val(null);
// 			expect($element).not.toHaveFloatedLabel('foo', { events: 'foo' });
// 		});
//
// 		it('floats while empty and having the class determined by the "errorClassname" parameter', function () {
// 			$element.val('').addClass('foo');
// 			expect($element).toHaveFloatedLabel('bar', { events: 'bar', errorClassname: 'foo' });
// 		});
//
// 		it('floats while empty and having the class "-invalid" by default', function () {
// 			$element.val('').addClass('-invalid');
// 			expect($element).toHaveFloatedLabel('bar', { events: 'bar' });
// 		});
//
// 		it('runs on elements determined by the "inputs" parameter', function () {
// 			expect($element).toHaveFloatedLabel('foo', { events: 'foo', inputs: 'input' });
// 		});
//
// 		it('runs on input elements by default', function () {
// 			expect($inputElement).toHaveFloatedLabel();
// 		});
//
// 		it('runs on select elements by default', function () {
// 			expect($selectElement).toHaveFloatedLabel();
// 		});
//
// 		it('runs on textarea elements by default', function () {
// 			expect($textareaElement).toHaveFloatedLabel();
// 		});
//
// 		it('binds automatically to targeted elements when "autobind" is true', function () {
// 			expect($element).toHaveFloatedLabel('foo', { events: 'foo', autobind: true });
// 		});
//
// 		it('does not bind automatically to targeted elements when "autobind" is false', function () {
// 			expect($element).not.toHaveFloatedLabel('foo', { events: 'foo', autobind: false });
// 		});
//
// 		it('binds automatically to targeted elements by default', function () {
// 			expect($element).toHaveFloatedLabel('foo', { events: 'foo' });
// 		});
//
// 		it('runs instantly on targeted elements when "autorun" is true', function () {
// 			expect($element).toHaveFloatedLabel({ autorun: true });
// 		});
//
// 		it('does not run instantly on targeted elements when "autorun" is false', function () {
// 			expect($element).not.toHaveFloatedLabel({ autorun: false });
// 		});
//
// 		it('runs instantly on targeted elements by default', function () {
// 			expect($element).toHaveFloatedLabel();
// 		});
//
// 		xit('delegates off the "root" parameter', function () {
// 			wmFloatLabel({ events: 'keyup', root: $nestedElement });
// 			expect($nestedElement).toHandle('keyup');
// 		});
//
// 		it('delegates off the "document" by default', function () {
// 			wmFloatLabel({ events: 'keyup' });
// 			expect($(document)).toHandle('keyup');
// 		});
//
// 		it('binds directly to the root when "selector" is undefined', function () {
// 			expect($element).toHaveFloatedLabel('foo', { events: 'foo', root: '.input', selector: undefined });
// 		});
//
// 		it('binds directly to the root when "selector" is null', function () {
// 			expect($element).toHaveFloatedLabel('foo', { events: 'foo', root: '.input', selector: null });
// 		});
// 	});
// });
