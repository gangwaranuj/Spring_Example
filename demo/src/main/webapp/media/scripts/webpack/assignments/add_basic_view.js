'use strict';

import Application from '../core';
import Backbone from 'backbone';
import _ from 'underscore';
import wmSelect from '../funcs/wmSelect';
import wmEmployeeList from '../funcs/wmEmployeeList';
import wysiwygHtmlClean from '../funcs/wysiwygHtmlClean';
import Template from './templates/creation/basic.hbs';

export default Backbone.View.extend({
	template: Template,
	events: {
		'change [name]': 'setValue'
	},

	initialize() {
		this.render();
	},

	render() {
		this.$el.html(this.template());

		this.$('[name="description"]').wysiwyg();
		wysiwygHtmlClean(`${this.el.className} [name="description"]`);
		this.$('[name="instructions"]').wysiwyg();
		wysiwygHtmlClean(`${this.el.className} [name="instructions"]`);

		const root = this.el;
		['[name="owner"]','[name="contact"]'].forEach((selector) => {
			const companyId = Application.UserInfo.companyId;
			wmEmployeeList({ root, selector, companyId }, { maxItems: 1 });
		});
		wmSelect({
			root,
			selector: '[name="industryId"]'
		}, {
			valueField: 'id',
			searchField: ['id','name'],
			sortField: 'name',
			labelField: 'name',
			preload: true,
			openOnFocus: true,
			load: _.once((query, callback) => $.getJSON('/industries-list', callback))
		});
	},

	setValue(event) {
		const { name, value } = event.target;
		this.model.set(name, value);
	},

	save(event) {
		event.preventDefault();

		let json = this.$el.serializeArray().reduce((memo, input) => {
			let { name, value } = input;
			memo[name] = value;
			return memo;
		}, {});

		this.model.save(json);
	}
});
