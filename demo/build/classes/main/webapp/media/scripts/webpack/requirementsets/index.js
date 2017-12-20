'use strict';

import Application from '../core';
import Backbone from 'backbone';
import _ from 'underscore';
import $ from 'jquery';
import RequirementSetModel from './model';
import RequirementSetsIndexView from './requirement_sets_index_view';
import RequirementsCollection from './requirements_collection';
import TypesCollection from './types_collection';
import RequirementListCollection from './requirementlist_collection';
import FormView from './form_view';

Backbone.emulateJSON = true;
Backbone.emulateHTTP = true;

// Compatibility override - Backbone 1.1 got rid of the automatic 'options' binding
// on Views - we need to keep that.
Backbone.View = (function (View) {
	return View.extend({
		constructor: function (options) {
			this.options = options || {};
			View.apply(this, arguments);
		}
	});
})(Backbone.View);

Backbone.Collection = (function (Collection) {
	return Collection.extend({
		fetch: function (options) {
			return Collection.prototype.fetch.call(this, _.extend(options, { reset: true }));
		}
	});
})(Backbone.Collection);

Backbone.syncWithJSON = function (method, model, options) {
	Backbone.sync.call(this, method, model, _.extend(options, { emulateJSON: false }));
};

const Router = Backbone.Router.extend({

	initialize: function (options) {
		this.isMandatoryRequirement = config.isMandatoryRequirement;

		$.when(
			this.types = new TypesCollection(),
			this.types.fetch(),
			this.listCollection = new RequirementListCollection(),
			this.listCollection.fetch({
				success: function (collection) {
					new RequirementSetsIndexView({ collection });
				},

				error: function () {
					alert('error loading Requirement Sets');
				}
			})
		).then(function () {
			Backbone.history.start();
		});
	},

	routes: {
		'new'      : 'newRequirementSet',
		'edit/:id' : 'editRequirementSet'
	},

	newRequirementSet: function () {
		var requirements = new RequirementsCollection([], {});
		var requirementSet = new RequirementSetModel({ requirements });
		new FormView({
			requirementSets: this.listCollection,
			requirementSet: requirementSet,
			requirementTypes: this.types,
			filter: 'REQUIREMENT_SET',
			isMandatoryRequirement: this.isMandatoryRequirement
		});
	},

	editRequirementSet: function (id) {
		var requirementSet = this.listCollection.findWhere({ id: parseInt(id, 10) });

		if (requirementSet) {
			var requirements = new RequirementsCollection({requirementSetId: parseInt(id, 10)});
			requirements.fetch({
				success: function (collection) {
					requirementSet.set({ requirements: collection });
					new FormView({
						requirementSets:  this.listCollection,
						requirementSet:   requirementSet,
						requirementTypes: this.types,
						filter: 'REQUIREMENT_SET'
					});
				}.bind(this),
				error: function () {
					alert('error loading Requirements');
				}
			});
		} else {
			this.navigate('#');
		}
	}
});

Application.init({ name: 'requirementsets', features: config }, Router);
