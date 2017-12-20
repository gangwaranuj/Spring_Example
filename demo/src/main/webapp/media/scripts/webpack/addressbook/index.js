import Backbone from 'backbone';
import Application from '../core';
import Addressbook from './main';
import UserProfilePopup from '../assignments/user_profile_popup_view';

const Router = Backbone.Router.extend({
	routes: {
		'addressbook(/)': 'index'
	},

	initialize () {
		Backbone.history.start({ pushState: true });
	},

	execute (callback, args) {
		if (callback) {
			Application.Events.trigger(`application:route:${callback.name}`, args);
			Application.Events.trigger('application:route', callback.name, args);
			callback.apply(this, args);
		}
	},

	index () {
		new Addressbook.MainView(); // eslint-disable-line no-new
		new UserProfilePopup({ // eslint-disable-line no-new
			el: '#location-map'
		});
	}
});

Application.init(config.addressbook, Router);
