import fetch from 'isomorphic-fetch';
import _ from 'underscore';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import wmSelect from '../../funcs/wmSelect';
import AssignmentListItem from '../../assignments/components/AssignmentListItem';

class AddCustomFieldsComponent extends Component {
	componentDidMount () {
		const root = this.node;
		const selector = '[name="custom-fields-select"]';
		this.customFieldsSelector = wmSelect({ root, selector }, {
			valueField: 'id',
			searchField: ['name'],
			sortField: 'name',
			labelField: 'name',
			placeholder: 'Search or select \u2026',
			preload: true,
			openOnFocus: true,
			hideSelected: true,
			maxItems: null,
			onChange: (value) => {
				if (value) {
					// update global state
					this.props.fetchCustomFields(this.state.uniquefields.find((group) => {
						return group.id === value[0];
					}));
					// remove the option from the selector
					this.customFieldsSelector.removeOption(value);
					this.customFieldsSelector.clear(true);
				}
			},
			load: (query, callback) => {
				fetch('/employer/v2/custom_field_groups?fields=name,required,id', {
					credentials: 'same-origin'
				})
					.then(res => res.json())
					.then((res) => {
						// load all custom field groups associated with the user
						const requiredCustomFieldGroups = [];
						const nonRequiredCustomFieldGroups = [];
						const allCustomFieldGroups = res.results;
						const uniquefields = _.uniq(allCustomFieldGroups, 'id');

						uniquefields.forEach((customFieldGroup) => {
							if (customFieldGroup.required === 'true') {
								requiredCustomFieldGroups.push(customFieldGroup);
							} else {
								nonRequiredCustomFieldGroups.push(customFieldGroup);
							}
						});

						// non-required fields are loaded into component state
						this.setState({ nonRequiredCustomFieldGroups, uniquefields });

						// required fields need to be immediately loaded into global state
						requiredCustomFieldGroups.forEach(customFieldGroup =>
							this.props.fetchCustomFields(customFieldGroup)
						);

						callback(nonRequiredCustomFieldGroups);
					});
			}
		})[0].selectize;

		this.customFieldInput = wmSelect({
			root,
			selector: 'select.custom-fields-select'
		}, {
			onChange: (value) => {
				// TODO: check into react replacement to avoid having to do the following
				const select = this.customFieldInput;
				const isEmployerField = select.attr('data-fieldtype') === 'employerField';

				if (!isEmployerField) {
					return;
				}

				const fieldId = parseFloat(select.attr('data-fieldid'));
				const groupId = parseFloat(select.attr('data-groupid'));

				this.setFieldValue(value, fieldId, groupId);
			}
		});

		this.workerCustomFieldInput = wmSelect({
			root,
			selector: 'select.worker-custom-fields-select'
		}, {
			onChange: (value) => {
				// TODO: check into react replacement to avoid having to do the following
				const select = this.workerCustomFieldInput;
				const isWorkerField = select.attr('data-fieldtype') === 'workerField';

				if (!isWorkerField) {
					return;
				}

				const fieldId = parseFloat(select.attr('data-fieldid'));
				const groupId = parseFloat(select.attr('data-groupid'));

				this.setFieldValue(value, fieldId, groupId);
			}
		});
	}

	componentWillReceiveProps (nextProps) {
		// if fields have default value, push that into the value prop so it will save
		const fieldGroups = nextProps.customFieldGroups;

		fieldGroups.forEach((fieldGroup) => {
			fieldGroup.fields.forEach((field) => {
				if (field.defaultValue && field.defaultValue.length && !field.value) {
					this.props.updateCustomFields({
						value: field.defaultValue,
						fieldId: field.id,
						groupId: fieldGroup.id
					});
				}
			});
		});
	}

	componentDidUpdate () {
		const root = this.node;

		this.customFieldInput = wmSelect({
			root,
			selector: 'select.custom-fields-select'
		}, {
			onChange: (value) => {
				// TODO: check into react replacement to avoid having to do the following
				const select = this.customFieldInput;
				const isEmployerField = select.attr('data-fieldtype') === 'employerField';

				if (!isEmployerField) {
					return;
				}

				const fieldId = parseFloat(select.attr('data-fieldid'));
				const groupId = parseFloat(select.attr('data-groupid'));

				this.setFieldValue(value, fieldId, groupId);
			}
		});

		this.workerCustomFieldInput = wmSelect({
			root,
			selector: 'select.worker-custom-fields-select'
		}, {
			onChange: (value) => {
				// TODO: check into react replacement to avoid having to do the following
				const select = this.workerCustomFieldInput;
				const isWorkerField = select.attr('data-fieldtype') === 'workerField';

				if (!isWorkerField) {
					return;
				}

				const fieldId = parseFloat(select.attr('data-fieldid'));
				const groupId = parseFloat(select.attr('data-groupid'));

				this.setFieldValue(value, fieldId, groupId);
			}
		});
	}

	setFieldValue (value, fieldId, groupId) {
		this.props.updateCustomFields({ value, fieldId, groupId });
	}

	removeCustomFieldGroup (customFieldGroup) {
		this.props.removeCustomFieldGroup(customFieldGroup.id);
		this.customFieldsSelector.addOption({
			id: customFieldGroup.id,
			name: customFieldGroup.name
		});
	}

