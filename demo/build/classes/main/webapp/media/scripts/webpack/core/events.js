import Backbone from 'backbone';
import _ from 'underscore';

const Events = _.clone(Backbone.Events);
Events.trigger = (event, ...args) => {
	Backbone.Events.trigger.call(Events, event, ...args);
};

export default Events;
