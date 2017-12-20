'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import BoxesManageTestView from './boxes_manage_test_view';
import '../dependencies/jquery.easypaginate';

export default Backbone.View.extend({
	el: '#browse',

	initialize: function (collection) {
		_.bindAll(this, 'loadPage', 'render');
		this.collection = collection;
		this.collection.fetchData();

		this.options.visibleItems = 4;
	},

	render: function () {
		if (this.collection.size() === 0) {
			return;
		}

		var startIndex = (this.collection.currentPage - 1) * this.collection.resultsPerPage;
		var endIndex = this.collection.size();

		_.each(_.toArray(this.collection.models).slice(startIndex, endIndex), function (model) {
			var t = new BoxesManageTestView({
				model: new BoxesManageTestView(model)
			});
			$('#browse').append(t.render().innerHTML);
		});

		if (this.collection.currentPage === 1) {
			var $browse = $('#browse');
			$browse.css('width', (260 * this.collection.totalResults));
			$browse.easyPaginate({
				count: this.collection.totalResults,
				step: this.options.visibleItems,
				controls: 'paginationBrowse',
				dataCallback: this.loadPage
			});
		}

		if (typeof this.showCarouselItemsCallback !== 'undefined') {
			this.showCarouselItemsCallback();
		}

		return this;
	},

	loadPage: function (page, showCallback) {
		if ((page * this.options.visibleItems) > (this.collection.currentPage * this.collection.resultsPerPage)) {
			this.collection.currentPage++;
			this.collection.fetchData();
			this.showCarouselItemsCallback = showCallback;
		} else {
			showCallback();
		}
	}
});
