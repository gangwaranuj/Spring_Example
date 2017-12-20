'use strict';

import confirmActionTemplate from '../../templates/modals/confirmAction.hbs';
import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import wmNotify from '../../funcs/wmNotify';
import wmModal from '../../funcs/wmModal';
import ajaxSendInit from '../../funcs/ajaxSendInit';
import '../../dependencies/jquery.tmpl';

export default Backbone.View.extend({
  el: '#form1099',
  itemTemplate: $('#tax_form_row_template').html(),

  events: {
    'click #generate_csv' : 'generateCSV',
    'click .delete'       : 'deleteCSV',
    'click #publish'      : 'publish'
  },

  initialize: function () {
    ajaxSendInit();
    _.bindAll(this, 'canPublish', 'addItem');

    this.render();
  },

  render: function () {
    this.$('.form_table').empty();
    _.each(this.options.taxReportSets, this.addItem);

    if (this.canPublish()) {
      this.$('#publish').removeClass('disabled');
    }

    return this;
  },

  canPublish: function () {
    return this.options.canPublish;
  },

  addItem: function (rpt) {
    var item = $.tmpl(this.itemTemplate, rpt);
    this.$('.form_table').append(item);
  },

  generateCSV: function (e) {
    e.preventDefault();

    var self = this;
    $.post('/admin/accounting/form_1099/generate_csv',
      function (response) {
        if (response.successful) {
          self.addItem(response.data);
          wmNotify({
            message: 'Your request is being processed. You will receive an email when the report is ready.'
          });
        } else {
          wmNotify({
            type: 'danger',
            message: 'An error occurred while generating the report. Please try again.'
          });
        }
      }
    );
  },

  deleteCSV: function (e) {
    e.preventDefault();

    var self = this;
    $.post('/admin/accounting/form_1099/delete/' + $(e.currentTarget).attr('data-id'),
      function (response) {
        if (response.successful) {
          self.$(e.currentTarget).closest('tr').remove();
        }
      }
    );

  },

  publish: function () {
    var self = this;
    var selectedItem = this.$(':radio:checked');
    if (!this.canPublish() || selectedItem.size() === 0) {
      wmNotify({
        type: 'danger',
        message: 'You must select one CSV to publish'
      });
      return;
    }

    this.confirmModal = wmModal({
      autorun: true,
      title: 'Confirm',
      destroyOnClose: true,
      content: confirmActionTemplate({
        message: 'Are you sure you want to publish the selected CSV?'
      })
    });

    $('.cta-confirm-yes').on('click', _.bind(function () {
      $.post('/admin/accounting/form_1099/publish/' + selectedItem.val(),
        function (response) {
          if (!_.isNull(response) && response.successful) {
            self.trigger('published', {
              id: selectedItem.val()
            });
            //move element to publish table
            $('#form1099_published .form_table').append($.tmpl($('#tax_form_published_row_template').html(), {
              taxYear: selectedItem.closest('tr').find('td:nth-child(2)').text(),
              createdOn: selectedItem.closest('tr').find('td:nth-child(3)').text(),
              id: selectedItem.val()
            }));
            //remove from arrays reports and reportsPublished
            self.$('.form_table tr').remove();
            //disable publish button
            self.$('#publish').attr('disabled', 'disabled');
          } else {
            wmNotify({
              type: 'danger',
              message: 'There was an error publishing your CSV'
            });
          }
        });
      this.confirmModal.hide();
    }, this));
  }
});
