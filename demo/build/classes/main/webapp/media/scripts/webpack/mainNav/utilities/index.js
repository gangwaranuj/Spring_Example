import _ from 'underscore';
const utils = {};

utils.getCSRFToken = () => {
	return _.find(document.getElementsByTagName("meta"), (meta) => {
		return meta.name === "csrf-token"
	}).content;
};

utils.checkStatus = (res) => {
	if (res.status >= 200 && res.status < 300) {
		return res;
	} else {
		var error = new Error(res.statusText)
		error.res = res;
		throw error;
	}
}

utils.browser = {
	isDesktop: () => screen.width > 1025,
	isSmallMobile: () => screen.width < 520
};

export default utils;
