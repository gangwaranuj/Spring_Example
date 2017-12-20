define([
	'funcs/wm-filters'
], function (wmFilters) {
	'use strict';

	describe('wmFilters', function () {
		'use strict';
		var fixture;

		beforeEach(function () {
			fixture = '<div class=filter-group><label class="filter"><input type=checkbox name=colors value=all><div class=filter--skin>All</div></label><label class=filter><input type=checkbox name=colors value=red><div class=filter--skin>Red</div></label><label class=filter><input type=checkbox name=colors value=green><div class=filter--skin>Green</div></label><label class=filter><input type=checkbox name=colors value=blue><div class=filter--skin>Blue</div></label></div>';
			$('#fixture').remove();
			$('body').append(fixture);
			wmFilters('.filter-group');
		});

		it('is defined', function() {
			expect(wmFilters).toBeDefined();
		});

		describe('select all', function() {
			it('will add select-all class to first filter', function() {
				expect($('.filter').eq(0).hasClass('filter--select-all')).toBe(true);
			});

			it('will uncheck all checkboxes when selected', function() {
				$('.filter').eq(1).find('input').prop('checked', true);
				$('.filter--select-all').click();
				expect($('.filter').eq(1).find('input').prop('checked')).toBe(false);
			});

			it('will uncheck select all when the second checkbox is selected', function() {
				$('.filter').eq(1).click();
				expect($('.filter--select-all').find('input').prop('checked')).toBe(false);
			});

		});


	});

});