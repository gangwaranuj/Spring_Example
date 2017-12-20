'use strict';

import Backbone from 'backbone';
import StandardReportingView from './standard_reporting_view';
import UsageReportingView from './usage_reporting_view';

export default Backbone.View.extend({
	el: '.content',

	initialize () {
		this.standardReporting = new StandardReportingView();
		this.usageReporting = new UsageReportingView();
	},

	render () {
		return this;
	}
});
