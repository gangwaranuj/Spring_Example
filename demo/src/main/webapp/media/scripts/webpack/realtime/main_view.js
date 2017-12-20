'use strict';

import Application from '../core';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import ListView from './list_view';
import PaginationView from '../pagination/pagination_view';
import wmSelect from '../funcs/wmSelect';
import ajaxSendInit from '../funcs/ajaxSendInit';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click #toggle_all'     : 'toggleAll',
		'click #toggle_filters' : 'toggleFilters'
	},

	initialize: function () {
		ajaxSendInit();
		this.assignment_list = new ListView({ parent: this });
		this.pagination = new PaginationView({ limit: Application.Features.page_length });
		this.filter = {};
		this.expanded = false;
		this.shown = true;
		this.request_ts = null;
		this.update_interval = Application.Features.update_interval;
		this.count_interval = Application.Features.count_interval;
		this.stagger_update = Application.Features.stagger_update;
		this.reload_count = 0;

		this.listenTo(Application.Events, 'assignments:workerActions:actionSuccess', this.reload);

		// Listen for pagination events.
		var self = this;
		this.pagination.bind('pagination:next', function () {
			self.changePage();
		});
		this.pagination.bind('pagination:previous', function () {
			self.changePage();
		});

		this.$('#realtime-filters select').change(function () {
			self.reload();
		});

		this.$('#filter_slider_offers').slider({
			max: 100,
			slide: function (event, ui) {
				$(this).closest('.control-group').find('.filter_value').html(ui.value);
			},
			change: function (event, ui) {
				$('#filter_offers').val(ui.value);
				self.reload();
			}
		});
		this.$('#filter_slider_rejections').slider({
			max: 100,
			slide: function (event, ui) {
				$(this).closest('.control-group').find('.filter_value').html(ui.value);
			},
			change: function (event, ui) {
				$('#filter_rejections').val(ui.value);
				self.reload();
			}
		});
		this.$('#filter_slider_viewed').slider({
			max: 100,
			slide: function (event, ui) {
				$(this).closest('.control-group').find('.filter_value').html(ui.value);
			},
			change: function (event, ui) {
				$('#filter_viewed').val(ui.value);
				self.reload();
			}
		});

		this.updateCounts();

	},

	toggleFilters: function () {
		if (this.shown) {
			this.collapseFilters();
		} else {
			this.expandFilters();
		}
	},

	expandFilters: function () {
		this.$('#realtime-filters').show();
		this.shown = true;
	},

	collapseFilters: function () {
		this.$('#realtime-filters').hide();
		this.shown = false;
	},

	updateCounts: function () {
		var self = this;

		this.$('#realtime-counts').ajaxSubmit({
			dataType: 'json',
			success: function (response) {
				if (response.counts == null) {
					clearTimeout(self.counts_updater);
					return;
				}
				// Update ticker counts.
				$('#total_count').html(response.counts.open_assignments);
				$('#today_sent').html(response.counts.today_sent_assignments);
				$('#today_created').html(response.counts.today_created_assignments);
				$('#today_voided').html(response.counts.today_voided_assignments);
				$('#today_cancelled').html(response.counts.today_cancelled_assignments);
				$('#today_accepted').html(response.counts.today_accepted_assignments);
				if (response.counts.gccBankAccounts > 0) {
					$('#gcc_bank_accounts').html(response.counts.gccBankAccounts);
				} else {
					$('#gcc_bank_accounts').remove();
				}

				setTimeout(function () {
					self.updateCounts();
				}, self.count_interval);
			},
			error: clearTimeout(this.counts_updater)
		});
	},

	render: function (maxUnansweredQuestions) {
		this.$('#filter_slider_questions').slider({
			max: maxUnansweredQuestions,
			slide: function (event, ui) {
				$(this).closest('.control-group').find('.filter_value').html(ui.value);
			},
			change: _.bind(function (event, ui) {
				$('#filter_questions').val(ui.value);
				this.reload();
			}, this)
		});

		this.assignment_list.render();
		this.pagination.render();
	},

	reload: function () {
		clearTimeout(this.update_updater);
		// load both on initial page load, then stagger once
		if (this.reload_count == 1) {
			setTimeout(_.bind(function () {this.doReload()}, this), this.stagger_update);
		} else {
			this.doReload();
		}
		this.reload_count++;
		Application.Events.trigger('realtime:main:reload');
	},

	doReload: function () {
		var data = {
			start: this.pagination.getStart(),
			limit: this.pagination.getLimit(),
			sortby: this.sort_column || '',
			sortorder: this.sort_order || ''
		};

		this.$('#realtime-filters').ajaxSubmit({
			dataType: 'json',
			data: data,
			context: this,
			success: function (response) {
				if (response.request_ts == null) {
					clearTimeout(this.update_updater);
					return;
				}
				// Make sure we're not getting stale data.
				if (response.request_ts > this.request_ts) {
					// Update last updated timestamp.
					this.request_ts = response.request_ts;
					$('#last_updated').html(response.last_updated);

					// Set view data.
					this.assignment_list.model = response;
					this.pagination.setTotal(response.results_count);

					this.updateOwners(response.owners);
					this.updateClients(response.clients);
					this.updateProjects(response.projects);

					this.render(response.max_unanswered_questions);
					this.update_count++;

					this.update_updater = setTimeout(_.bind(function () {this.reload()}, this),  this.update_interval);
				}
			},
			error: clearTimeout(this.update_updater)
		});
	},

	changePage: function () {
		this.assignment_list.update_count = 0;
		this.reload();
	},

	sort: function (column, order) {
		// Update column sort to have reverse order.
		$('#realtime_monitor th span')
			.addClass('ui-icon-triangle-2-n-s')
			.removeClass('ui-icon-triangle-1-n')
			.removeClass('ui-icon-triangle-1-s');
		if (order == 'asc') {
			$('#sort_' + column)
				.attr('href', '#/sort/' + column + '/desc')
				.find('span')
				.removeClass('ui-icon-triangle-2-n-s')
				.addClass('ui-icon-triangle-1-n');
		} else if (order == 'desc') {
			$('#sort_' + column)
				.attr('href', '#/sort/' + column + '/asc')
				.find('span')
				.removeClass('ui-icon-triangle-2-n-s')
				.addClass('ui-icon-triangle-1-s');
		}

		// Set sort info and reload data.
		this.assignment_list.update_count = 0;
		this.sort_column = column;
		this.sort_order = order;
		this.reload();
	},

	toggleAll: function () {
		if (this.expanded) {
			this.assignment_list.collapseAll();
			this.expanded = false;
		} else {
			this.assignment_list.expandAll();
			this.expanded = true;
		}
	},

	updateOwners: function (data) {
		wmSelect({ selector: '#internal_owners' }, {
			options: _.map(data || [], function (value) {
				return {
					value: value.userNumber,
					text: value.firstName + ' ' + value.lastName
				}
			}),
			items: _.map($('#internal_owners').find('option:selected'), function (option) {
				var $option = $(option);
				return {
					value: $option.val(),
					text: $option.text()
				}
			}),
			onChange: _.bind(this.reload, this)
		});
	},

	updateClients: function (data) {
		wmSelect({ selector: '#clients' }, {
			valueField: 'id',
			labelField: 'name',
			options: data || [],
			items: _.map($('#clients').find('option:selected'), function (option) {
				var $option = $(option);
				return {
					value: $option.val(),
					text: $option.text()
				}
			}),
			onChange: _.bind(this.reload, this)
		});
	},

	updateProjects: function (data) {
		wmSelect({ selector: '#projects' }, {
			valueField: 'id',
			labelField: 'name',
			options: data || [],
			items: _.map($('#projects').find('option:selected'), function (option) {
				var $option = $(option);
				return {
					value: $option.val(),
					text: $option.text()
				}
			}),
			onChange: _.bind(this.reload, this)
		});
	}
});
