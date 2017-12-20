'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import TemplatesCollection from './templates_collection';
import MappingsCollection from './mappings_collection';
import LabelsCollection from './labels_collection';
import UploaderSetupView from './uploader_setup_view';

export default Backbone.Router.extend({
	initialize: function () {
		this.templates = new TemplatesCollection();
		this.mappings = new MappingsCollection();
		this.labels = new LabelsCollection();

		$.when(
			this.templates.fetch(),
			this.mappings.fetch()
		).then(function () {
				Backbone.history.start();
				Backbone.history.navigate('new', true);
			}
		);
	},

	routes: {
		'new': 'newUpload'
	},

	newUpload: function () {
		new UploaderSetupView({
			templatesCollection: this.templates,
			mappingsCollection: this.mappings,
			labelsCollection: this.labels
		});
	}
});
