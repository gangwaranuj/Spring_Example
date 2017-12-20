export default (str) => {
	str = str.replace('http://', '//');
	str = str.replace('https://', '//');
	return str;
}
