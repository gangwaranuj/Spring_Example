import React from 'react';
import ReactDOM from 'react-dom';
import fetch from 'isomorphic-fetch';
import { WMCheckbox } from '@workmarket/front-end-components';
import ReactQuill from 'react-quill';
import PropTypes from 'prop-types';
import { Style } from 'radium';
import styles from './styles';
import wmSelect from '../../funcs/wmSelect';
import wmEmployeeList from '../../funcs/wmEmployeeList';
import Application from '../../core';
// TODO[bruno] - fix issues with this component and add back in
// import Skills from './skills';

const quillModules = {
	toolbar: [
		['bold', 'italic', 'underline'],
		[{ list: 'ordered' }, { list: 'bullet' }]
	]
};
const quillFormats = [
	'bold', 'italic', 'underline', 'list', 'bullet'
];

export default class BasicComponent extends React.Component {
	componentDidMount () {
		const root = ReactDOM.findDOMNode(this);
		const { companyId, userNumber } = Application.UserInfo;

		this.supportContactSelect = wmEmployeeList({ root, selector: '[name="supportContactId"]', companyId }, {
			maxItems: 1,
			onChange: value => this.props.setValue({ name: 'supportContactId', value }),
			onLoad: () => {
				if (!this.supportContactSelect.items[0]) {
					const supportContactId = this.props.supportContactId || userNumber;
					this.supportContactSelect.setValue(supportContactId);
				}
			}
		})[0].selectize;

		this.ownerSelect = wmEmployeeList({ root, selector: '[name="ownerId"]', companyId }, {
			maxItems: 1,
			onChange: value => this.props.setValue({ name: 'ownerId', value }),
			onLoad: () => {
				if (!this.ownerSelect.items[0]) {
					const ownerId = this.props.ownerId || userNumber;
					this.ownerSelect.setValue(ownerId);
				}
			}
		})[0].selectize;

		this.industrySelect = wmSelect({ root, selector: '[name="industryId"]' }, {
			valueField: 'id',
			searchField: ['id', 'name'],
			sortField: 'name',
			labelField: 'name',
			preload: true,
			openOnFocus: true,
			onChange: value => this.props.setValue({ name: 'industryId', value }),
			load: (query, callback) => fetch('/industries-list', { credentials: 'same-origin' })
				.then(res => res.json())
				.then(res => callback(res)),
			onLoad: () => {
				if (!this.industrySelect.items[0]) {
					const industryId = this.props.industryId || '1000';
					this.industrySelect.setValue(industryId);
				}
			}
		})[0].selectize;
	}


	componentWillReceiveProps (nextProps) {
		const { industryId, ownerId, supportContactId } = nextProps;

		if (industryId !== this.props.industryId) {
			this.industrySelect.setValue(industryId, true);
		}
		if (ownerId && ownerId.length && ownerId !== this.props.ownerId) {
			this.ownerSelect.setValue(ownerId, true);
		}
		if (supportContactId
		&& (supportContactId !== this.props.supportContactId || !this.supportContactSelect.items[0])) {
			this.supportContactSelect.setValue(supportContactId, true);
		}
	}

	componentDidUpdate () {
		let isModuleValid = false;
		isModuleValid = this.checkValidation(this.props);
		this.props.setModuleValidation(isModuleValid, this.props.id);
		const str = '<p><br></p>';
		if (this.props.description === str) {
			this.setValue({ target: { value: '', name: 'description' } });
		}
	}

	setValue = ({ target }) => {
		this.props.setValue(target);
	}

	checkValidation = (props) => {
		let isValid = false;
		const { title, description } = props;
		if (title.length && description.length) {
			isValid = true;
		}
		return isValid;
	}

