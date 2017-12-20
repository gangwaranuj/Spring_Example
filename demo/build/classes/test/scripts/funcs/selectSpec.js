define([
	'wmSelect',
	'selectize'
], function (wmSelect) {
	'use strict';

	describe('wmSelect', function () {
		var $select, $otherSelect, $singleSelect;

		beforeEach(function () {
			spyOn($.fn, 'selectize').and.callThrough();

			appendSetFixtures('<select class="wm-select" multiple><option></option></select>');
			appendSetFixtures('<select class="wm-select"><option></option></select>');
			appendSetFixtures('<select class="select"><option></option></select>');

			$select = $('.wm-select[multiple]');
			$singleSelect = $('.wm-select:not([multiple])');
			$otherSelect = $('.select');
		});

		afterEach(function () {
			$select = $otherSelect = $singleSelect = undefined;
		});

		it('is defined', function () {
			expect(wmSelect).toBeDefined();
		});

		it('can be initialized', function () {
			expect(wmSelect()).toBeDefined();
		});

		it('targets elements with a class "wm-select" by default', function () {
			wmSelect();
			expect($singleSelect).toHaveClass('selectized');
		});

		it('targets elements with the "selector" property', function () {
			wmSelect({ selector: '.select' });
			expect($otherSelect).toHaveClass('selectized');
		});

		it('calls the selectize plugin', function () {
			wmSelect();
			expect($.fn.selectize).toHaveBeenCalled();
		});

		it('calls the selectize plugin with options', function () {
			var options = { options: [] };
			wmSelect({}, options);
			expect($.fn.selectize).toHaveBeenCalledWith(options);
		});

		it('calls the selectize plugin with the remove plugin for multiple option select menus', function () {
			wmSelect({ selector: '.wm-select[multiple]' });
			expect($.fn.selectize.calls.mostRecent().args[0]).toEqual(jasmine.objectContaining({ plugins: ['remove_button'] }));
		});

		it('calls the selectize plugin with no max items for multiple option select menus', function () {
			wmSelect({ selector: '.wm-select[multiple]' });
			expect($.fn.selectize.calls.mostRecent().args[0]).toEqual(jasmine.objectContaining({ maxItems: null }));
		});
	});
});
