'use strict';
import $ from 'jquery';
import _ from 'underscore';
import SideBar from './sidebar';
import ajaxSendInit from '../funcs/ajaxSendInit';
import wmModal from '../funcs/wmModal';
import 'jquery-form/jquery.form';
import templateAvatarIcon from './templates/avatar_icon.hbs';
import templateNoAvatarIcon from './templates/no_avatar_icon.hbs';
import newPost from './templates/post.hbs';
import deletedPost from './templates/deleted-post.hbs';
import templateNewTitle from './templates/edit-title.hbs';
import templateNewPost from './templates/edit-post.hbs';
import templateEditComment from './templates/edit-comment.hbs';
import replyPostForm from './templates/reply-post.hbs';
import postReply from './templates/post-reply.hbs';
import BanModal from './templates/ban_modal.hbs';

export default (options) => {
	SideBar();
	ajaxSendInit();
	var templateWMBadge = _.template($('#tmplWMBadge').html());
	var $discussionContent = $('#discussionContent'),
		$forumsPostReplyForm = $('#forumsPostReplyForm');
	$('#forumHomeLink').on('click', function () {
		window.location = '/forums';
	});
	$forumsPostReplyForm.ajaxForm({
		dataType: 'json',
		method: 'POST',
		url: '/forums/post/reply',
		success: function (response) {
			if (response.successful) {
				if (response.data.avatarURI === null) {
					response.data.avatarURI = templateNoAvatarIcon();
				} else {
					response.data.avatarURI = templateAvatarIcon(response.data);
				}
				if (options.isInternal) {
					response.data.avatarURI += templateWMBadge();
				}
				response.data.comment = _.unescape($.escapeHTMLAndnl2br(response.data.comment));
				$('#replies').append(newPost(response.data));
				$('#commentReply' + response.data.id);
				$forumsPostReplyForm.trigger('reset');
				$('#addCommentBtn').prop('disabled', true);
				$forumsPostReplyForm.find('.post-char-countdown').hide();
			}
		}
	});
	$('.btn-focus').on('click', function () {
		$('#commentField').focus();
	});
	$('#followPostBtn').on('click', function (e) {
		var self = this;
		e.preventDefault();
		$.ajax({
			dataType: 'json',
			method: 'GET',
			url: self.href,
			success: function (response) {
				if (response.successful) {
					var $followPostBtn = $('#followPostBtn');
					if (response.data.isfollower) {
						$followPostBtn.children('i').removeClass('icon-eye-close').addClass('icon-eye-open').addClass('tooltipped tooltipped-n');
						$followPostBtn.attr('aria-label', 'Unfollow this post');
					} else {
						$followPostBtn.children('i').removeClass('icon-eye-open').addClass('icon-eye-close').addClass('tooltipped tooltipped-n');
						$followPostBtn.attr('aria-label', 'Follow this post');
					}
				}
			}
		});
	});
	$discussionContent.on('click', '.admin-post-delete-btn', function (e) {
		e.preventDefault();
		var postId = $(this).data('post-id');
		$.ajax({
			dataType: 'json',
			method: 'GET',
			url: '/forums/post/toggle/' + postId,
			success: _.bind(function (response) {
				if (response.successful) {
					if (response.data.deleted && response.data.isInternal) {
						$(this).html('Reactivate');
						$('#comment' + response.data.id).html(deletedPost);
					} else {
						$(this).html('Delete Post');
						$('#comment' + response.data.id).html(response.data.comment);
					}
				}
			}, this)
		});
	});
	$discussionContent.on('click', '.admin-ban-user-btn', function (e) {
		e.preventDefault();
		var userId = $(this).data('user-id'),
			self = this;

		const banModal = wmModal({
			autorun: true,
			title: 'Reason for Ban',
			destroyOnClose: true,
			content: BanModal(),
			controls: [
				{
					text: 'Cancel',
					close: true,
					classList: ''
				},
				{
					text: 'Submit Ban',
					primary: true,
					classList: ''
				}
			]
		});

		$('.-primary').on('click', function (e) {
			e.preventDefault();
			$.ajax({
				dataType: 'json',
				type: 'POST',
				url: '/admin/forums/ban/' + userId,
				data: {
					reason: $('#ban-reason').val()
				},
				success: _.bind(function (response) {
					if (response.successful) {
						banModal.destroy();
						$(self).attr('class', 'admin-unban-user-btn');
						$(self).text('Unban User');
					}
				}, self)
			});
		});
	});
	$discussionContent.on('click', '.admin-unban-user-btn', function(e) {
		e.preventDefault();
		var userId = $(this).data('user-id');
		var sure = confirm('Are you sure you want to unban this user?');
		if (!sure) {
			return;
		}
		$.ajax({
			url: '/admin/forums/unban/' + userId,
			type: 'POST',
			dataType: 'json',
			success: _.bind(function (response) {
				if (response.successful) {
					$(this).attr('class', 'admin-ban-user-btn');
					$(this).text('Ban User');
				}
			}, this)
		});
	});
	$discussionContent.on('click', '.user-post-delete-btn',  function (e) {
		e.preventDefault();
		var postId = $(this).data('post-id');
		$.ajax({
			dataType: 'json',
			method: 'GET',
			url: '/forums/post/toggle/' + postId,
			success: _.bind(function (response) {
				if (response.successful) {
					if (response.data.deleted) {
						$('#comment' + response.data.id).html(deletedPost);
						$(this).remove();
					} else {
						$(this).html('Delete Post');
					}
				}
			}, this)
		});
	});
	$discussionContent.on('click', '.post-flag-btn', function (event) {
		var postId =  $(this).data('post-id');
		event.preventDefault();
		$.post('/forums/post/flag/' + postId)
			.done(_.bind(function (response) {
				if (response.flagged) {
					this.insertAdjacentHTML('afterend', '<span class="admin-text">Post Reported<span>');
					$(this).remove();
				}
			}, this));
	});

	$discussionContent.on('click', '.admin-unflag-post-btn', function (e) {
		e.preventDefault();
		var postId = $(this).data('post-id');
		var sure = confirm('Are you sure you want to unflag this post?');
		if (!sure) {
			return;
		}
		$.ajax({
			url: '/admin/forums/unflag/' + postId,
			type: 'POST',
			dataType: 'json',
			success: function () {
				$(this).text('Report Post');
				$(this).removeClass('admin-unflag-post-btn').addClass('post-flag-btn');
			}.bind(this)
		});
	});

	$('#keywords').on('input', function() {
		$('#forumSearchSideBtn').prop('disabled', _.isEmpty($('#keywords').val()));
	});

	$discussionContent.on('click', '.user-post-reply-btn', function (e) {
		e.preventDefault();
		var id = $(this).attr('id').replace('commentReply', '');
		var rootId = $('.forum-post').attr('id').replace('comment','');
		if (document.getElementById('forum_post_reply_' + id) == null) {
			$('#comment' + id).parent().append(replyPostForm({
				id : id,
				rootId: rootId,
				isUserBanned: options.isUserBanned,
				isInternal: options.isInternal,
				categoryId: $forumsPostReplyForm.find('#categoryId').val()
			}));
			$('#forum_post_reply_' + id).find('textarea').focus();
			if (!options.isInternal) {
				$('#forum_post_reply_' + id).find('textarea').attr('maxLength', 1500);
			}
		}
		$('#commentReply' + id).hide();
		$('#forum_post_reply_' + id).ajaxForm({
			dataType: 'json',
			method: 'POST',
			url: '/forums/post/reply',
			success: function (response) {
				if (response.successful) {
					if (response.data.avatarURI ===  null) {
						response.data.avatarURI = templateNoAvatarIcon();
					} else {
						response.data.avatarURI = templateAvatarIcon(response.data);
					}
					$('#comment'+ id).parent().after(postReply(response.data));
					$('#commentReplyBox' + id).remove();
					$('#commentReply' + id).show();
				}
			}
		}
		);
	});
	$discussionContent.on('click', '.user-post-reply-close-btn', function (e) {
		e.preventDefault();
		var id = $(this).attr('id').replace('commentClose', '');
		$('#commentReplyBox' + id).remove();
		$('#commentReply' + id).show();
	});
	$discussionContent.on('click', '.user-post-edit-btn', function () {
		var oldTitle = $('.title').text();
		var oldPost = $('.post-content').html();
		$('.title').html(templateNewTitle({
			oldTitle : $.escapeHTML(oldTitle),
			maxLength: (options.isInternal) ? '' : 1500
		}));
		$('.post-content').html(templateNewPost({
			oldPost: (br2nl(oldPost.trim())) ,
			maxLength: (options.isInternal) ? '' : 1500
		}));
		$('.user-post-edit-btn').addClass('user-post-edit-save-btn').removeClass('user-post-edit-btn').text('Save Edit');
	});
	$discussionContent.on('click', '.user-post-edit-save-btn', function (e) {
		e.preventDefault();
		var newTitle = $('#title').val();
		var newPost = $('.new-post').val();
		var postId = $(this).data('post-id');
		$.ajax({
			url: '/forums/edit/' + postId,
			type: 'POST',
			dataType: 'json',
			data: {
				newTitle: newTitle,
				newPost: newPost,
				postId: postId
			},
			success: function () {
				$('.title').html($.escapeHTML(newTitle));
				$('.post-content').html($.escapeHTMLAndnl2br(newPost));
				$('.user-post-edit-save-btn').addClass('user-post-edit-btn').removeClass('user-post-edit-save-btn').text('Edit Post');
			}
		});
	});
	//editing comments
	$discussionContent.on('click', '.user-comment-edit-btn', function () {
		var commentId = $(this).data('post-id');
		var oldComment = $('#comment' + commentId).html();
		$('#comment' + commentId).html(templateEditComment({
			id: commentId,
			oldComment: (br2nl(oldComment.trim())),
			maxLength: (options.isInternal) ? '' : 1500
		}));
		$(this).addClass('user-comment-edit-save-btn').removeClass('user-comment-edit-btn').text('Save Edit');
	});
	$discussionContent.on('click', '.user-comment-edit-save-btn', function (e) {
		e.preventDefault();
		let commentId = $(this).data('post-id');
		let newComment = $('.new-comment-' + commentId).val();
		$.ajax({
			url: '/forums/reply/edit/' + commentId,
			type: 'POST',
			dataType: 'json',
			data: {
				newComment: newComment
			},
			success: _.bind(function () {
				$('#comment'+ commentId).html($.escapeHTMLAndnl2br(newComment));
				$(this).addClass('user-comment-edit-btn').removeClass('user-comment-edit-save-btn').text('Edit Post');
			}, this)
		});
	});
	$(document).on('input propertychange', '.input-block-level' , function () {
		var replyButton = $(this).parent().parent().find('button');
		$forumsPostReplyForm.find('.post-char-countdown').show();
		var countdown = $(this).parent().parent().find('.post-char-countdown');
		var textarea = $(this).parent().parent().find('textarea');
		updateCountdown(countdown, textarea);
		replyButton.prop('disabled', _.isEmpty($(this).val()));
	});
	function updateCountdown(countdown, textarea) {
		// 1500 is the max post length
		let remaining = 1500 - $(textarea).val().length;
		$(countdown).text(remaining + ' characters remaining.');
	}
	function br2nl(str) {
		return str.replace(/<br\s*\/?>/mg,'\n');
	}
};
