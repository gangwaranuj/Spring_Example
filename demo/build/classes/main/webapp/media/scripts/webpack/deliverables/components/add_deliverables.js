import PropTypes from 'prop-types';
import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import ReactQuill from 'react-quill';
import { WMRaisedButton } from '@workmarket/front-end-components';
import { WMCurrencyField } from '@workmarket/front-end-patterns';
import wmSelect from '../../funcs/wmSelect';
import AssignmentListItem from '../../assignments/components/AssignmentListItem';

const quillModules = {
	toolbar: [
		['bold', 'italic', 'underline'],
		[{ list: 'ordered' }, { list: 'bullet' }]
	]
};
const quillFormats = [
	'bold', 'italic', 'underline', 'list', 'bullet'
];

class DeliverableComponent extends Component {
	constructor (props) {
		super(props);
		this.state = {
			deliverableType: '',
			numberOfFiles: 0,
			description: '',
			valid: false
		};

		this.setDescriptionValue = this.setDescriptionValue.bind(this);
	}

	componentDidMount() {
		const root = ReactDOM.findDOMNode(this);
		const name = 'deliverables-deliverableType';
		this.deliverablesTypeSelect = wmSelect({ selector: `[name=${name}]`, root }, {
			placeholder: 'Deliverable Type',
			onChange: (value) => {
				let target = { name, value };
				this.setValue({ target });
			}
		})[0].selectize;

	}

	setValue({ target }) {
		let { name, value } = target;
		value = Number.isNaN(+value) ? value : +value;
		name = name.replace(/^deliverables-/, '');
		this.setState({[name]: value});
	}

	setDescriptionValue ({ target }) {
		const { value } = target;
		this.setState({ description: value });
	}

	setNumberOfFiles (value) {
		this.setState({ numberOfFiles: value });
	}

	addDeliverable() {
		let { description, deliverableType, numberOfFiles } = this.state;

		this.props.addDeliverable({ description, deliverableType, numberOfFiles });
		this.setState({
			deliverableType: '',
			numberOfFiles: 0,
			description: '',
			valid: false
		});

		this.deliverablesTypeSelect.clear();
	}

	componentDidUpdate() {
		let { deliverableType, numberOfFiles, description, valid } = this.state;
		if (deliverableType.length && numberOfFiles > 0 && description.length && !valid) {
			this.setState({ valid: true });
		}

		if (this.props.deliverablesGroup.deliverables.length) {
			const root = ReactDOM.findDOMNode(this);
			this.deliverablesTimeSelect = wmSelect({ selector: '[name=deliverables-hoursToComplete]', root }, {
				onChange: (value) => {
					this.props.updateDeliverablesTime(value);
				}
			})[0].selectize;
		}
	}

	componentWillReceiveProps(nextProps) {
		if (nextProps.deliverablesGroup.hoursToComplete && this.deliverablesTimeSelect && (this.deliverablesTimeSelect.items[0].toString() !== nextProps.deliverablesGroup.hoursToComplete)) {
			// selectize needs to be updated with new props
			this.deliverablesTimeSelect.setValue(nextProps.deliverablesGroup.hoursToComplete.toString(), true);
		}
	}

	render () {
		return (
			<div>
				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="deliverables-instructions">Instructions</label>
					<div className="assignment-creation--field">
						<ReactQuill
							id="deliverables-instructions"
							theme="snow"
							modules={ quillModules }
							formats={ quillFormats }
							toolbar={ false }
							bounds={ '._quill' }
							value={ this.props.deliverablesGroup.instructions }
							onChange={ value => this.props.updateDeliverablesInstructions(value) }
						>
							<div
								key="editor"
								className="quill-contents border_solid_top"
								dangerouslySetInnerHTML={ { __html: this.props.deliverablesGroup.instructions } }
							/>
						</ReactQuill>
					</div>
				</div>

				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="deliverables-deliverable">Required Deliverables</label>
				</div>
				<div className="assignment-creation--container">
					<div className="assignment-creation--list">
						<select
							className="wm-select deliverables--type"
							placeholder="0"
							name="deliverables-deliverableType"
							id="deliverables-deliverableType"
						>
							<option></option>
							<option value="sign_off">Sign-Off Form</option>
							<option value="photos">Photos</option>
							<option value="other">Other</option>
						</select>
						<div className="deliverables--numberOfFiles">
							<div className="assignment-creation--field" >
								<WMCurrencyField
									style={ { width: '70px' } }
									name="deliverables-numberOfFiles"
									id="deliverables-numberOfFiles"
									amount={ this.state.numberOfFiles }
									exponent={ 3 }
									onChange={ (event, value) => this.setNumberOfFiles(value) }
								/>
							</div>
							<span style={ { margin: '0px 10px' } }>files</span>
						</div>
						<div className="deliverables--description">
						<input 
							type="text"
							name="deliverables-description"
							placeholder="Description"
							value={ (this.state.description) }
							onChange={ this.setDescriptionValue }
						/>
						<small className="-required">Short descriptions for each deliverable type are required.</small>
						</div>
						<WMRaisedButton
							disabled={ !this.state.valid }
							onClick={ this.addDeliverable.bind(this) }
							label="Add"
							primary
							style={{
								marginTop: '-6px'
							}}
						/>
					</div>
				</div>
				

				{ this.props.deliverablesGroup && this.props.deliverablesGroup.deliverables.length > 0 &&
					<div>
						<div className="assignment-creation--container">
							<div className="assignment-creation--list">
								{ this.props.deliverablesGroup.deliverables.map((deliverable, index) =>
									<AssignmentListItem
										key={ index }
										title={ `${deliverable.description}  |  Type: ${deliverable.type}  |  Number of files: ${deliverable.numberOfFiles}` }
										item={ deliverable }
										onRemoveToClick={ () => this.props.removeDeliverable(index) }
									/>
								)}
							</div>
						</div>

						<div className="assignment-creation--container">
							<label className="assignment-creation--label" htmlFor="deliverables-hoursToComplete">Deliverables Due Date</label>
							<div className="assignment-creation--field">
								<select
									className="wm-select deliverables--hoursToComplete"
									name="deliverables-hoursToComplete"
									defaultValue={ this.props.deliverablesGroup.hoursToComplete }
								>
									<option value="0">None</option>
									<option value="24">1 day</option>
									<option value="48">2 days</option>
									<option value="72">3 days</option>
									<option value="96">4 days</option>
									<option value="120">5 days</option>
									<option value="144">6 days</option>
									<option value="168">7 days</option>
								</select>
							</div>
						</div>
					</div>
				}
			</div>
		);
	}
}

DeliverableComponent.propTypes = {
	updateDeliverablesInstructions: PropTypes.func.isRequired,
	deliverablesGroup: PropTypes.shape({
		deliverables: PropTypes.array,
		hoursToComplete: PropTypes.number,
		id: PropTypes.string,
		instructions: PropTypes.string
	})
};

export default DeliverableComponent;
