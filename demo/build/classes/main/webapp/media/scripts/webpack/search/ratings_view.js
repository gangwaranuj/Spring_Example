'use strict';
import Backbone from 'backbone';
import $ from 'jquery';
import 'datatables.net';

import NameTemplate from './templates/name.hbs';
import OverallTemplate from './templates/overall.hbs';
import QualityTemplate from './templates/quality.hbs';
import ProfessionalismTemplate from './templates/professionalism.hbs';
import CommunicationTemplate from './templates/communication.hbs';

let meta;

export default Backbone.View.extend({

	events: {
		'click button[name=scopeToCompany]' : 'redrawTable'
	},

	initialize: function (options) {
		this.userNumber = options.userNumber;
		this.$table = this.$el.find('table');
		this.buildTable();
	},

	buildTable: function () {
		this.$table.dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': false,
			'bFilter': false,
			'bAutoWidth': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'sAjaxSource': '/profile/' + this.userNumber + '/ratings.json',
			'iDisplayLength': 20,
			'aoColumnDefs': [
				{
					'mRender': renderName,
					'bSortable': false,
					'aTargets': [0]
				},
				{
					'mRender': renderOverall,
					'bSortable': false,
					'aTargets': [1]
				},
				{
					'mRender': renderQuality,
					'bSortable': false,
					'aTargets': [2]
				},
				{
					'mRender': renderProfessionalism,
					'bSortable': false,
					'aTargets': [3]
				},
				{
					'mRender': renderCommunication,
					'bSortable': false,
					'aTargets': [4]
				}



				/*{'bSortable': false, 'aTargets': ['_all']},
				{'mRender': cellRenderer(NameTemplate), 'aTargets': [0]},
				{'mRender': cellRenderer(OverallTemplate), 'aTargets': [1]},
				{'mRender': cellRenderer(QualityTemplate), 'aTargets': [2]},
				{'mRender': cellRenderer(ProfessionalismTemplate), 'aTargets': [3]},
				{'mRender': cellRenderer(CommunicationTemplate), 'aTargets': [4]},
				{'sTitle': 'Assignment name', 'aTargets': [0]},
				{'sTitle': 'Overall:', 'aTargets': [1]},
				{'sTitle': 'Quality:', 'aTargets': [2]},
				{'sTitle': 'Professionalism:', 'aTargets': [3]},
				{'sTitle': 'Communication:', 'aTargets': [4]},
				{'sClass': 'text-center', 'aTargets': [1]},
				{'sClass': 'text-center', 'aTargets': [2]},
				{'sClass': 'text-center', 'aTargets': [3]},
				{'sClass': 'text-center', 'aTargets': [4]}*/
			],
			'fnServerData': function (sSource, aoData, fnCallback) {
				aoData.push({
					name: 'scopeToCompany',
					value: this.closest('.dataTables_wrapper').prev().find('.active[name=scopeToCompany]').val()
				});
				$.getJSON(sSource, aoData, (json) => {
					meta = json.aMeta;
					fnCallback(json);
					var wrapper = this.closest('.dataTables_wrapper');
					if (json.aaData.length) {
						wrapper.show().next().hide();
					} else {
						wrapper.hide().next().show();
					}
				});
			}
		});
	},

	redrawTable: function(e) {
		e.preventDefault();
		$(e.currentTarget).addClass('active').siblings('button').removeClass('active');
		this.$table.fnDraw();
	}
});

function renderName (data, type, val, metaData) {
	return NameTemplate({
		workNumber: meta[metaData.row].workNumber,
		workTitle: meta[metaData.row].workTitle,
		workSchedule: meta[metaData.row].workSchedule,
		resourceLabels: meta[metaData.row].resourceLabels,
		isOwner: meta[metaData.row].isOwner,
		companyName: meta[metaData.row].companyName,
		ratingReview: meta[metaData.row].ratingReview
	});
}

function renderOverall (data, type, val, metaData) {
	return OverallTemplate({
		ratingCode: meta[metaData.row].ratingCode,
		ratingValue: meta[metaData.row].ratingValue
	});
}

function renderQuality (data, type, val, metaData) {
	return QualityTemplate({
		qualityCode: meta[metaData.row].qualityCode,
		qualityValue: meta[metaData.row].qualityValue
	});
}

function renderProfessionalism (data, type, val, metaData) {
	return ProfessionalismTemplate({
		professionalismCode: meta[metaData.row].professionalismCode,
		professionalismValue: meta[metaData.row].professionalismValue
	});
}

function renderCommunication (data, type, val, metaData) {
	return CommunicationTemplate({
		communicationCode: meta[metaData.row].communicationCode,
		communicationValue: meta[metaData.row].communicationValue
	});
}
