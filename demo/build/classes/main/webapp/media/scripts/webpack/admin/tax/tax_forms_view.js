'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import Form1099 from './tax_form_1099_view';
import Published1099Form from './tax_published_1099_form_view';

export default Backbone.View.extend({

  initialize: function () {
    // Fetch tax form reports
    var self = this;
    $.getJSON('/admin/accounting/form_1099.json', function (json) {
      if (json.successful && !_.isNull(json.data)) {
        var reports = [];
        var reportsPublished = [];

        _.each(json.data.taxForm1099Reports, function(rpt) {
          if (rpt.status.published) {
            reportsPublished.push(rpt);
          } else {
            reports.push(rpt);
          }
        });
        self.formView = new Form1099({
          taxReportSets: reports,
          canPublish: self.options.canPublish
        });
        self.formPublished = new Published1099Form({
          taxReportSets: reportsPublished
        });
      }
    });
  },

  render: function () {
    return this;
  }
});
