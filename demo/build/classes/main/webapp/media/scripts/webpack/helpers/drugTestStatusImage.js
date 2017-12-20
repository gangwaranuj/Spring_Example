export default (drugTestStatus) => {
	if (drugTestStatus === 'failed') {
		return mediaPrefix + '/images/live_icons/assignments/failed_checks_2.svg';
	} else if (drugTestStatus === 'passed') {
		return mediaPrefix + '/images/passed_drug.png';
	} else {
		return mediaPrefix + '/images/live_icons/assignments/pending_check_2.svg';
	}
};