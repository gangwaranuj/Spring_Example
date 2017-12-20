import $ from 'jquery';
import 'jquery-ui';
import wmMaskInput from '../funcs/wmMaskInput';

const loadIntroJs = async() => {
	const module = await import(/* webpackChunkName: "IntroJs" */ '../config/introjs');
	return module.default;
};

export default (options) => {
	$('[name=type]').on('change', function (event) {
		var type = $(this).val(),
			sel = '[rel=' + type + ']';

		if (type === 'gcc'){
			window.location.href = '/funds/accounts/gcc';
			event.stopPropagation();
		}

		if (type === 'ach' && options.country === '') {
			$('.config').hide();
			$('.info').hide();
			$('#countryControl').toggle(false);
			$('#taxInfoInstructions').toggle(true);
		} else {
			$('.config').hide().siblings(sel).show();
			$('.info').hide().siblings(sel).show();
			$('#countryControl').toggle(type === 'ach');
			$('#taxInfoInstructions').toggle(false);
		}
	}).trigger('change');

	$('#routing_number').autocomplete({
		minLength: 3,
		source: '/funds/accounts/routing-numbers?countryId=' + options.country,
		select: function (event, ui) {
			$('#routing_number').val(ui.item.routingNumber);
			$('#bank_name').val(ui.item.bankName);
			return false;
		}
	}).data('ui-autocomplete')._renderItem = function (ul, item) {
		return $('<li></li>')
			.data('item.autocomplete', item)
			.append('<a>' + item.bankName + ' (' + item.routingNumber + ')</a>')
			.appendTo(ul);
	};

	$('#branch_number').autocomplete({
		minLength: 3,
		source: '/funds/accounts/routing-numbers?countryId=' + options.country,
		select: function (event, ui) {
			$('#branch_number').val(ui.item.routingNumber.substr(4));
			$('#bank_name').val(ui.item.bankName);
			$('#institution_number').val(ui.item.routingNumber.substr(1, 3));
			return false;
		}
	}).data('ui-autocomplete')._renderItem = function (ul, item) {
		return $('<li></li>')
			.data('item.autocomplete', item)
			.append('<a>' + item.bankName + ' (' + item.routingNumber + ')</a>')
			.appendTo(ul);
	};

	wmMaskInput({ selector: '#account_number, #account_number_confirm' }, (new Array(50)).join('9'));

	if (options.isBuyer) {
		loadIntroJs().then((IntroJs) => {
			const intro = IntroJs('intro-add-account');
			intro.setOptions({
				steps: [{ intro: '<h4>Enabling payment terms</h4><p>Connecting your bank account to Work Market is easy.</p><p>Fill out the form to initiate 2 small deposits to your bank account.</p><p>Verify the amount of those deposits (will take ~3 days to hit) to complete your connection.</p>' }]
			});

			intro.watchOnce();
		})
	}

	$('#country').on('change', function () {
		var type = $(this).val();
		var controls = $('[data-country]');
		controls.forEach((control) => $(control).toggle($(control).data('country') === type));
	});
};
