import PropTypes from 'prop-types';
/* eslint-disable max-len */
import React from 'react';
import {
	WMMessageBanner,
	WMFlatButton,
	WMHeading,
	commonStyles
} from '@workmarket/front-end-components';

const styles = {
	link: {
		color: commonStyles.colors.baseColors.blue,
		cursor: 'pointer',
		textDecoration: 'none'
	}
};

const WMLockedMessaging = ({
	userHasLockedCompany,
	companyIsLocked,
	companyHasLockWarning,
	companyHasOverdueWarning,
	companyOverdueWarningDaysBetweenFromNow
}) => {
	const islockedAccount = companyIsLocked || userHasLockedCompany;
	return (
		<div>
			{ islockedAccount &&
				<WMMessageBanner
					hideDismiss
					status={ 'error' }
				>
					<WMHeading level="3">Important:</WMHeading>
					<p><strong>Your account is overdue by more than 3 days.</strong></p>
					<p>
						As a result, your account has been locked and your access to Work Market is limited until you pay your past due balances.
						Your account will not have access to send new assignments, withdraw funds, run reports, create talent pools, or create tests.
					</p>
					<p><strong>Note:</strong> Payment processing may take up to five minutes.</p>
					<a href="/payments/invoices/payables/past-due">
						<WMFlatButton
							secondary
							label="Pay Invoices"
							style={ { margin: '1em' } }
						/>
					</a>
					<a
						href="/payments/invoices/payables/past-due"
						target="_blank"
						rel="noopener noreferrer"
						style={ styles.link }
					>
						Help with a Locked Account
					</a>
				</WMMessageBanner>
			}

			{ companyHasLockWarning &&
				<WMMessageBanner
					status={ 'warning' }
				>
					<WMHeading level="3">Reminder:</WMHeading>
					<p>You currently have a one or more invoices coming due in the next 24 hours. Please pay your coming due invoices.</p>
					<a href="/payments/invoices/payables/upcoming-due">
						<WMFlatButton
							label="View Your Outstanding Invoices"
						/>
					</a>
				</WMMessageBanner>
			}

			{ companyHasOverdueWarning &&
				<WMMessageBanner
					status={ 'warning' }
				>
					<WMHeading level="3">Notice:</WMHeading>
					{
						(companyOverdueWarningDaysBetweenFromNow > 0) ?
							<p>Your account is currently past due by {companyOverdueWarningDaysBetweenFromNow} day(s). Your account will be locked passed 3 days. Please pay your balance as soon as possible.</p> :
							<p>Your account is currently past due. Your account will be locked passed 3 days. Please pay your balance as soon as possible.</p>
					}
					<a href="/payments/invoices/payables/past-due">
						<WMFlatButton
							label="View Your Outstanding Invoices"
						/>
					</a>
				</WMMessageBanner>
			}
		</div>
	);
};

export default WMLockedMessaging;

WMLockedMessaging.propTypes = {
	userHasLockedCompany: PropTypes.bool,
	companyIsLocked: PropTypes.bool,
	companyHasLockWarning: PropTypes.bool.isRequired,
	companyHasOverdueWarning: PropTypes.bool.isRequired,
	companyOverdueWarningDaysBetweenFromNow: PropTypes.oneOf([-1, 0, 1, 2])
};
