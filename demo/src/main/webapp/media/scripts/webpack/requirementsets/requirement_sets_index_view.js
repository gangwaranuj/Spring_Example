'use strict';

import Template from './templates/requirement-sets-index.hbs';
import Backbone from 'backbone';
import RequirementSetIndexView from './requirement_set_index_view';

export default Backbone.View.extend({
	el: '#requirement-sets',
	template: Template,

	initialize: function (options) {
		this.collection = options.collection;
		this.collection.bind('add', this.addRequirementSet, this);
		this.render();
	},

	render: function() {
		var container = document.createDocumentFragment();
		this.collection.each(function(requirementSet) {
			var requirementSetIndexView = new RequirementSetIndexView({ model: requirementSet }).el;
			container.appendChild(requirementSetIndexView);
		});

		this.$el.html(this.template());
		this.$el.find('tbody').html(container);
		return this;
	},

	addRequirementSet: function(requirementSet) {
		this.$el.find('tbody').append(new RequirementSetIndexView({ model: requirementSet }).el)
	}
});
