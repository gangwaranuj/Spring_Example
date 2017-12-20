'use strict';

import Application from '../core';
import UserProfilePopup from '../assignments/user_profile_popup_view';
import ListPage from '../invitations/list_page';
import SendPage from '../invitations/send_page';

Application.init(context, () => {});

if (Application.Data.mode === 'list') {
	new UserProfilePopup({ el: '.main.container' });
	new ListPage({ nowTime: Application.Data.nowTime });
} else if (Application.Data.mode === 'send') {
	new SendPage({
		companyName: Application.Data.companyName,
		isEmptyCompanyAvatar: Application.Data.isEmptyCompanyAvatar,
		isEmptyCompanyOverview: Application.Data.isEmptyCompanyOverview
	});
}
