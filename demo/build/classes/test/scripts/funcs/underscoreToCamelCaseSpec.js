define([
	'funcs/wm-underscoreToCamelCase'
], function (wmUnderscoreToCamelCase) {
	describe('wmUnderscoreToCamelCase', function () {
		'use strict';

		it('is defined', function () {
			expect(wmUnderscoreToCamelCase).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(wmUnderscoreToCamelCase()).toBeDefined();
		});

		it('returns the modified string', function () {
			expect(wmUnderscoreToCamelCase()).toEqual(jasmine.any(String));
		});

		it('camel-cases underscored words', function () {
			expect(wmUnderscoreToCamelCase('foo_bar')).toBe('fooBar');
		});

		it('always transforms to lowercase', function () {
			expect(wmUnderscoreToCamelCase('FOO_BAR')).toBe('fooBar');
		});

		it('does not transform non-underscored words', function () {
			expect(wmUnderscoreToCamelCase('foobar')).toEqual('foobar');
		});

		it('can split multiple words', function () {
			expect(wmUnderscoreToCamelCase('FOO_BAR bizz_baz')).toBe('fooBar bizzBaz');
		});
	});
});
