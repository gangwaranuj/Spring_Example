export default (laneType, includeDescription) => {
	var badge = [];
	switch(laneType) {
		case 0:
		case 1:
			badge = ['Employee', 'E'];
			break;
		case 2:
			badge = ['Invited Contractor', 'C'];
			break;
		case 3:
			badge = ['Third Party Contractor', '3'];
			break;
		default:
			return '';
	}

	var result = '<span class="lane-type-badge tooltipped tooltipped-n" aria-label="' + badge[0] + '">' + badge[1] + '</span>';

	if (!!includeDescription) {
		result += badge[0];
	}

	return result + '<br>';
};