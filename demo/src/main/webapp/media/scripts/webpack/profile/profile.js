import $ from 'jquery';
import _ from 'underscore';
import 'datatables.net';
import 'jquery-form/jquery.form';
import t from '@workmarket/translation';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import wmActionMenu from '../funcs/wmActionMenu';
import wmTabs from '../funcs/wmTabs';
import wmTags from '../funcs/wmTags';
import getCSRFToken from '../funcs/getCSRFToken';
import Template from '../funcs/templates/video_embed.hbs';
import UploadMediaTemplate from './templates/upload_media_modal.hbs';
import ReviewMediaTemplate from './templates/review_media_modal.hbs';
import BlockUserTemplate from './templates/block_user_modal.hbs';
import ReportConcernTemplate from './templates/report_concern_modal.hbs';
import ScoreCardTemplate from '../funcs/templates/score-card.hbs';
import qq from '../funcs/fileUploader';
import '../dependencies/jquery.tmpl';
import '../dependencies/jquery.bootstrap-collapse';

const loadAssignmentCreationModal = async() => {
	const module = await import(/* webpackChunkName: "newAssignmentCreation" */ '../assignments/creation_modal');
	return module.default;
};
const loadVideoJs = async() => await import(/* webpackChunkName: "videojs" */ 'video.js');

