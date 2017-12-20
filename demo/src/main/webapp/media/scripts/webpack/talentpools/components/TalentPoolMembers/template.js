/* eslint-disable react/no-multi-comp */
import $ from 'jquery';
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Radium from 'radium';
import { Map } from 'immutable';
import {
	commonStyles,
	WMTable,
	WMTableBody,
	WMTableHeader,
	WMTableHeaderCell,
	WMTableRow,
	WMTableCell,
	WMRaisedButton,
	WMFontIcon,
	WMIconButton,
	WMPopover,
	WMIconMenu,
	WMList,
	WMListItem,
	WMMenu,
	WMMenuItem,
	WMStatefulToolTip
} from '@workmarket/front-end-components';
import Application from '../../../core';
import SearchFilterUI from '../../../funcs/wmSearchFilter';
import wmSelect from '../../../funcs/wmSelect';
import wmSlider from '../../../funcs/wmSlider';
import CommonStyles from '../../styles/common';
import MemberStyles from '../../styles/members';

class TalentPoolMembers extends Component {
	componentDidMount () {
		this.searchFilter = new SearchFilterUI({
			el: '.search-filter-bucket',
			hideDateRange: true,
			change: () => {
				this.props.handleSearchFilterUpdate(this.searchFilter.getFilterObject());
			},
			placeholderText: 'Search skills/products, name, user number...',
			fullWidthFacets: true
		});

		this.searchState = {
			manualFilterEntry: false,
			memberTabMode: ''
		};
	}

	componentWillUpdate (nextProps) {
		this.searchState.manualFilterEntry = nextProps.talentPoolData.get('memberTabMode') === this.searchState.memberTabMode;
		this.searchState.memberTabMode = nextProps.talentPoolData.get('memberTabMode');
	}

	componentDidUpdate () {
		if (this.searchState.manualFilterEntry) return;

		const talentPoolData = this.props.talentPoolData;
		if (talentPoolData.get('memberTabMode') === 'member') {
			this.searchFilter.handleFilterEntry({
				label: 'Talent Pool Status',
				title: 'Member',
				filterValue: {
					label: 'Member',
					name: 'groupstatus',
					value: 'member'
				}
			}, true);
			this.searchFilter.handleFilterEntry({
				label: 'Talent Pool Status',
				title: 'Member Override',
				filterValue: {
					label: 'Member Override',
					name: 'groupstatus',
					value: 'memberoverride'
				}
			});
		} else if (talentPoolData.get('memberTabMode') === 'pending') {
			this.searchFilter.handleFilterEntry({
				label: 'Talent Pool Status',
				title: 'Pending - Meets Requirements',
				filterValue: {
					label: 'Pending - Meets Requirements',
					name: 'groupstatus',
					value: 'pending'
				}
			}, true);
			this.searchFilter.handleFilterEntry({
				label: 'Talent Pool Status',
				title: 'Pending - Requirements Not Met',
				filterValue: {
					label: 'Pending - Requirements Not Met',
					name: 'groupstatus',
					value: 'pendingoverride'
				}
			});
		} else if (talentPoolData.get('memberTabMode') === 'invited') {
			this.searchFilter.handleFilterEntry({
				label: 'Talent Pool Status',
				title: 'Invited',
				filterValue: {
					label: 'Invited',
					name: 'groupstatus',
					value: 'invited'
				}
			});
		}
	}

