'use strict';

import $ from 'jquery';

export default () => {

	$('#language_id').change(function() {
		if ($(this).val()) {
			$('#fluency_container').show();
			$('#language_proficiency_type').val('');
		} else {
			$('#fluency_container').hide();
			$('#button_container').hide();
		}
	});

	$('#language_proficiency_type').change(function() {
		if ($(this).val()) {
			$('#button_container').show();
		} else {
			$('#button_container').hide();
		}
	});

	$('#language_id').val('');
};

