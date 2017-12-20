import React from 'react';
import injectTapEventPlugin from 'react-tap-event-plugin';
import { render } from 'react-dom';
import LandingPage from './template';

// eslint-disable-next-line max-len
// TODO: @artivilla import this script across all desktop views that don't have mainnav on their page
injectTapEventPlugin();

render(
	<LandingPage
		encryptedId={ config.encryptedId }
		isInvitation={ config.isInvitation }
		csrf={ config.csrf }
		campaignText={ config.campaignText }
		companyNumber={ config.companyNumber }
	/>,
	document.getElementById('landing-page-bucket')
);