	updateSearchFilterUI (data = {}, showStatusFilter, hasMarketplace) {
		// clean slate
		this.searchFilter.removeAllFilters();

		if (typeof data.assessments !== 'undefined' && data.assessments.length > 0) {
			this.buildFacet(data.assessments, 'assessment', 'Tests', 'default');
		}

		// add "Licenses"
		if (typeof data.licenses !== 'undefined' && data.licenses.length > 0) {
			this.buildFacet(data.licenses, 'license', 'Licenses', 'default');
		}

		// add "Certifications"
		if (typeof data.certifications !== 'undefined' && data.certifications.length > 0) {
			this.buildFacet(data.certifications, 'certification', 'Certification', 'default');
		}

		// add "Worker Type"
		if (typeof data.lanes !== 'undefined' && data.lanes.length > 0) {
			const workerTypeFilterOptions = [];
			for (let i = 0; i < data.lanes.length; i += 1) {
				// don't expose lane 0 - internal users and lane 4 - everyone else
				if (data.lanes[i].id !== '0' && data.lanes[i].id !== '4') {
					workerTypeFilterOptions.push({ label: 'Type', id: data.lanes[i].id, title: data.lanes[i].name });
				}
			}

			if (hasMarketplace) {
				workerTypeFilterOptions.push({ label: 'Worker Type', id: 'WORKER', title: 'Workers Only' });
				workerTypeFilterOptions.push({ label: 'Worker Type', id: 'VENDOR', title: 'Companies Only' });
				workerTypeFilterOptions.push({ label: 'Picture', id: 'avatar', title: 'Has Profile Photo' });
			}

			if (typeof data.companytypes !== 'undefined' && data.companytypes.length > 0) {
				const moreTypes = data.companytypes.map(type => ({
					label: 'Company Type',
					id: `companyType:${type.id}`,
					title: type.name
				}));
				workerTypeFilterOptions.concat(moreTypes);
			}

			this.searchFilter.addFilter({
				name: 'lane',
				title: 'Type',
				template: 'default',
				options: workerTypeFilterOptions
			});
		}

		// add "ratings"
		this.searchFilter.addAdvancedFilter({
			name: 'ratings',
			title: 'Ratings',
			template: 'ratings',
			renderCallback: () => {
				wmSlider();
				$('input[name="slider"]').on('change', (event) => {
					const $target = $(event.currentTarget);
					const percentage = $target.val();
					const id = $target.attr('id');
					$('.wm-search-filter--ratings').find(`input[id="${id}"]`).val(percentage);
				});

				$('.apply-ratings').on('click', () => {
					if ($('input[id="satisfactionRate"]').val() > 0) {
						this.searchFilter.removeSelectionByName('Min Satisfaction');
						this.searchFilter.handleFilterEntry({
							label: 'Min Satisfaction',
							title: $('input[id="satisfactionRate"]').val(),
							filterValue: {
								name: 'satisfactionRate',
								label: 'Min Satisfaction',
								value: $('input[id="satisfactionRate"]').val()
							}
						});
					}

					if ($('input[id="onTimePercentage"]').val() > 0) {
						this.searchFilter.removeSelectionByName('Min On Time');
						this.searchFilter.handleFilterEntry({
							label: 'Min On Time',
							title: $('input[id="onTimePercentage"]').val(),
							filterValue: {
								name: 'onTimePercentage',
								label: 'Min On Time',
								value: $('input[id="onTimePercentage"]').val()
							}
						});
					}

					if ($('input[id="deliverableOnTimePercentage"]').val() > 0) {
						this.searchFilter.removeSelectionByName('Min Deliverable On Time');
						this.searchFilter.handleFilterEntry({
							label: 'Min Deliverable On Time',
							title: $('input[id="deliverableOnTimePercentage"]').val(),
							filterValue: {
								name: 'deliverableOnTimePercentage',
								label: 'Min Deliverable On Time',
								value: $('input[id="deliverableOnTimePercentage"]').val()
							}
						});
					}

					$('.close-advanced-facet').trigger('click');
				});
			}
		});

		// add "verifications"
		if (typeof data.verifications !== 'undefined' && data.verifications.length > 0) {
			this.buildFacet(data.verifications, 'verification', 'Verification', 'default');
		}

		// add "Org Units"
		if (typeof data.orgUnits !== 'undefined' && data.orgUnits.length > 0) {
			this.buildFacet(data.orgUnits, 'orgUnits', 'Org. Unit', 'default');
		}

		// add "Talent Pools"
		if (typeof data.groups !== 'undefined' && data.groups.length > 0) {
			this.buildFacet(data.groups, 'group', 'Talent Pool', 'default');
		}

		if (typeof data.sharedgroups !== 'undefined' && data.sharedgroups.length > 0) {
			this.buildFacet(data.sharedgroups, 'sharedgroup', 'Shared Pool', 'default');
		}

		// add "Industries"
		if (typeof data.industries !== 'undefined' && data.industries.length > 0) {
			this.buildFacet(data.industries, 'industry', 'Industry', 'default');
		}

		// add talent pool status

		if (showStatusFilter) {
			const groupDetailOptions = [
				{
					id: 'member',
					name: 'Member'
				},
				{
					id: 'memberoverride',
					name: 'Member Override'
				},
				{
					id: 'pending',
					name: 'Pending - Meets Requirements'
				},
				{
					id: 'pendingoverride',
					name: 'Pending - Requirements Not Met'
				},
				{
					id: 'invited',
					name: 'Invited'
				},
				{
					id: 'declined',
					name: 'Declined'
				}
			];
			this.buildFacet(groupDetailOptions, 'groupstatus', 'Talent Pool Status', 'default');
		}

		// add "Location"
		this.searchFilter.addAdvancedFilter({
			name: 'location',
			title: 'Location',
			template: 'location',
			renderCallback: () => {
				wmSelect({ selector: '#radius' });
				wmSelect({ selector: '#countries' }, {
					plugins: ['remove_button'],
					maxItems: null,
					placeholder: 'All Countries...',
					labelField: 'name',
					valueField: 'id',
					searchField: ['id', 'name']
				});
				if (typeof data.countries !== 'undefined') {
					const $selectize = $('#countries')[0].selectize;
					$selectize.loadedSearches = {};
					$selectize.userOptions = {};
					$selectize.renderCache = {};
					$selectize.options = $selectize.sifter.items = {};
					$selectize.lastQuery = null;
					data.countries.forEach((country) => {
						$selectize.addOption(country);
						if (country.filter_on) {
							$selectize.addItem(country.id, true);
						}
					});
				} else {
					$('#countries-filters').hide();
				}

				$('.apply-location').on('click', () => {
					const address = $('#address').val();
					const radius = $('#radius').val();
					const countries = $('#countries').val();

					if (address !== '') {
						let locationString = `Within ${radius} miles of ${address}`;
						if (countries !== null) {
							locationString += `, ${countries}`;
						}
						const locationValue = {
							address,
							radius,
							countries
						};
						this.searchFilter.removeSelectionByName('Location');
						this.searchFilter.handleFilterEntry({
							label: 'Location',
							title: locationString,
							filterValue: {
								name: 'location',
								label: 'Location',
								value: locationValue
							}
						});
					}
				});
			}
		});
	}

