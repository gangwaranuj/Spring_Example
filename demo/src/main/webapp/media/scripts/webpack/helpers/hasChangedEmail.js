export default (isOwner, changedEmail, email) => {
	if (isOwner && changedEmail) {
		return '<p class="tac">' +
			'You have changed your email address to <em>' + changedEmail + '</em>. Until you confirm' +
			'the new address, please use ' + email + ' to log in to the site.' +
			'</p>';
	}
};