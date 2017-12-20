export default (laneType, isInternal, workPhone, workPhoneExtension, mobilePhone) => {
	if (!!laneType || isInternal) {
		var result = '';
		if (workPhone) {
			result += '<a href="tel:' + workPhone +  '" class="user-contact--phone"><i class="wm-icon-phone-filled"></i>W: ' + workPhone;
		}
		if (workPhoneExtension) {
			result += ' ext. ' + workPhoneExtension;
		}
		if (workPhone) {
			result += '</a>';
		}
		if (mobilePhone) {
			result += '<a href="tel:' + mobilePhone + '" class="user-contact--phone"><i class="wm-icon-phone-filled"></i>M: ' + mobilePhone + '</a>';
		}
	}
	if (!!result) {
		return result;
	} else {
		return '';
	}
};