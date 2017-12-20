import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { WMModal, WMRaisedButton } from '@workmarket/front-end-components';

class TimeUpModal extends Component {
	constructor (props) {
		super(props);
		this.state = {
			id: props.id,
			open: props.open
		};
	}

	dismiss () {
		this.setState({ open: false });
	}

	redirect () {
		window.open(`/lms/view/details/${this.state.id}`, '_self');
	}

	render () {
		return (
			<WMModal
				modal
				actions={
					<WMRaisedButton
						primary
						label="OK"
						onClick={
							() => {
								this.dismiss();
								this.redirect();
							}
						}
					/>
				}
				open={ this.state.open }
				onRequestClose={ this.redirect }
			>
				{ 'The time limit has been reached, the test is now over. Your score will be based on the responses you have submitted up until now.' }
			</WMModal>
		);
	}
}

TimeUpModal.propTypes = {
	id: PropTypes.number.isRequired,
	open: PropTypes.bool
};

TimeUpModal.defaultProps = {
	open: false
};

export default TimeUpModal;
