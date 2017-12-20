define([
	'funcs/wm-fetchNearbyAssignments'
], function (wmFetchNearbyAssignments) {

	describe('wm.funcs.fetchNearbyAssignments', function () {
		'use strict';

		var minimumRequiredRequestOptions = {
			lat: 0,
			lon: 0,
			d: 60,
			w: 'all',
			res: false
		};

		beforeEach(function () {
			spyOn($, 'ajax').and.returnValue($.Deferred());
		});

		it('is defined', function () {
			expect(wmFetchNearbyAssignments).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(wmFetchNearbyAssignments()).toBeDefined();
		});

		it('does not default with all required parameters', function () {
			expect(wmFetchNearbyAssignments().state()).toBe('rejected');
		});

		it('requires latitude', function () {
			var nearbyAssignments = wmFetchNearbyAssignments(_.omit(minimumRequiredRequestOptions, 'lat'));
			expect(nearbyAssignments.state()).toBe('rejected');
		});

		it('requires longitude', function () {
			var nearbyAssignments = wmFetchNearbyAssignments(_.omit(minimumRequiredRequestOptions, 'lon'));
			expect(nearbyAssignments.state()).toBe('rejected');
		});

		it('requires a time range to filter nearby assignments)', function () {
			var nearbyAssignments = wmFetchNearbyAssignments(_.omit(minimumRequiredRequestOptions, 'w'));
			expect(nearbyAssignments.state()).toBe('rejected');
		});

		it('require a flag indicating if additional assignment details should be returned (e.g longitude, latitude, etc)', function () {
			var nearbyAssignments = wmFetchNearbyAssignments(_.omit(minimumRequiredRequestOptions, 'res'));
			expect(nearbyAssignments.state()).toBe('rejected');
		});

		it('returns a promise object', function () {
			var nearbyAssignments = wmFetchNearbyAssignments(minimumRequiredRequestOptions);
			expect(['pending','resolved','rejected']).toContain(nearbyAssignments.state());
		});

		_.each(['lat','lon'], function (parameter) {
			_.each({ string: '0', number: 0 }, function (value, type) {
				it('accepts a ' + parameter + ' of type ' + type, function () {
					var options = _.clone(minimumRequiredRequestOptions);
					options[parameter] = value;
					expect(wmFetchNearbyAssignments(options).state()).toBe('pending');
				});
			});
			_.each({
				'function': function () {},
				'boolean': true,
				object: {},
				array: [],
				'empty string': ''
			}, function (value, type) {
				it('does not accept a ' + parameter + ' of type ' + type, function () {
					var options = _.clone(minimumRequiredRequestOptions);
					options[parameter] = value;
					expect(wmFetchNearbyAssignments(options).state()).toBe('rejected');
				});
			});
		});

		_.each({ string: '0', 'non negative number': 0 }, function (value, type) {
			it('accepts a distance of type ' + type, function () {
				var options = _.defaults({ d: value }, minimumRequiredRequestOptions);
				expect(wmFetchNearbyAssignments(options).state()).toBe('pending');
			});
		});

		it('accepts a res of type true', function () {
			var options = _.defaults({ res: true }, minimumRequiredRequestOptions);
			expect(wmFetchNearbyAssignments(options).state()).toBe('pending');
		});

		_.each({
			'function': function () {},
			number: 2,
			object: {},
			array: [],
			string: 'foo'
		}, function (value, type) {
			it('does not accept a res of type ' + type, function () {
				var options = _.defaults({ res: value }, minimumRequiredRequestOptions);
				expect(wmFetchNearbyAssignments(options).state()).toBe('rejected');
			});
		});

		_.each({
			'function': function () {},
			'boolean': true,
			object: {},
			array: [],
			'empty string': '',
			'negative number': -1
		}, function (value, type) {
			it('does not accept a distance of type ' + type, function () {
				var options = _.defaults({ d: value }, minimumRequiredRequestOptions);
				expect(wmFetchNearbyAssignments(options).state()).toBe('rejected');
			});
		});

		describe('the request', function () {
			it('uses the GET method to fetch', function () {
				wmFetchNearbyAssignments(minimumRequiredRequestOptions);
				expect($.ajax.calls.mostRecent().args[0].type).toBe('get');
			});
		});
	});

});