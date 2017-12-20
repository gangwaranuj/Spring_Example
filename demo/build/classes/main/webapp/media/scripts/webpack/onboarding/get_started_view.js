'use strict';

import Template from './templates/get-started.hbs';
import _ from 'underscore';
import OnboardSlideView from './onboard_slide_view';
import wmFloatLabel from '../funcs/wmFloatLabel';

export default OnboardSlideView.extend({
	el: '#getStartedView .wm-modal--content',
	template: Template,

	initialize: function () {
		this.listenTo(this.model, 'request', this.showSpinner);
		this.listenTo(this.model, 'sync error', this.hideSpinner);

		this.model.fetch({ success: _.bind(this.render, this) });
	},

	render: function () {
		this.$el.html(this.template({
			//check this
			isMobile: this.model.toJSON().isMobile
		}));

		// Trigger all the floating label animations for populated fields
		wmFloatLabel({ root: this.el });

		this.trigger('render');

		return this;
	}
});
