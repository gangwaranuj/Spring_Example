'use strict';

import $ from 'jquery';
import wmSelect from '../funcs/wmSelect';

export default () => {

	//Hide div w/id extra
	$('#crmselect').css('display', 'none');

	// Add onclick handler to checkbox w/id checkme
	$('#crmcheck').on('click', function () {
		// If checked
		if ($('#crmcheck').is(':checked')) {
			//show the hidden div
			$('#crmselect').show('fast');
		} else {
			$('#crmselect').hide('fast');
		}
	});

	wmSelect({ selector: 'select[multiple]'});
};
