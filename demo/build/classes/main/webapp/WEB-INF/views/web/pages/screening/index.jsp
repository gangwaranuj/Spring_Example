<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="Screenings" bodyclass="screening">
<div class="row_sidebar_right">
	<div class="content">
		<div class="inner-container">
			<vr:rope>
				<vr:venue name="HIDE_BG_CHECKS" bypass="true">
					<sec:authorize access="hasFeature('background_check_international')">
						<div>
							<h3>Background Checks</h3>
							<p>Work Market has partnered with Sterling to provide background screenings for Work Market users. With your permission, Work Market will initiate a background check that verifies your address, name and social security number as well as conduct a full background check using Sterling's proprietary national criminal record database of all 50 states as well as physical searches of federal and county courthouses.</p>
							<a href="<c:url value="/screening/bkgrnd"/>" >Order a background check</a>
						</div>
					</sec:authorize>
				</vr:venue>
			</vr:rope>
			<hr/>
			<vr:rope>
				<vr:venue name="HIDE_DRUG_TESTS" bypass="true">
					<div>
						<h3>Drug Screens</h3>
						<p>Many organizations require drug testing to ensure a safer and more productive work environment. Work Market has partnered with Sterling to provide drug testing for Work Market users.  With your permission, Work Market will initiate your drug test and Sterling will refer you to a testing site that is convenient to you.</p>
						<a href="<c:url value="/screening/drug"/>" >Order a drug screen</a>
					</div>
				</vr:venue>
			</vr:rope>
		</div>
	</div>
	<c:import url="/WEB-INF/views/web/partials/screening/about.jsp"/>
</div>
</wm:app>
