import $ from 'jquery';

let form;

const track = (targets, field, action) => {
	[...targets].forEach(target => {
		target.addEventListener(action, ({ currentTarget }) => {
			analytics.track('Assignment Creation', {
				field,
				fieldId: currentTarget.id,
				fieldText: currentTarget.textContent,
				fieldClass: currentTarget.className,
				action
			});
		});
	});
};

const trackContainer = (selector, field) => {
	if (typeof form === 'undefined') {
		console.warn('The `trackContainer` method is called before variable `form` is assigned.');
		return;
	}

	const section = form.querySelector(selector);
	if (section === null) {
		console.warn(`The section ${selector} does not exist and cannot track its inputs.`);
		return;
	}

	track(section.querySelectorAll('input'), field, 'change');
	track(section.querySelectorAll('textarea'), field, 'change');
	track(section.querySelectorAll('select'), field, 'change');
	track(section.querySelectorAll('button'), field, 'click');
	track(section.querySelectorAll('a'), field, 'click');
};

export default () => {
	form = document.getElementById('assignments_form');

	track(form.querySelectorAll('#title-input'), 'title', 'change');
	track(form.querySelectorAll('#desired_skills-typeahead'), 'skills', 'change');
	track(form.querySelectorAll('div.uploader-container'), 'upload', 'click');
	track(form.querySelectorAll('#select_filemanager_files'), 'file manager', 'click');
	track(form.querySelectorAll('button[data-action=add-requirement-set]'), 'add requirements', 'click');

	trackContainer('.sending-options-container', 'sending options');
	trackContainer('#price-container', 'pricing');
	trackContainer('#assignment-location-container', 'location');
	trackContainer('#date-and-time-container', 'scheduling');
	trackContainer('#secondary_contact', 'secondary contact');
	trackContainer('#primary-contact', 'primary contact');
	trackContainer('#assignment-custom-fields', 'custom fields');

	setTimeout(() => {
		const description = $($('#desc-text').wysiwyg('document')).find('body');
		const instructions = $($('#instructions-text').wysiwyg('document')).find('body');
		track(description, 'description', 'click');
		track(instructions, 'instructions', 'click');
	}, 1000);
};
