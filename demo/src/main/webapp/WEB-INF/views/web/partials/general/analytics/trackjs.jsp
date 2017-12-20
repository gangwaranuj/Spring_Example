<%-- Begin TrackJS
<script>
	window._trackJs = {
		enabled: window.location.host.indexOf('localhost') === -1,
		token: '880b109fd5f34fd095ed7a81ef5623e7',
		application: 'app',
		sessionId: '${pageContext.session.id}',
		userId: '${currentUser.userNumber}',
		onError: function (payload, error) {
			var blackListURLs = [
				'app.listenloop.com',
				'qa.workmarket.com',
				'dev.workmarket.com'
			];

			for (var i = 0; i < blackListURLs.length; i++) {
				if (payload.url.indexOf(blackListURLs[i]) > -1) {
					// returning any kind of falsy value will reject error
					return false;
				}
			}

			// Test for IE9 or lower
			if (document.all && !window.atob) {
				return false;
			}

			return true; // Return any kind of truthy value here to allow transmission of error
		}
	};
</script>
<script src="https://d2zah9y47r7bi2.cloudfront.net/releases/current/tracker.js" crossorigin="anonymous"></script>
<%-- End TrackJS --%>
