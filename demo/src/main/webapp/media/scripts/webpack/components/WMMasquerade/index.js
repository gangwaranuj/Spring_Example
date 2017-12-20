import PropTypes from 'prop-types';
import React from 'react';
import {
	WMMessageBanner,
	WMFlatButton
} from '@workmarket/front-end-components';
import Application from '../../core';

const WMMasquerade = ({
		isMasquerading
}) => {
	const { userName } = Application.UserInfo;
	return (
		<div>
			{ isMasquerading &&
				<WMMessageBanner
					hideDismiss
					status={ 'warning' }
				>
					You are masquerading as {userName}
					<a href="/admin/usermanagement/masquerade/stop">
						<WMFlatButton
							secondary
							label="Stop Masquerading"
							style={ { margin: '1em' } }
						/>
					</a>
				</WMMessageBanner>
			}
		</div>
	);
};

WMMasquerade.propTypes = {
	isMasquerading: PropTypes.bool.isRequired
};

export default WMMasquerade;
