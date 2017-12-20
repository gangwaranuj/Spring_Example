'use strict';

import $ from 'jquery';
import wmMaskInput from '../funcs/wmMaskInput';

export default () => {
	var $contrySelector = $('#country,#country2');
	if ($contrySelector.val() === 'USA') {
		disableGovIdTypeOption(true);
		$('#govIdType option:first-child').attr('selected', 'selected');
	} else {
		disableGovIdTypeOption(false);
		$('#govIdType').val('NONUSTAXID');
	}

	$('#country, #country2').on('change', function () {
		var $contrySelector = $(this),
			stateFieldName = $contrySelector.attr('data-related-state'),  //state or state2
			provinceClass = $contrySelector.attr('data-related-province'); //province or province2

		if ($contrySelector.val() === 'USA') {
			$('select.' + stateFieldName)
				.show()
				.attr('name',stateFieldName);
			$('input.' + stateFieldName + ', select.' + provinceClass)
				.hide()
				.removeAttr('name');

			disableGovIdTypeOption(true);
			$('#govIdType option:first-child').attr('selected', 'selected');
		} else {
			if ($contrySelector.val() === 'CAN') {
				$('select.' + provinceClass)
					.show()
					.attr('name', stateFieldName);
				$('input.' + stateFieldName + ', select.' + stateFieldName)
					.hide()
					.removeAttr('name');
			} else {
				$('select.' + stateFieldName + ', select.' + provinceClass)
					.hide()
					.removeAttr('name');
				$('input.' + stateFieldName)
					.show()
					.attr('name', stateFieldName);
			}

			disableGovIdTypeOption(false);
			$('#govIdType').val('NONUSTAXID');
		}
	});

	$('.add_mailing_address').on('change', function () {
		$('#mailing_address').toggle();
	});

	$('#govIdType').on('change', function () {
		var value = this.value;
		if (value === 'SSN' || value === 'USTAXID') {
			wmMaskInput({ selector: '#govId' }, 'ssn');
		}
	});

	function disableGovIdTypeOption (toggle) {
		$('#govIdType option[value!="NONUSTAXID"]').prop('disabled', !toggle);
		$('#govIdType option[value="NONUSTAXID"]').prop('disabled', toggle);
	}

};
