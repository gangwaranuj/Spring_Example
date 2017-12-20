export default (document) => {
	if (document.verificationStatus === 'VERIFIED') {
		return '<small> - WM Verified</small>';
	}
};