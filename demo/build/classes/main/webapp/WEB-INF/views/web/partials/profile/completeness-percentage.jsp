<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style type="text/css">
	#completeness {
		display: inline-block;
		height: 16px;
		background-color: #bbb;
		width: 150px;
		text-align: left;
	}
	#completeness .progress {
		display: inline-block;
		width: <c:out value="${profileCompleteness.completedPercentage}"/>%; /* to be dynamic */
		height: 16px;
		vertical-align: top;
		background: #54bc30;
		background: -moz-linear-gradient(top, #54bc30 0%, #2f8e0d 100%);
		background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#54bc30), color-stop(100%,#2f8e0d));
		background: -webkit-linear-gradient(top, #54bc30 0%,#2f8e0d 100%);
		background: -o-linear-gradient(top, #54bc30 0%,#2f8e0d 100%);
		background: -ms-linear-gradient(top, #54bc30 0%,#2f8e0d 100%);
		filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#54bc30', endColorstr='#2f8e0d',GradientType=0 );
		background: linear-gradient(top, #54bc30 0%,#2f8e0d 100%);
	}
</style>

<div>
	<span id="completeness" class="ml">
		<span class="progress"></span>
	</span><br/>
	<small class="ml meta"><c:out value="${profileCompleteness.completedPercentage}"/>% complete</small>
</div>
