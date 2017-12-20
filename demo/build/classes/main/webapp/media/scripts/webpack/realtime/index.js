import Backbone from 'backbone';
import Application from '../core';
import Realtime from '../realtime/main';
import WorkerActionsView from '../assignments/worker_actions_view';
import '../config/datepicker';

const defaultSorts = {
	time_to_appt: 'asc',
	order_age: 'asc',
	scheduled_time: 'asc',
	details: 'asc',
	spend_limit: 'desc',
	questions: 'desc',
	offers: 'desc',
	declines: 'desc',
	last_updated: 'desc'
};

const Router = Backbone.Router.extend({
	routes: {
		'': 'sort',
		'sort/:column': 'sort',
		'sort/:column/:order': 'sort'
	},

	initialize () {
		this.realtimeView = Realtime.Main();
		new WorkerActionsView();
		Backbone.history.start();
	},

	sort (column = 'order_age', order = defaultSorts[column]) {
		this.realtimeView.sort(column, order);
	}
});

Application.init(config, Router);
