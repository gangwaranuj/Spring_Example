'use strict';

import Template from '../assignments/templates/bundles/unbundleAction.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import AssignmentBundleUnbundleModel from '../bundles/unbundle_model';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';

export default Backbone.View.extend({
	tagName: 'div',
	template: Template,

	events: {
		'click .confirm-unbundle' : 'save'
	},

	initialize: function (options) {
		this.bundleEvent = options.bundleEvent;
		this.render();
	},

	render: function () {
		this.model = new AssignmentBundleUnbundleModel();
		this.modal = wmModal({
			title: 'Unbundle Assignments',
			content: this.template,
			destroyOnClose: true,
			autorun: true,
			controls: [
				{
					text: 'Cancel',
					close: true
				},
				{
					text: 'Unbundle',
					primary: true
				}
			]
		});
		$('.wm-modal .-active').find('.-primary').on('click', () => this.save());
	},

	save: function () {
		this.model.save({ assignmentId: $(this.options.e.currentTarget).data('work') }, {
			success: _.bind(function (model) {
				var message = model.get('messages');
				if(model.get('successful')){
					this.modal.destroy();
					this.bundleEvent.trigger('refreshBundle');
				} else {
					this.showFailed(message);
				}
			}, this),
			error: this.showFailed
		});
	},

	showFailed: function (errors) {
		var message = !errors || errors.length < 1 ? 'There was an error unbundling your assignment.' : errors[0];
		this.modal.destroy();
		wmNotify({
			message: message,
			type: 'danger'
		});
	}

});
