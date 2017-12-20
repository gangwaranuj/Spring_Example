import $ from 'jquery';
import _ from 'underscore';

export default () => {
	$(document).on('click', '.forums-tag', function (e) {
		e.preventDefault();
		const clickedTag = $(this).text();
		$('.tag-form[value="' + clickedTag + '"]').prop('selected', true);
		$('#forumsSearchFormTag').trigger('submit');
	});

	$('#keywords').bind('input', function () {
		$('#forumSearchSideBtn').prop('disabled', _.isEmpty($('#keywords').val()));
	});

	$(document).ready(function () {
		$('#forumSearchSideBtn').prop('disabled', _.isEmpty($('#keywords').val()));
	});
};

