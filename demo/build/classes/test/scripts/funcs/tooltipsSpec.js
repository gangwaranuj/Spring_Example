define(['funcs/wm-tooltips'], function (wmTooltips) {
	'use strict';

	describe('wm.funcs.tooltips', function () {
		'use strict';
		var $tooltip;
		beforeEach(function () {
			appendSetFixtures('<div id="tooltip-test" data-tooltip-content="Test" data-tooltip-maxwidth="200" data-tooltip-position="top">Test</div>');
			wmTooltips('#tooltip-test');

			$tooltip = $('#tooltip-test').eq(0);

			jasmine.addMatchers({
				toBeGreaterThanOrEqualTo: function () {
					return {
						compare: function (actual, expected) {
							return {
								pass: actual >= expected
							};
						}
					};
				}
			});
		});


		it('is defined', function () {
			expect(wmTooltips).toBeDefined();
		});

		it('has the class "tooltipstered"', function () {
			wmTooltips('#tooltip-test');
			expect($tooltip).toHaveClass('tooltipstered');
		});

		it('is attached to the DOM', function () {
			expect($tooltip).toBeInDOM();
		});

		it(', the tooltip-content, is not empty', function () {
			var content = $tooltip.data("tooltip-content");
			expect(content).not.toBeEmpty();
		});

		it('has a value greater than 200', function () {
			var maxwidth = $tooltip.data("tooltip-maxwidth");
			expect(maxwidth).toBeGreaterThanOrEqualTo(200);
		});

		it(', the text inside the element, is not empty', function () {
			expect($tooltip).not.toBeEmpty();
		});

	});
});