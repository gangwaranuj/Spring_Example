import fetch from 'isomorphic-fetch';
import $ from 'jquery';
import wmModal from '../funcs/wmModal';
import languageConflictModalTemplate from './templates/language_conflict_modal.hbs';
import Application from '../core';

const ConflictModal = (locale, preferredLocale) => {
	const { supportedLocales } = window.workmarket;
	const localeName = supportedLocales.find(lang => lang.code === locale).language;
	const preferredLocaleName = supportedLocales.find(lang => lang.code === preferredLocale).language;

	const conflictModal = wmModal({
		autorun: true,
		title: 'WorkMarket Language Change',
		destroyOnClose: true,
		content: languageConflictModalTemplate({
			locale: localeName,
			preferred: preferredLocaleName
		}),
		showCloseIcon: false
	});

	$('#language_conflict_modal .cta-confirm-yes').on('click', () => {
		/** todo Update language switching to subdirectory when solidified */
		window.location.href = `${window.location.origin + window.location.pathname}?lang=${preferredLocale}`;
	});

	$('#language_conflict_modal .cta-confirm-no').on('click', () => {
		fetch('/service/language/update', {
			method: 'POST',
			credentials: 'same-origin',
			body: JSON.stringify({ locale }),
			headers: new Headers({
				'Content-Type': 'application/json',
				'X-CSRF-Token': Application.CSRFToken,
				'Data-Type': 'json'
			})
		})
			.then(res => res.json())
			.then((res) => {
				if (res.successful) {
					conflictModal.hide();
					return res;
				}
				throw new Error('Something went wrong trying to update the users preferred language', res);
			})
			.catch((error) => {
				console.error({ error }); //eslint-disable-line
			});
	});
};

export default ConflictModal;
