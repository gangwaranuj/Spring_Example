'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmFloatLabel from '../funcs/wmFloatLabel';
import wmGetInputFile from '../funcs/wmGetFileInput';

export default Backbone.View.extend({
	events: {
		'change [data-onboarding]' : 'setData',
		'change [type="file"]'     : 'populateFileInputName'
	},
	template: _.noop,

	initialize: function () {
		this.listenTo(this.model, 'change', this.render);
		this.render();
	},

	render: function () {
		this.$el.html(this.template(this.model));

		// Trigger all the floating label animations for populated fields
		wmFloatLabel({ root: this.el });

		this.trigger('render');
		return this;
	},

	setData: function (event) {
		var element = event.target,
			name = element.name,
			value = element.value;

		// Type cast some stringified booleans
		value = value === 'true' ? true : value;
		value = value === 'false' ? false : value;

		// If set fails, returning false will prevent bubbling, so the error
		// notification won't display
		return this.model.set(name, value);
	},

	populateFileInputName: function (event) {
		this.model.set('filename', event.target.files.item(0).name);
		$.when(wmGetInputFile(event.target)).then(_.bind(this.loadPhoto, this));
	},

	loadPhoto: function () {}
});
