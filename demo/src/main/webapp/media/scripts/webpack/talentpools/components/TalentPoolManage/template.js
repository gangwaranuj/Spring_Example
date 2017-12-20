import PropTypes from 'prop-types';
import React from 'react';
import { Map } from 'immutable';
import {
	commonStyles,
	WMSubHeader,
	WMTabs,
	WMCircularProgress,
	WMFontIcon
} from '@workmarket/front-end-components';
import TalentPoolForm from '../TalentPoolForm/index';
import TalentPoolMembers from '../TalentPoolMembers/index';
import TalentPoolMessages from '../TalentPoolMessages/index';
import TalentPoolRequirements from '../TalentPoolRequirements/index';
import ManageStyles from '../../styles/manage';

const Colors = commonStyles.colors;

const TalentPoolManage = ({
	onChangeTab,
	onCloseDrawer,
	talentPoolData,
	switchToInvite,
	enableOrgStructures
}) => {
	const getIcon = iconName =>
		<WMFontIcon
			className="material-icons"
			id="tab-icon"
		>
			{ iconName }
		</WMFontIcon>;

	const closeButton = (
		<WMFontIcon
			className="material-icons"
			id="talent-pool-drawer-close"
			style={ { margin: '0.7em' } }
			color={ Colors.baseColors.white }
		>
			close
		</WMFontIcon>
	);

	const drawerTabs =
		[{
			label: 'Details',
			value: 'details',
			icon: getIcon('mode_edit')
		}, {
			label: 'Requirements',
			value: 'requirements',
			icon: getIcon('list')
		}, {
			label: 'Members',
			value: 'members',
			icon: getIcon('people')
		}, {
			label: 'Messages',
			value: 'messages',
			icon: getIcon('message')
		}];
	if (talentPoolData.get('openMembership') === 'false') {
		drawerTabs.splice(1, 1);
		drawerTabs.splice(2, 1);
		if (talentPoolData.get('readOnly')) {
			drawerTabs.splice(1, 1);
		}
	} else if (talentPoolData.get('readOnly')) {
		drawerTabs.splice(2, 1);
	}
	const showRefreshIndicator =
		talentPoolData.get('isSaving') === true ||
		talentPoolData.get('isFetchingSearchResults') === true ||
		talentPoolData.get('isSavingRequirements') === true ||
		talentPoolData.get('isFetchingMessages') === true;
	return (
		<div>
			<div
				style={ ManageStyles.drawerHeader }
			>
				<WMSubHeader
					inset={ false }
				>
					<h2
						style={ {
							float: 'left',
							color: Colors.baseColors.white,
							width: '85%',
							whiteSpace: 'nowrap',
							overflow: 'hidden',
							textOverflow: 'ellipsis' } }
					>
						{ talentPoolData.get('inviting') &&
							<div
								style={ { display: 'inline-block', cursor: 'pointer' } }
								onClick={ () => switchToInvite(!talentPoolData.get('inviting'), talentPoolData.get('id')) }
							>
								<WMFontIcon
									id="talent-pool-invite-back"
									className="material-icons"
									style={ { position: 'relative', top: '5px', marginRight: '0.5em' } }
									color={ Colors.baseColors.white }
								>
									arrow_back
								</WMFontIcon>
							</div>
						}
						{ talentPoolData.get('name') }
					</h2>
					{ showRefreshIndicator &&
						<WMCircularProgress
							style={ { position: 'relative', display: 'inline-block', marginTop: '.75em', paddingTop: '.75em' } }
							thickness={ 5 }
							color="white"
						/>
					}
					<div
						style={ { float: 'right', cursor: 'pointer', paddingTop: '0.5em' } }
						onClick={ () => onCloseDrawer() }
					>
						{ closeButton }
					</div>
					<div style={ { clear: 'both' } } />
				</WMSubHeader>
				{ !talentPoolData.get('inviting') &&
					<WMTabs
						tabs={ drawerTabs }
						value={ talentPoolData.get('activeTab') }
						onChange={ value => onChangeTab(value, talentPoolData.get('id')) }
					/>
				}
			</div>
			<div style={ ManageStyles.drawerBody }>
				{ talentPoolData.get('activeTab') === 'details' ? <TalentPoolForm enableOrgStructures={ enableOrgStructures } /> : null }
				{ talentPoolData.get('activeTab') === 'requirements' ? <TalentPoolRequirements /> : null }
				{ talentPoolData.get('activeTab') === 'members' ? <TalentPoolMembers /> : null }
				{ talentPoolData.get('activeTab') === 'messages' ? <TalentPoolMessages /> : null }
			</div>
		</div>
	);
};

export default TalentPoolManage;

TalentPoolManage.propTypes = {
	onChangeTab: PropTypes.func.isRequired,
	onCloseDrawer: PropTypes.func.isRequired,
	talentPoolData: PropTypes.instanceOf(Map),
	switchToInvite: PropTypes.func.isRequired,
	enableOrgStructures: PropTypes.bool,
};
