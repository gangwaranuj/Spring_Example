'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import memberStatusModel from './member_status_model';
import csvModel from './csv_model';
import wmNotify from '../funcs/wmNotify';
import LegacyMissingRequirementsTemplate from './templates/missing_requirements_legacy.hbs';
import MissingRequirementsTemplate from './templates/missing_requirements.hbs';

export default Backbone.View.extend({
	el: '#cart',

	events: {
		'click .approve-users'            : 'approveUsersForGroup',
		'click .uninvite-users'           : 'uninviteUsersFromGroup',
		'click .remove-decline-users'     : 'removeUsersFromGroup',
		'click .download-documents'       : 'downloadDocumentations',
		'click .export-as-csv'            : 'exportAction',
		'click .missingreqs-quick-action' : 'checkForMissingRequirementsData'
	},

	initialize: function () {
		this.memberStatus = new memberStatusModel();
		this.csv = new csvModel();
		this.searchResults = $('#search_results');
	},

	approveUsersForGroup: function () {
		if (this.hasEmptyCart()) return;

		$.ajax({
			context: this,
			url: '/groups/' + this.options.groupId + '/approve_users',
			type: 'POST',
			data: {
				'userNumbers': this.collection.pluck('userNumber')
			},
			dataType: 'json',
			success: function (response) {
				Backbone.Events.trigger('resetWorkers');
				this.showNotice(response, 'approved');
			}
		});
	},

	removeUsersFromGroup: function () {
		if (this.hasEmptyCart()) return;

		$.ajax({
			context: this,
			url: '/groups/' + this.options.groupId + '/remove_users',
			type: 'POST',
			data: {
				'userNumbers': this.collection.pluck('userNumber')
			},
			dataType: 'json',
			success: function (response) {
				this.showNotice(response, ' removed');
			}
		});
	},

	declineUserMemberships: function () {
		if (this.hasEmptyCart()) return;

		$.ajax({
			context: this,
			url: '/groups/' + this.options.groupId + '/decline_users',
			type: 'POST',
			data: {
				'userNumbers': this.collection.pluck('userNumber')
			},
			dataType: 'json',
			success: function (response) {
				this.showNotice(response, ' declined');
			}
		});
	},

	uninviteUsersFromGroup: function () {
		if (this.hasEmptyCart()) return;

		$.ajax({
			context: this,
			url: '/groups/' + this.options.groupId + '/cancel_invitations',
			type: 'POST',
			data: {
				'userNumbers': this.collection.pluck('userNumber')
			},
			dataType: 'json',
			success: function (response) {
				this.showNotice(response, 'uninvited');
			}
		});
	},

	downloadDocumentations: function () {
		if (this.hasEmptyCart()) return;

		$.ajax({
			context: this,
			url: '/groups/' + this.options.groupId + '/documentations',
			type: 'POST',
			data: {
				'userNumbers': this.collection.pluck('userNumber')
			},
			dataType: 'json',
			success: function (response) {
				if (response.successful) {
					Backbone.Events.trigger('deselectAll');
				}
				_.each(response.messages, function (message) {
					wmNotify({
						message: message,
						type: response.successful ? 'success' : 'danger'
					});
				});
			}
		});
	},

	hasEmptyCart: function () {
		if (this.collection.isEmpty()) {
			wmNotify({
				message: 'Your cart is empty, please select at least one worker.',
				type: 'danger'
			});
			return true;
		}
	},

	showNotice: function (response, action) {
		if (response.successful) {
			wmNotify({
				message: 'Your workers were ' + action + '.'
			});

			Backbone.Events.trigger('loadData');
		} else {
			wmNotify({
				message: response.message,
				type: 'danger'
			});
		}
	},

	bulkRefreshUiAfterAction: function (action) {
		var getFacetCount = this.options.attributes.getFacetCount,
			setFacetCount = this.options.attributes.setFacetCount,
			removeRows    = this.options.attributes.removeRows;

		switch (action) {
			// declined, pending passed, pending failed
			case 'approved':
				var pendingPassedCount = this.options.attributes.selectedPendingPassed.length,
					existingPendingPassedCount = getFacetCount('#pending'),
					pendingFailedCount = this.options.attributes.selectedPendingFailed.length,
					existingPendingFailedCount = getFacetCount('#pendingoverride'),
					declinedCount = this.options.attributes.selectedDeclined.length,
					existingDeclinedCount = getFacetCount('#declined');

				setFacetCount('#declined', existingDeclinedCount - declinedCount);
				setFacetCount('#pending', existingPendingPassedCount - pendingPassedCount);
				setFacetCount('#pendingoverride', existingPendingFailedCount - pendingFailedCount);
				setFacetCount('#memberoverride', getFacetCount('#memberoverride') + declinedCount + pendingFailedCount);
				setFacetCount('#member', getFacetCount('#member') + pendingPassedCount);
				this.updateRows(this.options.attributes.selectedPendingPassed, 'member', 'MEMBER', this.memberStatus.attributes.member, action);
				this.updateRows(this.options.attributes.selectedDeclined.concat(this.options.attributes.selectedPendingFailed), 'pending', 'MEMBER OVERRIDE', this.memberStatus.attributes.memberOverride, action);
				break;

			// pending passed, pending failed,
			case 'declined':
				var pendingPassedCount = this.options.attributes.selectedPendingPassed.length,
					existingPendingPassedCount = getFacetCount('#pending'),
					pendingFailedCount = this.options.attributes.selectedPendingFailed.length,
					existingPendingFailedCount = getFacetCount('#pendingoverride');

				setFacetCount('#pending', existingPendingPassedCount - pendingPassedCount);
				setFacetCount('#pendingoverride', existingPendingFailedCount - pendingPassedCount);
				setFacetCount('#declined', getFacetCount('#declined') + pendingPassedCount + pendingFailedCount);
				this.updateRows(this.options.attributes.selectedPendingPassed.concat(this.options.attributes.selectedPendingFailed), 'declined', 'DECLINED', this.memberStatus.attributes.declined, action);
				break;

			// member, member override
			case 'removed':
				var memberCount = this.options.attributes.selectedMembers.length,
					existingMemberCount = getFacetCount('#member'),
					memberOverrideCount = this.options.attributes.selectedMembersOverride.length,
					existingMemberOverrideCount = getFacetCount('#memberoverride');

				setFacetCount('#member', existingMemberCount - memberCount);
				setFacetCount('#memberoverride', existingMemberOverrideCount - memberOverrideCount);
				removeRows(this.options.attributes.selectedMembers.concat(this.options.attributes.selectedMembersOverride));
				break;

			// invitees
			case 'uninvited':
				var inviteesCount = this.options.attributes.selectedInvitees.length,
					existingInviteesCount = getFacetCount('#invited');

				setFacetCount('#invited', existingInviteesCount - inviteesCount);
				removeRows(this.options.attributes.selectedInvitees);
				break;
		}

		if (this.searchResults.children().length == 0) {
			this.showEmptyResultsUI();
		}
	},

	showEmptyResultsUI: function () {
		var numberOfFacetsWithZeroCounts = _.filter($('.facet-count'), function (facet) {
			return $(facet).text() == '0'
		}).length;

		if (numberOfFacetsWithZeroCounts === this.options.attributes.NUMBER_OF_MEMBER_STATUSES) {
			$('.all-results').addClass('disabled');
		}
		this.searchResults.empty();
		this.searchResults.append(noResultsMessage({
			title   : 'No Talent Pool Results Found',
			messages:  [
				'Some suggestions on how to improve your results:',
				'-Expand your search by removing one or more search filters.',
				'-Try changing or removing any keywords you\'re using.'
			]
		}));
	},

	updateRows: function (userIds, newClass, text, attr, action) {
		_.each(userIds, function (id) {
			var $row = this.getRow(id);
			this.relabelRow($row, newClass, text);
			this.updateData($row, attr);
		}, this);
	},

	relabelRow: function ($row, newClass, text) {
		var $label = $row.find('.wm-status-label');
		$label.removeClass('member pending invited declined').addClass(newClass);
		$label.text(text);
	},

	updateData: function ($row, attr) {
		$row.data('derivedStatus', attr);
	},

	getDataObjectMapsForWorkersOnPage: function () {
		return _.map($('.profile-card--photo'), function (card) {
			return $(card).data();
		});
	},

	exportAction: function () {
		this.downloadAllBeyondPage(_.bind(this.exportCsvFrontEndAll, this));
	},

	downloadAllBeyondPage: function () {
	var _callbacks = arguments,
		data = {
			limit : 20000,
			search_type: 'PEOPLE_SEARCH_GROUP_MEMBER',
			resource_mode: 'workers',
			group_id: this.options.groupId,
			noFacetsFlag : true
		};

		$.ajax({
			url: '/search/retrieve',
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function (data) {
				return _.reduce(_callbacks, function (workerData, callback) {
					return callback(workerData)
				}, data.results);
			}
		});
	},

	exportCsvFrontEndAll: function (workers) {
		var fileName = 'group-members-' + this.options.groupId + '.csv';
		var rows = _.reduce(workers, _.bind(function (memo, worker) {
			var row = _.map(_.pick(worker, this.csv.get('userAttributes')), function (prop) {
				return encodeURIComponent(String(prop).replace("|", "_"));
			}).join('|');
			memo.push(row);
			return memo;
		}, this), [ this.csv.get('fields').join('|') ]);

		$('[name="csvData"]').val(rows.join(','));
		$('[name="filename"]').val(fileName);
		$('#export-csv').submit();
	},

	getRow: function (userId) {
		return $('.results-row').filter(function () {
			return $(this).data('userId') == userId;
		});
	},

	decorateMissingReqsData: function (data) {
		if (data.availability) {
			_.each(data.availability, function (availability) {
				var timeframe;
				if (availability.isAllDayAvailable == 'true') {
					timeframe = 'All Day';
				} else {
					timeframe = availability.fromTime + ' to ' + availability.toTime;
				}
				availability.timeframe = timeframe;
			});
		}
		if (data.lane) {
			var displayText;
			if (data.lane.resource) {
				displayText = 'May be contractor for or employee of ';
			} else if (data.lane.lane1) {
				displayText = 'Must be employee of ';
			} else if (data.lane.lane2) {
				displayText = 'Must be contractor for ';
			}
			displayText += data.groupName;
			data.lane.displayText = displayText;
		}
		if (data.assessments) {
			_.each(data.assessments, function (assessment) {
				var status;
				if (assessment.inProgress) {
					status = 'In Progress';
				} else if (assessment.gradePending) {
					status = 'Grade Pending';
				} else if (assessment.reattemptAllowed) {
					status = 'Failed';
				} else if (!assessment.reattemptAllowed) {
					status = 'Failed';
				} else {
					status = 'No Attempt';
				}
				assessment.status = status;
			});
		}
		return data;
	},

	refreshUiAfterAction: function (userId, action) {
		var getFacetCount = self.getFacetCount,
			setFacetCount = self.setFacetCount,
			$row = self.getRow(userId);
		var status = $row.data('derivedStatus');

		if (action == 'approved') {
			if (status == this.memberStatus.attributes.pending) {
				setFacetCount('#member', getFacetCount('#member') + 1);
				setFacetCount('#pending', getFacetCount('#pending') - 1);
				this.relabelRow($row,'member', 'MEMBER');
				this.updateData($row, this.memberStatus.attributes.member);

			} else if (status == this.memberStatus.attributes.pendingFailed || status == this.memberStatus.attributes.declined) {
				setFacetCount('#memberoverride', getFacetCount('#memberoverride') + 1);
				if (status == this.memberStatus.attributes.pendingFailed) {
					setFacetCount('#pendingoverride', getFacetCount('#pendingoverride') - 1);
				} else {
					setFacetCount('#declined', getFacetCount('#declined') - 1);
				}
				this.relabelRow($row,'pending', 'MEMBER OVERRIDE');
				this.updateData($row, this.memberStatus.attributes.memberOverride);
			}
		}

		if (action == 'declined') {
			if (status == this.memberStatus.attributes.pending) {
				setFacetCount('#pending', getFacetCount('#pending') - 1);
			} else {
				setFacetCount('#pendingoverride', getFacetCount('#pendingoverride') - 1);
			}
			setFacetCount('#declined', getFacetCount('#declined') + 1);

			this.relabelRow($row, 'declined', 'DECLINED');
			this.updateData($row, this.memberStatus.attributes.declined);
		}

		if (action == 'removed') {
			if (status == this.memberStatus.attributes.member) {
				setFacetCount('#member', getFacetCount('#member') - 1);
			} else {
				setFacetCount('#memberoverride', getFacetCount('#memberoverride') - 1);
			}
			$row.remove();
		} else if (action == 'uninvited') {
			setFacetCount('#invited', getFacetCount('#invited') - 1);
			$row.remove();
		}

		if (!this.searchResults.children().length) {
			this.showEmptyResultsUI();
		}
	},

	checkForMissingRequirementsData: function (e) {
		e.preventDefault();
		var $div = $(e.currentTarget).closest('.profile-card--shade'),
			$template = $div.parent().find('.missing-reqs--template');

		if ($template.children().length) return;

		$.ajax({
			context: this,
			url: '/groups/' + this.options.groupId + '/validateRequirements/' + $div.find('.profile-card--view-profile').data('user-number'),
			type: 'GET',
			dataType: 'json',
			success: function (response) {
				if (response.successful && response.data.data) {
					let data = response.data.data;
					if (typeof data !== 'undefined' && data.legacy) {
						$template.append(LegacyMissingRequirementsTemplate({
							data: this.decorateMissingReqsData(data)
						}));
					} else {
						$template.append(MissingRequirementsTemplate({
							data: this.decorateMissingReqsData(data)
						}));
					}
				} else {
					wmNotify({
						message: 'Oops! There was a problem fetching the missing requirements.',
						type: 'danger'
					});
				}
			}
		});
	}

});
