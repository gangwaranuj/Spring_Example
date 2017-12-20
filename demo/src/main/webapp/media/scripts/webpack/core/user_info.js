const getMetaInformation = name => document.querySelector(`meta[name="${name}"]`).getAttribute('content');

const userInfo = {};
[
	'userNumber',
	'userName',
	'firstName',
	'userEmail',
	'companyNumber',
	'companyUuid',
	'companyName',
	'companyId',
	'isBuyer',
	'isAdmin',
	'isManager'
].forEach((info) => {
	try {
		let metaInfo = getMetaInformation(info);

		if (metaInfo === 'true') {
			metaInfo = true;
		} else if (metaInfo === 'false') {
			metaInfo = false;
		}
		userInfo[info] = metaInfo;
	} catch (exception) {
		console.log(`Application is missing ${info}.`); // eslint-disable-line no-console
	}
});

export default userInfo;
