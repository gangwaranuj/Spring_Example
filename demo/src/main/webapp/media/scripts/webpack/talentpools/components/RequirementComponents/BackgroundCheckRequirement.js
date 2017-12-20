import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const BackgroundCheckRequirement = ({ applyRequirement }) => {
	return (
		<WMFormRow id="requirement__bgcheck">
			<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
				<WMRaisedButton
					secondary
					label="Add this requirement"
					style={ { margin: '1em 0 0 1em' } }
					onClick={ () => {
						const data = {
							name: 'Passed Sterling Background Check',
							$type: 'BackgroundCheckRequirement',
							$humanTypeName: 'Background Check'
						};
						applyRequirement(data);
					} }
				/>
			</div>
		</WMFormRow>
	);
};

const mapStateToProps = ({ requirementsData }) => ({
	requirementComponentData: requirementsData.toObject()
});

const mapDispatchToProps = (dispatch) => {
	return {
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BackgroundCheckRequirement);

BackgroundCheckRequirement.propTypes = {
	applyRequirement: PropTypes.func.isRequired
};
