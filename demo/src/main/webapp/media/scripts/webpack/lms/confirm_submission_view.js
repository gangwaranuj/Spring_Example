import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { WMModal, WMRaisedButton } from '@workmarket/front-end-components';

class ConfirmSubmissionView extends Component {
	constructor (props) {
		super(props);
		this.state = {
			open: props.open
		};
	}

	dismiss () {
		this.setState({ open: false });
	}

	componentWillReceiveProps(newProps) {
		this.setState({ open: newProps.open});
	}

	render () {
		return (
			<WMModal
				modal
				actions={ (
					<div>
						<WMRaisedButton
							primary
							label="YES"
							onClick={
								(e) => {
									this.props.onConfirm(e);
									this.dismiss();
								}
							}
							style={ { marginRight: '10px' } }
						/>
						<WMRaisedButton
							secondary
							label="NO"
							onClick={
								(e) => {
									this.props.onCancel(e);
									this.dismiss();
								}
							}
						/>
					</div>
					)
				}
				open={ this.state.open }
			>
				{ 'Are you sure you want to submit the test?' }
			</WMModal>
		);
	}
}

ConfirmSubmissionView.propTypes = {
	open: PropTypes.bool,
	onConfirm: PropTypes.func.isRequired,
	onCancel: PropTypes.func.isRequired
};

ConfirmSubmissionView.defaultProps = {
	open: false,
	onConfirm: () => {},
	onCancel: () => {}
};

export default ConfirmSubmissionView;