'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import VenueOptionView from './venue_option_view';

export default Backbone.View.extend({
	el: '#venues-form',

	events: {
		'click [data-action=add]' : 'add',
		'change select'           : 'reset'
	},

	initialize: function (options) {
		options = options || {};
		this.venues = options.venues;
		this.venues.bind('reset', this.render, this);
		this.render();
	},

	render: function () {
		var prompt = $('<option>').text('- Select -');
		this.$('select:first').html(prompt);
		var container = document.createDocumentFragment();
		this.venues.each(function(venue) {
			var option = new VenueOptionView({
				venue: venue
			});
			container.appendChild(option.el);
		});

		this.$('select:first').append(container);
		return this;
	},

	add: function () {
		var selectedVenue = this.$('select:first').val();
		var venue = this.venues.findWhere({name: selectedVenue});
		Backbone.Events.trigger('VenuesCart:add', { venue: venue });
	},

	reset: function () {
		this.$('[data-action=add]').prop('disabled', false);
	}
});
