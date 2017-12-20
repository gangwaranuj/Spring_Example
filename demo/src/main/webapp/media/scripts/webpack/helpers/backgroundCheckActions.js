export default (isOwner, isInternal, backgroundCheckStatus, priorPassedBackgroundCheck) => {
	if ((isOwner || isInternal) && !(backgroundCheckStatus === 'requested')) {
		return '<a href="/screening/bkgrnd" class="renew-screening">(Renew)</a>';
	} else if (!!priorPassedBackgroundCheck && backgroundCheckStatus === 'requested') {
		return '<span class="renew-screening">(new check results pending)</span>';
	}
};