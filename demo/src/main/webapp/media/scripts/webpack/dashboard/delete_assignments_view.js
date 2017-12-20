'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import DeleteVoidModel from './bulk_delete_assignments_model';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	el: '#bulk-delete-assignments',
	events: {
		'click #delete_button' : 'save'
	},

	initialize: function (options) {
		this.$('#assignment_id_delete').val(options.selectedWorkNumbers);
	},

	save: function () {
		var model = new DeleteVoidModel();
		Backbone.emulateJSON = true;
		model.save({
			assignment_id_delete: '[' + this.options.selectedWorkNumbers + ']'
		},{
			success: _.bind(function (model) {
				var message = model.get('messages');
				if (model.get('successful') === true){
					wmNotify({ message: message });
				} else {
					wmNotify({
						message: message,
						type: 'danger'
					});
				}
				this.options.modal.destroy();
				Backbone.Events.trigger('getDashboardData');
			}, this),
			error: function () {
				wmNotify({
					message: 'There was an error deleting your assignments.',
					type: 'danger'
				});
			}
		});
	}

});