	render () {
		return (
			<div>
				<div className="assignment-creation--container">
					<label className="assignment-creation--label -required" htmlFor="basics-title">Title</label>
					<div className="assignment-creation--field">
						<input
							id="basics-title"
							type="text"
							name="title"
							value={ this.props.title }
							onChange={ this.setValue }
						/>
					</div>
				</div>

				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="basics-industryId">Category</label>
					<div className="assignment-creation--field">
						<select
							id="basics-industryId"
							className="wm-select"
							name="industryId"
							onChange={ this.setValue }
						/>
					</div>
				</div>

				<div className="assignment-creation--container">
					<label
						className="assignment-creation--label -required"
						htmlFor="basics-description"
					>
						Description
					</label>
					<div className="assignment-creation--field">
						<ReactQuill
							id="basics-description"
							theme="snow"
							modules={ quillModules }
							formats={ quillFormats }
							toolbar={ false }
							bounds={ '._quill' }
							value={ this.props.description }
							onChange={
								value => this.setValue({
									target: { value, name: 'description' }
								})
							}
						/>
						<small>
							The description field is publicly viewable.
							Please use this field for a high level overview of the assignment details.
						</small>
					</div>
				</div>

				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="basics-instructions">Special Instructions</label>
					<div className="assignment-creation--field">
						<ReactQuill
							id="basics-instructions"
							theme="snow"
							modules={ quillModules }
							formats={ quillFormats }
							toolbar={ false }
							bounds={ '._quill' }
							value={ this.props.instructions }
							onChange={
								value => this.setValue({
									target: { value, name: 'instructions' }
								})
							}
						/>
						<WMCheckbox
							classList="assignment-creation--instructions-privacy"
							name="private_instructions"
							onCheck={ () => {
								const target = { name: 'instructionsPrivate' };
								this.setValue({ target });
							} }
							checked={ this.props.instructionsPrivate }
							label="Only show to the assigned worker. Instructions will not be displayed to invited workers."
						/>
					</div>
				</div>

				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="basics-skills">Desired Skills</label>
					<div className="assignment-creation--field">
						<input
							id="basics-skills"
							type="text"
							name="skills"
							value={ this.props.skills }
							onChange={ this.setValue }
						/>
					</div>
				</div>

				{this.props.configuration.uniqueExternalIdEnabled ? (
				[
					<div className="assignment-creation--container">
						<label
							className="assignment-creation--label -required"
							htmlFor="basics-uniqueExternalId"
							key={ `${this.props.configuration.uniqueExternalIdDisplayName}-label` }
						>
							{this.props.configuration.uniqueExternalIdDisplayName}
						</label>,
						<div className="assignment-creation--field" key={ `${this.props.configuration.uniqueExternalIdDisplayName}-input-container` }>
							<input
								key={ `${this.props.configuration.uniqueExternalIdDisplayName}-input` }
								id="basics-uniqueExternalId"
								type="text"
								name="uniqueExternalId"
								onChange={ this.setValue }
							/>
						</div>
					</div>
				]
				) : ''}
				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="basics-ownerId">Owner</label>
					<div className="assignment-creation--field">
						<select
							id="basics-ownerId"
							className="wm-select"
							name="ownerId"
							onChange={ this.setValue }
						/>
					</div>
				</div>
				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="basics-supportContactId">Support Contact</label>
					<div className="assignment-creation--field">
						<select
							id="basics-supportContactId"
							className="wm-select"
							name="supportContactId"
							onChange={ this.setValue }
						/>
					</div>
				</div>
				<Style
					scopeSelector=".quill-contents"
					rules={ styles.editor }
				/>
			</div>
		);
	}
}

BasicComponent.propTypes = {
	id: PropTypes.number,
	title: PropTypes.string.isRequired,
	description: PropTypes.string.isRequired,
	configuration: PropTypes.string,
	instructions: PropTypes.string,
	supportContactId: PropTypes.string,
	industryId: PropTypes.string,
	ownerId: PropTypes.string,
	skills: PropTypes.string,
	setValue: PropTypes.func,
	setModuleValidation: PropTypes.func.isRequired,
	instructionsPrivate: PropTypes.bool
};
