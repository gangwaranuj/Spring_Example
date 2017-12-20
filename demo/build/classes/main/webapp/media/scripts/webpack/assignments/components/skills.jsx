'use strict';

import Application from '../../core';
import React from 'react';
import ReactDOM from 'react-dom';
import fetch from 'isomorphic-fetch';
import { WMAutocomplete } from '@workmarket/front-end-components';
import injectTapEventPlugin from 'react-tap-event-plugin';

export default class SkillsComponent extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			skillsResults: [],
			recommendedSkills: [],
			jobTitles: [],
			selectedSkills: [],
			selectedJobTitle: ''
		};
	}

	componentDidMount() {
		fetch('/v2/suggest/jobTitle', { credentials: 'same-origin' })
			.then((res) => res.ok ? res.json() : { results: [] })
			.then((res) => this.setState({jobTitles: res.results}))

		this.updateRecommendedSkills(true);
	}

	componentWillReceiveProps(nextProps) {
		const { skills, title, description } = nextProps;
		if (this.props.skills === "" && skills !== this.props.skills) {
			let fields = skills.split("--");
			if (fields.length > 1 ) {
				this.setState({selectedJobTitle: fields[1]});
			}

			if (fields[0] !== '') {
				this.setState({selectedSkills: fields[0].toLowerCase().split(',')}, () => this.updateRecommendedSkills(true));
			}
		}

		if (title !== this.props.title || description !== this.props.description) {
			this.updateRecommendedSkills(true);
		}
	}

	setValue({ target }) {
		this.props.setValue(target);
	}

	skillsAutocompleteSearch(text,  datasource){
		fetch(`/v2/suggest/skill?q=${text}`, { credentials: 'same-origin' })
			.then((res) => res.ok ? res.json() : {results: []})
			.then((res) => this.setState({skillsResults: res.results.map(function (r) {return $.extend(r, {value: r.name})})}))
	}

	updateDesiredSkills() {
		let text = this.state.selectedSkills.join() + '--' + this.state.selectedJobTitle;
		this.props.setValue({ name: 'skills', value: text });
	}

	updateJobTitle(jobTitle) {
		this.setState({selectedJobTitle: jobTitle}, () => {
			this.updateRecommendedSkills(true);
			this.updateDesiredSkills();
		});
	}

	addSkill(skill, recommended, index) {
		skill = skill instanceof Object ? skill.value.toLowerCase() : skill.toLowerCase();
		let skills = this.state.selectedSkills;
		if (skills.indexOf(skill) < 0) {
			if (recommended || index >= 0) { //disallow user free text
				skills.push(skill);
				this.setState({selectedSkills: skills}, () => this.updateDesiredSkills());
			}
		}

		if (recommended) {
			this.removeRecommendedSkill(skill);
		}

		this.updateRecommendedSkills(false);
	}

	removeSkill(skill, recommended) {
		let skills = this.state.selectedSkills;
		let index = skills.indexOf(skill.toLowerCase());
		if (index >= 0) {
			skills.splice(index, 1);
			this.setState({selectedSkills: skills}, () => this.updateDesiredSkills());
		}
	}

	removeRecommendedSkill(skill) {
		let skills = this.state.recommendedSkills;
		let index = skills.indexOf(skill.toLowerCase());
		if (index >= 0) {
			skills.splice(index, 1);
			this.setState({recommendedSkills: skills});
		}
	}

	updateRecommendedSkills(force) {
		if (force || this.state.recommendedSkills.length == 0) {
			var requestData = {
				jobTitle: this.state.selectedJobTitle,
				offset: 0,
				limit: 10,
				industries: [this.props.industryId],
				selectedSkills: this.state.selectedSkills,
				definedSkills: [],
				removedSkills: [],
				assignmentTitle: this.props.title,
				assignmentDescription: this.props.description
			};

			fetch('/v2/recommend/skill',
				{
					credentials: 'same-origin',
					method: "POST",
					headers: new Headers({
						'Content-Type': 'application/json',
						//for some reason, spring doesn't like fetch post and flags as a csrf attack. This adds the token to prevent this. Credentials doesn't add this
						'X-CSRF-Token': Application.CSRFToken
					}),
					body: JSON.stringify(requestData)
				})
				.then((res) => res.ok ? res.json() : { results: []})
				.then((res) => {
					let recommendedSkills = res.results.map(function (obj) { return obj.name.toLowerCase()});
					this.state.selectedSkills.forEach(function(skill) {
						let index = recommendedSkills.indexOf(skill);
						if (index >= 0) {
							recommendedSkills.slice(index, 1); //remove if in skills list already
						}
					});

					this.setState({recommendedSkills: recommendedSkills})
				})
		}
	}

	render() {
		return (
			<div>
				<label className="assignment-creation--label -required" htmlFor="job-function-autocomplete">Job Function</label>
				<WMAutocomplete
					id="job-function-autocomplete"
					hintText="Job Function"
					searchText={this.state.selectedJobTitle}
					filter="caseInsensitiveFilter"
					dataSource={this.state.jobTitles}
					dataSourceConfig={{ text: 'name', value: 'uuid'}}
					onNewRequest={(item, index) => this.updateJobTitle(item.name)}
					onBlur={(e) => this.updateJobTitle(e.target.value)}
					popoverProps={{zDepth: 1, className: 'autocomplete-popover-fix'}}
				/>

				<label className="assignment-creation--label" htmlFor="basics-skills">Desired Skills</label>
				<div className="assignment-creation--field">
					<input
						id="basics-skills"
						type="hidden"
						name="skills"
						value={this.props.skills}
						onChange={this.setValue.bind(this)}
					/>
				<div>
					<ul className="your-skills your-skills-wrapper">
						<span className="skills-list">
						{this.state.selectedSkills.map((skill) => {
							return (<li className='skill' onClick={() => this.removeSkill(skill, false)}>{skill}</li>)
						})}
						</span>
						<div className="skills-select">
							<WMAutocomplete
								id="skills-autocomplete"
								hintText="Search or Select..."
								filter={(searchText, key) => true}
								dataSource={this.state.skillsResults}
								dataSourceConfig={{ text: 'value', value: 'id'}}
								onUpdateInput={(text, datasource) => this.skillsAutocompleteSearch(text, datasource)}
								onNewRequest={(item, index) => this.addSkill(item, false, index)}
								style={{marginLeft: "10px", zIndex: 10000000}}
								inputStyle={{width: "165px"}}
								underlineStyle={{width: "165px"}}
								popoverProps={{zDepth: 1, className: 'autocomplete-popover-fix'}}
							/>
						</div>
					</ul>
					<small>Provide a list of skills and specialties needed to perform the work.</small>
				</div>
				<div className="recommended-skills">
					<p>Suggested Skills</p>
					<ul className="skills">
						{this.state.recommendedSkills.slice(0, 6).map((skill) => {
							return (<li className='skill' onClick={() => this.addSkill(skill, true)}>{skill}</li>)
						})}
					</ul>
				</div>
			</div>
		</div>
		);
	}
}
