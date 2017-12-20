'use strict';

import Application from '../core';
import ProfileIndex from './profile';

Application.init(config, () => {});

ProfileIndex(
	config.userNumber,
	config.userId,
	config.allScorecard,
	config.companyScorecard,
	config.paidassignforcompany,
	config.facade,
	config.isDispatch,
	config.isOwner
);
