'use strict';

import $ from 'jquery';
import 'datatables.net';
import 'jquery-ui';
import newTag from './templates/new-tag.hbs';
import templateSearchPostTitle from './templates/title.hbs';
import templateSearchPostTitleReply from './templates/title-reply.hbs';
import templateSearchCategory from './templates/category.hbs';

export default () => {

	let searchMeta,
		$searchSelector = $('#searchTable'),
		searchTable = $searchSelector.dataTable({
			'oLanguage': {
				'sEmptyTable': 'No posts matched your search criteria.'
			},
			'bLengthChange': false,
			'bFilter': false,
			'bRetrieve': true,
			'sPaginationType': 'full_numbers',
			'sAjaxSource': '/forums/search',
			'bServerSide': true,
			'iDisplayLength': 10,
			'fnServerData': function (sSource, aoData, fnCallback, oSettings) {
				aoData = aoData.concat($('#forumsSearchForm').serializeArray());
				oSettings.jqXHR = $.ajax( {
					'dataType': 'json',
					'type': 'POST',
					'url': '/forums/search',
					'data': aoData,
					'success': function(response) {
						searchMeta = response.data.json.aMeta;
						fnCallback(response.data.json);
					}
				} );
			},
			'fnRowCallback': function (nRow, aData, iDisplayIndex) {
				nRow.className = 'forums-padded-row';
				return nRow;
			},
			'aoColumnDefs': [
				{
					'mRender': renderSearchPostTitle,
					'bSortable': true,
					'aTargets': [0]
				},
				{
					'mRender': renderSearchCategory,
					'bSortable': true,
					'aTargets': [1]
				},
				{
					'mRender': renderSearchTags,
					'bSortable': false,
					'aTargets': [2]
				},
				{
					'bSortable': false,
					'aTargets': [3,5]
				}
			]
		});

	function renderSearchCategory(data, type, val, metaData) {
		return templateSearchCategory({
			categoryId: searchMeta[metaData.row].categoryId,
			categoryName: searchMeta[metaData.row].categoryName
		});
	}

	function renderSearchPostTitle(data, type, val, metaData) {
		if (searchMeta[metaData.row].isReply){
			return templateSearchPostTitleReply({
				postId: searchMeta[metaData.row].postId,
				title: $.escapeHTML(searchMeta[metaData.row].title),
				rootId:  searchMeta[metaData.row].rootId
			});
		}
		else {
			return templateSearchPostTitle({
				postId: searchMeta[metaData.row].postId,
				title: $.escapeHTML(searchMeta[metaData.row].title)
			});
		}
	}

	function renderSearchTags(data, type, val, metaData) {
		let tags = searchMeta[metaData.row].tags;
		let tagsCell = '';
		if (tags !== null) {
			for (var i = 0 ; i < tags.length; i++){
				tagsCell += newTag({ tag: tags[i] });
			}
		}
		return tagsCell;
	}

	$('#forumHomeLink').on('click', function () {
		window.location = '/forums';
	});

	$('#from').datepicker({dateFormat:'mm/dd/yy', onSelect:function () {
		$('#from').removeClass('placeholder');
	}});

	$('#to').datepicker({dateFormat:'mm/dd/yy', onSelect:function () {
		$('#to').removeClass('placeholder');
	}});

	$('#forumsSearchClear').on('click', function () {
		$('#forumsSearchForm').trigger('reset');
		$('.tag-form[value="All"]').attr('selected', true);
		$('#keywords').val('');
	});

	$searchSelector.on('click', '.forums-tag', function (e) {
		let clickedTag = $(e.target).text().trim();
		$('#keywords').val('');
		$('.tag-form[value="' + clickedTag + '"]').prop('selected',true);
		$('#forumSearchMainBtn').trigger('click');
	});

	$('#forumSearchMainBtn').click(function (e) {
		e.preventDefault();
		searchTable.fnDraw();
	});
};
