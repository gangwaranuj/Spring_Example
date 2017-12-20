<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public pagetitle="Authentication" bodyclass="page-api">
<div class="container">
	<h1>Work Market API / Authentication</h1>
	<div class="">
		<div class="sidebar">
			<jsp:include page="../../partials/general/api_sidebar.jsp"/>
		</div>
		<div class="content">
			<h2>Authentication</h2>
			<p>The Work Market API can only be accessed via <a href="http://oauth.net/2/">OAuth 2.0</a>. It's a standard used by various large API providers.</p>

			<h3>1. Generate an API Key</h3>
			<p>Start by <a href="/mmw/api">generating a new API key</a> and obtaining your API credentials, consisting of an API <code>token</code> and <code>secret</code>.</p>

			<h3>2. Obtain an Access Token</h3>
			<p>An access token is obtained via the OAuth 2.0 Client Credentials Flow. Quite simply, present us with your API <code>token</code> and <code>secret</code> and we return an <code>access_token</code> which authorizes you to make API requests.</p>
			<p>Your client code will make an HTTP POST request for:</p>
			<pre>https://www.workmarket.com/api/v1/authorization/request</pre>
			<p>With the parameters <code>token</code> and <code>secret</code> set to the equivalent values of your API token.</p>
			<p>The envelope response will include the <code>access_token</code>. <strong>Save</strong> this access token for subsequent API requests</p>

			<h3>3. Make Requests</h3>
			<p>Once you have an access token, it&rsquo;s easy to use any of the <a href="/apidocs/endpoints">endpoints</a> by simply adding <code>access_token=ACCESS_TOKEN</code> to your GET or POST request. For example, from the command line, you can do:</p>
			<pre>% curl https://www.workmarket.com/api/v1/assignments/list?access_token=ACCESS_TOKEN</pre>
			<p>Yeah. It&rsquo;s that simple.</p>

			<h3>Notes</h3>
			<p>Although at this time we do not expire OAuth access tokens, you should be prepared for this possibility in the future.</p>
		</div>
	</div>
</div>

</wm:public>
