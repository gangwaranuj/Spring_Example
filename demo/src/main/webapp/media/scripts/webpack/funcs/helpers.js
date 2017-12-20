/**
 * @deprecated This file is no longer used for handlebars helpers.
 * Create new helper modules in webpack/helpers/ directory
 */
import moment from 'moment';
import _ from 'underscore';
import 'underscore.inflection';
import Handlebars from 'handlebars-template-loader/runtime';


// ******** PROFILE MODAL *********

Handlebars.registerHelper('limitedVisibility', function (limitedVisibility, lastName) {
	if (!!limitedVisibility) {
		return lastName.substring(0,1);
	} else {
		return lastName;
	}
});

Handlebars.registerHelper('hasChangedEmail', function (isOwner, changedEmail, email) {
	if (isOwner && changedEmail) {
		return '<p class="tac">' +
			'You have changed your email address to <em>' + changedEmail + '</em>. Until you confirm' +
			'the new address, please use ' + email + ' to log in to the site.' +
			'</p>';
	}
});

Handlebars.registerHelper('canAddToNetwork', function (isSuspended, isBlocked, isOwner, options) {
	if (!isSuspended && !isBlocked && !isOwner) {
		return options.fn(this);
	}
	return options.inverse(this);
});

Handlebars.registerHelper('showPhoneNumbers', function (laneType, isInternal, workPhone, workPhoneExtension, mobilePhone) {
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
});

Handlebars.registerHelper('hasVideosOrPhotos', function (hasVideo, hasPhoto, options) {
	if (!!hasVideo || !!hasPhoto) {
		return options.fn(this);
	}
	return options.inverse(this);
});

Handlebars.registerHelper('drugTestResult', function (drugTestStatus, priorPassedDrugTest) {
	if (drugTestStatus === 'requested' && !!priorPassedDrugTest) {
		return 'Drug Test PASSED';
	} else {
		return drugTestStatus && drugTestStatus.toLowerCase();
	}
});

Handlebars.registerHelper('drugTestActions', function (isOwner, isInternal, drugTestStatus, priorPassedDrugTest) {
	if ((isOwner || isInternal) && !(drugTestStatus === 'requested')) {
		return '<a href="/screening/drug" class="renew-screening">(Retake)</a>';
	} else if (!!priorPassedDrugTest && drugTestStatus === 'requested') {
		return '<span class="renew-screening">(new test results pending)</span>';
	}
});

Handlebars.registerHelper('backgroundCheckResult', function (backgroundCheckStatus, priorPassedBackgroundCheck) {
	if (backgroundCheckStatus === 'requested' && !!priorPassedBackgroundCheck) {
		return 'Background Check PASSED';
	} else {
		return backgroundCheckStatus && backgroundCheckStatus.toLowerCase();
	}
});

Handlebars.registerHelper('backgroundCheckActions', function (isOwner, isInternal, backgroundCheckStatus, priorPassedBackgroundCheck) {
	if ((isOwner || isInternal) && !(backgroundCheckStatus === 'requested')) {
		return '<a href="/screening/bkgrnd" class="renew-screening">(Renew)</a>';
	} else if (!!priorPassedBackgroundCheck && backgroundCheckStatus === 'requested') {
		return '<span class="renew-screening">(new check results pending)</span>';
	}
});

Handlebars.registerHelper('taxEntityStatus', function (hasTaxEntity, hasVerifiedTaxEntity) {
	if (!!hasVerifiedTaxEntity) {
		return '<p>Tax Information: <b>Verified</b></p>';
	} else if (!!hasTaxEntity) {
		return '<p>Tax Information: <b>Unverified</b></p>';
	}
});

Handlebars.registerHelper('ensureProtocol', function (url) {
	if (/^http:\/\//.test(url)) {
		return url;
	} else {
		return 'http://' + url;
	}
});

Handlebars.registerHelper('testStatus', function (testStatus) {
	if (testStatus === 'VERIFIED') {
		return 'Passed';
	} else {
		return 'Failed';
	}
});

Handlebars.registerHelper('doesNotBelongToAnyGroup', function (publicGroups, privateGroups, options) {
	publicGroups  = publicGroups || [];
	privateGroups = privateGroups || [];
	if (publicGroups.length + privateGroups.length > 0) {
		return options.inverse(this);
	}
	return options.fn(this);
});

Handlebars.registerHelper('canSeeRoles', function (isOwner, isAdmin, options) {
	if (!isOwner && isAdmin) {
		return options.fn(this);
	}
	return options.inverse(this);
});

Handlebars.registerHelper('hasHourlyRate', function (onSiteHourlyRate, offSiteHourlyRate, options) {
	if (onSiteHourlyRate > 0 || offSiteHourlyRate > 0) {
		return options.fn(this);
	}
	return options.inverse(this);
});

Handlebars.registerHelper('industriesList', function (industries) {
	var list = _.map(industries, function (industry) {
		return industry.name;
	});
	return list.join(', ');
});

Handlebars.registerHelper('documentVerified', function (document) {
	if (document.verificationStatus === 'VERIFIED') {
		return '<small> - WM Verified</small>';
	}
});

