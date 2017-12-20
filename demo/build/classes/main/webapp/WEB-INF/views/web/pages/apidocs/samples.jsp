<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public pagetitle="Samples" bodyclass="page-api">
<div class="container">

	<h1>Work Market API / Examples</h1>

	<div>
		<div class="sidebar">
			<jsp:include page="../../partials/general/api_endpoint_sidebar.jsp"/>
		</div>
		<div class="content">
			<h2>API Usage Examples</h2>
			<p>Here are some samples of API usage.  In these examples, we're using cURL from the command line, though your implementation could use any language capable of issuing HTTP GET and POST requests and parsing responses.</p>
			<p>POST requests have all parameters in the post body (the quoted text after "--data").  For GET requests, you could just use a browser as well.</p>
			<p>You can test these yourself in our <strong>sandbox environment</strong> by replacing <code>https://www.workmarket.com</code> with <code>https://api.dev.workmarket.com</code> Be sure to set up your company profile and get an access token there.

			<h4>Get Your Access Token</h4>
			<p><strong>Note:</strong> replace <code>token</code> and <code>secret</code> paramters with your account token and secret values.  These can be generated on the API Access section of the Settings page in your Work Market account.</p>
			<pre>curl -k --data "token=VZCtbBDJ16YGkO58l6VM&secret=Eqj9O5KcWdDpWuo0yOEQcfdqzu2c0TSKj0ar0DDS" https://www.workmarket.com/api/v1/authorization/request</pre>

			<h4>Create A Basic Assignment</h4>
			<pre>curl -k --data "access_token=1234567890abcdefgh&title=API+Testing&description=This+is+a+test&industry_id=1000&scheduled_start=1357068350&pricing_type=flat&pricing_flat_price=1.00&location_address1=20+W+20th+St&location_city=New+York&location_state=NY&location_zip=10011&location_country=USA" https://www.workmarket.com/api/v1/assignments/create</pre>

			<h4>Get Assignment Details</h4>
			<pre>curl -k "https://www.workmarket.com/api/v1/assignments/get?access_token=1234567890abcdefgh&id=1090758109"</pre>
		</div>
	</div>
</div>
</wm:public>
