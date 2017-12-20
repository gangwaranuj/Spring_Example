define([
	'funcs/wm-tags'
], function (wmTags) {
	describe('wmTags', function () {
		'use strict';

		it('is defined', function () {
			expect(wmTags).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(wmTags()).toBeDefined();
		});
	});
});