export default function (userNumber, userId, allScorecard, companyScorecard, paidassignforcompany, facade, isDispatch, isOwner) {
	let $commentsTable = $('#user_comment_table'),
		$testsSelect = '#test-id-slider',
		testForm = '#collapseTests',
		$groupForm = $('#add_to_group_form'),
		$tagsForm = $('#edit_company_tags_form'),
		$slider = $('.profile--quick-actions'),
		$actionsBox = $('.action-menu'),
		$toggles = $slider.find('.switch--checkbox'),
		$profile = $('.user-profile'),
		$sidebar = $('.sidebar'),
		$commentsForm = $('#add_comment_to_user_form'),
		deleteCommentModal = wmModal({
			title: t('profile.deleteComment'),
			content: t('profile.deleteThisComment'),
			controls: [
				{
					text: t('close'),
					close: true
				},
				{
					text: t('delete'),
					primary: true,
					close: true
				}
			],
			customHandlers: [
				{
					event: 'click',
					selector: '.wm-modal--control.-primary',
					callback: (event) => {
						event.preventDefault();
						const id = event.currentTarget.getAttribute('data-comment-id');

						$.ajax({
							url: '/profile/delete_user_comment',
							type: 'POST',
							data: { id },
							dataType: 'json',
							global: true,
							success: ({ successful, messages }) => {
								const [message] = messages;
								if (successful) {
									$commentsTable.fnDraw();
									deleteCommentModal.hide();
									wmNotify({ message });
								} else {
									wmNotify({ message, type: 'danger' });
								}
							}
						});
					}
				}
			]
		}),
		addMediaModal = wmModal({
			title: t('profile.uploadPhoto'),
			content: UploadMediaTemplate()
		}),
		$addMediaModal = $('#add_media_modal'),
		reviewMediaModal = wmModal({
			title: t('profile.reviewMedia'),
			content: ReviewMediaTemplate()
		}),
		$reviewMediaModal = $('#review_media_modal'),
		$saveMediaButton = $('#save_media'),
		$downloadMediaButton = $('#download_link'),
		$removeMediaButton = $('#remove-media'),
		$noComment = $('.no_comment'),
		meta;

	const cellRenderer = function (template) {
		return (data, type, val, metaData) => {
			return $(template).tmpl({
				data,
				meta: meta[metaData.row]
			}).html();
		};
	};

	let $ratingsDatatable = $('#ratings-datatable').dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: false,
			bFilter: false,
			bAutoWidth: false,
			bStateSave: false,
			bProcessing: true,
			bServerSide: true,
			sAjaxSource: `/profile/${userNumber}/ratings.json`,
			iDisplayLength: 10,
			aoColumnDefs: [
				{ bSortable: false, aTargets: ['_all'] },
				{ mRender: cellRenderer('#cell-title-tmpl'), aTargets: [0] },
				{ mRender: cellRenderer('#cell-feedback-tmpl'), aTargets: [1] },
				{ sTitle: 'Details', aTargets: [0] },
				{ sTitle: 'Ratings', aTargets: [1] },
				{ sClass: 'text-center', aTargets: [1] }
			],
			fnServerData (sSource, aoData, fnCallback) {
				aoData.push({
					name: 'scopeToCompany',
					value: $('.active[name=scopeToCompany]').val()
				});

				$.getJSON(sSource, aoData, (json) => {
					this.data('meta', json.aMeta);
					meta = json.aMeta;
					fnCallback(json);

					this.closest('.dataTables_wrapper')
						.toggle(json.aaData.length > 0)
						.siblings()
						.toggle(json.aaData.length <= 0);
				});
			}
		}),
		currentModal;

	// Copied from app/profile/profile_model
	// Delete when this file goes to RequireJS
	function scoreCardParse (response) {
		// Compile some score card data from the controller payload
		response.scoreCard = {
			abandoned: getValues('ABANDONED_WORK'),
			cancelled: getValues('CANCELLED_WORK'),
			paidAssignments: getValues('COMPLETED_WORK'),
			deliverables: getValues('DELIVERABLE_ON_TIME_PERCENTAGE'),
			onTime: getValues('ON_TIME_PERCENTAGE'),
			satisfaction: getValues('SATISFACTION_OVER_ALL'),
			paidAssignmentsForCompany: response.paidassignforcompany
		};

		// Satisfaction is returned to us as a decimal, we need it as a percentage
		_.chain(response.scoreCard)
			.pick('satisfaction', 'onTime', 'deliverables')
			.each((value) => {
				value.all.all = Math.round(value.all.all * 100);
				value.all.net90 = Math.round(value.all.net90 * 100);
				value.company.all = Math.round(value.company.all * 100);
				value.company.net90 = Math.round(value.company.net90 * 100);
			});

		return response;

		function getValues (property) {
			return {
				all: _.pick(response.allScorecard.values[property], 'all', 'net90'),
				company: _.pick(response.companyScorecard.values[property], 'all', 'net90')
			};
		}
	}

	function redirectWithFlash (url, type, msg) {
		const form = $('<form></form>');
		form.attr({
			action: '/message/create',
			method: 'POST'
		});
		form.append($('<input>').attr({
			name: 'message[]',
			value: msg
		}));
		form.append($('<input>').attr({
			name: 'type',
			value: type
		}));
		form.append($('<input>').attr({
			name: 'url',
			value: url
		}));
		form.append($('<input>').attr({
			name: '_tk',
			value: getCSRFToken()
		}));
		$('body').append(form);
		form.submit();
	}

	const scoreCardData = {
		showrecent: true,
		isDispatch,
		values: scoreCardParse({ allScorecard, companyScorecard, paidassignforcompany }).scoreCard
	};
	$('#scorecard-holder').append(ScoreCardTemplate(scoreCardData));
	$('[name="score-card-toggle"]').on('change', ({ currentTarget }) => {
		$(currentTarget).closest('.score-card').toggleClass('-company', $(currentTarget).val() !== 'all');
	});

	$('#collapseGroup, #collapseTests').on('shown', ({ target: { id } }) => $(`#${id}`).css({ overflow: 'visible' }));
	$('#collapseGroup, #collapseTests').on('hide', ({ target: { id } }) => $(`#${id}`).css({ overflow: 'hidden' }));

	$commentsTable.dataTable({
		sPaginationType: 'full_numbers',
		bLengthChange: false,
		bFilter: false,
		bStateSave: false,
		bProcessing: true,
		bServerSide: true,
		sAjaxSource: (`/profile/get_user_comments?id=${userNumber}`),
		iDisplayLength: 10,
		aaSorting: [[0, 'desc']],
		aoColumnDefs: [
			{ bSortable: false, aTargets: [1, 2, 3] },
			{ sClass: 'actions', aTargets: [3] }
		],
		fnServerData (sSource, aoData, fnCallback) {
			$.getJSON(sSource, aoData, (json) => {
				if (json && json.aaData.length) {
					for (let i = 0, size = json.aaData.length; i < size; i++) {
						json.aaData[i][3] = `<a class="delete_user_comment" data-comment-id="${json.aMeta[i].id}"><i class="wm-icon-trash icon-large muted"></i></a>`;
					}
					$noComment.hide().siblings().show();
					fnCallback(json);
				} else {
					$noComment.show().siblings().hide();
				}
			});
		}
	});

	$('.scorecard_well').on('click', '.btn-mini', function ({ target }) {
		const val = $(this).val();
		const scorecard = $(target).parents('.scorecard_well');
		$(this).addClass('active').siblings().removeClass('active');
		scorecard.find('.ratings span').hide().filter(`[ref=${val}]`).show();
		scorecard.find('table').hide().filter(`[ref=${val}]`).show();
	}).filter('.active').trigger('click');

	$sidebar.on('click', '#test_list_toggle', ({ currentTarget }) => {
		const isVisible = $testListSmall.is(':visible');
		$('#tests_all').toggle(isVisible);
		$('#tests_limited').toggle(!isVisible);
		$(currentTarget).html(isVisible ? t('profile.showLess') : t('profile.showTests'));
	});

	wmActionMenu();
	wmSelect();
	wmTabs();

	$tagsForm.find('.wm-tags').val(facade.private_tags.join(','));
	const $tagsList = wmTags({ root: $tagsForm });

	// set up videos
	const videoIds = [];
	let hasVideos = false;

	$('.profile_video').get().forEach(({ dataset }) => {
		const { id } = dataset;

		if (id) {
			videoIds.push(id);
			hasVideos = true;
		}
	});

	if (hasVideos) {
		$.ajax({
			dataType: 'json',
			type: 'POST',
			data: { userNumber },
			url: '/profile-edit/media-download-videos',
			success: (data) => {
				loadVideoJs()
					.then((videojs) => {
						Object.keys(data).forEach((key) => {
							const $videoDiv = $(`#videoplayer_${data[key].id}`);
							$videoDiv.append(Template(data[key]));
							videojs(data[key].id.toString(), {
								width: '512',
								height: '370'
							});
						});
					});
			},
			error: (jqXHR, status, errorThrown) => {
				throw `JSONFixture could not be loaded: /profile-edit/download_profile_video.json (status: ${status}, message: ${errorThrown})`;
			}
		});
	}

	function videoSetupCallback (_id, { id, code, uuid, remoteUri }, videoType, type) {
		const $video = $(`#videoplayer_video_${_id}_wrapper`);
		$video.attr('data-id', id);
		$video.attr('data-code', code);
		$video.attr('data-uuid', uuid || remoteUri);
		$video.attr('data-type', type);
		$video.attr('data-filetype', videoType);
		$video.addClass('filled_video');
	}

	// add photo modal
	$profile.on('click', '.add-media', () => {
		$.getJSON('/profile-edit/media-next-position', { userNumber }, ({ position }) => {
			if (position >= 0 && position < 10) {
				// allow user to add media
				addMediaModal.show();
				const handleUpload = function (position, mediaType) {
					$('#position').val(position);

					new qq.FileUploader({
						element: document.getElementById('file-uploader'),
						params: {
							imageType: mediaType,
							order: position,
							userNumber
						},
						action: '/profile-edit/media-upload',
						allowedExtensions: ['m4a', 'xls', 'xlsx', 'jpeg', 'pdf', 'docm', 'mp4', 'mp3', 'txt', 'tsv', 'xlsm', 'f4a', '7z', 'f4v', 'office', 'zip', 'gz', 'rtf', 'mov', 'bmp', 'jpg', 'png', 'flv', 'm4v', 'doc', 'tar', 'docx', 'csv', 'gif', 'rtx'],
						CSRFToken: getCSRFToken(),
						sizeLimit: 150 * 1024 * 1024, // 150MB
						multiple: false,
						template: $('#qq-uploader-tmpl').html(),

						onSubmit () {
							this.params.permission = $('input[name=privacy_upload]:checked').val();
							this.params.caption = $('#add_profile_mult_photo_id').val();
						},

						onComplete (id, fileName, { successful, smallAssetUri, largeAssetUri, name, assetId, errors }) {
							if (successful) {
								$('.qq-upload-button').hide();
								const isAcceptableVideo = (fileName) => {
									const index = fileName.lastIndexOf('.');
									if (index >= 0) {
										const mimeType = fileName.substr(index + 1).toLowerCase();
										const types = ['mp4', 'm4v', 'f4v', 'mov', 'flv', 'm4a', 'f4a', 'mp3'];
										if (types.indexOf(mimeType) >= 0) {
											return { success: true, mimeType };
										}
									}
									return { success: false, mimeType: null };
								};

								let { success, mimeType } = isAcceptableVideo(fileName);
								if (success) {
									if (mimeType === 'mov') {
										mimeType = 'mp4';
									}
									$('#image_preview').append(`<video id="preview_video" class="video-js vjs-default-skin" controls preload="auto" width="530" height="300" poster="${largeAssetUri}">
										<source src="${largeAssetUri}" type="${mimeType}" />
									</video>`);
									loadVideoJs().then(videojs => videojs('preview_video'));
								} else {
									$('#image_preview').append(`<img src="${smallAssetUri}" /><p>${name}</p>`);
								}
								$('#media_id').val(assetId);
								$saveMediaButton.removeAttr('disabled');
								$('.media_details').hide();
							} else {
								const [message] = errors;
								wmNotify({ message, type: 'danger' });
							}
						}
					});
				};
				handleUpload(position, 'image');
			} else {
				wmNotify({
					message: t('profile.featurePhotoAndVideos'),
					type: 'danger'
				});
			}
		});
	});

	$('.send-assignment').on('click', (event) => {
		event.preventDefault();
		loadAssignmentCreationModal().then(CreationModal => new CreationModal());
	});

	// save media action
	$addMediaModal.on('click', '#save_media', (event) => {
		event.preventDefault();
		// save photo or non-youtube video
		$.getJSON('/profile-edit/media-save-photo', {
			asset_id: $('#media_id').val(),
			permission: $('input[name=privacy_upload]:checked').val(),
			caption: $('#add_profile_mult_photo_id').val(),
			userNumber
		}, ({ successful, redirect, messages, errors }) => {
			if (successful) {
				redirectWithFlash(redirect, 'success', messages);
			} else {
				const [message] = errors;
				wmNotify({ message, type: 'danger' });
			}
		});
	});

	// review photo
	$profile.on('click', '.gallery--image', ({ currentTarget: { dataset } }) => {
		let asset = $('.asset_id'),
			title = $('#review_media_modal').closest('.wm-modal').find('.wm-modal--title');

		const { name, bytes, id, type, code, uuid, uri } = dataset;
		reviewMediaModal.show();
		title.attr('data-filename', `(${name}`);
		title.attr('data-filesize', bytes);
		asset.attr('data-id', id);
		asset.attr('data-type', type);
		$(`input[value=${code}]:radio`).attr('checked', 'checked');
		$downloadMediaButton.attr('href', `/asset/download/${uuid}`).show();
		if (isOwner) {
			$removeMediaButton.attr({ 'data-type': type, 'data-id': id });
		} else {
			$removeMediaButton.hide();
		}
		$('#mediadetail').empty().append(`<img src="${uri}" />`);
	});

	// review video
	$profile.on('mousedown', '.filled_video', function () {
		let { uuid, type, id, code, fileType } = this.dataset;
		const assetOrder = this.firstElementChild.id.match('[0-9]+')[0];

		reviewMediaModal.show();
		$('#mediadetail').empty().append(`<div id="videoplayer_${assetOrder}"/>`);
		const asset = $('.asset_id');
		asset.attr('data-id', id);
		asset.attr('data-type', type);
		$('#file_details').hide();
		$downloadMediaButton.hide();

		if (isOwner) {
			$removeMediaButton.attr({ 'data-type': type, 'data-id': id });
		} else {
			$removeMediaButton.hide();
		}

		$(`input[value=${code}]:radio`).attr('checked', 'checked');
		const setup = {
			flashplayer: '/media/scripts/jwplayer/jwplayer.flash.swf',
			width: 'auto',
			height: 460,
			file: uuid,
			wmode: 'opaque',
			primary: 'flash'
		};
		if (fileType !== null) {
			setup.type = fileType.toLowerCase() === 'mov' ? fileType = 'mp4' : fileType;
		}
		jwplayer(`videoplayer_${assetOrder}`).setup(setup);

		if ($('#delete-image').is(':visible')) {
			$(`#videoplayer_${assetOrder}`).parent().addClass('media-player');
		}
	});

	// remove photo
	$removeMediaButton.on('click', (event) => {
		event.preventDefault();
		const { id, type } = event.currentTarget.dataset;

		$.getJSON('/profile-edit/media-remove', { id, type, userNumber }, ({ redirect, successful, messages }) => {
			const [message] = messages;
			if (successful) {
				redirectWithFlash(redirect, 'success', message);
			} else {
				wmNotify({ message, type: 'danger' });
			}
		});
	});

	// update media privacy settings
	$reviewMediaModal.on('click', '#save_privacy', (event) => {
		event.preventDefault();
		const permission = $('input[name=privacy_setting]:checked').val();
		const { id, type } = event.currentTarget.dataset;

		$.getJSON('/profile-edit/media-edit', { id, type, permission, userNumber }, ({ successful, messages }) => {
			const [message] = messages;
			if (successful) {
				$(`*[data-id=${mediaId}]`).attr('data-code', permission);
				wmNotify({ message });
			} else {
				wmNotify({ message, type: 'danger' });
			}
		});
	});


	$(document).on('click', '#add-to-network-submit', (event) => {
		event.preventDefault();
		$.ajax({
			url: '/relationships/addtolane3',
			dataType: 'json',
			data: JSON.stringify({ userNumber }),
			type: 'POST',
			contentType: 'application/json'
		})
		.done(({ message }) => {
			wmNotify({ message });
			toggleNetworkIcon(event.target.offsetParent, false);
		})
		.fail(({ message }) => wmNotify({ message, type: 'danger' }));
	});

	$(document).on('click', '#remove-from-network-submit', (event) => {
		event.preventDefault();
		$.ajax({
			url: '/relationships/removefromlane',
			dataType: 'json',
			data: JSON.stringify({ userNumber }),
			type: 'POST',
			contentType: 'application/json'
		})
			.done(({ message }) => {
				wmNotify({ message });
				toggleNetworkIcon(event.target.offsetParent, true);
			})
			.fail(({ message }) => wmNotify({ message, type: 'danger' }));
	});

	$(document).on('click', '.esignature-link', (event) => {
		$.ajax({
			context: this,
			async: false,
			url: '/v2/esignature/get_signed',
			type: 'GET',
			data: {
				userNumber: userNumber,
				templateUuid: event.target.dataset.templateuuid
			},
			dataType: 'json',
			success: function (response) {
				event.target.href = response.results[0].executedUrl;
			},
			error: function () {
				e.preventDefault();
			}
		});
	});

	function toggleNetworkIcon (element, shouldToggleAdd) {
		element.setAttribute('aria-label', shouldToggleAdd ? t('profile.addToNetwork') : t('profile.removeFromNetwork'));
		element.classList.toggle('remove-from-network', !shouldToggleAdd);
		element.classList.toggle('add-to-network', shouldToggleAdd);
		element.setAttribute('id', shouldToggleAdd ? 'add-to-network-submit' : 'remove-from-network-submit');

		const icon = element.querySelector('.wm-icon-checkmark-circle, .wm-icon-plus');
		icon.classList.toggle('wm-icon-checkmark-circle', !shouldToggleAdd);
		icon.classList.toggle('wm-icon-plus', shouldToggleAdd);
	}

	$('button[name=scopeToCompany]').on('click', function () {
		$(this).addClass('active').siblings('button').removeClass('active');
		$ratingsDatatable.fnDraw();
	});

	$commentsTable.on('click', '.delete_user_comment', (event) => {
		event.preventDefault();
		deleteCommentModal.show();
		document.querySelector('.wm-modal--control.-primary').setAttribute('data-comment-id', event.currentTarget.getAttribute('data-comment-id'));
	});

	$tagsForm.on('click', '#tag-submit', (event) => {
		event.preventDefault();

		$.ajax({
			url: '/tags/tag_user',
			type: 'POST',
			data: {
				resource_id: userId,
				'tags_list[tags]': $tagsList[0].selectize.items
			},
			dataType: 'json',
			success: ({ successful, messages }) => {
				const [message] = messages;
				if (successful) {
					$('#collapseTags').collapse('hide');
					$tagsForm.trigger('reset');
					wmNotify({ message });
				} else {
					$tagsForm.trigger('reset');
					wmNotify({ message, type: 'danger' });
				}
			}
		});
	});

	$commentsForm.ajaxForm({
		dataType: 'json',
		method: 'POST',
		success ({ successful, messages }) {
			const [message] = messages;
			if (successful) {
				$commentsTable.fnDraw();
				$('#collapseComment').collapse('hide');
				wmNotify({ message });
			} else {
				wmNotify({ message, type: 'danger' });
			}
			$commentsForm.trigger('reset');
		}
	});

	$groupForm.on('click', '#group_submit', (event) => {
		event.preventDefault();

		$.ajax({
			url: `/groups/invite_workers/${$('[name="groups-select"]').val()}`,
			type: 'POST',
			data: {
				selected_workers: [userNumber]
			},
			dataType: 'json',
			success ({ successful, messages }) {
				const [message] = messages;
				if (successful) {
					wmNotify({ message });
					$('#collapseGroup').collapse('hide');
				} else {
					wmNotify({ message, type: 'danger' });
				}
			}
		});
	});


	$(testForm).on('click', '#invite_to_test_btn', (event) => {
		event.preventDefault();

		$.ajax({
			url: '/lms/manage/send_invite_user',
			data: {
				user_number: userNumber,
				assessment_ids: $($testsSelect).val()
			},
			type: 'POST',
			dataType: 'json',
			success: ({ successful, messages }) => {
				const [message] = messages;
				if (successful) {
					$(testForm).collapse('hide');
					wmNotify({ message: t('profile.selectedTests') });
				} else {
					wmNotify({ message, type: 'danger' });
				}
			}
		});
	});

	$slider.on('click', '.mainToggle', (event) => {
		let $toggle = $(event.currentTarget),
			buttonClicked = $toggle.attr('href'),
			$openDrawer = $('.accordion-body.in'),
			$currentActiveSwitch = $slider.find('.switch--checkbox:checked');

		$toggles.prop('checked', false);
		if ($openDrawer.is(':visible') && buttonClicked !== (`#${$openDrawer.attr('id')}`)) {
			$openDrawer.collapse('toggle');
		}

		if (!($currentActiveSwitch.length && $toggle.attr('href') === `#${$currentActiveSwitch.val()}`)) {
			$toggle.find('input').prop('checked', true);
		}
	});

	function downloadProfilePhotos () {
		$.getJSON('/profile-edit/download_profile_photos', { userNumber }, ({ successful, messages }) => {
			const [message] = messages;
			if (successful) {
				wmNotify({ message });
			} else {
				wmNotify({ message, type: 'danger' });
			}
		});
	}

	function unblockResource () {
		$.ajax({
			url: '/profile-edit/unblockresource',
			type: 'GET',
			data: {
				resource_id: userId
			},
			success: ({ successful, messages }) => {
				const [message] = messages;
				if (successful) {
					location.reload();
				} else {
					wmNotify({ message, type: 'danger' });
				}
			}
		});
	}


	function blockWorker () {
		currentModal = wmModal({
			title: t('profile.blockWorker'),
			template: BlockUserTemplate,
			autorun: true,
			root: '.user-profile',
			destroyOnClose: true
		});
	}

	$profile.on('click', '.profile--block-user', () => {
		$.ajax({
			url: '/profile-edit/blockresource',
			type: 'POST',
			data: {
				resource_id: userId
			},
			success: ({ successful, messages }) => {
				const [message] = messages;
				if (successful) {
					location.reload();
				} else {
					wmNotify({ message, type: 'danger' });
				}
			}
		});
	});

	function reportWorker () {
		currentModal = wmModal({
			title: t('profile.reportConcern'),
			template: ReportConcernTemplate,
			autorun: true,
			root: '.user-profile',
			destroyOnClose: true
		});
	}

	$profile.on('click', '.profile--report-concern', () => {
		const concernMessage = $('#concern_content').val();

		if (!concernMessage) {
			wmNotify({
				message: t('profile.addMessage'),
				type: 'danger'
			});
		} else {
			$.ajax({
				url: '/quickforms/concern',
				type: 'POST',
				data: {
					id: userId,
					type: 'profile',
					content: $('#concern_content').val()
				},
				success: ({ messages }) => {
					const [message] = messages;
					wmNotify({ message });
					currentModal.destroy();
				}
			});
		}
	});

	$actionsBox.on('change', ({ currentTarget: { value } }) => {
		switch (value) {
		case 'download photos':
			downloadProfilePhotos();
			break;
		case 'unblock':
			unblockResource();
			break;
		case 'block':
			blockWorker();
			break;
		case 'report':
			reportWorker();
			break;
		}
		$actionsBox[0].selectize.clear();
	});

	$('#show-ratings').on('click', () => $('.wm-tabs [data-content="#ratings"]').click());

	$profile.on('click', '[href="#collapseTests"]', () => {
		if (!$($testsSelect).length) {
			$('.test-holder').append('<select id="test-id-slider" class="profile--select-tests"></select>');
			$.ajax({
				url: '/lms/manage/tests.json',
				type: 'GET',
				dataType: 'json',
				success: (data = []) => {
					data.sort((a, b) => a.name.localeCompare(b.name));
					wmSelect({ selector: $testsSelect }, {
						plugins: ['remove_button'],
						maxItems: null,
						placeholder: `${t('profile.selectOneOrMore')}...`,
						labelField: 'name',
						valueField: 'id',
						searchField: ['id', 'name'],
						options: data
					});
				}
			});
		}
	});
}
