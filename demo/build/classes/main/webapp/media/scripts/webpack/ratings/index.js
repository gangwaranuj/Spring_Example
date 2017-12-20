'use strict';

import Application from '../core';
import $ from 'jquery';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import ajaxSendInit from '../funcs/ajaxSendInit';
import 'datatables.net';
import '../dependencies/jquery.tmpl';
import '../dependencies/jquery.rating';

ajaxSendInit();
var meta, datatableObj, datatableOpts,
	cellRenderer = (template) => {
		return  (data, type, val, metaData) => {
			return $(template).tmpl({
				data,
				meta: meta[metaData.row]
			}).html();
		};
	},
	delegateSubmitReviewButton = function ($el) {
		$el.on('click', '[data-behavior="finish-review"]', function () {
			var $info = $el.find('#assignment_info');
			var postData = {
				'ratedUserId': $info.find('[name*="ratedUserId"]').val(),
				'raterUserId': $info.find('[name*="raterUserId"]').val(),
				'review': '',
				'value': 0,
				'quality': 0,
				'professionalism': 0,
				'communication': 0,
				'workId': $info.find('[name*="workId"]').val()
			};
			postData.review = $el.find('textarea').val();
			postData.value = $el.find('.rating-value:checked').val();
			postData.quality = $el.find('.rating-quality:checked').val();
			postData.professionalism = $el.find('.rating-professionalism:checked').val();
			postData.communication = $el.find('.rating-communication:checked').val();
			if (postData.value === 0 || postData.quality === 0 || postData.professionalism === 0 || postData.communication === 0) {
				return false;
			}

			$.ajax({
				url: '/assignments/rate_assignment',
				type: 'POST',
				data: postData,
				dataType: 'json',
				success: function ({ successful, messages }) {
					if (successful) {
						datatableObj.fnDestroy();
						datatableObj.dataTable(datatableOpts);
						let [message] = messages;
						wmNotify({ message });
					} else {
						wmNotify({
							type: 'danger',
							message: 'Please complete all the rating categories before submitting.'
						});
					}
				}
			});
		});
	},
	decorateReview = function (obj) {
		obj.rating({
			required: true,

			focus: function (value, link) {
				var $pullContainer = $(link).parents('.pull-left');
				var container = $pullContainer.parent();
				var tip = $('.rating-text', container);
				tip[0].data = tip[0].data || tip.html();
				tip.html(link.title || 'value: ' + value);
			},

			blur: function (value, link) {
				var $pullContainer = $(link).parents('.pull-left');
				var container = $pullContainer.parent();
				var tip = $('.rating-text', container);
				tip.html(tip[0].data || '');
			},

			callback: function (value, link) {
				//prevent multiple inputs from being checked
				var container = $(link).parents('.pull-left');
				$('input', container).each(function () {
					if (value !== $(this).val()) {
						$(this).removeAttr('checked');
					}
				});
				container.parent().parent().find('.rating-extras').show();
			}
		});
	},
	delegateEditReviewButton = function ($el) {
		$el.on('click', '[data-behavior="cancel-review"]', function () {
			var container = $(this).parents('.rating');
			$('textarea', container).val('');
			$('.rating-extras', container).hide();
			$('input', container).each(function () {
				$(this).removeAttr('checked');
			});

			container.find('.rating-cancel').trigger('click');
		});

		$el.on('click', '[data-behavior="finish-review"]', function () {
			var container = $(this).parents('.rating');
			$('.rating-extras', container).hide();
		});
	},
	datatableOpts = {
		'sPaginationType': 'full_numbers',
		'bLengthChange': false,
		'bFilter': false,
		'bStateSave': false,
		'bProcessing': true,
		'bServerSide': true,
		'sAjaxSource': '/ratings',
		'iDisplayLength': 10,
		'oLanguage': { 'sEmptyTable': 'No assignments awaiting review' },
		'aoColumnDefs': [
			{'mRender': cellRenderer('#details-cell-tmpl'), 'aTargets': [0], 'bSortable': false},
			{'mRender': cellRenderer('#rating-form-cell-tmpl'), 'aTargets': [1], 'bSortable': false, 'sClass': 'rating'},
		],
		'fnServerData': function (sSource, aoData, fnCallback) {
			$.getJSON(sSource, aoData, function (json) {
				meta = json.aMeta;
				fnCallback(json);
				_.each($('td.rating'), function (el) {
					decorateReview($('input.stars', this));
					delegateEditReviewButton($(el));
					delegateSubmitReviewButton($(el));
				});

				$('.three-level').on('click', function (){
					$(this).parent().parent().parent().parent().parent().find('.rating-extras').show();
				});

				//hide info about ratings beyond the initial view
				$('#rr_list_info').hide();
				$('#rr_list_paginate').hide();
			});
		}
	};

datatableObj = $('#rr_list').dataTable(datatableOpts);

Application.init(config, () => {});
