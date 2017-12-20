define([
	'wmNotify',
	'wmAlert',
	'wmTemplates'
], function (wmNotify, wmAlert, wmTemplates) {
	'use strict';

	describe('wmNotify', function () {
		beforeEach(function () {
			spyOn($, 'notify').and.callThrough();
			wmTemplates.alert = jasmine.createSpy('alert');
		});

		it('is defined', function () {
			expect(wmNotify).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(wmNotify()).toBeDefined();
		});

		it('calls $.notify', function () {
			wmNotify({ messages: ['foo', 'bar'] });
			expect($.notify).toHaveBeenCalled();
		});

		it('accepts a string message as its paramater', function () {
			wmNotify('foo');
			expect($.notify.calls.mostRecent().args[0].message).toEqual('foo');
		});

		it('accepts an array of messages as its paramater', function () {
			wmNotify(['foo', 'bar', 'baz']);
			expect($.notify.calls.count()).toEqual(3);
			expect($.notify.calls.argsFor(0)[0].message).toEqual('foo');
			expect($.notify.calls.argsFor(1)[0].message).toEqual('bar');
			expect($.notify.calls.argsFor(2)[0].message).toEqual('baz');
		});
	});

	describe('wmAlert', function () {
		'use strict';

		beforeEach(function () {
			wmTemplates.alert = jasmine.createSpy('alert');
		});

		it('is defined', function () {
			expect(wmAlert).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(wmAlert()).toBeDefined();
		});
	});
});
