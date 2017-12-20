import en from './translation/lang_en';
import es from './translation/lang_es';

function format (str, params) {
	let formattedStr = str;
	for (let i = 0; i < params.length; i += 1) {
		const regexp = new RegExp(`\\{${i}\\}`, 'gi');
		formattedStr = formattedStr.replace(regexp, params[i]);
	}
	return formattedStr;
}


function getCookie (name) {
	let result = '';
	const value = `;  ${document.cookie}`;
	const parts = value.split(`; ${name}=`);
	if (parts.length === 2) result = parts.pop().split(';').shift();

	return result;
}

export default function translate (key, ...params) {
	const lang = getCookie('lang');

	let translation = `!!! ${key} !!!`;
	if (en[key] !== undefined) {
		translation = en[key];
	}

	if (lang === 'es' && es[key] !== undefined) {
		translation = es[key];
	}

	if (params.length) {
		translation = format(translation, params);
	}
	return translation;
}
