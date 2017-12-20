'use strict';

import Application from '../core';
import ListPage from './list_page';
import FormPage from './form_page';
import DetailsPage from './details_page';

Application.init({ name: 'campaigns', features: config }, () => {});

if (config.type === 'list') {
	ListPage();
} else if (config.type === 'form') {
	FormPage({
		emptyCompanyAvatar: config.emptyCompanyAvatar,
		emptyCompanyOverview: config.emptyCompanyOverview,
		emptyCustomCompanyOverview: config.emptyCustomCompanyOverview,
		groupId: config.groupId,
		groupName: config.groupName
	});
} else if (config.type === 'details') {
	DetailsPage({
		hasShortURL: config.hasShortURL,
		shortURL: config.shortURL,
		encryptedId: config.encryptedId,
		baseUrl: config.baseUrl,
		showStats: config.showStats,
		campaignId: config.campaignId
	});
}