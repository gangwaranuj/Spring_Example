import {
	commonStyles
} from '@workmarket/front-end-components';

export default {
	avatar: {
		height: '34px',
		width: '34px',
		borderRadius: '3px',
		marginRight: '1em'
	},
	defaultAvatar: {
		background: 'url(/media/cache/images/default-profile-image.svg)'
	},
	nameContainer: {
		display: 'flex'
	},
	memberName: {
		flex: 1
	},
	subText: {
		color: commonStyles.colors.baseColors.grey,
		fontSize: '0.85em'
	},
	memberTabContainer: {
		display: 'flex',
		flexFlow: 'column nowrap',
		justifyContent: 'center',
		alignContent: 'center',
		alignItems: 'center',
		maxHeight: '100%'
	},
	searchFilterUIContainer: {
		order: 1,
		flex: '0 1 auto',
		alignSelf: 'auto',
		minWidth: '100%',
		minHeight: 'auto'
	},
	memberResultsContainer: {
		order: 2,
		flex: '1 1 auto',
		alignSelf: 'auto',
		minWidth: '100%',
		minHeight: 'auto',
		overflowY: 'auto',
		overflowX: 'hidden'
	},
	bulkActionsContainer: {
		order: 3,
		flex: '0 1 auto',
		alignSelf: 'auto',
		minWidth: '100%',
		minHeight: 'auto',
		borderTop: `1px solid ${commonStyles.colors.baseColors.lightGrey}`
	},
	hideBulkContainer: {
		display: 'none'
	},
	bulkButton: {
		margin: '0.5em'
	},
	memberPagination: {
		flex: 1,
		textAlign: 'right',
		paddingRight: '0.5em',
		paddingBottom: '1em'
	},
	memberPaginationArrow: {
		position: 'relative',
		cursor: 'pointer',
		top: '10px',
		display: 'inline-block'
	},
	orangeStatus: {
		color: commonStyles.colors.baseColors.orange
	},
	greenStatus: {
		color: commonStyles.colors.baseColors.green
	},
	redStatus: {
		color: commonStyles.colors.baseColors.red
	}
};
