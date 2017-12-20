'use strict';

import $ from 'jquery';
import wmSelect from '../funcs/wmSelect';

export default function () {
	//Hide div w/id extra
	$('#crmselect').css('display', 'none');

	// Add onclick handler to checkbox w/id checkme
	$('#crmcheck').click(function () {

		// If checked
		if ($('#crmcheck').is(':checked')) {
			//show the hidden div
			$('#crmselect').show('fast');
		} else {
			//otherwise, hide it
			$('#crmselect').hide('fast');
		}
	});

	if ($('#crmcheck').is(':checked')) {
		$('#crmselect').show('fast');
	}

	$('#internal').on('click', function (){
		if ($('#internal').is(':checked')) {
			$('#internal-rights').show();
		} else {
			$('input[name="internal_roles"]').removeAttr('checked');
			$('#internal-rights').hide();
		}

	});

	if ($('#internal').is(':checked')) {
		$('#internal-rights').show("fast");
	}

	$('button#deletesubmit').click(function () {
		if (confirm('Are you sure you want to delete this user?')) {
			$('#submitaction').val('delete');
			$('#editform').trigger('submit');
			return true;
		} else {
			return false;
		}
	});

	wmSelect({selector: 'select[multiple]'});
};
