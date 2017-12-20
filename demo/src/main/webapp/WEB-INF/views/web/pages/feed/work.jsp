<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public pagetitle="${work.title}" bodyclass="page-public-work">

	<div class="work">
		<div class="work-content">
			<div>
				<h1><c:out value="${work.title}" /></h1>
				<div class="work-content--status">
					<i class="icon-time">
						<span>Real Time Status:</span>
						<c:choose>
							<c:when test="${work.available}">
								<span id="availability" class="work-content--success">Available</span>
							</c:when>
							<c:otherwise>
								<span id="availability" class="work-content--danger">Unavailable</span>
							</c:otherwise>
						</c:choose>
					</i>
				</div>
			</div>

			<div>
				<h3>Description</h3>
				<p><c:out escapeXml="false" value="${work.description}" /></p>
				<div class="work-content--alert">
					<i class="wm-icon-lock-circle icon-2x"></i>
					You must apply or be logged in to view additional assignment details. Please <a href="/signup/worker">Apply</a> or <a href="/login">Log In</a> on Work Market
				</div>
			</div>

			<div>
				<h3>Relevant Skills &amp; Specialties</h3>
				<p><c:out value="${work.desiredSkills}" /></p>
			</div>
		</div>

		<div class="work-secondary">
			<div class="sidebar-card">
				<h2 class="sidebar-card--title">Work Details</h2>
					<div class="sidebar-card--details">
						<i class="wm-icon-clock"></i><span>Time & Scheduling:</span>
						<strong>
							<h5>${wmfmt:formatCalendarWithTimeZone('EEEE', work.schedule.from, work.timeZone)} ${wmfmt:formatCalendarWithTimeZone('MMMM d, yyyy', work.schedule.from, work.timeZone)}</h5>
						</strong>
					</div>

					<div class="sidebar-card--details">
						<i class="wm-icon-coins-hollow"></i><span>Pricing:</span>
						<strong>
							<h5><c:out value="${work.pricingStrategy}" /></h5>
						</strong>
					</div>

					<div class="sidebar-card--details">
						<i class="wm-icon-globe-circle"></i><span>Location:</span>
						<strong>
							<c:choose>
								<c:when test="${work.address != null}">
									<h5><c:out value="${work.address.city}" /></h5>
									<h5><c:out value="${work.address.state}" /></h5>
								</c:when>
								<c:otherwise>
									<h5>Can be completed anywhere (virtual)</h5>
								</c:otherwise>
							</c:choose>
						</strong>
					</div>

					<div class="sidebar-card--call-to-action">
						<div>
							<strong>Apply for this assignment and many more by signing up for a free Work Market Account</strong>
						</div>
						<br/>
						<a href="/signup/worker" class="button -primary">Sign Up Now</a>

						<div>
							<strong>Already have a profile?</strong>
						</div>
						<br/>
						<a class="button -light" href="/login">Log In!</a>

						<div>
							<strong>Share this assignment:</strong>
						</div>
						<br/>
						<a id="linkedin" href="#"
						   data-socialize="linkedIn"
						   data-title="<c:out value="${work.title}" />"
						   data-url="http://www.workmarket.com/work/${work.workNumber}"
						   data-summary="${work.sanitizedDescription}">
							<i class="icon-linkedin-sign icon-2x muted"></i>
						</a>
						<a id="facebook" href="#"
						   data-socialize="facebook"
						   data-title="<c:out value="${work.title}" />"
						   data-url="http://www.workmarket.com/work/${work.workNumber}"
						   data-summary="${work.sanitizedDescription}">
							<i class="icon-facebook-sign icon-2x muted"></i>
						</a>
						<a id="twitter" href="#"
						   data-socialize="twitter"
						   data-text="Find work @workmarket <c:out value="${work.title}" />"
						   data-url="http://www.workmarket.com/work/${work.workNumber}">
							<i class="icon-twitter-sign icon-2x muted"></i>
						</a>
					</div>
			</div>

			<div class="sidebar-card">
				<h2 class="sidebar-card--title">Other Assignments</h2>
				<div id="feed">
					<script type="text/javascript">
						<!--
						wm_feed_title = '';
						wm_feed_background_color = "#FFFFFF";
						wm_feed_width = "300px";
						wm_feed_text_font = "12px";
						wm_feed_link_color = "#429ecb";
						wm_feed_border = "0";
						wm_feed_padding = "0";
						//-->
					</script>
					<script type="text/javascript" src="/feed/s?l=2"></script>
				</div>
			</div>
		</div>
	</div>

</wm:public>
