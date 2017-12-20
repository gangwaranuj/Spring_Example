'use strict';

import $ from 'jquery';
import _ from 'underscore';

export default function () {

	function getParameterByName(name) {
		name = name.replace(/[\[]/, '\\\[').replace(/[\]]/, '\\\]');
		var regexS = '[\\?&]' + name + '=([^&#]*)';
		var regex = new RegExp(regexS);
		var results = regex.exec(window.location.search);
		return (results == null) ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
	}

	$('#form_industries').on('submit', function () {

		_.each($('[name="industry"]:checked'), function (el) {
			$('<input/>', {type: 'hidden', name: 'industry[]', value: $(el).val().split('_')[1]}).appendTo('#form_industries');
		});

		var groupVal = getParameterByName('group');
		if (groupVal !== '') {
			$('<input/>', {type: 'hidden', name: 'group', value: parseInt(groupVal, 10)}).appendTo('#form_industries');
		}
	});
};

