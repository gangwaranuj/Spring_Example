const getMetaInformation = name => document.querySelector(`meta[name="${name}"]`).getAttribute('content');

const getCSRFToken = () => {
	try {
		const token = getMetaInformation('csrf-token');
		return token;
	} catch (exception) {
		return console.warn('Application is missing a CSRF token.'); // eslint-disable-line no-console
	}
};

const CSRFToken = getCSRFToken();

export default CSRFToken;
