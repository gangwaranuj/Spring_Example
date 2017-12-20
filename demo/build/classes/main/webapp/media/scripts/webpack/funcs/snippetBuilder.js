'use strict';

import $ from 'jquery';
import _ from 'underscore';
import wmModal from '../funcs/wmModal';
import WorkFeedSnippetTemplate from '../templates/public/work-feed-snippet.hbs';

$.fn.snippetBuilder = function (options) {
	var form = this;
	var preview = $(options.preview);
	var modalTemplate = _.template($.trim($(options.modalTemplate).html()));
	var textarea = $(options.textarea);
	var json = {};
	var data = {};

	var getQuery = function (data) {
		var keys = ['companyId', 'limit', 'industry', 'postal', 'distance'];
		var queries = [];
		_.each(keys, function (key) {
			if (data[key]) {
				queries.push(key[0] + '=' + encodeURI(data[key]));
			}
		});

		return (queries.length > 0 ? '?' : '') + queries.join('&');
	};

	var serialize = function (form) {
		return _(form.serializeArray()).reduce(function (obj, attr) {
			obj[attr.name] = attr.value;
			return obj;
		}, {});
	};

	var whenValid = function (data, options) {
		$.get('/feed/validate_postal_code', {
			p: data.postal
		}, function (postalCodeFound) {
			if (postalCodeFound) {
				$('#feed-postal').parents('.control-group:first').removeClass('error');
				$('#postal-msg').text('');
				options.success(data);
			} else {
				$('#feed-postal').parents('.control-group:first').addClass('error');
				$('#postal-msg').text('Postal Code is invalid');
				options.error();
			}
		});
	};

	var submit = function (callback) {
		json = serialize(form);
		data = _.extend(options, json);


		whenValid(data, {
			success: function (data) {
				textarea.val(WorkFeedSnippetTemplate({
					data: json,
					query: getQuery(data)
				}));

				textarea.focus();

				if (callback) {
					callback();
				}
			},
			error: function () {
				textarea.val('');
			}
		});
	};

	textarea.on('focus', function () {
		$(this)[0].select();
	});

	form.on('submit', function (e) {
		e.preventDefault();
		submit();
	});

	preview.on('click', function (e) {
		e.preventDefault();

		submit(function() {
			let wm_feed_title, wm_feed_background_color, wm_feed_width,
				wm_feed_text_font, wm_feed_link_color, wm_feed_border, wm_feed_padding;

			wm_feed_title = data.title;
			wm_feed_background_color = data.backgroundColor;
			wm_feed_width = data.width;
			wm_feed_text_font = data.font;
			wm_feed_link_color = data.linkColor;
			wm_feed_border = data.border;
			wm_feed_padding = data.padding;

			var temp  = document.write;
			var content = '';
			document.write = function (str) {
				content += str;
			};

			var modal = $(modalTemplate({
				width: data.width
			}));

			$.getScript('/feed/s' + getQuery(data), function () {
				modal.find('#sample').html(content);
				document.write = temp;

				wmModal({
					autorun: true,
					destroyOnClose: true,
					content: modal.html()
				});
			});
		});
	});
};
