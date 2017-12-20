import $ from 'jquery';
import Backbone from 'backbone';
import Template from '../search/templates/user_profile_modal_container.hbs';
import wmModal from '../funcs/wmModal';

const loadUserProfile = async() => {
	const UserProfile = await import(/* webpackChunkName: "UserProfile" */ '../profile/profile_model');
	const UserProfileModal = await import(/* webpackChunkName: "UserProfile" */ '../search/user_profile_modal_view');
	return {
		UserProfile: UserProfile.default,
		UserProfileModal: UserProfileModal.default
	};
};

export default Backbone.View.extend({

	events: {
		'click .open-user-profile-popup': 'openProfile'
	},

	openProfile (event) {
		event.preventDefault();
		loadUserProfile()
			.then(({
				UserProfile,
				UserProfileModal
			}) => {
				const userNumber = $(event.currentTarget).data('number');
				const type = $(event.currentTarget).data('type');
				const isDispatch = $(event.currentTarget).data('dispatch');
				const url = type === 'vendors' ? `/profile/company/${userNumber}` : `/profile/${userNumber}`;

				this.popup = wmModal({
					title: url,
					root: 'body',
					destroyOnClose: true,
					template: Template
				});

				this.popup.show();

				const profile = new UserProfile({
					userNumber,
					isVendor: type === 'vendors',
					isDispatch
				});
				new UserProfileModal({
					model: profile,
					root: '.wm-modal .wm-modal--content'
				});
			});
	}
});
