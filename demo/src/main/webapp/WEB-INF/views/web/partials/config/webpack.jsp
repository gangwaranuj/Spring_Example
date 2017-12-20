<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Begin WebpackProd --%>
<c:set var="entryPoint" value="${param.script}.js" />
<c:set var="entryPointVendor" value="${param.script}_vendor.js" />
<c:if test="${not empty manifestMap.get('common_vendor.js')}">
    <script src="${mediaPrefix}/builds/${manifestMap.get('common_vendor.js')}"></script>
</c:if>
<script src="${mediaPrefix}/builds/${manifestMap.get('common.js')}"></script>
<c:if test="${not empty manifestMap.get(entryPointVendor)}">
    <script src="${mediaPrefix}/builds/${manifestMap.get(entryPointVendor)}"></script>
</c:if>
<script src="${mediaPrefix}/builds/${manifestMap.get(entryPoint)}"></script>
<%-- End WebpackProd --%>

<%-- Begin WebpackLocal
<script src="http://localhost:8181/common.js"></script>
<script src="http://localhost:8181/${param.script}.js"></script>
<%-- End WebpackLocal --%>
