export default (isInternal, verificationStatus) => {
	if (verificationStatus === 'PENDING' && isInternal) {
		return '<a href="/admin/certifications/review" title="Review"><i class="wm-icon-bell"></i></a>';
	}
};