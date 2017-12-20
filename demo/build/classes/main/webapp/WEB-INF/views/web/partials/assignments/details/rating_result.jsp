<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- This form is used for assignment detail page --%>
<script id="rating-result-tmpl" type="text/x-jquery-tmpl">
	<dl style="margin-bottom:20px">
		<dd class="clear">
			<div>
				<div class="span2 text-right">Overall </div> <div class="span2 text-left \${meta.valueCode} "> \${meta.valueDescription} </div> <br/>
				<div class="span2 text-right">Quality </div> <div class="span2 text-left \${meta.qualityCode} "> \${meta.qualityDescription} </div> <br/>
				<div class="span2 text-right">Professionalism </div> <div class="span2 text-left \${meta.professionalismCode} "> \${meta.professionalismDescription} </div><br/>
				<div class="span2 text-right">Communication </div> <div class="span2 text-left \${meta.communicationCode} "> \${meta.communicationDescription} </div> <br/>
				<div class="span2 text-right">Review </div> <div class="span5 text-left"> <blockquote><em>\${meta.review}</em></blockquote></div> <br/>
			</div>
		</dd>
	</dl>
</script>
