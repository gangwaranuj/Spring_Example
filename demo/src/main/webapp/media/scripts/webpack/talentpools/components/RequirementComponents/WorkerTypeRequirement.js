import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import {
	WMFormRow,
	WMMenuItem,
	WMRaisedButton,
	WMSelectField
} from '@workmarket/front-end-components';
import * as actions from '../../actions';

const WorkerTypeRequirement = ({
	handleChange,
	applyRequirement,
	requirementComponentData
}) => {
	const {
		workerType,
		workerTypes
		} = requirementComponentData;
	const workerTypeList = workerTypes.map(workerTypeMap => (
		<WMMenuItem
			key={ workerTypeMap.id }
			value={ workerTypeMap.id }
			primaryText={ workerTypeMap.name }
		/>
	));
	return (
		<WMFormRow id="requirements-workerType" >
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<WMSelectField
					onChange={ (event, index, value) => handleChange(value) }
					fullWidth
					name="workerType"
					hintText="Select a worker type"
					value={ workerType }
				>
					{ workerTypeList }
				</WMSelectField>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !workerType }
						onClick={ () => {
							const data = {
								name: workerTypes.find(workerTypeItem =>
									(workerTypeItem.id === workerType)).name,
								$type: 'ResourceTypeRequirement',
								requirable: workerTypes.find(workerTypeItem =>
									(workerTypeItem.id === workerType)),
								$humanTypeName: 'Worker Type'
							};
							applyRequirement(data);
						} }
					/>
				</div>
			</div>
		</WMFormRow>
	);
};

const mapStateToProps = ({ requirementsData }) => ({
	requirementComponentData: requirementsData.toJS()
});

const mapDispatchToProps = (dispatch) => {
	return {
		handleChange: (value) => {
			dispatch(actions.changeRequirementField('workerType', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(WorkerTypeRequirement);

WorkerTypeRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		workerType: PropTypes.number,
		workerTypes: PropTypes.array.isRequired
	})
};
