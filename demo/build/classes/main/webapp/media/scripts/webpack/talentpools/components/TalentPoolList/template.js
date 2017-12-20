import React, { PureComponent } from 'react';
import { Link } from 'react-router';
import { Map } from 'immutable';
import PropTypes from 'prop-types';
import Radium from 'radium';
import {
	commonStyles,
	WMDrawer,
	WMPaper,
	WMRaisedButton,
	WMSubHeader,
	WMToggle,
	WMTable,
	WMTableBody,
	WMTableHeader,
	WMTableHeaderCell,
	WMTableRow,
	WMTableCell,
	WMFontIcon
} from '@workmarket/front-end-components';
import * as interactionModes from '../../constants/interactionModes';
import TalentPoolCreateForm from '../TalentPoolCreateForm/index';
import TalentPoolManage from '../TalentPoolManage/index';
import SortStyles from '../../styles/sortingStyles';
import Styles from '../../styles/common';

class TalentPoolList extends PureComponent {
	shouldComponentUpdate (nextProps) {
		if (this.props.talentPoolData.get('talentPools') !== nextProps.talentPoolData.get('talentPools')) {
			return true;
		}
		if (this.props.talentPoolData.get('drawerIsOpen') !== nextProps.talentPoolData.get('drawerIsOpen')) {
			return true;
		}
		if (this.props.talentPoolData.get('readOnly') !== nextProps.talentPoolData.get('readOnly')) {
			return true;
		}
		if (this.props.talentPoolData.get('isFetchingTalentPools') !== nextProps.talentPoolData.get('isFetchingTalentPools')) {
			return true;
		}
		if (this.props.talentPoolData.get('talentPoolSortField') !== nextProps.talentPoolData.get('talentPoolSortField')) {
			return true;
		}
		if (this.props.talentPoolData.get('talentPoolSortDirection') !== nextProps.talentPoolData.get('talentPoolSortDirection')) {
			return true;
		}
		if (this.props.talentPoolData.get('interactionMode') !== nextProps.talentPoolData.get('interactionMode')) {
			return true;
		}
		if (this.props.talentPoolData.get('enableOrgStructures') !== nextProps.talentPoolData.get('enableOrgStructures')) {
			return true;
		}
		return false;
	}
	render () {
		const {
			onManageGroup,
			onOpenProfileModal,
			newTalentPool,
			talentPoolData,
			onToggleActive,
			onSort,
			onCloseDrawer,
			enableOrgStructures
		} = this.props;

		const getOpenMembershipLabel = (isOpenMembership, isSearchable) => {
			if (isOpenMembership && isSearchable) {
				return 'Public';
			} else if (isOpenMembership) {
				return 'Public (unlisted)';
			}
			return 'Private';
		};

		let list = [];
		list = talentPoolData.get('talentPools').map((talentPool, index) => (
			<WMTableRow key={ index }>
				<WMTableCell>
					<Link to={ `groups/${talentPool.get('id')}` } activeStyle={ Styles.activeLink } onClick={ () => onManageGroup(talentPool, 'all') } style={ Styles.link }>
						<WMFontIcon
							className="material-icons"
							id="edit-talent-pool"
							style={ Styles.editIcon }
						>
							mode_edit
						</WMFontIcon>
						{ talentPool.get('name') }
					</Link>
				</WMTableCell>
				{ enableOrgStructures &&
				<WMTableCell>
					{
						talentPool.get('org_units')
							.toArray()
							.map(orgUnit => orgUnit.get('name'))
							.sort()
							.join(', ')
					}
				</WMTableCell>
				}
				<WMTableCell>
					{ getOpenMembershipLabel(talentPool.get('open_membership'), talentPool.get('searchable')) }
				</WMTableCell>
				<WMTableCell>
					<a onClick={ () => onOpenProfileModal(talentPool.get('owner_user_number')) } style={ Styles.link }>
						{ talentPool.get('owner_full_name') }
					</a>
				</WMTableCell>
				<WMTableCell>
					{ !talentPoolData.get('readOnly') ?
						<Link to={ `groups/${talentPool.get('id')}` } activeStyle={ Styles.activeLink } onClick={ () => onManageGroup(talentPool, 'member') } style={ Styles.link }>
							{ talentPool.get('count') }
						</Link> :
						talentPool.get('count')
					}
				</WMTableCell>
				<WMTableCell>
					{ talentPool.get('open_membership') && (
					<div>
						{ talentPoolData.get('readOnly') ? (
							<div>
								{ talentPool.get('pendingCount') }
							</div>
						) : (
							<Link to={ `groups/${talentPool.get('id')}` } activeStyle={ Styles.activeLink } onClick={ () => onManageGroup(talentPool, 'pending') } style={ Styles.link }>
								{ talentPool.get('pendingCount') }
							</Link>
						) }
					</div>
					) }
				</WMTableCell>
				<WMTableCell>
					{ talentPool.get('open_membership') && (
					<div>
						{ talentPoolData.get('readOnly') ? (
							<div>
								{ talentPool.get('invitedCount') }
							</div>
						) : (
							<Link to={ `groups/${talentPool.get('id')}` } activeStyle={ Styles.activeLink } onClick={ () => onManageGroup(talentPool, 'invited') } style={ Styles.link }>
								{ talentPool.get('invitedCount') }
							</Link>
						) }
					</div>
					) }
				</WMTableCell>

				<WMTableCell>
					{ talentPoolData.get('readOnly') && talentPool.get('isActive') &&
						<WMFontIcon
							className="material-icons"
							id="sort-down"
							color={ commonStyles.colors.baseColors.green }
							style={ SortStyles.downArrow }
						>
							check_circle
						</WMFontIcon>
					}
					{ !talentPoolData.get('readOnly') && !talentPool.get('auto_generated') &&
						<a onClick={ () => onToggleActive(talentPool.get('id'), talentPool.get('name'), talentPool.get('isActive')) }>
							<WMToggle
								className="material-icons"
								toggled={ talentPool.get('isActive') }
							/>
						</a>
					}
				</WMTableCell>
			</WMTableRow>
		));


		const loadingText = <div style={ { width: '200px' } }>Loading your talent pools...</div>;
		const emptyState =
			(<WMTableRow>
				<WMTableCell id="emptyTalentPools">
					<a
						onClick={ () => this.props.newTalentPool() }
						style={ { cursor: 'pointer' } }
					>
						{ talentPoolData.get('isFetchingTalentPools') ? loadingText : "You don't have any talent pools. Create one now!" }
					</a>
				</WMTableCell>
				{ enableOrgStructures && <WMTableCell>&nbsp;</WMTableCell> }
				<WMTableCell>&nbsp;</WMTableCell>
				<WMTableCell>&nbsp;</WMTableCell>
				<WMTableCell>&nbsp;</WMTableCell>
				<WMTableCell>&nbsp;</WMTableCell>
				<WMTableCell>&nbsp;</WMTableCell>
			</WMTableRow>);

		return (
			<div style={ Styles.container }>
				<WMPaper>
					<WMSubHeader>
						<span style={ Styles.listHeader }>
							Talent Pools
						</span>
						{ !talentPoolData.get('readOnly') && !talentPoolData.get('isFetchingTalentPools') &&
							<WMRaisedButton
								secondary
								style={ Styles.newButton }
								label="Create Talent Pool"
								onClick={ () => newTalentPool() }
							/>
						}
					</WMSubHeader>
					<WMTable
						striped={ list.size > 0 }
						hideDisabledCheckboxes
					>
						<WMTableHeader>
							<WMTableHeaderCell data-component-identifier="talentpools_headerCell_name">
								<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'name', talentPoolData.get('talentPoolSortDirection')) }>Name</a>
								{ talentPoolData.get('talentPoolSortField') === 'name' &&
									<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'name', talentPoolData.get('talentPoolSortDirection'), false, true) }>
										<WMFontIcon
											className="material-icons"
											id="sort-down"
											color={ talentPoolData.get('talentPoolSortDirection') === 'desc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.downArrow }
										>
											arrow_downward
										</WMFontIcon>
										<WMFontIcon
											className="material-icons"
											id="sort-up"
											color={ talentPoolData.get('talentPoolSortDirection') === 'asc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.upArrow }
										>
										arrow_upward
										</WMFontIcon>
									</a>
								}
							</WMTableHeaderCell>
							{ enableOrgStructures &&
							<WMTableHeaderCell data-component-identifier="talentpools_headerCell_orgUnits">
								<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'org_unit_names', talentPoolData.get('talentPoolSortDirection')) }>Org Unit</a>
								{ talentPoolData.get('talentPoolSortField') === 'org_unit_names' &&
									<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'org_unit_names', talentPoolData.get('talentPoolSortDirection'), false, true) }>
										<WMFontIcon
											className="material-icons"
											id="sort-down"
											color={ talentPoolData.get('talentPoolSortDirection') === 'desc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.downArrow }
										>
											arrow_downward
										</WMFontIcon>
										<WMFontIcon
											className="material-icons"
											id="sort-up"
											color={ talentPoolData.get('talentPoolSortDirection') === 'asc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.upArrow }
										>
											arrow_upward
										</WMFontIcon>
									</a>
								}
							</WMTableHeaderCell>
							}
							<WMTableHeaderCell data-component-identifier="talentpools_headerCell_type">
								<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'open_membership', talentPoolData.get('talentPoolSortDirection')) }>Type</a>
								{ talentPoolData.get('talentPoolSortField') === 'open_membership' &&
									<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'open_membership', talentPoolData.get('talentPoolSortDirection'), false, true) }>
										<WMFontIcon
											className="material-icons"
											id="sort-down"
											color={ talentPoolData.get('talentPoolSortDirection') === 'desc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.downArrow }
										>
											arrow_downward
										</WMFontIcon>
										<WMFontIcon
											className="material-icons"
											id="sort-up"
											color={ talentPoolData.get('talentPoolSortDirection') === 'asc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.upArrow }
										>
										arrow_upward
										</WMFontIcon>
									</a>
								}
							</WMTableHeaderCell>
							<WMTableHeaderCell data-component-identifier="talentpools_headerCell_owner">
								<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'owner_full_name', talentPoolData.get('talentPoolSortDirection')) }>Owner</a>
								{ talentPoolData.get('talentPoolSortField') === 'owner_full_name' &&
									<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'owner_full_name', talentPoolData.get('talentPoolSortDirection'), false, true) }>
										<WMFontIcon
											className="material-icons"
											id="sort-down"
											color={ talentPoolData.get('talentPoolSortDirection') === 'desc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.downArrow }
										>
											arrow_downward
										</WMFontIcon>
										<WMFontIcon
											className="material-icons"
											id="sort-up"
											color={ talentPoolData.get('talentPoolSortDirection') === 'asc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.upArrow }
										>
										arrow_upward
										</WMFontIcon>
									</a>
								}
							</WMTableHeaderCell>
							<WMTableHeaderCell data-component-identifier="talentpools_headerCell_members">
								<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'count', talentPoolData.get('talentPoolSortDirection'), true) }>Members</a>
								{ talentPoolData.get('talentPoolSortField') === 'count' &&
									<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'count', talentPoolData.get('talentPoolSortDirection'), true, true) }>
										<WMFontIcon
											className="material-icons"
											id="sort-down"
											color={ talentPoolData.get('talentPoolSortDirection') === 'desc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.downArrow }
										>
											arrow_downward
										</WMFontIcon>
										<WMFontIcon
											className="material-icons"
											id="sort-up"
											color={ talentPoolData.get('talentPoolSortDirection') === 'asc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.upArrow }
										>
										arrow_upward
										</WMFontIcon>
									</a>
								}
							</WMTableHeaderCell>
							<WMTableHeaderCell data-component-identifier="talentpools_headerCell_applied">
								<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'pendingCount', talentPoolData.get('talentPoolSortDirection'), true) }>Applied</a>
								{ talentPoolData.get('talentPoolSortField') === 'pendingCount' &&
									<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'pendingCount', talentPoolData.get('talentPoolSortDirection'), true, true) }>
										<WMFontIcon
											className="material-icons"
											id="sort-down"
											color={ talentPoolData.get('talentPoolSortDirection') === 'desc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.downArrow }
										>
											arrow_downward
										</WMFontIcon>
										<WMFontIcon
											className="material-icons"
											id="sort-up"
											color={ talentPoolData.get('talentPoolSortDirection') === 'asc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.upArrow }
										>
										arrow_upward
										</WMFontIcon>
									</a>
								}
							</WMTableHeaderCell>
							<WMTableHeaderCell data-component-identifier="talentpools_headerCell_invited">
								<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'invitedCount', talentPoolData.get('talentPoolSortDirection'), true) }>Invited</a>
								{ talentPoolData.get('talentPoolSortField') === 'invitedCount' &&
									<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'invitedCount', talentPoolData.get('talentPoolSortDirection'), true, true) }>
										<WMFontIcon
											className="material-icons"
											id="sort-down"
											color={ talentPoolData.get('talentPoolSortDirection') === 'desc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.downArrow }
										>
										arrow_downward
										</WMFontIcon>
										<WMFontIcon
											className="material-icons"
											id="sort-up"
											color={ talentPoolData.get('talentPoolSortDirection') === 'asc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.upArrow }
										>
										arrow_upward
										</WMFontIcon>
									</a>
								}
							</WMTableHeaderCell>


							<WMTableHeaderCell data-component-identifier="talentpools_headerCell_active">
								<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'isActive', talentPoolData.get('talentPoolSortDirection')) }>Active</a>
								{ talentPoolData.get('talentPoolSortField') === 'isActive' &&
									<a onClick={ () => onSort(talentPoolData.get('talentPools'), 'isActive', talentPoolData.get('talentPoolSortDirection'), false, true) }>
										<WMFontIcon
											className="material-icons"
											id="sort-down"
											color={ talentPoolData.get('talentPoolSortDirection') === 'desc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.downArrow }
										>
											arrow_downward
										</WMFontIcon>
										<WMFontIcon
											className="material-icons"
											id="sort-up"
											color={ talentPoolData.get('talentPoolSortDirection') === 'asc' ? commonStyles.colors.baseColors.darkGrey : commonStyles.colors.baseColors.lightGrey }
											style={ SortStyles.upArrow }
										>
										arrow_upward
										</WMFontIcon>
									</a>
								}
							</WMTableHeaderCell>
						</WMTableHeader>
						<WMTableBody>
							{ list.size > 0 ? list.toJS() : emptyState }
						</WMTableBody>
					</WMTable>
					<div>
						<WMDrawer
							swipeAreaWidth={ 50 }
							open={ talentPoolData.get('drawerIsOpen') }
							openSecondary
							docked={ false }
							width={ 800 }
							onRequestChange={ () => onCloseDrawer() }
						>
							{ talentPoolData.get('interactionMode') === interactionModes.CREATE && <TalentPoolCreateForm enableOrgStructures={ enableOrgStructures } /> }
							{ talentPoolData.get('interactionMode') === interactionModes.MANAGE && <TalentPoolManage enableOrgStructures={ enableOrgStructures } /> }
						</WMDrawer>
					</div>
				</WMPaper>
			</div>
		);
	}
}

TalentPoolList.propTypes = {
	talentPoolData: PropTypes.instanceOf(Map),
	onOpenProfileModal: PropTypes.func.isRequired,
	onManageGroup: PropTypes.func.isRequired,
	newTalentPool: PropTypes.func.isRequired,
	onToggleActive: PropTypes.func.isRequired,
	onSort: PropTypes.func.isRequired,
	onCloseDrawer: PropTypes.func.isRequired,
	enableOrgStructures: PropTypes.bool,
};

export default Radium(TalentPoolList);
