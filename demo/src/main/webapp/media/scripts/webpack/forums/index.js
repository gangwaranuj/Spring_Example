'use strict';

import Application from '../core';
import IndexPage from './index_page';
import DiscussionPage from './discussion_page';
import SearchPage from './search_page';
import PostPage from './post_page';
import '../funcs/jquery-helpers';

Application.init({ name: 'forums', features: config }, () => {});

switch (config.type) {
	case 'index':
		IndexPage({
			categoryId: config.categoryId
		});
		break;
	case 'discussionPage':
		DiscussionPage({
			isInternal: config.isInternal,
			isUserBanned: config.isUserBanned
		});
		break;
	case 'search':
		SearchPage();
		break;
	case 'post':
		PostPage();
		break;
}
