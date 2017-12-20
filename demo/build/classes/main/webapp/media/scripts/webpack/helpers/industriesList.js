export default (industries) => {
	return industries.map(({ name }) => name).join(', ');
};