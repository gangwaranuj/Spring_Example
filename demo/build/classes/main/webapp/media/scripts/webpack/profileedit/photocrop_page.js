'use strict';

import $ from 'jquery';
import '../dependencies/jquery.imgareaselect';

export default function (options) {

	$('#x1').val(options._x1);
	$('#y1').val(options._y1);
	$('#x2').val(options._x2);
	$('#y2').val(options._y2);
	$('#w').val(options._x2 - options._x1);
	$('#h').val(options._y2 - options._y1);

	$('img#original_photo').imgAreaSelect({
		aspectRatio: '1:1',
		parent: '#profile-pic-crop',
		imageWidth: options._width,
		imageHeight: options._height,
		onSelectEnd: function (img, selection) {
			$('#x1').val(selection.x1);
			$('#y1').val(selection.y1);
			$('#x2').val(selection.x2);
			$('#y2').val(selection.y2);
			$('#w').val(selection.width);
			$('#h').val(selection.height);
		},
		x1: parseFloat(options._x1),
		y1: parseFloat(options._y1),
		x2: parseFloat(options._x2),
		y2: parseFloat(options._y2)
	});

	$('#submitCrop').on('click', 	function submitForm(event) {
		if ($('#x1').val() == '' || $('#y1').val() == '' || $('#x2').val() == '' || $('#y2').val() == '' || $('#w').val() == '' || $('#h').val() == '') {
			alert('You must make a selection first!');
			return false;
		} else {
			return $(event.target).closest('form').trigger('submit');
		}
	});
}
