export default (testStatus) => {
	if (testStatus === 'VERIFIED') {
		return 'Passed';
	} else {
		return 'Failed';
	}
};