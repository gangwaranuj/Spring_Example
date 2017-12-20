'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import ReportTypesView from './types_view';
import SavedFiltersView from './saved_filters_view';
import RecurrenceView from './recurrence_view';
import ResultsDisplay from './results_view';
import FiltersView from './filters_view';
import BucketsView from './buckets_view';
import BucketCollection from './work_report_entity_buckets_collection';
import RecurrenceModel from './recurrence_model';
import SavedFiltersModel from './saved_filters_model';
import ReportSaveModel from './save_model';
import FiltersModel from './filters_model';
import PaginationView from '../pagination/pagination_view';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import 'jquery-form/jquery.form';

export default Backbone.Router.extend({
	routes: {
		'step1'   : 'workReportEntityBuckets',
		'step2'   : 'workReportFilters',
		'results' : 'workReportResults'
	},

	initialize: function (options) {
		this.options = options || {};
		this.options.router = this;
		this.reportTypesView = new ReportTypesView(this.options);
		this.reportTypesView.render();

		if (this.options.mode === 'results') {
			this.workReportSavedFiltersView = new SavedFiltersView(this.options);
			this.workReportRecurrenceView = new RecurrenceView(this.options);
			this.workReportDisplayView = new ResultsDisplay(this.options);
			this.workReportResults();
		} else if (this.options.mode === 'manage') {
			this.workReportEntityBucketsView = new BucketsView(this.options);
			this.workReportFilterView = new FiltersView(this.options);
			this.pagination = new PaginationView({ limit: 50 });
			this.pagination.bind('pagination:next', function () {
				this.workReportFilters();
			}, this);
			this.pagination.bind('pagination:previous', function () {
				this.workReportFilters();
			}, this);
			this.pagination.bind('pagination:limit_changed', function () {
				this.workReportFilters();
			}, this);
			this.workReportEntityBuckets();
		}
	},

	workReportEntityBuckets: function () {
		var reportingReportType = $('#work_report_types').val();
		$('#reporting-recurrence-settings').hide();
		$('#report-options').hide();
		$('#report_list').hide();
		$('#work_report_filter_main').empty();
		$('#work_report_filter_form').hide();

		var options = {
			reportingReportType: reportingReportType,
			reportKey: this.options.savedReportKey
		};

		if (reportingReportType > -1) {
			if (typeof this.workReportEntityBucketsCollections === 'undefined') {
				//is App.Reporting.WorkReportFilters.Collections collection needed ?
				this.workReportEntityBucketsCollections = new BucketCollection(options);
				this.workReportEntityBucketsCollections.fetch({
					success: _.bind(function (model) {
						this.workReportEntityBucketsView.render({ model: model });
					}, this),
					error: function () {
						console.log('error:workReportEntityBuckets:' + reportingReportType);
					}
				});
			} else {
				this.workReportEntityBucketsView.render({ model: null });
			}
		} else {
			this.workReportEntityBucketsView.render(false);
		}
	},

	workReportFilters: function () {
		var options = {
			reportingReportType: $('#work_report_types').val()
		};

		// Collections doesn't have a save object.
		var workReportFilters = new FiltersModel(options);

		var formFields = {
			frm: $('#work_report_entity_bucket_form').formSerialize(),
			start: this.pagination.getStart(),
			limit: this.pagination.getLimit(),
			name: this.options.name,
			workCustomFieldIds: _.map($('.custom-fields-multi-select :selected'), function (opt) { return $(opt).val(); })
		};

		Backbone.emulateJSON = true;
		workReportFilters.save(formFields, {
			success: _.bind(function () {
				this.workReportFilterView.render(workReportFilters);

				// Set total number of records.
				var pagination = workReportFilters.get('paginationThrift');
				if (pagination) {
					this.pagination.setTotal(pagination.total);
					this.pagination.render();
				}
				this.workReportRecurrence();
				$('#report-options').show();
			}, this),
			error: _.bind(function () {
				$('#work_report_entity_bucket_next').button('reset');
				wmNotify({
					message: 'You must select one or more report fields.',
					type: 'danger'
				});
				this.navigate('step1');
			}, this)
		});
	},

	workReportRecurrence: function () {
		var recurrence = new RecurrenceModel(this.options);
		this.workReportRecurrenceView = new RecurrenceView(this.options);

		recurrence.fetch({
			success: _.bind(function (model) {
				this.workReportRecurrenceView.render(model);
			}, this),
			error: _.bind(function (model, response) {
				if (response.errors && response.errors.length > 0) {
					console.log(response.errors)
				} else {
					this.workReportRecurrenceView.render(model);
				}
			}, this)
		});

	},

	openSaved: function(reportKey) {
		var url = window.location.href;
		var idx = url.lastIndexOf('manage');
		if (idx !== -1) {
			url = url.substring(0, idx - 1) + '/results/?report_id=' + reportKey;
			window.location.href = url;
		}
	},

	hasQueryString: function (field) {
		var url = window.location.href;
		if (url.indexOf('?' + field + '=') !== -1) {
			return true;
		} else if (url.indexOf('&' + field + '=') !== -1) {
			return true;
		}
		return false
	},

	workReportSave: function (report_name) {

		var options = {
			reportingReportType: $('#work_report_types').val()
		};

		var reportKey = this.options.savedReportKey;
		// Empty key causes a new report to be created
		if (this.hasQueryString('isCopy')) {
			reportKey = '';
		}

		var formFields = {
			name: report_name,
			key: reportKey,
			frm: $('#work_report_entity_bucket_form').formSerialize(),
			workCustomFieldIds: _.map($('.custom-fields-multi-select :selected'), function (opt) { return $(opt).val(); })
		};

		var workReportSave = new ReportSaveModel(options);

		Backbone.emulateJSON = true;
		workReportSave.save(formFields, {
			success: _.bind(function (model, response) {
				if (response.successful) {
					this.workReportRecurrenceView.updateReportKey(response.report_key);
					this.options.savedReportKey = response.report_key;
					if (this.workReportRecurrenceView.hasRecurrence()) {
						this.workReportRecurrenceView.saveRecurrence();
					}
					this.openSaved(response.report_key);
				} else {
					wmNotify({
						message: 'There was a problem saving your report.',
						$type: 'danger'
					});
				}
			}, this),
			error: function () {
				wmNotify({
					message: 'There was a problem saving your report.',
					type: 'danger'
				});
			}
		});
	},

	downloadReport: function () {
		var options = {
			reportingReportType: $('#work_report_types').val()
		};

		var workReportFilters = new FiltersModel(options);

		var formFields = {
			frm: $('#work_report_entity_bucket_form').formSerialize(),
			export_csv: 1,
			name: this.options.name,
			workCustomFieldIds: _.map($('.custom-fields-multi-select :selected'), function (opt) { return $(opt).val(); })
		};

		Backbone.emulateJSON = true;
		workReportFilters.save(formFields, {
			success: function () {
				$.ajax({
					type: 'GET',
					url: '/reports/custom/export_to_csv',
					context: this,
					success: function (response) {
						if (!_.isEmpty(response)) {
							wmModal({
								autorun: true,
								title: 'Export Custom Report to CSV',
								destroyOnClose: true,
								content: response
							});
						}
					}

				});
			},
			error: function (model, response) {
				console.log(response);
			}
		});
	},

	workReportResults: function () {
		var options = {
			reportingReportType: $('#work_report_types').val()
		};

		$('#work_report_filter_form').show();
		var savedWorkReportFilters = new SavedFiltersModel(this.options);
		savedWorkReportFilters.fetch({
			success: _.bind(function (model) {
				this.workReportSavedFiltersView.render(model);
			}, this),
			error: function (model, response) {
				console.log(response.errors);
			}
		});
		$('#report-options').show();
		var recurrence = new RecurrenceModel(this.options);
		recurrence.fetch({
			success: _.bind(function (model) {
				this.workReportRecurrenceView.render(model);
			}, this),
			error: _.bind(function (model, response) {
				if (response.errors && response.errors.length > 0) {
					console.log(response.errors)
				} else {
					this.workReportRecurrenceView.render(model);
				}
			}, this)
		});
	},

	workReportDisplayViewRender: function () {
		this.workReportDisplayView.render();
	}
});
