<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="parts-and-logistics">
	<div class="accordion media completion" id="partsAccord">
		<div class="media-body">
			<div class="accordion-heading">
				<a data-toggle="collapse" data-parent="#partsAccord" href="#partsWell">
					<h4 class="muted">
						Parts and Logistics
						<i class="toggle-icon pull-right icon-minus-sign"></i>
					</h4>
				</a>
			</div>
			<div id="partsWell" class="accordion-body collapse in">
				<div class="_partsRoot_">
					<div id="partsSent"></div>
					<div id="partsReturn"></div>
				</div>
			</div>
		</div>
	</div>
</div>
