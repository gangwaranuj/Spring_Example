define([
	'jquery',
	'funcs/togglePersona'
], function ($, togglePersona) {

	describe('togglePersona', function () {
		'use strict';

		var $performElement, $createElement, $activeElement;

		beforeEach(function () {
			appendSetFixtures('<a class="perform-work-toggle" href="javascript:void(0)">Test</a>');
			appendSetFixtures('<a class="create-work-toggle" href="javascript:void(0)">Test</a>');
			appendSetFixtures('<a class="create-work-toggle active" href="javascript:void(0)">Test</a>');

			$performElement = $('.perform-work-toggle:not(.active)');
			$createElement = $('.create-work-toggle:not(.active)');
			$activeElement = $('.active');

			spyOn($, 'ajax');
		});

		it('is defined', function () {
			expect(togglePersona).toBeDefined();
		});

		it('can be instantiated', function () {
			expect(togglePersona()).toBeDefined();
		});

		it('returns false', function () {
			expect(togglePersona()).toBeFalsy();
		});

		it('sends an AJAX request', function () {
			togglePersona();
			expect($.ajax).toHaveBeenCalled();
		});

		it('does not send an AJAX request when the button is active', function () {
			togglePersona.call($activeElement);
			expect($.ajax).not.toHaveBeenCalled();
		});

		it('sends a POST request', function () {
			togglePersona();
			expect($.ajax.calls.mostRecent().args[0].type).toBe('post');
		});

		it('sends a data payload', function () {
			togglePersona();
			expect($.ajax.calls.mostRecent().args[0].data).toBeDefined();
		});

		it('sends a value for buyer', function () {
			togglePersona();
			expect($.ajax.calls.mostRecent().args[0].data.buyer).toBeDefined();
		});

		it('sends a boolean value for buyer', function () {
			togglePersona();
			expect($.ajax.calls.mostRecent().args[0].data.buyer).toEqual(jasmine.any(Boolean));
		});

		it('sends a value for seller', function () {
			togglePersona();
			expect($.ajax.calls.mostRecent().args[0].data.seller).toBeDefined();
		});

		it('sends a boolean value for seller', function () {
			togglePersona();
			expect($.ajax.calls.mostRecent().args[0].data.seller).toEqual(jasmine.any(Boolean));
		});

		it('sends a buyer value of true for create work button', function () {
			togglePersona.call($createElement);
			expect($.ajax.calls.mostRecent().args[0].data.buyer).toBeTruthy();
		});

		it('sends a seller value of false for create work button', function () {
			togglePersona.call($createElement);
			expect($.ajax.calls.mostRecent().args[0].data.seller).toBeFalsy();
		});

		it('sends a buyer value of false for perform work button', function () {
			togglePersona.call($performElement);
			expect($.ajax.calls.mostRecent().args[0].data.buyer).toBeFalsy();
		});

		it('sends a seller value of true for create work button', function () {
			togglePersona.call($performElement);
			expect($.ajax.calls.mostRecent().args[0].data.seller).toBeTruthy();
		});
	});

});