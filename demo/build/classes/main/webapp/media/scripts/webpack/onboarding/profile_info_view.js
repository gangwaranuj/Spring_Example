'use strict';

import Template from './templates/profile-info.hbs';
import _ from 'underscore';
import OnboardSlideView from './onboard_slide_view';
import ProfileView from './profile_view';
import PhonesView from './phones_view';
import wmSelect from '../funcs/wmSelect';
import wmFloatLabel from '../funcs/wmFloatLabel';

export default OnboardSlideView.extend({
	el: '#profileInfoView .wm-modal--content',
	template: Template,

	initialize: function () {
		this.listenTo(this.model, 'request', this.showSpinner);
		this.listenTo(this.model, 'sync error', this.hideSpinner);

		this.model.fetch({ success: _.bind(this.render, this) });
	},

	render: function () {
		this.$el.html(this.template({
			firstName: this.model.get('firstName'),
			lastName: this.model.get('lastName'),
			email: this.model.get('email')
		}));

		// Initialize our sub views
		this.avatar = new ProfileView({ model: this.model.avatar });

		this.phones = new PhonesView({ collection: this.model.phones, countryCodes: this.model.countryCodes });


		// Trigger all the floating label animations for populated fields
		wmFloatLabel({ root: this.el });
		wmSelect({ root: this.$('.contact-information') });

		this.trigger('render');

		return this;
	}
});
