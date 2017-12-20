'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import parentViewTemplate from './templates/parent-view.hbs';

export default Backbone.View.extend({
	el: '.worker-services-bucket',

	template: parentViewTemplate,

	events: {
		'click .button.worker-services--nav ' : 'goToView',
		'click .back'                         : 'goToView'
	},

	initialize(options) {
		this.render();
	},

	render() {
		this.$el.html(this.template());
	},

	goToView(event) {
		event.preventDefault();

		let slug = $(event.target).data('slug') || '';

		this.$('.worker-services')
			.removeClass('animate-in')
			.addClass('animate-out');

		Backbone.history.navigate(`workerservices/${slug}`, true);
	}
});

