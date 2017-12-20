export default (isInternal, userId, verificationStatus) => {
	if (verificationStatus === 'VERIFIED' && isInternal) {
		return '<a href="/admin/licenses/edit_userlicense?id={{license.id}}&user_id={{facade.id}}" title="Edit"><i class="wm-icon-edit"></i></a>';
	}
};