import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const ProfileVideoRequirement = ({ applyRequirement }) => {
	return (
		<WMFormRow id="requirement__profilevideo" >
			<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
				<WMRaisedButton
					secondary
					label="Add this requirement"
					style={ { margin: '1em 0 0 1em' } }
					onClick={ () => {
						const data = {
							$type: 'ProfileVideoRequirement',
							$humanTypeName: 'Profile Video',
							name: 'At least one profile video required'
						};
						applyRequirement(data);
					} }
				/>
			</div>
		</WMFormRow>
	);
};

const mapStateToProps = state => ({
	requirementComponentData: state.requirementsData.toObject()
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
)(ProfileVideoRequirement);

ProfileVideoRequirement.propTypes = {
	applyRequirement: PropTypes.func.isRequired
};
