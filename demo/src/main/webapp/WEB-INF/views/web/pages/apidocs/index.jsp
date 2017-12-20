<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public pagetitle="API Docs" bodyclass="page-api">
	<jsp:body>
		<div class="container">
			<h1>Work Market API / Overview</h1>
			<div class="">
				<div class="sidebar">
					<jsp:include page="/WEB-INF/views/web/partials/general/api_sidebar.jsp"/>
				</div>
				<div class="content">
					<h2>Overview</h2>

					<p>The Work Market API uses many of the same standards as other APIs you may be familiar with. All requests are simple HTTP GET or POST requests that return JSON, JSONP, or XML responses. (Note that the request parameters are not JSON but rather are standard HTTP keys and values.) All requests MUST be https.</p>

					<h3>Sandbox Environment</h3>
					<p> Work Market has an API sandbox environment to help our clients test out their integrations.  You can create your company login and set up your API access there as you would in our production environment.  The domain is:</p>
					<pre>https://api.dev.workmarket.com</pre>

					<p>To create a new client account on the sandbox, go here:</p>
					<pre>https://api.dev.workmarket.com/signup/creatework</pre>

					<h3>Requests</h3>
					<p>All requests accept the following parameters in addition to any parameters specified in the documentation:</p>

					<p><strong>NOTE:</strong> For POST requests, put all parameters into the <strong>body</strong> of the post, rather than the URL.</p>
					<table>
						<tbody>
							<tr>
								<td><code>access_token</code></td>
								<td>Required for all API methods requiring authentication. See the <a href="/apidocs/authentication">Authentication documentation</a> for details on acquiring an API access token.</td>
							</tr>
							<tr>
								<td><code>output_format</code></td>
								<td>One of <code>json</code>, <code>jsonp</code>, or <code>xml</code></td>
							</tr>
							<tr>
								<td><code>callback</code></td>
								<td>For use only with the <code>jsonp</code> output format, specifies the name of a callback function to execute. We will respond with <code>callback(response)</code></td>
							</tr>
						</tbody>
					</table>


					<h3>Responses</h3>
					<p>All responses will look roughly like:</p>

	<pre>
	{
		"meta": {
			"status_code": 200,
			"errors": [ ... ],
			"version": 1,
			"execution_time": 0.1752,
			"timestamp": 1310745670
		},
		"response": {
			...
		}
	}
	</pre>

					<h3>Errors</h3>

					<p>As much as possible, Work Market attempts to use appropriate HTTP status codes to indicate the general class of problem, and this status code is repeated in the <code>status_code</code> section of the <code>meta</code> response.</p>

					<table>
					<tbody>
						<tr>
						<td width="30%"><code>400</code> (Bad Request)</td>
						<td>Any case where a parameter is invalid, or a required parameter is missing. This includes the case where no token is provided and the case where a worker ID is specified incorrectly in a path.</td>
						</tr>
						<tr>
						<td><code>401</code> (Unauthorized)</td>
						<td>The access token was provided but was invalid.</td>
						</tr>
						<tr>
						<td><code>403</code> (Forbidden)</td>
						<td>The requested information cannot be viewed by the acting user, for example, because they are not eligible to view details of the assignment they are trying to get.</td>
						</tr>
						<tr>
						<td><code>404</code> (Not Found)</td>
						<td>Endpoint does not exist.</td>
						</tr>
						<tr>
						<td><code>405</code> (Method Not Allowed)</td>
						<td>Attempting to use POST with a GET-only endpoint, or vice-versa.</td>
						</tr>
						<tr>
						<td><code>500</code> (Internal Server Error)</td>
						<td>Work Markets's servers are unhappy. The request is probably valid but needs to be retried later.</td>
						</tr>
					</tbody>
					</table>
				</div>
			</div>
		</div>
	</jsp:body>
</wm:public>
