'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import CartVenueView from './cart_venue_view';
import AdmissionsCollection from './admissions_collection';

export default Backbone.View.extend({
	el: '#venues-cart',

	initialize: function (options) {
		options = options || {};
		this.venues = options.venues;

		Backbone.Events.bind('VenuesCart:add', this.add, this);
	},

	render: function () {
		var self = this;

		var container = document.createDocumentFragment();
		if (this.plan && this.plan.get('admissions')) {
			this.plan.get('admissions').each(function (admission) {
				var venue = self.venues.findWhere({ name: admission.get('venue') });
				var row = new CartVenueView({ venue: venue });
				container.appendChild(row.render().el);
			});
		}
		this.$el.html(container);

		$('#venues').children().first().prop('selected', true);

		return self;
	},

	add: function (data) {
		if (!(data && data.venue)) {
			return;
		}
		var venue = data.venue;

		if (this.plan.get('admissions')) {
			this.plan.get('admissions').add({ venue: venue.get('name') });
		} else {
			this.plan.set({
				admissions: new AdmissionsCollection([
					{venue: venue.get('name')}
				])
			});
		}

		this.render();
	}
});
