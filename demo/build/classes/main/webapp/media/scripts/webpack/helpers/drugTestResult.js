export default (drugTestStatus, priorPassedDrugTest) => {
	if (drugTestStatus === 'requested' && !!priorPassedDrugTest) {
		return 'Drug Test PASSED';
	} else {
		return drugTestStatus && drugTestStatus.toLowerCase();
	}
};