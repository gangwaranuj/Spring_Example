'use strict';

import Application from '../core';
import Backbone from 'backbone';
import WorkerServicesParentView from './workerservices_parent_view';
import WorkerServicesView from './workerservices_view';
import StrideHealthView from './stride_view';

const views = {
	parentView: new WorkerServicesParentView()
};

function getView (service, Constructor) {
	if (!views[service]) {
		return views[service] = new Constructor({ service });
	}

	return views[service].render();
}

const Router = Backbone.Router.extend({
	routes: {
		'workerservices(/)'        : 'index',
		'workerservices/stride(/)' : 'stride',
		'workerservices/:service'  : 'serviceDetails',
		'*path'                    : 'default'
	},

	initialize() {
		Backbone.history.start({ pushState: true });
	},

	serviceDetails(service) {
		return getView(service, WorkerServicesView);
	},

	index() {
		return getView('index', WorkerServicesView);
	},

	stride() {
		return getView('stride', StrideHealthView);
	},

	default() {
		Backbone.history.navigate('workerservices', true);
	}
});

Application.init(config, Router);

