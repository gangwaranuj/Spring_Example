import {
	Record,
	Map,
	List
} from 'immutable';

const CompanyProfileRecord = new Record({
	name: '',
	overview: '',
	website: '',
	avatar: '',
	avatarSmall: '',
	avatarUUID: '',
	location: new Map({
		id: 0,
		name: '',
		number: '',
		addressLine1: '',
		addressLine2: '',
		city: '',
		state: '',
		zip: '',
		country: '',
		longitude: 0,
		latitude: 0
	}),
	yearFounded: 0,
	createdOn: '',
	workInviteSentToUserId: '',
	inVendorSearch: false,
	jobFunctions: new List(),
	skills: new List(),
	employees: 0,
	backgroundCheck: false,
	drugTest: false
});

export default CompanyProfileRecord;
