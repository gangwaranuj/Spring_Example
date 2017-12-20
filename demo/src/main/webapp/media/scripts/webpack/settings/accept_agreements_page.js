import $ from 'jquery';

export default function (versionId) {
	$('button[data-action="agree"]').on('click', () => {
		$.post('/agreements/accept', { versionId }, () => {
			window.close();
		});
	});

	$('button[data-action="cancel"]').on('click', () => {
		window.close();
	});
}
