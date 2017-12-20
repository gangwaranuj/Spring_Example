export default (backgroundCheckStatus, priorPassedBackgroundCheck) => {
	if (backgroundCheckStatus === 'requested' && !!priorPassedBackgroundCheck) {
		return 'Background Check PASSED';
	} else {
		return backgroundCheckStatus && backgroundCheckStatus.toLowerCase();
	}
};