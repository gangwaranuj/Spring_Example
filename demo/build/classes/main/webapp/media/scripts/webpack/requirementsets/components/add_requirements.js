import React, { Component } from 'react';
import wmSelect from '../../funcs/wmSelect';
import AssignmentListItem from '../../assignments/components/AssignmentListItem';

export default class RequirementsComponent extends Component {
	constructor (props) {
		super(props);
		this.state = { selectedRequirementSets: [], allRequirementSets: [] };
	}

	componentDidMount () {
		const root = this.node;
		this.requirementSetSelect = wmSelect({ root, selector: '[name="requirementSet-select"]' }, {
			valueField: 'id',
			searchField: ['id', 'name'],
			sortField: 'name',
			labelField: 'name',
			preload: true,
			openOnFocus: true,
			onChange: (id) => {
				if (id) {
					this.props.addRequirementSet(parseInt(id, 10));
					this.fetchRequirements(id);
					this.requirementSetSelect.clear();
				}
			},
			onLoad: (value) => {
				if (!this.requirementSetSelect.items[0]) {
					const filtered = value.filter(field => field.required === 'true');
					if (filtered.length) {
						this.props.addRequirementSet(parseInt(filtered[0].id, 10));
						this.fetchRequirements(filtered[0].id);
					}
				}
			},
			load: (query, callback) => {
				fetch('/employer/v2/requirement_sets?fields=id,name,required', {
					credentials: 'same-origin'
				})
				.then(response => response.json())
				.then((response) => {
					// load all requirement sets associated with the user
					this.setState({ allRequirementSets: response.results });
					callback(response.results);
				});
			}
		})[0].selectize;
	}

	shouldComponentUpdate (nextProps, nextState) {
		// wait for all requirementsets to be fetched
		if (nextState.allRequirementSets && nextState.allRequirementSets.length > 0) {
			nextProps.requirementSetIds.forEach(id => this.fetchRequirements(id));
			return true;
		}
		return false;
	}

	getRequirementSetName (id) {
		const requirementSet = this.state.allRequirementSets
			.find(requirement => parseInt(requirement.id, 10) === id);
		return requirementSet && requirementSet.name;
	}

	wrapUpRequirements (id, res) {
		const formattedId = parseInt(id, 10);
		const name = this.getRequirementSetName(formattedId) || '';
		const newRequirementSet = {
			id: formattedId,
			name,
			requirements: res
		};
		if (!this.state.selectedRequirementSets.find(requirement => requirement.id === formattedId)) {
			this.setState({ selectedRequirementSets:
				this.state.selectedRequirementSets.concat(newRequirementSet) });
		}
	}

	fetchRequirements (id) {
		fetch(`/requirement_sets/${id}/requirements`, { credentials: 'same-origin'})
		.then(res => res.json())
		.then(res => this.wrapUpRequirements(id, res));
	}

	removeRequirementSet (id) {
		this.props.removeRequirementSet(id);
		const selectedRequirementSets = this.state.selectedRequirementSets
			.filter(requirement => requirement.id !== id);
		this.setState({ selectedRequirementSets });
	}

	render () {
		const { selectedRequirementSets } = this.state;

		const renderAssignmentDetails = (requirement, index) => {
			return (
				<div key={ index } className="assignment-creation--list-item--details">
					<div className="assignement-creation--label">{requirement.$humanTypeName} : </div>
					{''}
					<span>
						<div className="assignement-creation--label">{requirement.name || requirement.requirable.name}</div>
					</span>
				</div>
			);
		};

		return (
			<div className="test">
				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="requirementSet-select">Requirement Sets</label>
					<div className="assignment-creation--field">
						<select id="requirementsSet-select" className="wm-select" name="requirementSet-select" />
					</div>
				</div>

				<div className="assignment-creation--list">
					{ selectedRequirementSets.map((selectedRequirementSet) => {
						if (selectedRequirementSets.length) {
							return (
								<div
									key={ selectedRequirementSet.id }
									className="assignment-creation--list-container"
								>
									<AssignmentListItem
										item={ selectedRequirementSet }
										onRemoveToClick={ () => this.removeRequirementSet(selectedRequirementSet.id) }
									/>
									{ selectedRequirementSet.requirements.map(renderAssignmentDetails) }
								</div>
							);
						} return '';
					})}
				</div>

			</div>
		);
	}
}
