import React from 'react';
import { render } from 'react-dom';
import { Router, Route, IndexRoute, browserHistory } from 'react-router';
import { StyleRoot } from 'radium';
import { Provider } from 'react-redux';
import Application from '../core';
import configureStore from './configureStore';
import * as actions from './actions';
import WMApprovalsApp from './components/WMApprovalsApp';

const store = configureStore();

Application.init({}, () => {});

const fetchApprovalConfiguration = (nextState, replace, callback) => {
	store.dispatch(actions.getEmployeeList());
	store.dispatch(actions.fetchApprovalConfiguration());
	callback();
};

const renderApprovalsApplication = () => {
	render(
		<StyleRoot>
			<Provider store={ store }>
				<Router history={ browserHistory }>
					<Route path="/settings/manage/approvals">
						<IndexRoute component={ WMApprovalsApp } onEnter={ fetchApprovalConfiguration } />
					</Route>
				</Router>
			</Provider>
		</StyleRoot>,
	document.getElementById('approvals_container')
	);
};

renderApprovalsApplication();
