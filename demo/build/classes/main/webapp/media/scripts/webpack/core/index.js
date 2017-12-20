import Backbone from 'backbone';
import Events from './events';
import Metrics from './metrics';
import Sync from './sync';
import CSRFToken from './csrf';
import UserInfo from './user_info';
import ajaxSendInit from '../funcs/ajaxSendInit';

ajaxSendInit();

class Application {
	constructor () {
		this.CSRFToken = CSRFToken;
		this.UserInfo = UserInfo;
		this.Sync = Sync;
		this.Events = Events;
	}
	init ({ name, features, data }, Router) {
		if (Backbone.History.started) {
			console.warn('Application has already been initialized.'); // eslint-disable-line no-console
			return;
		}

		this.name = name;
		this.Metrics = new Metrics(this);
		if (features) {
			this.Features = features;
			Object.freeze(this.Features);
		}
		if (data) {
			this.Data = data;
			Object.freeze(this.Data);
		}

		new Router(); // eslint-disable-line no-new

		this.Events.trigger('application:initialize');
		this.Metrics.monitor('application:initialize');
		if (window.performance && window.performance.timing) {
			const { navigationStart } = window.performance.timing;
			this.Metrics.monitor('application:initializeTiming', Date.now() - navigationStart);
		}
	}
}

export default new Application();
