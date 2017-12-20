import PropTypes from 'prop-types';
import React from 'react';
import {
	WMMessageBanner,
	WMFlatButton
} from '@workmarket/front-end-components';

export const styles = {
	positioning: {
		position: 'absolute',
		marginTop: '-1em',
		width: '100%',
		zIndex: '2'
	}
};

const WMWorkerBanner = ({
		invitationsCount
}) => {
	return (
		<div
			style={ styles.positioning }
		>
			{ invitationsCount > 0 &&
				<WMMessageBanner
					status={ 'notice' }
				>
					You have {invitationsCount} pending talent pool invitations.
					<WMFlatButton
						secondary
						href="/groups/invitations"
						label="View Invitations"
						style={ { margin: '1em' } }
					/>
				</WMMessageBanner>
			}
		</div>
	);
};

WMWorkerBanner.propTypes = {
	invitationsCount: PropTypes.number.isRequired
};

export default WMWorkerBanner;
