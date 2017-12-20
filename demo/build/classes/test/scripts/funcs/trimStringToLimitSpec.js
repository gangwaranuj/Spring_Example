define([
	'funcs/wm-trimStringToLimit'
], function (wmTrimStringToLimit) {

	describe('wm.funcs.trimStringToLimitSpec', function () {
		it('is defined', function () {
			expect(wmTrimStringToLimit).toBeDefined();
		});

		it('can be initialized', function () {
			expect(wmTrimStringToLimit()).toBeDefined();
		});

		it('returns a blank string if given a blank string', function () {
			expect(wmTrimStringToLimit('', 10)).toEqual('');
		});

		it('returns the input string if it does not go beyond the limit', function () {
			expect(wmTrimStringToLimit('sup', 3)).toEqual('sup');
		});

		it('trims a string to a limit if the string goes beyond the limit ', function () {
			expect(wmTrimStringToLimit('longerThanTenChars', 10)).toEqual('longerT&hellip;');
		});
	});
});
