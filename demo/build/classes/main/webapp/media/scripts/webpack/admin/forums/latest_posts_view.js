'use strict';

import $ from 'jquery';
import 'jquery-ui';
import '../../config/datepicker';
import Template from '../templates/posts/posts.hbs';

export default function () {
	var template = Template;

	function getParams(pageNumber) {
		return {
			fromDate: $('#from').val(),
			toDate: $('#to').val(),
			user: $('#user-filter').val(),
			pageNumber: pageNumber
		};
	}

	function fetchPosts(pageNumber) {
		var numberOfPages = Number($('span.number-of-pages').first().text());

		if (pageNumber <= 0) {
			return;
		}

		if (pageNumber > numberOfPages) {
			return;
		}

		$.ajax({
			url: '/admin/forums/posts',
			type: 'GET',
			dataType: 'json',
			data: getParams(pageNumber),
			success: function (response) {
				$('span.page-number').text(response.pageNumber);
				$('span.number-of-pages').text(response.numberOfPages === 0 ? 1 : response.numberOfPages);
				$('#posts-table tbody').html(template({posts: response.posts}));
				$('tr').on('click', function () {
					$(this).find('.comment').toggleClass('-collapsed');
				});
			}
		});
	}

	fetchPosts(1);

	$('.next, .previous, .search').on('click', function (e) {
		e.preventDefault();

		var inc = 0;
		if (e.target.className === 'next') {
			inc = 1;
		} else if (e.target.className === 'previous') {
			inc = -1;
		}

		var currentPage = Number($('span.page-number').first().text());
		var nextPage = currentPage + inc < 0 ? 0 : currentPage + inc;

		fetchPosts(nextPage);
	});

	$('#from').datepicker({
		dateFormat: 'mm/dd/yy', onSelect: function () {
			$('#from').removeClass('placeholder');
		}
	});

	$('#to').datepicker({
		dateFormat: 'mm/dd/yy', onSelect: function () {
			$('#to').removeClass('placeholder');
		}
	});

	$('.clear-search').on('click', function () {
		$('#from, #to, #user-filter').val('');
		fetchPosts(1);
	});
}
