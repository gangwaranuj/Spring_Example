import $ from 'jquery';

export default function () {
	$('#userName').on('change', () => {
		$('input#password').val('');
	});

	$('#clear_credentials').click((event) => {
		event.preventDefault();
		$('#autotask_user_fields').find(':input').val('');
	});

	const $notesEnabled = $('#notesEnabled');
	const $notesInternal = $('#notesInternal');

	if ($notesEnabled.prop('checked')) {
		$notesInternal.parent().show();
	} else {
		$notesInternal.parent().hide();
	}
	$notesEnabled.on('change', function onChange () {
		if ($(this).prop('checked')) {
			$notesInternal.parent().show();
		} else {
			$notesInternal.parent().hide();
		}
	});
}