Handlebars.registerHelper('canShowAssets', function (isOwner, isInternal, assets, options) {
	if ((isOwner || isInternal) && assets) {
		return options.fn(this);
	}
	return options.inverse(this);
});

Handlebars.registerHelper('canReviewDocument', function (isInternal, verificationStatus) {
	if (verificationStatus === 'PENDING' && isInternal) {
		return '<a href="/admin/certifications/review" title="Review"><i class="wm-icon-bell"></i></a>';
	}
});

Handlebars.registerHelper('canEditDocument', function (isInternal, userId, verificationStatus) {
	if (verificationStatus === 'VERIFIED' && isInternal) {
		return '<a href="/admin/licenses/edit_userlicense?id={{license.id}}&user_id={{facade.id}}" title="Edit"><i class="wm-icon-edit"></i></a>';
	}
});

Handlebars.registerHelper('hasLinkedInPositions', function (linkedInVerified, linkedInPositions, options) {
	if (linkedInVerified && linkedInPositions && linkedInPositions.length > 0) {
		return options.fn(this);
	}
	return options.inverse(this);
});

Handlebars.registerHelper('positionEndDate', function (current, dateToMonth, dateToYear) {
	if (!!current) {
		return 'Present';
	} else if (!!dateToMonth) {
		return moment('' + dateToMonth).format('MMM') + ' ' + dateToYear;
	} else {
		return dateToYear;
	}
});

Handlebars.registerHelper('educationEndDate', function (dateToMonth, dateToYear) {
	if (!dateToYear) {
		return 'Present';
	} else if (!!dateToMonth) {
		return moment('' + dateToMonth).format('MMM') + ' ' + dateToYear;
	} else {
		return dateToYear;
	}
});

Handlebars.registerHelper('canDisplayTags', function (allowTagging, isOwner, isLane4LimitedVisibility, options) {
	if (allowTagging && !isOwner && !isLane4LimitedVisibility) {
		return options.fn(this);
	}
	return options.inverse(this);
});

Handlebars.registerHelper('laneTypeBadge', function (laneType, includeDescription) {
	var badge = [];
	switch(laneType) {
	case 0:
		badge = ['Employee', 'E'];
		break;
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
});

Handlebars.registerHelper('resourceCount', function (collection) {
	if (collection && collection.length > 0) {
		return collection.length;
	}
	return 0;
});

Handlebars.registerHelper('isAsset', function (type, options) {
	if (type === 'ASSET') {
		return options.fn(this);
	} else if (type === 'LINK') {
		return options.inverse(this);
	}
});

Handlebars.registerHelper('canShowAsset', function (isOwner, isGroupOwner, availabilityCode, options) {
	if (isOwner || (isGroupOwner && (availabilityCode === 'group')) || availabilityCode === 'all') {
		return options.fn(this);
	} else {
		return options.inverse(this);
	}
});

Handlebars.registerHelper('assetsImageUrl', function (assets, index) {
	return assets[index].relativeUri;
});

Handlebars.registerHelper('qualityValueNotApplicable', function (qualityValue, options) {
	if (qualityValue !== 'Not applicable') {
		return options.fn(this);
	}
	return options.inverse(this);
});

Handlebars.registerHelper('resourceLabels', function (labels) {
	var result = '';
	_.each(labels, function (value, key) {
		result += '<span class="label important tooltipped tooltipped-n" aria-label="' + value + '">' + key + '</span>';
	});
});

Handlebars.registerHelper('drugTestStatusImage', function (drugTestStatus) {
	if (drugTestStatus === 'failed') {
		return mediaPrefix + '/images/live_icons/assignments/failed_checks_2.svg';
	} else if (drugTestStatus === 'passed') {
		return mediaPrefix + '/images/passed_drug.png';
	} else {
		return mediaPrefix + '/images/live_icons/assignments/pending_check_2.svg';
	}
});

Handlebars.registerHelper('backgroundCheckStatusImage', function (drugTestStatus) {
	if (drugTestStatus === 'failed') {
		return mediaPrefix + '/images/live_icons/assignments/failed_checks_2.svg';
	} else if (drugTestStatus === 'passed') {
		return mediaPrefix + '/images/live_icons/assignments/passed_check_2.svg';
	} else {
		return mediaPrefix + '/images/live_icons/assignments/pending_check_2.svg';
	}
});

Handlebars.registerHelper('activeBadge', function (count) {
	return count > 0 ? '-active' : '';
});

Handlebars.registerHelper('formatDate', function (date) {
	return moment(date).format('MMMM D YYYY');
});

Handlebars.registerHelper('formatDateShort', function (date) {
	return moment(date).format('MMM YYYY');
});

Handlebars.registerHelper('monthName', function (month) {
	return moment('' + month).format('MMM');
});

Handlebars.registerHelper('formatTime', function (time) {
	return moment(time).format('h:mm A');
});

Handlebars.registerHelper('existingTags', function (tags) {
	return tags.join(', ');
});

Handlebars.registerHelper('stripProtocol', function (str) {
	str = str.replace('http://', '//');
	str = str.replace('https://', '//');
	return str;
});
