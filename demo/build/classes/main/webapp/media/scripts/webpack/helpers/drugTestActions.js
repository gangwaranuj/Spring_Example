export default (isOwner, isInternal, drugTestStatus, priorPassedDrugTest) => {
	if ((isOwner || isInternal) && !(drugTestStatus === 'requested')) {
		return '<a href="/screening/drug" class="renew-screening">(Retake)</a>';
	} else if (!!priorPassedDrugTest && drugTestStatus === 'requested') {
		return '<span class="renew-screening">(new test results pending)</span>';
	}
};