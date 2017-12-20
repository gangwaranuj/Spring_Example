'use strict';

import Application from '../core';
import SearchPage from './search_page';
import InvitationsPage from './invitations_page';
import MembershipsPage from './memberships_page';
import Helpers from './helpers';
import '../config/wysiwyg';

Application.init(config, () => {});

switch (config.data.type) {
	case 'invitations':
		InvitationsPage();
		break;
	case 'memberships':
		new MembershipsPage();
		break;
	case 'search':
		SearchPage(config.data.start);
		break;
}

Helpers();
