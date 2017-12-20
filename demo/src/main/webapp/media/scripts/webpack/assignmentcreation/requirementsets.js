'use strict';

import RequirementSetTemplate from '../assignments/templates/creation/requirement_set.hbs';
import RequirementTemplate from '../assignments/templates/creation/requirement.hbs';
import $ from 'jquery';
import _ from 'underscore';

export default {
	init: function (requirementSetIds) {
		this.requirementSetIds = requirementSetIds || [];
		this.requirementSets = {};

		this.$section = $('#work-form-requirements');
		this.$requirementSets = this.$section.find('[data-placeholder="current-requirement-sets"]');
		this.$currentRequirements = this.$section.find('[data-placeholder="current-requirements"]');
		this.$providedRequirements = this.$section.find('[data-placeholder="provided-requirements"]');
		this.$dropdown = this.$section.find('select[data-select="requirement-set"]');
		this.$button = this.$section.find('button[data-action="add-requirement-set"]');

		this.requirementSetTemplate = $('#requirement-set-tmpl').html();
		this.requirementTemplate = $('#requirement-tmpl').html();

		$.fn.extend({
			removeItem: function (value) {
				return $.grep(this, function (elem, index) {
					return elem !== value;
				});
			}
		});

		_.each(this.$dropdown.find('[data-required="true"]'), function (el) {
			var id = parseInt($(el).val(), 10);
			this.loadRequirementSet(id, true);
			this.requirementSetIds = $(this.requirementSetIds).removeItem(id);
		}, this);

		this.loadRequirementSets();

		this.$dropdown.on('change', _.bind(function () {
			this.changeRequirementSet();
		}, this));

		this.$button.on('click', _.bind(function (e) {
			e.preventDefault();
			this.addRequirementSet();
		}, this));
	},

	decorateReq: function() {
		this.displayName = function() {
			var suffix = this.mandatory ? ' *' : '';
			if (this.requirable) {
				return this.requirable.name + suffix;
			} else {
				return this.name + suffix;
			}
		};
	},

	decorateRequirements: function(values) {
		_.each(values, function(req) {
			this.decorateReq.call(req);
		}, this);
	},

	loadRequirementSets: function () {
		_.each(this.requirementSetIds, function (id) {
			this.loadRequirementSet(id);
		}, this);
	},

	loadRequirementSet: function (id, required) {
		var self = this;
		if (self.requirementSets[id]) {
			return;
		}

		required = required || false;

		$.get('/requirement_sets/' + id + '/requirements', function (data) {
			self.requirementSets[id] = data;
			self.appendRequirementSet({
				requirementSet: {
					id: id,
					name: self.$dropdown.find('option[value="' + id + '"]').text(),
					required: required
				}
			});
			self.refreshRequirements();
		});
	},

	addRequirementSet: function () {
		var currentRequirementSetId = this.$dropdown.val();

		var invalidOption = (currentRequirementSetId === '');

		if (this.requirementSets[currentRequirementSetId] || invalidOption) {
			return;
		}

		this.requirementSets[currentRequirementSetId] = this.providedRequirementSets;

		this.appendRequirementSet({
			requirementSet: {
				id: currentRequirementSetId,
				name: this.$dropdown.find('option[value="' + currentRequirementSetId + '"]').text()
			}
		});

		this.refreshRequirements();
	},

	appendRequirementSet: function (options) {
		var $requirementSet = RequirementSetTemplate(options);
		this.$requirementSets.append($requirementSet);

		this.$requirementSets.find('[data-action="trash"]').on('click', _.bind(function (event) {
			$(event.currentTarget).parent().parent().remove();
			delete this.requirementSets[options.requirementSet.id];
			this.refreshRequirements();
		}, this));
	},

	refreshRequirements: function() {
		var self = this;
		var values = _.values(this.requirementSets);
		var flattened = _.flatten(values);
		var sorted = _.sortBy(flattened, function (req) {
			return req.$humanTypeName + '|' + req.name;
		});
		var requirements = _.uniq(sorted, false, function (req) {
			if (req.requirable) {
				return req.$humanTypeName + '|' + req.requirable.name;
			} else {
				return req.$humanTypeName + '|' + req.name;
			}
		});

		this.decorateRequirements(requirements);
		this.$currentRequirements.html(RequirementTemplate({requirements: requirements}));
	},

	changeRequirementSet: function () {
		var self = this;
		var requirementSetId = self.$dropdown.val();

		$.get('/requirement_sets/' + requirementSetId + '/requirements', function (data) {
			self.providedRequirementSets = data;

			var values       = _.values(data);
			var requirements = _.sortBy(values, function(req) {
				return req.$humanTypeName + '|' + req.name;
			});

			self.decorateRequirements(values);
			self.$providedRequirements.html(RequirementTemplate({requirements: requirements})).show();

			self.$button.prop('disabled', false);
			self.$dropdown.find('option.prompt').remove();
		});
	}
};
