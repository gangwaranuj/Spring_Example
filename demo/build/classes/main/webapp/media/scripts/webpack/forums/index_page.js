'use strict';

import $ from 'jquery';
import Sidebar from './sidebar';
import 'datatables.net';
import templatePostTitle from './templates/followed_post_title.hbs';
import templateAvatarIcon from './templates/avatar_icon.hbs';
import templateNoAvatarIcon from './templates/no_avatar_icon.hbs';
import templateFollowedCategory from './templates/followed_category.hbs';
import templateFollowedLastPost from './templates/followed_last_post.hbs';
import templateUnFollowPostButton from './templates/unfollow_post_button.hbs';


export default (options) => {
	Sidebar();

	let meta,
	datatable_obj = $('#categoryPosts').dataTable({
			'oLanguage': {
				'sEmptyTable': 'There are no posts.'
			},
			'bLengthChange': false,
			'bFilter': false,
			'sPaginationType': 'full_numbers',
			'sAjaxSource': '/forums/get_posts',
			'bServerSide': true,
			'iDisplayLength': 6,
			'fnServerParams': function (aoData) {
				aoData.push({
					'name': 'categoryId',
					'value': options.categoryId
				});
				aoData.push({
					'name': 'following',
					'value':  false
				});
			},
			'aoColumnDefs': [
				{
					'mRender': renderPostTitle,
					'bSortable': false,
					'aTargets': [0]
				},
				{
					'mRender': renderPostComment,
					'bSortable': false,
					'aTargets': [1]
				},
				{
					'mRender': renderPostCreator,
					'bSortable': false,
					'aTargets': [2]
				}
			],
			'fnServerData': function(sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json);
				});
			},
			'fnRowCallback': function(nRow, aData, iDisplayIndex) {
				nRow.className = 'forums-padded-row';
				return nRow;
			}
		}),
		followingDatatable_obj = $('#followedPosts').dataTable({
			'oLanguage': {
				'sEmptyTable': 'You are not following any posts.'
			},
			'bLengthChange': false,
			'bFilter': false,
			'sPaginationType': 'full_numbers',
			'sAjaxSource': '/forums/get_posts',
			'bServerSide': true,
			'iDisplayLength': 4,
			'fnServerParams': function (aoData) {
				aoData.push({
					'name': 'categoryId',
					'value': options.categoryId
				});
				aoData.push({
					'name': 'following',
					'value':  true
				});
			}, 'aoColumnDefs': [
				{
					'mRender': renderPostTitle,
					'bSortable': false,
					'aTargets': [0]
				},
				{
					'mRender': renderPostCategory,
					'bSortable': false,
					'aTargets': [1]
				},
				{
					'mRender': renderPostCreatorFollowed,
					'bSortable': false,
					'aTargets': [2]
				}
			], 'fnServerData': function (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json);
				});
			}, 'fnRowCallback': function (nRow, aData, iDisplayIndex) {
				nRow.className = 'forums-padded-row';
				return nRow;
			}
		});

	$('#followedPosts').on('click', '.follow-post-btn', function (e) {
		var self = this;
		e.preventDefault();
		$.ajax({
			dataType: 'json',
			method: 'GET',
			url: self.href,
			success: function (response) {
				if (response.successful) {
					followingDatatable_obj.fnDraw();
				}
			}
		});
	});

	$('#forumHomeLink').on('click', function (e) {
		window.location = '/forums';
	});

	function renderPostTitle(data, type, val, metaData) {
		return templatePostTitle({
			postId: meta[metaData.row].postId,
			title: $.escapeHTML(meta[metaData.row].title),
			commentCount: meta[metaData.row].commentCount
		});
	}

	function renderPostComment(data, type, val, metaData) {
		const commentCount = meta[metaData.row].commentCount;
		if (commentCount === 0) {
			return 'No comments in thread.';
		} else {
			return '<a href="/forums/post/' + meta[metaData.row].postId + '#commentBox' + meta[metaData.row].lastCommentId + '">' + $.escapeHTML(meta[metaData.row].lastComment) + '</a>';
		}
	}
	function renderPostCreator(data, type, val, metaData) {
		const avatarURI = meta[metaData.row].avatarURI;
		const creatorName = "<p>" + $.escapeHTML(meta[metaData.row].name) + "</p>";
		if (avatarURI === null) {
			return templateNoAvatarIcon() + creatorName;
		} else {
			return templateAvatarIcon({avatarURI: avatarURI}) + creatorName;
		}
	}
	function renderPostCategory(data, type, val, metaData) {
		const categoryName = meta[metaData.row].categoryName;
		const commentCount = meta[metaData.row].commentCount;
		const categoryId = meta[metaData.row].categoryId;
		const followedCategory = templateFollowedCategory({
			categoryName: categoryName,
			categoryId: categoryId
		});
		if (commentCount === 0) {
			return followedCategory;
		} else {
			return followedCategory + templateFollowedLastPost({
				lastCommentDate: meta[metaData.row].lastCommentDate
			});
		}
	}

	function renderPostCreatorFollowed(data, type, val, metaData) {
		return renderPostCreator(data, type, val, metaData) + templateUnFollowPostButton({ postId: meta[metaData.row].postId});
	}
};
