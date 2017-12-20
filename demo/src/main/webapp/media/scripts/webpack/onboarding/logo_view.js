'use strict';

import Template from './templates/logo.hbs';
import _ from 'underscore';
import OnboardView from './onboard_view';
import wmFloatLabel from '../funcs/wmFloatLabel';

export default OnboardView.extend({
	el: '.company-logo',
	template: Template,

	render: function () {
		// If we are rendering an image which was taken off the user's computer
		// instead of one sent from the server, then prepend it with the base64
		// string to render in browser.
		this.$el.html(this.template({
			src: _.isNull(this.model.get('url')) ? 'data:image/jpeg;base64,' + this.model.get('image') : this.model.get('url'),
			isLoaded: !_.isNull(this.model.get('url')) || !_.isNull(this.model.get('image'))
		}));

		// Trigger all the floating label animations for populated fields
		wmFloatLabel({ root: this.el });

		this.trigger('render');
		return this;
	},

	loadPhoto: function (response) {
		var endOfBase = response.indexOf(','),
			image = response.substring(endOfBase + 1);

		this.imageBase = response.substring(0, endOfBase);
		this.model.set('url', null, { silent: true });
		this.model.set('image', image);
	}
});
