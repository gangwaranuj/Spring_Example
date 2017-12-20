'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import BoxesManageTestView from './boxes_manage_test_view';
import '../dependencies/jquery.easypaginate';

export default Backbone.View.extend({
	el: '#recommended',

	initialize: function (collection) {
		this.collection = collection;
		this.collection.fetchData(this);
	},

	render: function () {
		_.each(this.collection.models, function (model) {
			var t = new BoxesManageTestView({
				model: new BoxesManageTestView(model)
			});
			this.$el.append(t.render().innerHTML);
		}, this);

		this.$el.css('width', (260 * this.collection.size()));
		this.$el.easyPaginate({
			count: this.collection.totalResults,
			step: 4,
			controls: 'paginationRecommended'
		});

		return this;
	}
});
