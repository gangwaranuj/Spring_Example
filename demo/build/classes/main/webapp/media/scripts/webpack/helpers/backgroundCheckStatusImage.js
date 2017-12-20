export default (drugTestStatus) => {
	if (drugTestStatus === 'failed') {
		return mediaPrefix + '/images/live_icons/assignments/failed_checks_2.svg';
	} else if (drugTestStatus === 'passed') {
		return mediaPrefix + '/images/live_icons/assignments/passed_check_2.svg';
	} else {
		return mediaPrefix + '/images/live_icons/assignments/pending_check_2.svg';
	}
};