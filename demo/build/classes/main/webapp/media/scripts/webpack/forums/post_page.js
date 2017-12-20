'use strict';
import $ from 'jquery';
import _ from 'underscore';
import wmSelect from '../funcs/wmSelect';

export default () => {

	function updateCountdown() {
		// 1500 is the max post length
		const remaining = 1500 - $('#commentField').val().length;
		$('.post-char-countdown').text(remaining + ' characters remaining.');
	}

	$(document).on('input propertychange', '#commentField, #titleField' , function () {
		updateCountdown();
		$('button').prop('disabled', (_.isEmpty($('#commentField').val()) || _.isEmpty($('#titleField').val())));
	});

	$(document).ready(function() {
		updateCountdown();
		var options, selectizeOptions;
		options = {
			selector: '.chzn-select'
		};
		selectizeOptions = {
			placeholder: 'Select Some Tags'
		};
		wmSelect(options, selectizeOptions);
		$('#tagField_chzn').removeAttr('style');
		$('#tagField_chzn').width('100%');
	});
}

