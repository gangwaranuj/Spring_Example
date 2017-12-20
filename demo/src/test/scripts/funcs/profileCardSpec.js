define([
	'funcs/wm-profileCard'
], function (wmProfileCard) {

	describe('wmProfileCard', function () {
		'use strict';

		var profileCard;

		beforeEach(function () {
			appendSetFixtures('<div class="profile-card"></div>');

			profileCard = $('.profile-card');
		});

		it('is defined', function () {
			expect(wmProfileCard).toBeDefined();
		});

		it('is can be instantiated', function () {
			expect(wmProfileCard()).toBeDefined();
		});
	});

});