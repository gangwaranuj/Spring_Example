import fetch from 'isomorphic-fetch';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSelectField, WMMenuItem } from '@workmarket/front-end-components';
import * as actions from '../../actions';

class AssessmentsRequirement extends React.Component {
	constructor (props) {
		super(props);
		this.state = {
			assessmentList: []
		};
	}

	componentDidMount () {
		this.fetchTests();
	}

	fetchTests = (urlRoot = '') => {
		return fetch(`${urlRoot}/tests`, { credentials: 'same-origin' })
			.then(res => res.json())
			.then(res => this.setState({ assessmentList: res }));
	}

	render () {
		const {
			handleChange,
			applyRequirement,
			assessmentId } = this.props;
		const { assessmentList } = this.state;

		const renderAssessments = assessmentList.map(item => (
			<WMMenuItem
				key={ item.id }
				value={ item.id }
				primaryText={ item.name }
			/>
		));

		return (
			<div>
				<WMFormRow
					data-component-identifier="requirements_Row"
					id="requirements-assessment"
					baseStyle={ { margin: '1em 0' } }
				>
					<WMSelectField
						data-component-identifier="requirements_assessmentSelect"
						onChange={ (event, index, value) => handleChange(value) }
						fullWidth
						name="tests"
						hintText="Select Tests"
						value={ assessmentId }
					>
						{ renderAssessments }
					</WMSelectField>
				</WMFormRow>
				<WMFormRow
					data-component-identifier="requirements_buttonRow"
					id="requirements-buttons"
					baseStyle={ { margin: '1em 0' } }
				>
					<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
						<WMRaisedButton
							secondary
							data-component-identifier="requirements_buttons"
							label="Add this requirement"
							disabled={ !assessmentId }
							onClick={ () => {
								const data = {
									$type: 'TestRequirement',
									$humanTypeName: 'Test',
									notifyOnExpiry: false,
									removeMembershipOnExpiry: false,
									requirable: assessmentList.find(item => (item.id === assessmentId))
								};
								applyRequirement(data);
							} }
						/>
					</div>
				</WMFormRow>
			</div>
		);
	}
}

const mapStateToProps = ({ requirementsData }) => ({
	assessmentId: requirementsData.toJS().assessmentId
});

const mapDispatchToProps = (dispatch) => {
	return {
		handleChange: (value) => {
			dispatch(actions.changeRequirementField('assessmentId', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data, true));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(AssessmentsRequirement);

AssessmentsRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	assessmentId: PropTypes.number
};
