define([
	'funcs/wm-actionMenu',
	'selectize'
], function (wmActionMenu) {
	'use strict';

	describe('wmActionMenu', function () {
		var $actionMenu, $otherSelect;

		beforeEach(function () {
			spyOn($.fn, 'selectize').and.callThrough();

			appendSetFixtures('<select class="action-menu"><option></option></select>');
			appendSetFixtures('<select class="select"><option></option></select>');

			$actionMenu = $('.action-menu');
			$otherSelect = $('.select');
		});

		afterEach(function () {
			$actionMenu = $otherSelect = undefined;
		});

		it('is defined', function () {
			expect(wmActionMenu).toBeDefined();
		});

		it('can be initialized', function () {
			expect(wmActionMenu()).toBeDefined();
		});

		it('targets elements with a class "action-menu" by default', function () {
			wmActionMenu();
			expect($actionMenu).toHaveClass('selectized');
		});

		it('targets elements with the "selector" property', function () {
			wmActionMenu({ selector: '.select' });
			expect($otherSelect).toHaveClass('selectized');
		});

		it('calls the selectize plugin', function () {
			wmActionMenu();
			expect($.fn.selectize).toHaveBeenCalled();
		});

		it('calls the selectize plugin with a options', function () {
			var options = { options: [] };
			wmActionMenu({}, options);
			expect($.fn.selectize.calls.mostRecent().args[0]).toEqual(jasmine.objectContaining(options));
		});

		it('calls the selectize plugin with a callback for dropdown open', function () {
			wmActionMenu();
			expect($.fn.selectize.calls.mostRecent().args[0]).toEqual(jasmine.objectContaining({ onDropdownOpen: jasmine.any(Function) }));
		});

		it('calls the selectize plugin with a callback for dropdown close', function () {
			wmActionMenu();
			expect($.fn.selectize.calls.mostRecent().args[0]).toEqual(jasmine.objectContaining({ onDropdownClose: jasmine.any(Function) }));
		});

		xdescribe('the dropdown open callback', function () {
			it('adds the -left class to the dropdown when left justified', function () {
				$actionMenu = wmActionMenu().siblings('.action-menu');
				var actionMenuDropdown = $actionMenu.find('.action-menu');
				$actionMenu.css({
					position: 'absolute',
					right: 0
				});
				// $actionMenu[0].style.position = 'absolute';
				// $actionMenu[0].style.right = '0';
				expect(actionMenuDropdown.get(0).getBoundingClientRect().right).toBe({});
				$actionMenu.trigger('focus');
				expect(actionMenuDropdown[0].classList).toHaveClass('-left');
			});
		});
	});
});
