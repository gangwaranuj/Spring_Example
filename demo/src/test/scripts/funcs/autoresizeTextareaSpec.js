define([
	'jquery',
	'dependencies/autoresizeTextarea'
], function ($) {
	'use strict';

	describe('$.fn.autoresizeTextarea', function () {
		var $textArea;

		beforeEach(function () {
			appendSetFixtures('<textarea rows="1" class="field_value" style="min-width: 1em; max-width: 1em; height: auto;"></textarea>');
			$textArea = $('.field_value');

			jasmine.addMatchers({
				toResizeOn: function (util, customEqualityTesters) {
					return {
						compare: function (actual, event) {
							var result = {};

							var startingHeight = actual.height();
							actual.autoresizeTextarea();
							actual.val('AAA');
							actual.trigger(event);
							var endingHeight = actual.height();

							result.pass = !util.equals(startingHeight, endingHeight, customEqualityTesters);
							if (result.pass) {
								result.message = 'Expected ' + actual + ' not to have resized with ' + event;
							} else {
								result.message = 'Expected ' + actual + ' to have resized with ' + event;
							}
							return result;
						}
					};
				}
			});
		});

		afterEach(function () {
			$textArea = null;
		});

		it('is defined', function () {
			expect($.fn.autoresizeTextarea).toBeDefined();
		});

		it('resizes textarea after initialization', function () {
			var startingHeight = $textArea.height();
			$textArea.val('AAA');
			$textArea.autoresizeTextarea();
			var endingHeight = $textArea.height();

			expect(startingHeight).not.toEqual(endingHeight);
		});

		it('resizes textarea on change', function () {
			expect($textArea).toResizeOn('change');
		});

		it('resizes textarea on keyup', function () {
			expect($textArea).toResizeOn('keyup');
		});

		it('resizes textarea on keydown', function () {
			expect($textArea).toResizeOn('keydown');
		});

		it('resizes textarea on paste', function () {
			expect($textArea).toResizeOn('paste');
		});

		it('resizes textarea on cut', function () {
			expect($textArea).toResizeOn('cut');
		});
	});
});
