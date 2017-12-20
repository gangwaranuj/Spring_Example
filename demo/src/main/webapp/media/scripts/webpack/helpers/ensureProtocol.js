export default (url) => {
	if (/^http:\/\//.test(url)) {
		return url;
	} else {
		return 'http://' + url;
	}
};