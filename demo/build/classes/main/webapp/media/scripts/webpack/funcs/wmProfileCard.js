'use strict';

import $ from 'jquery';

export default (options) => {

	let settings = Object.assign({
		selector: '.profile-card',
		root: document
	}, typeof options === 'object' ? options : {});

	let toggleDrawerAction = function (event) {
		let profile = $(event.delegateTarget),
			profileActionButton = $(event.currentTarget).closest('.switch'),
			action = profileActionButton.find('.switch--checkbox').val(),
			currentActiveSwitch = profile.find('.switch--checkbox:checked');

		profileActionButton.toggleClass('-active').siblings('.profile-card--action').removeClass('-active');

		if (currentActiveSwitch.length && action === currentActiveSwitch.val() ) {
			event.preventDefault();
			closeDrawer(event);
		} else {
			let isActive = profileActionButton.hasClass('-active');
			closeAllOthers();
			profile.toggleClass('-open', isActive);
			profile.find('.profile-card--' + action).toggle(isActive).siblings().hide();
			profile.find('.profile-card--drawer').toggleClass('-active', isActive);
		}


	};

	let closeDrawer = function (event) {
		let profile = $(event.delegateTarget);

		profile.removeClass('-open');
		profile.find('.profile-card--action').removeClass('-active');
		profile.find('.switch--checkbox').prop('checked', false);
		profile.find('.profile-card--drawer').children().hide();
	};

	let closeAllOthers = function () {
		let $profileCards = $(settings.selector);
		$profileCards.removeClass('-open');
		$profileCards.find('.profile-card--action').removeClass('-active');
		$profileCards.find('.switch--checkbox').prop('checked', false);
		$profileCards.find('.profile-card--drawer').children().hide();
	};

	let initialize = function (index, element) {
		$(element).on('click', '.profile-card--action .switch--skin', toggleDrawerAction);
		$(element).on('click', '.profile-card--close', closeDrawer);
		componentHandler.upgradeAllRegistered();

		return true;
	};

	return $(settings.selector, settings.root).map(initialize);
};
