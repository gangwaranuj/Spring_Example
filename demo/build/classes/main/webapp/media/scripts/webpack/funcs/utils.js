/* eslint-disable */
/**
 * Parse query string i.e. convert ?a=1&b=2&c=3 to {a: 1, b: 2, c: 3}
 *
 * @param {String} (optional) queryString
 * @return {Object} query params
 */
export const getQueryParams = (queryString) => {
    // delete the leading '?'
	const query = (queryString || window.location.search).substring(1);
	if (!query) {
		return false;
	}
	return query.split('&').reduce((obj, elem) => {
		const arr = elem.split('=');
		return Object.assign(obj, { [arr[0]]: decodeURIComponent(arr[1]) });
	}, {});
};
