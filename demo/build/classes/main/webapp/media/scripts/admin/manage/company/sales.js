var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.manage = wm.pages.admin.manage || {};
wm.pages.admin.manage.company = wm.pages.admin.manage.company || {};

wm.pages.admin.manage.company.sales = function (companyId) {
	'use strict';

	return function () {
		$.getJSON('/admin/manage/company/' + companyId + '/accountOwner', function (response){
			if (response) {
				$('#account-owner').html(response.data['results']);
				$('#account-owner').find('.wm-spinner').hide();
			}
		});
	}
};
