'use strict';

import Backbone from 'backbone';
import RecommendedTestCollection from './recommended_test_collection';
import RecommendedTestView from './recommended_tests_view';
import BrowseTestsCollection from './browse_tests_collection';
import BrowseTestsView from './browse_tests_view';
import MyTestsCollection from './my_tests_collection';
import ToggleButtonsView from './toggle_buttons_view';

export default Backbone.View.extend({
	initialize: function () {
		this.recommendedTestsList = new RecommendedTestCollection();
		new RecommendedTestView(this.recommendedTestsList);

		this.browseTestsList = new BrowseTestsCollection();
		this.browseTests = new BrowseTestsView(this.browseTestsList);
		Backbone.Events.on('browseDataLoaded', this.browseTests.render);

		this.myTests = new MyTestsCollection();
		this.toggleButtonsView = new ToggleButtonsView(this.myTests);
		Backbone.Events.on('myTestsDataLoaded', this.toggleButtonsView.render);
	}
});
