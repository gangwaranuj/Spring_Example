import React from 'react';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import { Router, Route, IndexRoute, browserHistory } from 'react-router';
import { StyleRoot } from 'radium';
import Application from '../core';
import configureStore from './configureStore';
import TalentPoolList from './components/TalentPoolList/index';
import WMLockedMessaging from '../components/WMLockedMessaging';
import WMMasquerade from '../components/WMMasquerade';
import * as actions from './actions';

Application.init(config, () => {});

const store = configureStore();

if (Application.Features.hasOrgStructures) {
	Application.Events.on('org:modechange', (orgMode) => {
		store.dispatch(actions.changeOrgMode(orgMode));
	});

	const currentOrgMode = window.navConfig.currentUser.orgModes.find(it => {
		return it.uuid === window.navConfig.currentUser.savedOrgMode;
	});
	store.dispatch(actions.changeOrgMode(currentOrgMode));
}

const openNewTalentPool = (nextState, replace, callback) => {
	store.dispatch(actions.newTalentPool());
	callback();
};

const manageTalentPool = (nextState, replace, callback) => {
	const groupId = Number(nextState.params.groupId);
	store.dispatch(actions.manageTalentPoolByGroupId(groupId));
	callback();
};

const TalentPoolApp = () => (
	<div>
		<WMMasquerade
			isMasquerading={ config.data.isMasquerading }  // eslint-disable-line no-undef
		/>
		<WMLockedMessaging
			{ ...notificationConfig }	// eslint-disable-line no-undef
		/>
		<TalentPoolList
			enableOrgStructures={ Application.Features.hasOrgStructures }
		/>
	</div>
);

const renderTalentPoolApplication = () => {
	render(
		<StyleRoot>
			<Provider store={ store }>
				<Router history={ browserHistory }>
					<Route path="groups">
						<IndexRoute component={ TalentPoolApp } />
						<Route path="create" component={ TalentPoolApp } onEnter={ openNewTalentPool } />
						<Route path=":groupId" component={ TalentPoolApp } onEnter={ manageTalentPool } />
					</Route>
					<Route path="groups/manage">
						<IndexRoute component={ TalentPoolApp } />
						<Route path="step1/*" component={ TalentPoolApp } />
					</Route>
				</Router>
			</Provider>
		</StyleRoot>,
		document.getElementById('talent-pool-bucket')
	);
};

renderTalentPoolApplication();
store.dispatch(actions.fetchTalentPools());