	buildFacet (filterObj, filterName, filterLabel, filterTemplate) {
		const filterOptions = filterObj.reduce((memo, { id, name }) => {
			const title = name;
			const label = filterLabel;
			return [...memo, { label, id, title }];
		}, []);

		this.searchFilter.addFilter({
			name: filterName,
			title: filterLabel,
			template: filterTemplate,
			options: filterOptions
		});
	}

	render () {
		const talentPoolData = this.props.talentPoolData;

		// todo: refactor search UI
		let membersArray = [];
		if (talentPoolData.get('members') !== undefined) {
			membersArray = talentPoolData.get('members').toArray();
		}

		if (this.searchFilter !== undefined && talentPoolData.get('filters').size > 0) {
			this.updateSearchFilterUI(
				talentPoolData.get('filters').toJS(),
				!(talentPoolData.get('inviting') || talentPoolData.get('openMembership') === 'false'),
				talentPoolData.get('hasMarketplace')
			);
		}

		Application.Events.off('talentpools:resetSearchFilter').on('talentpools:resetSearchFilter', () => {
			this.searchState = { manualFilterEntry: false, memberTabMode: '' };
			this.searchFilter.reset(true);
		});

		const emptySearchResults = (
			<div
				id="emptySearchResults"
				style={ { padding: '1em' } }
			>
				{(talentPoolData.get('isFetchingSearchResults') ? <div style={ { padding: '0.5em' } }>Loading results...</div> :
				<div>
						Double check the spelling of your search.
						<br />
						Try another keyword or broaden your results by removing one or more search filters.
					</div>
				)}
			</div>
		);

		const getIcon = (iconName, colorCode) => (
			<WMFontIcon
				className="material-icons"
				id="status-icon"
				style={ { verticalAlign: 'middle', margin: '-5px 5px 0 0', fontSize: '23px', color: { colorCode } } }
			>
				{ iconName }
			</WMFontIcon>
		);

		const memberIcon = (
			<WMFontIcon
				className="material-icons"
				style={ { display: 'block', margin: 'auto', width: '150px', height: '150px', fontSize: '150px', color: commonStyles.colors.baseColors.lightGrey } }
				id="member"
			>
				people
			</WMFontIcon>
		);

		const getRequirements = (talentPoolCriterion) => {
			const descriptions = [];

			let noCriteriaMet = true;
			let criteriaMet = 0;
			let requirementsMet = '';

			if (talentPoolCriterion && talentPoolCriterion.size > 0) {
				talentPoolCriterion.forEach((criteria) => {
					if (criteria.get('met')) {
						noCriteriaMet = false;
					}
					criteriaMet += criteria.get('met');
				});

				requirementsMet = `${criteriaMet} of ${talentPoolCriterion.size}`;

				talentPoolCriterion.forEach((criteria, index) => {
					descriptions.push(
						<WMListItem
							disabled
							innerDivStyle={ {
								fontSize: commonStyles.typography.body.fontSize,
								padding: noCriteriaMet ? '8px' : '8px 8px 8px 29px'
							} }
							key={ index }
							leftIcon={ criteria.get('met') ?
								<WMFontIcon
									className="material-icons"
									id="status-icon"
									style={ {
										color: commonStyles.colors.baseColors.green,
										fontSize: '1.5em',
										margin: '3px 8px 0 0'
									} }
								>
									check
								</WMFontIcon>
								: undefined
							}
							primaryText={
								<span style={ { color: 'white', fontWeight: (criteria.get('met') ? 'bold' : 'normal'), display: 'block', maxWidth: '300px', overflow: 'hidden' } }>{criteria.get('typeName')}: {criteria.get('name')}</span>
							}
						/>
					);
				});
			}

			return {
				met: requirementsMet,
				description: <WMList>{descriptions && descriptions.map(description => description)}</WMList>
			};
		};

		const canInviteVendors = talentPoolData.get('hasVendorPoolsFeature');
		const hasRequirements = (membersArray && membersArray.length && membersArray[0].get('talentPoolCriterion') && membersArray[0].get('talentPoolCriterion').size > 0);
		let selectionHasInvited = false;
		let selectionHasMember = false;
		let selectionHasOverride = false;
		let selectionHasPending = false;

		const list = membersArray.map((member) => {
			let friendlyStatus;
			switch (member.get('derivedStatus')) {
			case 'MEMBER_PASSED':
				friendlyStatus = (<div style={ MemberStyles.greenStatus }>
					{ getIcon('fiber_manual_record', MemberStyles.greenStatus) }
					Member
				</div>);
				if (member.get('selected')) {
					selectionHasMember = true;
				}
				break;
			case 'MEMBER_OVERRIDE':
				friendlyStatus = (<div style={ MemberStyles.greenStatus }>
					{ getIcon('fiber_manual_record', MemberStyles.greenStatus) }
					Member Override
				</div>);
				if (member.get('selected')) {
					selectionHasMember = true;
				}
				break;
			case 'INVITED':
				friendlyStatus = (<div style={ MemberStyles.orangeStatus }>
					{ getIcon('fiber_manual_record', MemberStyles.orangeStatus) }
					Invited
				</div>);
				if (member.get('selected')) {
					selectionHasInvited = true;
				}
				break;
			case 'PENDING_FAILED':
				friendlyStatus = (<div style={ MemberStyles.orangeStatus }>
					{ getIcon('fiber_manual_record', MemberStyles.orangeStatus) }
					Requirements Not Met
				</div>);
				if (member.get('selected')) {
					selectionHasOverride = true;
				}
				break;
			case 'PENDING_PASSED':
				friendlyStatus = (<div style={ MemberStyles.orangeStatus }>
					{ getIcon('fiber_manual_record', MemberStyles.orangeStatus) }
					Pending
				</div>);
				if (member.get('selected')) {
					selectionHasPending = true;
				}
				break;
			case 'DECLINED':
				friendlyStatus = (<div style={ MemberStyles.redStatus }>
					{ getIcon('fiber_manual_record', MemberStyles.redStatus) }
					Declined
				</div>);
				break;
			default:
				friendlyStatus = <div />;
				break;
			}

			const userProfileModal = participant => (
				<a style={ CommonStyles.link } onClick={ () => this.props.handleOpenProfileModal(participant.get('userNumber')) }>
					{ participant.get('first_name') } { participant.get('last_name')}
				</a>
			);

			const participantProfileModal = participant => (
				<a
					style={ CommonStyles.link }
					onClick={ () => this.props.handleOpenParticipantProfileModal(participant) }
				>
					{ participant.get('first_name') } { participant.get('last_name')}
				</a>
			);

			const requirements = getRequirements(member.get('talentPoolCriterion'));

			return (talentPoolData.get('isFetchingSearchResults') ? <div style={ { padding: '0.5em' } }>Loading results...</div> :
			<WMTableRow
				key={ member.get('userNumber') }
				selected={ member.get('selected') }
				onSelectRow={ isInputChecked => this.props.handleSelect(member, isInputChecked) }
			>
				<WMTableCell>
					<div style={ MemberStyles.nameContainer }>
						{ member.get('avatar_asset_uri') ?
							<div style={ [MemberStyles.avatar, { background: `url(${member.get('avatar_asset_uri')})` }] } /> :
							<div style={ [MemberStyles.avatar, MemberStyles.defaultAvatar] } />
						}
						<div style={ MemberStyles.memberName }>
							<div>
								{ !canInviteVendors && userProfileModal(member) }
								{ canInviteVendors && participantProfileModal(member) }
							</div>
							<div style={ MemberStyles.subText }>
								{ member.get('city') }, { member.get('state') }
							</div>
						</div>
					</div>
				</WMTableCell>
				{hasRequirements &&
					<WMTableCell style={ { textAlign: 'center', position: 'relative' } }>
						<WMStatefulToolTip
							id={ `requirementsTooltip_${member.get('userNumber')}` }
							tooltipContent={ requirements.description }
							anchorOrigin={ { horizontal: 'right', vertical: 'center' } }
							targetOrigin={ { horizontal: 'left', vertical: 'center' } }
							style={ {
								background: 'rgba(0, 0, 0, 0.8)',
								color: '#f1f1f1',
								fontSize: '14px',
								marginLeft: '14px',
								padding: '4px'
							} }
						>
							{ requirements.met }
						</WMStatefulToolTip>
					</WMTableCell>
				}
				<WMTableCell>
					{ talentPoolData.get('inviting') ? member.get('rating') : friendlyStatus }
				</WMTableCell>
				<WMTableCell>
					{ talentPoolData.get('inviting') ? (member.get('work_completed_company_count') || 0) :
					<WMIconMenu
						open={ member.get('actionMenuIsOpen') }
						onTouchTap={ () => this.props.handleMemberActionMenuOpen(true, member.get('userNumber')) }
						onRequestChange={ open => this.props.handleMemberActionMenuOpen(open, member.get('userNumber')) }
						iconButtonElement={
							<WMIconButton>
								<WMFontIcon
									className="material-icons"
									id="member-actions"
								>
										more_horiz
										</WMFontIcon>
							</WMIconButton>
								}
					>
						{ member.get('derivedStatus') === 'INVITED' && canInviteVendors ?
							<WMMenuItem
								primaryText="Uninvite"
								onTouchTap={ () => this.props.handleUninviteParticipants(talentPoolData.get('id'), [member]) }
							/> :
							canInviteVendors &&
							<WMMenuItem
								primaryText="Remove/Decline"
								onTouchTap={ () => this.props.handleRemoveDeclineParticipants(talentPoolData.get('id'), [member]) }
							/>
						}
						{ member.get('derivedStatus') === 'INVITED' && !canInviteVendors ?
							<WMMenuItem
								primaryText="Uninvite"
								onTouchTap={ () => this.props.handleUninvite(talentPoolData.get('id'), [member]) }
							/> :
							!canInviteVendors &&
							<WMMenuItem
								primaryText="Remove/Decline"
								onTouchTap={ () => this.props.handleRemoveDecline(talentPoolData.get('id'), [member]) }
							/>
						}
						{ member.get('derivedStatus') === 'PENDING_PASSED' && canInviteVendors &&
						<WMMenuItem
							primaryText="Approve"
							onTouchTap={ () => this.props.handleApproveParticipants(talentPoolData.get('id'), [member]) }
						/>
						}
						{ member.get('derivedStatus') === 'PENDING_PASSED' && !canInviteVendors &&
						<WMMenuItem
							primaryText="Approve"
							onTouchTap={ () => this.props.handleApprove(talentPoolData.get('id'), [member]) }
						/>
						}
						{ member.get('derivedStatus') === 'PENDING_FAILED' && canInviteVendors &&
						<WMMenuItem
							primaryText="Override"
							onTouchTap={ () => this.props.handleApproveParticipants(talentPoolData.get('id'), [member]) }
						/>
						}
						{ member.get('derivedStatus') === 'PENDING_FAILED' && !canInviteVendors &&
						<WMMenuItem
							primaryText="Override"
							onTouchTap={ () => this.props.handleApprove(talentPoolData.get('id'), [member]) }
						/>
							}
					</WMIconMenu>
					}
				</WMTableCell>
			</WMTableRow>
			);
		});

		const loadingMessage =
			talentPoolData.get('memberResultsCount') > talentPoolData.get('memberResultPageSize') ?
				<div style={ { padding: '0.5em' } }>Loading more results...</div> :
				<div style={ { padding: '0.5em' } }>Loading results...</div>;

		const tableHeader = (
			<WMTableHeader
				onSelectAllRows={ (isChecked) => { this.props.handleSelectAll(isChecked); } }
				allRowsSelected={ talentPoolData.get('selectedAll') }
			>
				<WMTableHeaderCell>Name</WMTableHeaderCell>
				{hasRequirements && <WMTableHeaderCell style={ { textAlign: 'center' } }>Requirements Met</WMTableHeaderCell>}
				<WMTableHeaderCell>{ talentPoolData.get('inviting') ? 'Satisfaction' : 'Status' }</WMTableHeaderCell>
				<WMTableHeaderCell>{ talentPoolData.get('inviting') ? 'Work Completed for You' : 'Actions' }</WMTableHeaderCell>
			</WMTableHeader>
		);
		const searchFiltersAndUsers = ((talentPoolData.get('isFetchingSearchResults') ? <div>{ loadingMessage }</div> :
		(<div>
			<WMTable
				striped
				hideDisabledCheckboxes
				selectable
				multiSelectable
			>
				{ tableHeader }
				<WMTableBody>
					{ list }
				</WMTableBody>
			</WMTable>
		</div>)
		));

		const inactiveEmptyTalentPoolState = (
			<div id="emptyMembers">
				{(talentPoolData.get('isFetchingSearchResults') ? <div style={ { padding: '0.5em' } }>Loading results...</div> :
				<div>
					<div>
						{ memberIcon }
					</div>
					<div style={ CommonStyles.emptyOrInactiveText }>
							To add or invite members to a talent<br />
							pool, it must first be activated.
							<br />
						<br />
					</div>
					<WMRaisedButton
						label="Edit Details"
						style={ { marginRight: '1em', marginTop: '1em', marginBottom: '1em', display: 'table', margin: '0 auto' } }
						backgroundColor={ commonStyles.colors.baseColors.green }
						labelColor={ commonStyles.colors.baseColors.white }
						onClick={ () => this.props.handleEditDetails(!talentPoolData.get('inviting'), talentPoolData.get('id')) }
					/>
				</div>
				)}
			</div>
		);

		const emptyReadOnlyTalentPoolState = (
			<div id="emptyMembers">
				{(talentPoolData.get('isFetchingSearchResults') ? <div style={ { padding: '0.5em' } }>Loading results...</div> :
				<div>
					{ memberIcon }
					<div style={ CommonStyles.emptyOrInactiveText }>
							You have not yet paid any resources.
							<br />
						<br />
					</div>
				</div>
				)}
			</div>
		);

		const emptyTalentPoolState = (
			<div id="emptyMembers">
				{(talentPoolData.get('isFetchingSearchResults') ? <div style={ { padding: '0.5em' } }>Loading results...</div> :
				<div>
					<div>
						{ memberIcon }
					</div>
					{ talentPoolData.get('openMembership') === 'true' &&
						<div style={ CommonStyles.emptyOrInactiveText }>
								Get started by inviting members from the marketplace.
							<br />
							<br />
								You can also set up talent requirements for this talent pool in the tab above.
							<br />
							<br />
						</div> }
					{ talentPoolData.get('openMembership') !== 'true' &&
						<div style={ CommonStyles.emptyOrInactiveText }>
								Get started by adding members from the marketplace.
							<br />
							<br />
						</div> }
					<WMRaisedButton
						label={ talentPoolData.get('openMembership') === 'true' ? 'Invite Members' : 'Add Members' }
						style={ { marginRight: '1em', marginTop: '1em', marginBottom: '1em', display: 'table', margin: '0 auto' } }
						backgroundColor={ commonStyles.colors.baseColors.green }
						labelColor={ commonStyles.colors.baseColors.white }
						onClick={ () => this.props.switchToInvite(!talentPoolData.get('inviting'), talentPoolData.get('id')) }
					/>
				</div>
				)}
			</div>
		);

		const talentPoolHasMembers = talentPoolData.get('allInvitedOrApplied').toArray().length > 0;
		const clickToReturnToMembers =
			(<div style={ { fontSize: '0.75em' } }>
				<a
					style={ { color: commonStyles.colors.baseColors.lightGrey } }
					onClick={ () => this.props.switchToInvite(!talentPoolData.get('inviting'), talentPoolData.get('id')) }
				>
					Click to return to members
				</a>
			</div>);

		const refreshIcon = (
			<a style={ { float: 'right', marginTop: '.2em' } } onClick={ () => this.props.handleRefresh() }>
				<WMFontIcon
					className="material-icons"
					id="refresh"
					color={ commonStyles.colors.baseColors.lightGrey }
					hoverColor={ commonStyles.colors.baseColors.grey }
				>
					refresh
				</WMFontIcon>
			</a>
		);

		const addParticipantsToPoolButton = (
			<div>
				<WMRaisedButton
					primary
					label={ talentPoolData.get('openMembership') === 'true' ? 'Send Invitations' : 'Add Members' }
					style={ { flex: 1 } }
					disabled={ membersArray.findIndex(member => (member.get('selected'))) < 0 }
					onClick={ () => this.props.handleInviteParticipants(talentPoolData.get('id'),
											membersArray.filter((member) => { return member.get('selected'); })) }
				/>
			</div>
		);

		const addWorkersToPoolButton = (
			<div>
				<WMRaisedButton
					primary
					label={ talentPoolData.get('openMembership') === 'true' ? 'Send Invitations' : 'Add Members' }
					style={ { flex: 1 } }
					disabled={ membersArray.findIndex(member => (member.get('selected'))) < 0 }
					onClick={ () => this.props.handleInvite(talentPoolData.get('id'),
												membersArray.filter((member) => { return member.get('selected'); })) }
				/>
			</div>
		);

		const workerUninvite = (
			<WMMenuItem
				primaryText="Uninvite"
				onTouchTap={ () => this.props.handleUninvite(talentPoolData.get('id'), talentPoolData.get('members'), true) }
			/>
		);

		const workerApprove = (
			<WMMenuItem
				primaryText="Approve"
				onTouchTap={ () => this.props.handleApprove(talentPoolData.get('id'), talentPoolData.get('members'), true) }
			/>
		);

		const workerOverride = (
			<WMMenuItem
				primaryText="Override"
				onTouchTap={ () => this.props.handleApprove(talentPoolData.get('id'), talentPoolData.get('members'), true) }
			/>
		);

		const workerRemoveDecline = (
			<WMMenuItem
				primaryText="Remove/Decline"
				onTouchTap={ () => this.props.handleRemoveDecline(talentPoolData.get('id'), talentPoolData.get('members'), true) }
			/>
		);

		const participantUninvite = (
			<WMMenuItem
				primaryText="Uninvite"
				onTouchTap={ () => this.props.handleUninviteParticipants(talentPoolData.get('id'), talentPoolData.get('members'), true) }
			/>
		);

		const participantApprove = (
			<WMMenuItem
				primaryText="Approve"
				onTouchTap={ () => this.props.handleApproveParticipants(talentPoolData.get('id'), talentPoolData.get('members'), true) }
			/>
		);

		const participantOverride = (
			<WMMenuItem
				primaryText="Override"
				onTouchTap={ () => this.props.handleApproveParticipants(talentPoolData.get('id'), talentPoolData.get('members'), true) }
			/>
		);

		const participantRemoveDecline = (
			<WMMenuItem
				primaryText="Remove/Decline"
				onTouchTap={ () => this.props.handleRemoveDeclineParticipants(talentPoolData.get('id'), talentPoolData.get('members'), true) }
			/>
		);

		return (
			<div style={ MemberStyles.memberTabContainer }>
				<div style={ MemberStyles.searchFilterUIContainer }>
					{ !talentPoolData.get('autoGenerated') && talentPoolData.get('isActive') && talentPoolHasMembers &&
						<div
							className="invite-button"
							style={ {
								display: talentPoolHasMembers ? 'flex' : 'none',
								justifyContent: 'space-between',
								background: commonStyles.colors.baseColors.offWhite,
								padding: '1em',
								cursor: 'pointer'
							} }
						>
							{ talentPoolData.get('openMembership') === 'true' ?
								<div style={ { flex: 2 } }>
									{ talentPoolData.get('inviting') ?
										<div>
											Inviting Members
											{ clickToReturnToMembers }
										</div> :
										<WMRaisedButton
											secondary
											data-component-identifier="tp_inviting_members"
											label="Invite Members"
											onClick={ () => this.props.switchToInvite(!talentPoolData.get('inviting'), talentPoolData.get('id')) }
										/>
									}
									{ !talentPoolData.get('inviting') && !talentPoolData.get('isFetchingSearchResults') && refreshIcon }
								</div>
								:
								<div style={ { flex: 2 } }>
									{ talentPoolData.get('inviting') ?
										<div>
											Adding Members
												{ clickToReturnToMembers }
										</div> :
										<WMRaisedButton
											secondary
											data-component-identifier="tp_inviting_members"
											label="Add Members"
											onClick={ () => this.props.switchToInvite(!talentPoolData.get('inviting'), talentPoolData.get('id')) }
										/>
									}
									{ !talentPoolData.get('inviting') && !talentPoolData.get('isFetchingSearchResults') && refreshIcon }
								</div>
							}

							{ talentPoolData.get('inviting') && canInviteVendors && addParticipantsToPoolButton }
							{ talentPoolData.get('inviting') && !canInviteVendors && addWorkersToPoolButton }
							<div style={ { clear: 'both' } } />
						</div>
					}
					<div
						className="search-filter-bucket"
						style={ { fontSize: '0.75em', display: talentPoolHasMembers ? 'inline' : 'none' } }
					/>
				</div>
				<div style={ MemberStyles.memberResultsContainer }>
					{ talentPoolHasMembers && list.length > 0 && searchFiltersAndUsers }
					{ talentPoolHasMembers && list.length < 1 && emptySearchResults }
					{ !talentPoolHasMembers && talentPoolData.get('autoGenerated') && emptyReadOnlyTalentPoolState }
					{ !talentPoolHasMembers && !talentPoolData.get('autoGenerated') && talentPoolData.get('isActive') && emptyTalentPoolState }
					{ !talentPoolHasMembers && !talentPoolData.get('autoGenerated') && !talentPoolData.get('isActive') && inactiveEmptyTalentPoolState }
				</div>
				{(talentPoolData.get('isFetchingSearchResults') ? null :
				<div
					style={
						talentPoolHasMembers ?
						MemberStyles.bulkActionsContainer :
						MemberStyles.hideBulkContainer
					}
				>
					<div style={ { display: 'flex' } }>
						<div style={ { flex: 1 } }>
							{!talentPoolData.get('inviting') && !talentPoolData.get('autoGenerated') ?
								<div>
									<div
										style={ { display: 'inline-block' } }
										className="talentpools-bulk-menu-button"
									>
										<WMRaisedButton
											secondary
											disabled={ talentPoolData.get('selectionLength') === 0 }
											style={ MemberStyles.bulkButton }
											onClick={ () => {
												if (talentPoolData.get('selectionLength') > 0) {
													this.props.handleBulkMenuOpen(true);
												} else {
													this.props.handleBulkMenuOpen(false);
												}
											} }
											label="Bulk Actions"
										/>
									</div>
									{ talentPoolData.get('selectionLength') > 0 ? <span> { talentPoolData.get('selectionLength') } selected</span> : null }
									<WMPopover
										open={ talentPoolData.get('bulkMenuIsOpen') }
										anchorEl={ document.querySelector('.talentpools-bulk-menu-button') }
										onRequestClose={ () => this.props.handleBulkMenuOpen(false) }
										style={ talentPoolData.get('inviting') && { display: 'none' } }
									>
										<WMMenu>
											{ (canInviteVendors && selectionHasInvited) && participantUninvite }
											{ (canInviteVendors &&
												(selectionHasMember || selectionHasPending || selectionHasOverride))
												&& participantRemoveDecline }
											{ (canInviteVendors && selectionHasPending) && participantApprove }
											{ (canInviteVendors && selectionHasOverride) && participantOverride}
											{ (!canInviteVendors && selectionHasInvited) && workerUninvite }
											{ (!canInviteVendors &&
												(selectionHasMember || selectionHasPending || selectionHasOverride))
												&& workerRemoveDecline }
											{ (!canInviteVendors && selectionHasPending) && workerApprove }
											{ (!canInviteVendors && selectionHasOverride) && workerOverride }
											<WMMenuItem
												primaryText="Download Documents"
												onTouchTap={ () => this.props.handleDownloadDocumentation(talentPoolData.get('id'), talentPoolData.get('members')) }
											/>
										</WMMenu>
									</WMPopover>
								</div>
									:
								<div style={ { paddingLeft: '1em', paddingTop: '0.8em' } }>
									{ !talentPoolData.get('autoGenerated') && talentPoolData.get('selectionLength') > 0 &&
									<span> { talentPoolData.get('selectionLength') } selected</span>
										}
								</div>
								}
						</div>
						{ talentPoolData.get('memberResultsCount') <= talentPoolData.get('memberResultPageSize') ? null :
						<div style={ MemberStyles.memberPagination }>
							<div
								style={ MemberStyles.memberPaginationArrow }
								onClick={ talentPoolData.get('memberResultPage') === 1 ? null : () => this.props.handleMemberPagination('prev', talentPoolData.get('memberResultPage'), talentPoolData.get('memberResultPageSize'), talentPoolData.get('memberResultsCount')) }
							>
								<WMFontIcon
									className="material-icons"
									id="member-prev-page"
									color={ talentPoolData.get('memberResultPage') === 1 ? commonStyles.colors.baseColors.lightGrey : commonStyles.colors.baseColors.grey }
								>
									arrow_left
								</WMFontIcon>
							</div>
							<span><strong>{ (talentPoolData.get('memberResultPageSize') * (talentPoolData.get('memberResultPage') - 1)) + 1 } - { (talentPoolData.get('memberResultPageSize') * (talentPoolData.get('memberResultPage') - 1)) + membersArray.length }</strong> of <strong>{ talentPoolData.get('memberResultsCount') }</strong> members</span>
							<div
								style={ MemberStyles.memberPaginationArrow }
								onClick={ talentPoolData.get('memberResultPage') === Math.ceil(talentPoolData.get('memberResultsCount') / talentPoolData.get('memberResultPageSize')) ? null : () => this.props.handleMemberPagination('next', talentPoolData.get('memberResultPage'), talentPoolData.get('memberResultPageSize'), talentPoolData.get('memberResultsCount')) }
							>
								<WMFontIcon
									className="material-icons"
									id="member-next-page"
									color={ talentPoolData.get('memberResultPage') === Math.ceil(talentPoolData.get('memberResultsCount') / talentPoolData.get('memberResultPageSize')) ? commonStyles.colors.baseColors.lightGrey : commonStyles.colors.baseColors.grey }
								>
									arrow_right
								</WMFontIcon>
							</div>
						</div>
							}
					</div>
				</div>
				)}
			</div>
		);
	}
}

