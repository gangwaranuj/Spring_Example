'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import BulkEditProjectModel from './bulk_edit_project_model';
import wmNotify from '../funcs/wmNotify';
import wmSelect from '../funcs/wmSelect';

export default Backbone.View.extend({
	el: '#bulk_edit_projects_container',
	model: BulkEditProjectModel,
	data: {},
	events: {
		'click #do_update_project'        : 'save',
		'change select[name="client_id"]' : 'changeProjectBasedOnClient'
	},

	initialize: function (options) {
		$.getJSON('/assignments/get_clients_projects', { work_numbers: this.options.selectedWorkNumbers }, _.bind(function (json) {
			options.successful = json.successful;
			this.render(undefined, options);

			if (json.successful) {
				var $clientsSelect = this.$el.find('select[name="client_id"]');
				$clientsSelect.find('option').remove().end();
				$clientsSelect.append($('<option/>', { value: '', text: ''}));

				var clients = json.data.clients;
				_.each(clients, function (value, key) {
					$clientsSelect.append($('<option/>', { value: key, text: value}));
				});

				wmSelect({ selector: $clientsSelect });

				if (json.data.projects && json.data.projects.length > 0) {
					this.data.projects = json.data.projects;
				}

				$('#assignment_id_project').val(this.options.selectedWorkNumbers);

			} else {
				_.each(json.messages, function (theMessage) {
					wmNotify({
						message: theMessage,
						type: 'danger'
					});
				});
			}
		}, this));
	},

	save: function (event) {
		event.preventDefault();

		Backbone.emulateJSON = true;
		var clientId = this.$el.find('select[name="client_id"]').val(),
			projectId = this.$el.find('select[name="project_id"]').val();

		if (!clientId) {
			wmNotify({
				message: 'Please select client',
				type: 'danger'
			});
			return false;
		} else {
			$.ajax({
				context: this,
				url: '/assignments/bulk_update_client_project',
				type: 'POST',
				dataType: 'json',
				data: {
					workNumbers: this.options.selectedWorkNumbers,
					clientId: clientId,
					projectId: projectId === 'Select' ? null : projectId
				},
				success: function (response) {
					if (response.successful) {
						_.each(response.messages, function (theMessage) {
							wmNotify({ message: theMessage });
						});
						this.options.modal.destroy();
						Backbone.Events.trigger('getDashboardData');

					} else {
						_.each(response.messages, function (theMessage) {
							wmNotify({
								message: theMessage,
								type: 'danger'
							});
						});
					}
				}
			});
		}
	},

	changeProjectBasedOnClient: function () {
		var clientId = this.$el.find('select[name="client_id"]').val(),
			$projectsSelect = this.$el.find('select[name="project_id"]');

		if (clientId && this.data.projects && this.data.projects.length > 0) {
			$projectsSelect.find('option').remove().end();
			$projectsSelect.append($('<option/>', {value: '', text: ''}));

			_.each(this.data.projects, function (project) {
				if (project['clientCompany.id'] === parseInt(clientId, 10)) {
					$projectsSelect.append($('<option/>', {
						value: project.id,
						text: project.name
					}));
				}
			});

			if ($projectsSelect.find('option').length > 1) {
				$projectsSelect.prop('disabled', false);
			} else {
				$projectsSelect.prop('disabled', true);
			}
		} else {
			$projectsSelect.find('option').remove().end();
			$projectsSelect.append($('<option/>', {value: '', text: ''}));
			$projectsSelect.prop('disabled', true);
		}

		var options = {
			selector: $projectsSelect
		};
		wmSelect(options);
	}
});
