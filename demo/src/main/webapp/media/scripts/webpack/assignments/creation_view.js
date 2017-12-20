'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import wmTabs from '../funcs/wmTabs';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	events: {
		'click .wm-modal--control.-primary'     : 'save',
		'click .assignment-creation--templates' : 'openTemplates'
	},

	initialize({ templatesCollection }) {
		this.templatesCollection = templatesCollection;
		this.render();
	},

	render() {
		const root = this.el;
		wmTabs({ selector: '.assignment-creation--tab', root });
		this.templates = wmSelect({ selector: '[name="assignment-creation-template"]', root }, {
			valueField: 'workNumber',
			searchField: ['workNumber','templateName'],
			sortField: 'templateName',
			labelField: 'templateName',
			preload: 'focus',
			onChange: (value) => {
				const url = `/employer/v2/assignments/templates/${value}`;
				this.model.fetch({ url });
			},
			load: _.once((query, callback) => {
				const data = {
					fields: ['workNumber','templateName'].join(',')
				};
				this.templatesCollection.fetch({
					data,
					success: (model) => callback(model.toJSON())
				});
			})
		})[0].selectize;

		// Move the datepicker into the modal
		let datePicker = document.getElementById('ui-datepicker-div');
		let slide = document.querySelector('.wm-modal--slide');
		this.el.insertBefore(datePicker, slide);
	},

	save() {
		this.model.save(null, {
			success({ id }) {
				window.location = `/assignments/details/${id}`;
			},
			error(model, { response: { results }}) {
				results.forEach((error) => {
					wmNotify({
						message: error.message,
						type: 'danger'
					});
				});
			}
		});
	},

	// TODO: Move this behavior into the style guide
	openTemplates() {
		this.$('.assignment-creation--sidebar .wm-select .selectize-input').trigger('mousedown');
	}
});