TalentPoolMembers.propTypes = {
	talentPoolData: PropTypes.instanceOf(Map),
	handleOpenProfileModal: PropTypes.func.isRequired,
	handleOpenParticipantProfileModal: PropTypes.func.isRequired,
	switchToInvite: PropTypes.func.isRequired,
	handleEditDetails: PropTypes.func.isRequired,
	handleInvite: PropTypes.func.isRequired,
	handleInviteParticipants: PropTypes.func.isRequired,
	handleSelect: PropTypes.func.isRequired,
	handleSelectAll: PropTypes.func.isRequired,
	handleMemberActionMenuOpen: PropTypes.func.isRequired,
	handleRemoveDecline: PropTypes.func.isRequired,
	handleRemoveDeclineParticipants: PropTypes.func.isRequired,
	handleUninvite: PropTypes.func.isRequired,
	handleUninviteParticipants: PropTypes.func.isRequired,
	handleRefresh: PropTypes.func.isRequired,
	handleSearchFilterUpdate: PropTypes.func.isRequired,
	handleApprove: PropTypes.func.isRequired,
	handleApproveParticipants: PropTypes.func.isRequired,
	handleDownloadDocumentation: PropTypes.func.isRequired,
	handleBulkMenuOpen: PropTypes.func.isRequired,
	handleMemberPagination: PropTypes.func.isRequired
};

export default Radium(TalentPoolMembers);
