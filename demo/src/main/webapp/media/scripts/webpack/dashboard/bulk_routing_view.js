'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import Routing from '../routing/main';
import BulkInvitationModel from './bulk_routing_invitation_model';
import BulkWorkSendModel from './bulk_routing_worksend_model';
import BulkWorkPublishModel from './bulk_routing_publish_model';
import BulkWorkUnpublishModel from './bulk_routing_unpublish_model';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	el: '.wm-modal',
	events: {
		'click .bulk-routing-apply' : 'saveBulkRouting'
	},

	initialize: function () {
		$.get('/assignments/batch_send/routable_groups', (result) => {
			this.options.routableGroups = result;
			this.render();
		}, 'json');
	},

	render: function () {
		let properties = {
			routableGroups: this.options.routableGroups,
			isBulk: true
		};
		let el = '.routing-bucket';
		this.bulkRouting = new Routing.Main({ properties, el });
	},

	saveBulkRouting: function () {
		var routingResult = this.bulkRouting.getRoutingObject();
		this.model = {};

		var isDirectSend = routingResult.routing.sendType === 'direct_send';
		var hasDirectSendRecipients = routingResult.routing.resourceIds !== null ||
			routingResult.routing.groupIds !== null ||
			routingResult.routing.assignToFirstGroupIds !== null ||
			routingResult.routing.companyIds !== null;

		if (isDirectSend && hasDirectSendRecipients) {
			var assignToFirstToAcceptUserNumbers = routingResult.routing.assignToFirstTalent ? routingResult.routing.resourceIds : null;
			var needToApplyUserNumbers = routingResult.routing.assignToFirstTalent ? null : routingResult.routing.resourceIds;
			var assignToFirstToAcceptGroupIds = routingResult.routing.assignToFirstGroupIds;
			var needToApplyGroupIds = routingResult.routing.groupIds;
			var assignToFirstToAcceptCompanyNumbers = routingResult.routing.assignToFirstVendor ? routingResult.routing.companyIds : null;
			var needToApplyCompanyNumbers = routingResult.routing.assignToFirstVendor ? null : routingResult.routing.companyIds;

			this.model = new BulkInvitationModel({
				workNumbers: this.options.selectedWorkNumbers.split(','),
				assignToFirstToAcceptUserNumbers: assignToFirstToAcceptUserNumbers,
				needToApplyUserNumbers: needToApplyUserNumbers,
				assignToFirstToAcceptGroupIds: assignToFirstToAcceptGroupIds,
				needToApplyGroupIds: needToApplyGroupIds,
				assignToFirstToAcceptVendorCompanyNumbers: assignToFirstToAcceptCompanyNumbers,
				needToApplyVendorCompanyNumbers: needToApplyCompanyNumbers
			});
		}

		if (routingResult.routing.sendType === 'work_send') {
			this.model = new BulkWorkSendModel({ workNumbers: this.options.selectedWorkNumbers.split(',') });
		}

		if (routingResult.routing.bulkPublish === 'publish' || routingResult.routing.bulkPublish === 'unpublish') {
			if (routingResult.routing.bulkPublish === 'publish') {
				this.publishRequest = new BulkWorkPublishModel({ workNumbers: this.options.selectedWorkNumbers.split(',') });
			} else {
				this.publishRequest = new BulkWorkUnpublishModel({ workNumbers: this.options.selectedWorkNumbers.split(',') });
			}
			this.publishRequest.save()
			.done((result) => {
				if (result.successful) {
					Backbone.Events.trigger('getDashboardData');
					wmNotify({
						type: 'success',
						message: result.messages[0]
					});
				} else {
					wmNotify({
						type: 'danger',
						message: 'An error occurred while publishing the assignments.'
					});
				}
				this.options.modal.destroy();
				if (!_.isEmpty(this.model)) {
					this.saveInvitations();
				} else {
					this.off();
					this.remove();
				}
			})
			.fail(function (result) {
				wmNotify({
					type: 'danger',
					message: result.messages[0]
				});
				this.off();
				this.remove();
			});
		} else {
			if (!_.isEmpty(this.model)) {
				this.saveInvitations();
			} else {
				this.options.modal.destroy();
				wmNotify({
					type: 'success',
					message: 'Existing publish settings will be retained.'
				});
				this.off();
				this.remove();
			}
		}
	},

	saveInvitations: function () {
		this.model.save()
		.done((result) => {
			if (result.successful) {
				Backbone.Events.trigger('getDashboardData');
				wmNotify({
					type: 'success',
					message: result.messages[0]
				});
			} else {
				wmNotify({
					type: 'danger',
					message: 'An error occurred while routing the assignments.'
				});
			}
			this.options.modal.destroy();
			this.off();
			this.remove();
		})
		.fail(function (result) {
			wmNotify({
				type: 'danger',
				message: result.messages[0]
			});
			this.off();
			this.remove();
		});
	},
});
