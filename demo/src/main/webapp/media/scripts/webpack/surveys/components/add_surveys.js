'use strict';

import React, { Component } from 'react';
import $ from 'jquery';
import ReactDOM from 'react-dom';
import wmSelect from '../../funcs/wmSelect';
import AssignmentListItem from '../../assignments/components/AssignmentListItem';

export default class SurveysContainer extends Component {
	constructor(props) {
		super(props);
		this.state = { allSurveys: [] };
	}

	componentDidMount() {
		const root = ReactDOM.findDOMNode(this);
		this.surveySelect = wmSelect({ root, selector: '[name="surveys-select"]' }, {
			valueField: 'id',
			searchField: ['name'],
			sortField: 'name',
			labelField: 'name',
			preload: true,
			openOnFocus: true,
			onChange: (value) => {
				if (value) {
					const intVal = parseInt(value);
					this.props.addSurvey(this.state.allSurveys.find(({id}) => id === intVal));
					this.surveySelect.removeOption(value);
					this.surveySelect.clear();
				}
			},
			load: (query, callback) => {
				$.ajax({
					url: '/employer/v2/surveys?fields=id,name',
					type: 'GET',
					dataType: 'json',
					error: () => {
						callback();
					},
					success: (res) => {
						this.setState({ allSurveys: res.results });
						callback(res.results);
					}
				});
			}
		})[0].selectize;
	}

	getSurveyName(surveyId) {
		let survey = this.state.allSurveys.find((survey) => surveyId === survey.id);
		return survey.name;
	}

	render() {
		const { surveys } = this.props;

		return (
			<div>
				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="surveys-select">Surveys</label>
					<div className="assignment-creation--field">
						<select id="surveys-select" className="wm-select" name="surveys-select"></select>
						<small><a href='/lms/manage/surveys' target='_blank'>Manage Surveys</a></small>
					</div>
				</div>
				<div className="assignment-creation--list">
					{surveys.map((survey, index) => {
						// wait until survey info comes back from server
						if (this.state.allSurveys.length) {
							survey.name = this.getSurveyName(survey.id);
							return (
								<AssignmentListItem
									key={index}
									item={survey}
									onRemoveToClick={() => {
										this.surveySelect.addOption(survey);
										this.props.removeSurvey(index);
									}}
									onToggleToClick={ () => this.props.toggleRequired(index) }
								/>
							);
						} else {
							return '';
						}
					})}
				</div>
			</div>
		);
	}
}
