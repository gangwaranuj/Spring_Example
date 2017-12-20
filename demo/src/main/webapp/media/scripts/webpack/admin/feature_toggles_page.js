'use strict';

import $ from 'jquery';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import ajaxSendInit from '../funcs/ajaxSendInit';
import 'jquery-form/jquery.form';

export default () => {
	ajaxSendInit();
	let names = ['features', 'add_feature', 'add_segment', 'add_segment_reference', 'update_feature', 'remove_feature', 'remove_segment', 'remove_reference'],
		templates = {},
		features,
		setupDropdowns,
		setupForms,
		getAllFeatures;

	_.templateSettings = {
		evaluate: /\{\{(.+?)\}\}/g,
		interpolate: /\{\{\=(.+?)\}\}/g,
		escape: /\{\{\-(.+?)\}\}/g
	};

	setupDropdowns = function () {
		var segmentDropdowns = ['add_segment_reference', 'remove_segment', 'remove_reference'];
		_.each(segmentDropdowns, function (segmentDropdown) {
			$('#' + segmentDropdown + '_feature_name').on('change', function (choice) {
				var featureName = $('#' + segmentDropdown + '_feature_name').val();
				$('#' + segmentDropdown + '_segment_name').empty();
				$('#' + segmentDropdown + '_segment_name').append($('<option></option>').val('Select Segment').html('Select Segment'));
				_.each(Object.keys(features[featureName].segments), function (segmentName) {
					$('#' + segmentDropdown + '_segment_name').append($('<option></option>').val(segmentName).html(segmentName));
				});
			});
		});

		$('#remove_reference_segment_name').on('change', function () {
			var featureName = $('#remove_reference_feature_name').val(),
				segmentName = $('#remove_reference_segment_name').val();
			$('#remove_reference_reference_value').empty();
			$('#remove_reference_reference_value').append($('<option></option>').val('Select Reference').html('Select Reference'));
			_.each(features[featureName].segments[segmentName], function(referenceValue) {
				$('#remove_reference_reference_value').append($('<option></option>').val(referenceValue).html(referenceValue));
			});
		});
	};

	// .form_button class on button in form
	// data-action attribute on button in form
	// .ajax_form class on form tag
	setupForms = function () {
		$('.form_button').on('click', function (c) {
			$.ajax({
				url: $(c.target).data('action'),
				data: $(c.target).closest('.ajax_form').serialize(),
				type: 'POST',
				dataType: 'json',
				success: function (data) {
					if (data.successful) {
						wmNotify({'message': 'Success! NOTE: you must still refresh live toggles. Follow the wiki post.'});
						getAllFeatures();
						$('#feature_toggle_action').val('no_action').change();
					} else {
						wmNotify({
							'message': 'Failure!',
							'type': 'danger'
						});
					}
				}
			});
		});
	};

	getAllFeatures = function () {
		$.get('/admin/features/', function (res) {
			features = res.data;
			_.each(names, function (name) {
				templates[name] = _.template($('#' + name + '_template').html());
				$('#' + name).html(templates[name](res));
			});
			setupDropdowns();
			setupForms();
		})
	};

	getAllFeatures();

	$('#feature_toggle_action').on('change', function () {
		var selection = $('#feature_toggle_action').val();
		$('.toggle_div').hide();
		$('#' + selection).show();
	});
};
