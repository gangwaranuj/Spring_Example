import React from 'react';
import { render } from 'react-dom';
import { Router, Route, IndexRoute, browserHistory } from 'react-router';
import { Provider } from 'react-redux';
import Application from '../core';
import configureStore from './configureStore';

import Root from './components/Root';
import WMChecklist from './components/WMChecklist';
import WMEmployees from './components/WMEmployees';
import WMAssignmentPreferencesView from './components/WMAssignmentPreferencesView';
import WMFirstAssignmentView from './components/WMFirstAssignmentView';

const store = configureStore();

Application.init(config, () => {});

render((
	<Provider store={ store }>
		<Router history={ browserHistory }>
			<Route path="/settings/onboarding" component={ Root }>
				<IndexRoute component={ WMChecklist } />
				<Route path="/settings/onboarding/employees" component={ WMEmployees } />
				<Route
					path="/settings/onboarding/assignment-preferences"
					component={ WMAssignmentPreferencesView }
				/>
				<Route
					path="/settings/onboarding/first-assignment"
					component={ WMFirstAssignmentView }
				/>
			</Route>
		</Router>
	</Provider>
), document.getElementById('app'));
