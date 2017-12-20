

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.Model.extend({

	initialize (options) {
		this.options = options;
	},

	/**
	 * Override the default URL for the model.
	 */
	 // TODO: Have Tim add recurrence information to this as well.
	url () {
		let base = '/assignments/fetch_dashboard_results';

		// Cleanup base by making sure it ends with /.
		base += (base.charAt(base.length - 1) === '/' ? '?1=1' : '/?1=1');

		if (this.options.status) {
			base += `&status=${this.options.status}`;
		}

		if (this.options.sub_status) {
			base += `&sub_status=${this.options.sub_status}`;
		}

		if (this.options.type) {
			base += `&type=${this.options.type}`;
		}

		if (this.options.start) {
			base += `&start=${this.options.start}`;
		}

		if (this.options.page_size) {
			base += `&pageSize=${this.options.page_size}`;
		}

		if (this.options.current_view) {
			base += `&current_view=${this.options.current_view}`;
		}

		// See if sort was set
		if (this.options.sort) {
			base += `&sort=${this.options.sort}`;
		}

		if (this.options.dir) {
			base += `&dir=${this.options.dir}`;
		}

		const filters = this.listFilters();
		if (filters !== '') {
			base += filters;
		}
		return base;
	},

	listFilters () {
		let filters = $('#list_filters').serializeArray(),
			data = '',
			workersSelect = $('#resources-dropdown')[0].selectize,
			vendorsSelect = $('#vendors-dropdown')[0].selectize,
			bundlesSelect = $('#bundles-dropdown')[0].selectize,
			projects = _.where(filters, { name: 'projects' });

		// TODO: Use this model to store these form values, rather than
		// fetching data from the DOM.
		if (projects) {
			filters = _.reject(filters, (filter) => { return filter.name === 'projects'; });
			filters.push({
				name: 'projects',
				value: _.pluck(projects, 'value').join(',')
			});
		}

		if (filters.length > 0) {
			if (this.options.current_view === 'list') {
				filters.forEach((n) => {
					if (n.name === 'assigned_resources' || n.name === 'bundles' || n.name === 'assigned_vendors') {
						return;
					}
					if (n.value !== '') {
						if (n.name === 'lanes') {
							for (let i = 0; i < n.value.length; i++) {
								data += `&${n.name}=${n.value.charAt(i)}`;
							}
						} else {
							data += `&${n.name}=${n.value}`;
						}
					}
				});

				if (typeof workersSelect !== 'undefined') {
					const workers = _.pick(workersSelect.options, workersSelect.items);
					_.each(workers, (worker) => {
						data += `&assigned_resources=${worker.id}`;
					});
				}

				if (typeof vendorsSelect !== 'undefined') {
					var vendors = _.pick(vendorsSelect.options, vendorsSelect.items);
					_.each(vendors, function (vendor) {
						data += '&assigned_vendors='+vendor.id;
					});
				}

				if (typeof bundlesSelect !== 'undefined') {
					const bundles = _.pick(bundlesSelect.options, bundlesSelect.items);
					_.each(bundles, (bundle) => {
						data += `&bundles=${bundle.id}`;
					});
				}
			} else if (this.options.current_view === 'calendar') {
				filters.forEach(function (n) {
					// Always keep schedule from and through in calendar view
					if (n.name === 'schedule_from') {
						data += `${'&' + 'schedule_from' + '='}${this.options.calendar_schedule_from}`;
					} else if (n.name === 'schedule_through') {
						data += `${'&' + 'schedule_through' + '='}${this.options.calendar_schedule_through}`;
					} else if (n.value !== '') {
						if (n.name === 'workMilestone') {
							n.value = '0';
							data += `&${n.name}=${n.value}`;
						} else if (n.name === 'workDateRange') {
							n.value = '7';
							data += `&${n.name}=${n.value}`;
						} else if (n.name === 'schedule_from') {
							n.value = this.options.calendar_schedule_from;
							data += `&${n.name}=${n.value}`;
						} else if (n.name === 'schedule_through') {
							n.value = this.options.calendar_schedule_through;
							data += `&${n.name}=${n.value}`;
						} else if (n.name === 'lanes') {
							for (let i = 0; i < n.value.length; i++) {
								data += `&${n.name}=${n.value.charAt(i)}`;
							}
						} else {
							data += `&${n.name}=${n.value}`;
						}
					}
				}, this);
			}
		}
		return data;
	}
});
