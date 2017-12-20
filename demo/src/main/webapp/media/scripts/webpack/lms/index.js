'use strict';

import Application from '../core';
import $ from 'jquery';
import _ from 'underscore';
import AssetsPage from './assets_page';
import BoxesManageMainView from './boxes_manage_main_view';
import GradeView from './grade_view';
import ManageListView from './manage_list_view';
import ViewDetailsView from './view_details';
import BoxesView from './boxes_view';
import ManageDetailsView from './manage_details';
import ManageQuestionsView from './manage_questions_view';
import TakeView from './take';
import SurveyWorkerView from './survey_worker';
import '../config/wysiwyg';
import '../dependencies/jquery.bootstrap-tab';

Application.init({ name: 'lms', features: config }, () => {});

switch (config.mode) {
	case 'assets':
		new AssetsPage(config.assessmentId);
		break;
	case 'boxesManage':
		new BoxesManageMainView();
		break;
	case 'grade':
		new GradeView();
		break;
	case 'manageList':
		new ManageListView({
			type: config.type
		});
		break;
	case 'viewDetails':
		new ViewDetailsView({
			id: config.id,
			name: config.name,
			userId: config.userId,
			userCompanyId: config.userCompanyId,
			companyBlocked: config.companyBlocked,
			clientIdToBlock: config.clientIdToBlock,
			blockedClientTooltip: config.blockedClientTooltip
		});
		break;
	case 'boxesView':
		new BoxesView();
		break;
	case 'manageDetails':
		if (config.assessmentSetId) {
			new ManageDetailsView({
				itemCount: config.assessmentItemsLength
			});
		} else {
			new ManageDetailsView();
		}

		$('#show-options').on('click', function () {
			var $options = $('#options'),
				$showOptions = $('#show-options');
			if ($options.is(':visible')) {
				$options.hide();
				$showOptions.html('View Advanced Options &#9660;');
			} else {
				$options.show();
				$showOptions.html('Hide Advanced Options &#9650;');
			}
		});
		break;
	case 'manageQuestions':
		new ManageQuestionsView({
			id: config.id,
			questions: config.questions,
			questionTypes: config.questionTypes
		});
		break;
	case 'take':
		var initParams = {
			id: config.id,
			type: config.type,
			questions: $.parseJSON(config.assessmentItemsJson),
			assignment: config.assignment,
			onBehalfOf: config.onBehalfOf,
			durationMinutes: config.durationMinutes,
			timeLeft: config.timeLeft
		};

		if (config.isLatestAttemptInprogress) {
			initParams = _.extend(initParams, { responses: $.parseJSON(config.latestAttemptResponsesJson) });
		}

		var questionTypes = {};
		questionTypes[config.assessmentItemType.SINGLE_LINE_TEXT] = 'singleline';
		questionTypes[config.assessmentItemType.MULTIPLE_LINE_TEXT] = 'multiline';
		questionTypes[config.assessmentItemType.SINGLE_CHOICE_RADIO] = 'radio';
		questionTypes[config.assessmentItemType.MULTIPLE_CHOICE] = 'checkboxes';
		questionTypes[config.assessmentItemType.SINGLE_CHOICE_LIST] = 'dropdown';
		questionTypes[config.assessmentItemType.DIVIDER] = 'segment';
		questionTypes[config.assessmentItemType.DATE] = 'date';
		questionTypes[config.assessmentItemType.PHONE] = 'phonenumber';
		questionTypes[config.assessmentItemType.EMAIL] = 'email';
		questionTypes[config.assessmentItemType.NUMERIC] = 'numeric';
		questionTypes[config.assessmentItemType.ASSET] = 'asset';

		initParams = _.extend(initParams, { questionTypes: questionTypes });

		if (config.isWorkNotEmpty) {
			initParams = _.extend(initParams, { completedReturn: '/assignments/details/' + config.workNumber });
		} else if (config.isGroupNotEmpty) {
			initParams = _.extend(initParams, { completedReturn: '/groups/' + config.group + '#requirements' });
		}
		new TakeView(initParams);

		break;
	case 'manageSurveys':
		new SurveyWorkerView();
		break;
}
