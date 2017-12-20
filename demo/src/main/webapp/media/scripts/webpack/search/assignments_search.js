'use strict';

import $ from 'jquery';
import _ from 'underscore';
import '../dependencies/jquery.tmpl';

export default function (defaultSearch) {
	let $keywordInput = $('#keywords_input'),
		$typeInput = $('#type_input'),
		$resultsContainer = $('#search_results'),
		params = {};

	const resetParams = () => {
		params = {
			paging : 0,
			keywords : '',
			type : 'all'
		};
	};

	resetParams();

	const loadData = () => {

		$.ajax({
			context: this,
			url: '/assignments/search/retrieve',
			data: buildUrlString(),
			type: 'POST',
			dataType: 'json'
		}).then((data) => {
			redrawResults(data);
		});
	};

	const redrawResults = (data) => {

		resetParams();

		if (data.results.length) {

			// Add the search results.
			_.each(data.results, (result) => {
				console.log($('#results_item_assignments'));
				$('#results_item_assignments').tmpl(result).appendTo($resultsContainer);
			});

			// If paging is 1, then this is the first search. Add the "more results" button.
			if (data.paging.paging === 1 && data.total_rows > 10) {
				// Add the button to the bottom of the search_results container.
				$('#more_results').tmpl(data.paging).appendTo($('#show_more_results'));
				$('#show_more_btn').on('click',  () => {
					loadData();
				});
			}

			// Keep track internally.
			params.paging = data.paging.paging;

			// Push current keyword into the params array
			params.keywords = data.keywords;

		} else {
			let rendered;

			if (data.paging.paging === 1) {
				rendered = '<div class="alert">No results found.</div>';
			} else {
				rendered = '<div class="alert">No additional results</div>';
			}

			// Attach the no more results to the end of the container.
			$resultsContainer.append(rendered);

			// Make sure the show more results button is removed.
			$('#show_more_results').html('');
		}
	};

	const keywordButton = () => {
		resetAll();
		params.keywords =  $keywordInput.val();
		params.type = $typeInput.val();
		loadData();
	};

	const buildUrlString = () => {
		return 'keywords=' + params.keywords +
			'&type=' + params.type +
			'&paging=' + params.paging;
	};

	const resetAll = () => {

		resetParams();

		// Reset search container.
		$resultsContainer.html('');
		// Make sure the show more results button is removed.
		$('#show_more_results').html('');
	};

	$('#keyword_btn').on('click', () => {
		keywordButton();
	});

	$typeInput.on('change', () => {
		$keywordInput.focus();
	});

	$keywordInput.on('keyup', (event) => {
		const isInputEmpty = ($(event.target).val().length === 0);

		if (event.keyCode === 13 && !isInputEmpty) {
			keywordButton();
		}
	});

	if (!_.isEmpty(defaultSearch)) {
		keywordButton();
	}
}
