'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import wmNotify from '../funcs/wmNotify';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	initialize: function () {
		this.$el.ajaxForm({
			dataType: 'json',
			success: function (data) {
				if (data && data.successful) {
					// Find the label which needs to be removed
					var removables = $(window.document).find('.dragRemove.' + data.data.workNumber);
					removables.each(function () {
						var idmatches = $(this).parent().attr('href').match(/([0-9]+)/);
						if ((null != idmatches) && (idmatches[1] === data.data.labelId)) {
							// If this is the only label on the row, we need to remove any extra labels UI
							if (removables.length === 1) {
								$(this).parent().parent().siblings('.status-alert').remove();
							}

							// Finally, remove the label
							$(this).parent().remove();

							return false;
						}
					});

					$('.wm-modal--close').trigger('click');
				} else {
					_.each(data.messages, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});
	}
});
