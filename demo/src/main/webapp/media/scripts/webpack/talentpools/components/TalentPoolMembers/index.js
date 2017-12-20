import { connect } from 'react-redux';
import * as actions from '../../actions';
import template from './template';

const mapStateToProps = ({ rootData }) => ({
	talentPoolData: rootData
});

const asTalentPoolParticipantObject = (participantSearchObjects) => {
	const talentPoolParticipants = [];
	participantSearchObjects.forEach((participantSearchObject) => {
		const talentPoolParticipant = {
			number: participantSearchObject.get('userNumber'),
			participantType: participantSearchObject.get('userType'),
			requestType: participantSearchObject.get('derivedStatus') ? participantSearchObject.get('derivedStatus') : 'ALL'
		};
		talentPoolParticipants.push(talentPoolParticipant);
	});
	return talentPoolParticipants;
};

const mapDispatchToProps = (dispatch) => {
	return {
		getMembers: (id, filters, inviting) => {
			dispatch(actions.getMembers(id, filters.toJS(), inviting));
		},
		switchToInvite: (inviting, id) => {
			dispatch(actions.showInviteFlow(inviting, id));
		},
		handleOpenProfileModal: (userNumber) => {
			dispatch(actions.openProfileModal(userNumber));
		},
		handleOpenParticipantProfileModal: (participant) => {
			if (participant.get('userType') === 'VENDOR') {
				dispatch(actions.openCompanyProfileModal(participant.get('userNumber')));
			} else {
				dispatch(actions.openProfileModal(participant.get('userNumber')));
			}
		},
		handleEditDetails: (id) => {
			dispatch(actions.changeTab('details', id));
		},
		handleRefresh: () => {
			dispatch(actions.refreshMembers());
		},
		handleInvite: (id, usersToInvite) => {
			const userNumbers = [];
			usersToInvite.forEach((user) => {
				userNumbers.push(user.get('userNumber'));
			});
			dispatch(actions.inviteMembers(id, userNumbers));
		},
		handleInviteParticipants: (id, participantsToInvite) => {
			const participants = asTalentPoolParticipantObject(participantsToInvite);
			dispatch(actions.inviteParticipants(id, participants));
		},
		handleSelect: (member, selected) => {
			if (selected) {
				dispatch(actions.addToSelection(member.toObject()));
			} else {
				dispatch(actions.removeFromSelection(member.toObject()));
			}
		},
		handleSelectAll: (selected) => {
			dispatch(actions.toggleSelectAll(selected));
		},
		handleMemberActionMenuOpen: (isOpen, userNumber) => {
			dispatch(actions.openMemberActionMenu(isOpen, userNumber));
		},
		handleBulkMenuOpen: (isOpen) => {
			dispatch(actions.openBulkActionMenu(isOpen));
		},
		handleRemoveDecline: (id, users, bulk = false) => {
			let selectedUsers = users;
			if (bulk) {
				selectedUsers = users.filter(user => user.get('selected'));
			}
			const userNumbers = [];
			selectedUsers.forEach((user) => {
				userNumbers.push(user.get('userNumber'));
			});
			dispatch(actions.removeOrDecline(id, userNumbers));
		},
		handleRemoveDeclineParticipants: (id, participantsToInvite, bulk = false) => {
			let selectedParticipants = participantsToInvite;
			if (bulk) {
				selectedParticipants = participantsToInvite.filter(user => user.get('selected'));
			}
			const participants = asTalentPoolParticipantObject(selectedParticipants);
			dispatch(actions.removeOrDeclineParticipants(id, participants));
		},
		handleUninvite: (id, users, bulk = false) => {
			let selectedUsers = users;
			if (bulk) {
				selectedUsers = users.filter(user => user.get('selected'));
			}
			const userNumbers = [];
			selectedUsers.forEach((user) => {
				userNumbers.push(user.get('userNumber'));
			});
			dispatch(actions.uninvite(id, userNumbers));
		},
		handleUninviteParticipants: (id, participantsToUninvite, bulk = false) => {
			let selectedParticipants = participantsToUninvite;
			if (bulk) {
				selectedParticipants = participantsToUninvite.filter(user => user.get('selected'));
			}
			const participants = asTalentPoolParticipantObject(selectedParticipants);
			dispatch(actions.removeOrDeclineParticipants(id, participants));
		},
		handleSearchFilterUpdate: (filterObj) => {
			dispatch(actions.searchFilterUpdate(filterObj));
		},
		handleApprove: (id, users, bulk = false) => {
			let selectedUsers = users;
			if (bulk) {
				selectedUsers = users.filter(user => user.get('selected'));
			}
			const userNumbers = [];
			selectedUsers.forEach((user) => {
				userNumbers.push(user.get('userNumber'));
			});
			dispatch(actions.approve(id, userNumbers));
		},
		handleApproveParticipants: (id, participantsToInvite, bulk = false) => {
			let selectedParticipants = participantsToInvite;
			if (bulk) {
				selectedParticipants = participantsToInvite.filter(user => user.get('selected'));
			}
			const participants = asTalentPoolParticipantObject(selectedParticipants);
			dispatch(actions.approveParticipants(id, participants));
		},
		handleDownloadDocumentation: (id, users) => {
			const selectedUsers = users.filter(user => user.get('selected'));
			const userNumbers = [];
			selectedUsers.forEach((user) => {
				userNumbers.push(user.get('userNumber'));
			});
			dispatch(actions.downloadDocumentation(id, userNumbers));
		},
		handleMemberPagination: (direction, page, pageSize, totalResults) => {
			dispatch(actions.handleMemberPagination(direction, page, pageSize, totalResults));
		}
	};
};

const TalentPoolMembers = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default TalentPoolMembers;
