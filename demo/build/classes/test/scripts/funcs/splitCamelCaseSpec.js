define([
	'funcs/wm-splitCamelCase'
], function (wmSplitCamelCase) {

	describe('wmSplitCamelCase', function () {
		'use strict';

		it('is defined', function () {
			expect(wmSplitCamelCase).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(wmSplitCamelCase()).toBeDefined();
		});

		it('returns the split string', function () {
			expect(wmSplitCamelCase()).toEqual(jasmine.any(String));
		});

		it('splits camel-cased words', function () {
			expect(wmSplitCamelCase('fooBar')).toBe('Foo Bar');
		});

		it('always capitalizes', function () {
			expect(wmSplitCamelCase('foobar')).toBe('Foobar');
		});

		it('does not split non-camel-cased words', function () {
			expect(wmSplitCamelCase('foobar').split(' ').length).toEqual(1);
		});

		it('can split multiple words', function () {
			expect(wmSplitCamelCase('fooBar bizzBaz')).toBe('Foo Bar Bizz Baz');
		});
	});
});
