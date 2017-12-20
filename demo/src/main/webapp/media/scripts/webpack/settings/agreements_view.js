import $ from 'jquery';
import wmModal from '../funcs/wmModal';
import '../config/wysiwyg';

export default function () {
	const agreementAlertModal = wmModal({
		title: 'Edit Agreement',
		content: `<p>
			Updating an agreement will remove all current members of any talent pool using this agreement
			as a requirement. This will give members of affected talent pools an opportunity to re-sign
			your updated agreement. This may take a few minutes to complete, depending on the size of your talent pool.
		</p>
		<p>Do you want to continue?</p>`,
		controls: [
			{
				text: 'Cancel',
				close: true
			},
			{
				text: 'Continue',
				primary: true,
				close: true
			}
		],
		customHandlers: [
			{
				event: 'click',
				selector: '.wm-modal--control.-primary',
				callback: () => $('#editagreement_form').submit()
			}
		]
	});

	$('#edit_agreement').on('click', (event) => {
		event.preventDefault();
		agreementAlertModal.show();
	});
}
