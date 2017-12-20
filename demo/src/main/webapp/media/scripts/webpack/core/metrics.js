import Backbone from 'backbone';
import _ from 'underscore';
import sync from './sync';
import UserInfo from './user_info';

// Data Store //
const setEvents = (namespace, events) => localStorage.setItem(namespace, JSON.stringify(events));
const getEvents = namespace => JSON.parse(localStorage.getItem(namespace) || '[]');
const clearEvents = namespace => localStorage.removeItem(namespace);

const getMonitoringEvent = (name, events, args) => {
	let { value } = args;
	let monitoringEvent;
	value = `${name}.${value}`.replace(':', '.');

	monitoringEvent = events.find(event => event.value === value);
	if (monitoringEvent) {
		monitoringEvent.count += 1;
	} else {
		monitoringEvent = Object.assign({}, args, { value });
	}

	return monitoringEvent;
};

// Server Communication //
const MonitoringModel = Backbone.Model.extend({
	idAttribute: 'value',
	defaults: {
		type: 'meter',
		count: 1
	}
});

const MonitoringCollection = Backbone.Collection.extend({
	model: MonitoringModel,
	url: '/metrics',
	send () {
		this.reset(getEvents('monitoring'));
		sync.call(this, 'create', this);
		this.reset();
		clearEvents('monitoring');
	}
});

const metrics = new MonitoringCollection();

const send = (namespace) => {
	if (namespace === 'monitoring') {
		metrics.send();
	} else if (namespace === 'segment') {
		console.warn('Segment isn\'t hooked up yet.'); // eslint-disable-line no-console
	}
};

let sendEvents = send;
const throttleOutgoingEvents = (interval, start) => {
	// `interval` is in seconds, `start` is in minutes
	const func = () => {
		sendEvents = _.throttle(send, interval * 1000, { leading: false });
	};
	if (start) {
		setTimeout(func, start * 60000);
	} else {
		func();
	}
};

const stopOutgoingEvents = (timeout) => {
	// `timeout` is in minutes
	setTimeout(() => {
		sendEvents = () => {};
	}, timeout * 60000);
};


// Monitoring //
const trackErrors = (Metrics) => {
	const XHRSend = window.XMLHttpRequest.prototype.send;
	window.XMLHttpRequest.prototype.send = function newXHRSend (...args) {
		if (this.hasOwnProperty('_trackJs') && !this._trackJs) { // eslint-disable-line no-underscore-dangle, no-prototype-builtins
			Metrics.monitor('error');
		}
		return XHRSend.apply(this, args);
	};
};

const trackDOMContentLoaded = (Metrics) => {
	const contentHasLoaded = () => {
		if (window.performance && window.performance.timing) {
			const { domInteractive, navigationStart } = window.performance.timing;
			Metrics.monitor('contentLoadTiming', domInteractive - navigationStart);
		}
	};

	if (document.readyState === 'complete' || document.readyState === 'interactive') {
		contentHasLoaded();
	} else {
		document.addEventListener('DOMContentLoaded', contentHasLoaded);
	}
};

const trackWindowLoad = (Metrics) => {
	const windowHasLoaded = () => {
		if (window.performance && window.performance.timing) {
			const { domComplete, navigationStart } = window.performance.timing;
			Metrics.monitor('windowLoadTiming', domComplete - navigationStart);
		}
	};

	if (document.readyState === 'complete') {
		windowHasLoaded();
	} else {
		document.addEventListener('load', windowHasLoaded);
	}
};


// Exports //
class Metrics {
	constructor ({ name }) {
		this.name = name;
		this.userInfo = UserInfo;

		throttleOutgoingEvents(10);
		throttleOutgoingEvents(60, 5);
		stopOutgoingEvents(30);

		trackErrors(this);
		trackDOMContentLoaded(this);
		trackWindowLoad(this);
	}
	monitor (value, count = 1, type = 'meter') {
		const events = getEvents('monitoring');
		events.push(getMonitoringEvent(this.name, events, { value, type, count }));
		setEvents('monitoring', events);
		sendEvents('monitoring');
	}
}

export default Metrics;
