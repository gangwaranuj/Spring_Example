'use strict';

import $ from 'jquery';
import _ from 'underscore';
import wmModal from '../funcs/wmModal';
import 'datatables.net';
import '../dependencies/jquery.calendrical';

export default function (assessmentId) {
	var
		meta,
		datatableObj,
		selectedAssets = [],
		fileSizes = {};

	const cellRenderer = (template) => {
		return  (data, type, val, metaData) => {
			return $(template).tmpl({
				data,
				meta: meta[metaData.row]
			}).html();
		};
	};

	$('.datepicker').datepicker();

	datatableObj = $('#assets_list').dataTable({
		'sPaginationType': 'full_numbers',
		'bLengthChange': false,
		'bFilter': false,
		'iDisplayLength': 25,
		'aoColumnDefs': [
			{'bSortable': false, 'mRender': cellRenderer('#select-cell-tmpl'), 'aTargets': [0]},
			{'bSortable': false, 'mRender': cellRenderer('#thumbnail-cell-tmpl'), 'aTargets': [1]},
			{'bSortable': false, 'mRender': cellRenderer('#meta-cell-tmpl'), 'aTargets': [2]},
			{'mRender': cellRenderer('#assignment-cell-tmpl'), 'aTargets': [3]},
			{'mRender': cellRenderer('#resource-cell-tmpl'), 'aTargets': [4]},
			{'mRender': cellRenderer('#uploadedon-cell-tmpl'), 'aTargets': [5]},
			{'bSortable': false, 'mRender': cellRenderer('#caption-cell-tmpl'), 'aTargets': [6]}
		],
		'bProcessing': true,
		'bServerSide': true,
		'sAjaxSource': '/lms/manage/assets/' + assessmentId + '.json',
		'fnServerData': function (sSource, aoData, fnCallback) {
			$.each($('#filter_options').serializeArray(), function (i, item) {
				aoData.push(item);
			});

			$.getJSON(sSource, aoData, function (json) {
				meta = json.aMeta;
				fileSizes = $.extend(fileSizes, json.responseMeta.fileSizes);

				fnCallback(json);

				$('#select_all').prop('checked', false);
				for (var i = 0, size = selectedAssets.length; i < size; i++) {
					$('#select_'+selectedAssets[i]).prop('checked', true);
				}

				$('#download_all').html('Download All ('+json.iTotalRecords+' item'+((json.iTotalRecords !== 1) ? 's' : '')+', '+bytesToSize(json.responseMeta.totalBytes, 1)+')');
			});
		}
	});

	$('#filters_apply').on('click', function (e) {
		e.preventDefault();
		datatableObj.fnDraw();
	});

	$('#filters_clear').on('click', function (e) {
		e.preventDefault();
		$('#filter_options').trigger('reset');
		datatableObj.fnDraw();
	});

	$('#select_all').on('click', selectAll);
	$('#assets_list tbody').delegate('input[type="checkbox"]:not(:disabled)', 'click', selectAsset);

	$('#assets_list tbody').delegate('.view_asset', 'click', function (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Photo Details',
						destroyOnClose: true,
						content: response
					});
				}
			}
		});

	});

	$('#download_all').on('click', function (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.target.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Download All',
						destroyOnClose: true,
						content: response
					});
				}
			}
		});
	});

	$('#download_selected').on('click', function (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.target.href,
			data: {uuids: selectedAssets},
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Download Selected',
						destroyOnClose: true,
						content: response
					});
				}
			}
		});
	});

	function selectAll() {
		var selected = $(this).prop('checked');
		$('#assets_list tbody input[type="checkbox"]:not(:disabled)').each(function (i, el) {
			$(el).prop('checked', selected);
			if (selected) {
				if ($.inArray($(el).val(), selectedAssets) === -1) {
					selectedAssets.push($(el).val());
				}
			} else {
				var removeValue = $(el).val();
				selectedAssets = $.grep(selectedAssets, function (value) {
					return value !== removeValue;
				});
			}
		});
		updateSelected();
	}

	function selectAsset() {
		var selected = $(this).prop('checked');
		if (selected) {
			if ($.inArray($(this).val(), selectedAssets) === -1) {
				selectedAssets.push($(this).val());
			}
		} else {
			var removeValue = $(this).val();
			selectedAssets = $.grep(selectedAssets, function (value) {
				return value !== removeValue;
			});
		}
		updateSelected();
	}

	function updateSelected() {
		var totalBytes = 0;
		for (var i = 0, size = selectedAssets.length; i < size; i++) {
			if (fileSizes[selectedAssets[i]]) {
				totalBytes += parseInt(fileSizes[selectedAssets[i]], 10);
			}
		}
		$('#download_selected').html('Download Selected ('+selectedAssets.length+' item'+((selectedAssets.length !== 1) ? 's' : '')+', '+bytesToSize(totalBytes, 1)+')');
	}

	function bytesToSize(bytes, precision) {
		var
			kilobyte = 1024,
			megabyte = kilobyte * 1024,
			gigabyte = megabyte * 1024,
			terabyte = gigabyte * 1024;

		if ((bytes >= 0) && (bytes < kilobyte)) {
			return bytes + ' B';
		} else if ((bytes >= kilobyte) && (bytes < megabyte)) {
			return (bytes / kilobyte).toFixed(precision) + ' KB';
		} else if ((bytes >= megabyte) && (bytes < gigabyte)) {
			return (bytes / megabyte).toFixed(precision) + ' MB';
		} else if ((bytes >= gigabyte) && (bytes < terabyte)) {
			return (bytes / gigabyte).toFixed(precision) + ' GB';
		} else if (bytes >= terabyte) {
			return (bytes / terabyte).toFixed(precision) + ' TB';
		} else {
			return bytes + ' B';
		}
	}

};
