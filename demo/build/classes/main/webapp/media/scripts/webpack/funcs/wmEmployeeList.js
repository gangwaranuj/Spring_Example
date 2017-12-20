'use strict';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import Users from '../assignments/users_collection';
import wmSelect from '../funcs/wmSelect';
import Template from '../assignments/templates/details/employee.hbs';

export default (options, selectizeOptions) => {
	const settings = _.extend({
		companyId: 0,
		staticGroupName: '',
		populatePersistedEmployees: _.noop
	}, typeof options === 'object' ? options : {});

	const populateEmployeeList = (callback, employees) => {
		const users = Users.add(employees, { parse: true });
		// Easiest way to get a JSON representation of the model array is to
		// wrap it in a collection.
		const json = (new Backbone.Collection(users)).toJSON();
		// Add each employee into the "Company Employees" option group.
		json.forEach((model) => model.group = 'company');

		callback(json);

		settings.populatePersistedEmployees();
	};

	const fetchEmployees = (query, callback) => {
		$.getJSON(`/companies/${settings.companyId}/employees`, _.partial(populateEmployeeList, callback));
	};

	const selectizeSettings = _.extend({
		valueField: 'id',
		searchField: ['id','firstName','lastName'],
		sortField: [
			{ field: 'lastName' },
			{ field: 'firstName' }
		],
		// Enable multiple values
		maxItems: null,
		placeholder: 'Enter employee name',
		create: false,
		// Static items in the "assignment roles" section, the rest of the
		// options we'll get from the employee list fetch
		options: settings.staticList,
		preload: true,
		openOnFocus: true,
		plugins: ['remove_button'],
		// The Assignment Roles section will be first, and will contain
		// "My Company", and either "All Applied Workers" or the assigned worker.
		// The Company Employees section comes after, with a list of all active
		// company employees.
		optgroups: [
			{ id: 'static', name: settings.staticGroupName },
			{ id: 'company', name: 'Company Employees' }
		],
		// Choose the option group based on the item's "group" value
		optgroupField: 'group',
		// Choose the label for the option group from the `optgroups` name
		// property.
		optgroupLabelField: 'name',
		// Match the item's "group" field (as specified above) with the
		// optgroup's "id" field.
		optgroupValueField: 'id',
		optgroupOrder: ['static','company'],
		render: {
			item: Template,
			option: Template
		},
		// If we don't memoize the function with _.once, it will fetch the
		// employee list on every key stroke. It is designed to be a real time
		// search function.
		load: _.once(fetchEmployees)
	}, typeof selectizeOptions === 'object' ? selectizeOptions : {});

	return wmSelect(settings, selectizeSettings);
};
