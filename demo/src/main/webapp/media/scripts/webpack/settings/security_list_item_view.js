import $ from 'jquery';
import Backbone from 'backbone';
import '../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	tagName: 'tr',
	template: $('#ip-item').template(),

	events: {
		'click [data-behavior=delete]': 'delete'
	},

	initialize () {
		this.model.bind('destroy', this.remove, this);
	},

	render () {
		$(this.el).html($.tmpl(this.template, this.model.toJSON()).html());
		return this;
	},

	delete () {
		// NOTE We don't use `model.destroy()` because it expects a RESTful resource
		this.model.collection.remove(this.model);
		this.remove();
	}
});