	render () {
		const { customFieldGroups } = this.props;
		const workerFields = [];
		const employerFields = [];
		const uniquefields = _.uniq(customFieldGroups, 'id');

		uniquefields.forEach((customFieldGroup) => {
			customFieldGroup.fields.forEach((customField) => {
				const newCustomField = Object.assign({}, customField, { groupId: customFieldGroup.id });
				if (customField.type === 'owner') {
					employerFields.push(newCustomField);
				} else {
					workerFields.push(newCustomField);
				}
			});
		});

		return (
			<div
				ref={ node => (this.node = node) }
			>
				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="custom-fields-select">Field Sets</label>
					<div className="assignment-creation--field">
						<select
							id="custom-fields-select"
							className="assignment-creation--select wm-select"
							name="custom-fields-select"
						/>
					</div>
				</div>

				<div className="clear">
					<div className="assignment-creation--list">
						{
							uniquefields.map((customFieldGroup, index) => {
								if (uniquefields.length) {
									return (
										<AssignmentListItem
											required={ (customFieldGroup.required === 'true' || customFieldGroup.required === true) || false }
											key={ index }
											item={ customFieldGroup }
											onRemoveToClick={ () => this.removeCustomFieldGroup(customFieldGroup) }
										/>
									);
								}
								return '';
							})
						}
					</div>
				</div>

				{
					employerFields.length > 0 &&
						<div className="assignment-creation--subsection clear">
							<div className="assignment-creation--subsection-header">My Fields</div>
							{
								employerFields.map((employerField) => {
									const optionValues = employerField.defaultValue;
									const options = optionValues.split(',').map(option => option.trim());
									const isDropdown = Array.isArray(options) && options.length > 1;
									if (isDropdown) {
										return [
											<div className="assignment-creation--container">
												<label
													className={
														employerField.required === 'true' || employerField.required === true
															? 'assignment-creation--label -required'
															: 'assignment-creation--label'
													}
													htmlFor={ `${employerField.id}-field-select` }
												>
													{ employerField.name }
												</label>
												<div className="assignment-creation--field">
													<select
														onChange={ (event) => {
															this.setFieldValue(
																event.target.value,
																employerField.id,
																employerField.groupId
															);
														} }
														style={ { width: '100%' } }
														value={ employerField.value }
													>
														<option value="">Select</option>
														{
															options.map((option, index) =>
																<option
																	key={ index }
																	value={ option }
																>
																	{ option }
																</option>
															)
														}
													</select>
												</div>
											</div>
										];
									}

									const fieldValue = employerField.value && employerField.value.length
										? employerField.value
										: employerField.defaultValue;

									return [
										<div className="assignment-creation--container">
											<label
												className={
													employerField.required === 'true' || employerField.required === true
														? 'assignment-creation--label -required'
														: 'assignment-creation--label'
												}
												htmlFor={ `${employerField.id}-field-select` }
											>
												{ employerField.name }
											</label>
											<div className="assignment-creation--field">
												<input
													id={ `employer-field-${employerField.id}` }
													type="text"
													key={ employerField.id }
													defaultValue={ fieldValue }
													value={ fieldValue }
													onChange={
														({ target: { value } }) =>
															this.setFieldValue(value, employerField.id, employerField.groupId)
													}
												/>
											</div>
										</div>
									];
								})
							}
						</div>
				}
				{
					workerFields.length > 0 &&
						<div className="assignment-creation--subsection">
							<div className="assignment-creation--subsection-header">Worker Fields</div>
							{
								workerFields.map((workerField) => {
									const optionValues = workerField.defaultValue;
									const options = optionValues.split(',').map(option => option.trim());
									const isDropdown = Array.isArray(options) && options.length > 1;

									if (isDropdown) {
										return [
											<div className="assignment-creation--container">
												<label
													className={
														workerField.required === 'true' || workerField.required === true
															? 'assignment-creation--label -required'
															: 'assignment-creation--label'
													}
													htmlFor={ `${workerField.id}-field-select` }
												>
													{ workerField.name }
												</label>
												<div className="assignment-creation--field">
													<select
														onChange={ (event) => {
															this.setFieldValue(
																event.target.value,
																workerField.id,
																workerField.groupId
															);
														} }
														style={ { width: '100%' } }
														value={ workerField.value }
													>
														<option value="">Select</option>
														{
															options.map((option, index) =>
																<option
																	key={ index }
																	value={ option }
																>
																	{ option }
																</option>
															)
														}
													</select>
												</div>
											</div>
										];
									}

									const fieldValue = workerField.value && workerField.value.length
										? workerField.value
										: workerField.defaultValue;

									return [
										<div className="assignment-creation--container">
											<label
												className={
													workerField.required === 'true' || workerField.required === true
														? 'assignment-creation--label -required'
														: 'assignment-creation--label'
												}
												htmlFor={ `${workerField.id}-field-select` }
											>
												{ workerField.name }
											</label>
											<div className="assignment-creation--field">
												<input
													id={ `worker-field-${workerField.id}` }
													type="text"
													key={ workerField.id }
													defaultValue={ fieldValue }
													onChange={ ({ target: { value } }) =>
														this.setFieldValue(value, workerField.id, workerField.groupId)
													}
												/>
											</div>
										</div>
									];
								})
							}
						</div>
				}
			</div>
		);
	}
}

AddCustomFieldsComponent.propTypes = {
	customFieldGroups: PropTypes.arrayOf(PropTypes.shape({
		id: PropTypes.oneOfType([
			PropTypes.string,
			PropTypes.number
		]).isRequired,
		name: PropTypes.string.isRequired,
		required: PropTypes.string.isRequired,
		position: PropTypes.number.isRequired,
		workCustomFields: PropTypes.arrayOf(PropTypes.shape({
			id: PropTypes.number.isRequired,
			name: PropTypes.string.isRequired,
			value: PropTypes.string,
			defaultValue: PropTypes.string,
			required: PropTypes.bool.isRequired,
			type: PropTypes.string.isRequired
		}))
	})),
	fetchCustomFields: PropTypes.func.isRequired,
	updateCustomFields: PropTypes.func.isRequired,
	removeCustomFieldGroup: PropTypes.func.isRequired
};

export default AddCustomFieldsComponent;
