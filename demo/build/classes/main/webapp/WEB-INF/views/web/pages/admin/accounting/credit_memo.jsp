<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<wm:admin pagetitle="Issue Credit Memo" webpackScript="admin">

    <script>
        var config = {
            mode: 'details'
        }
    </script>

    <c:set var="hasAccoutingRole" value="false" scope="request"/>
    <sec:authorize access="hasRole('ROLE_WM_ACCOUNTING')">
        <c:set var="hasAccoutingRole" value="true" scope="request"/>
    </sec:authorize>

    <div class="sidebar admin">
        <c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
    </div>

    <div class="content">
        <h1>Issue Credit Memo</h1>
        <hr/>

        <c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
            <c:param name="bundle" value="${bundle}" />
        </c:import>

        <form:form modelAttribute="form" action="/admin/accounting/credit_memo" method="post" id="credit_memo_form" onsubmit="return confirm('Are you sure?') ? tree :false;">
            <wm-csrf:csrfToken />
            <form:hidden path="refInvoiceId"></form:hidden>

        <table id="wm_credit_memo_table" border="0">
            <tr>
                <td align="left" width="30%">Invoice #:</td>
                <td align="right"><c:out value="${invoiceRef.invoiceNumber}"/></td>
                <td align="left" width="30%"></td>
            </tr>
            <tr>
                <td align="left" width="30%">Company Name:</td>
                <td align="right"><c:out value="${invoiceRef.company.name}"/></td>
                <td align="left" width="30%"></td>
            </tr>
            <tr>
                <td align="left" width="30%">Issue Date:</td>
                <td align="right" width="30%"><fmt:formatDate pattern="MM/dd/yyyy" type="date" value="${invoiceRef.createdOn.time}"/></td>
                <td align="left" width="30%"></td>
            </tr>
            <tr>
                <td align="left" width="30%">Due Date:</td>
                <td align="right"><fmt:formatDate pattern="MM/dd/yyyy" type="date" value="${invoiceRef.dueDate.time}"/></td>
                <td align="left" width="30%"></td>
            </tr>
            <tr>
                <td align="left" width="30%">Payment Date:</td>
                <td align="right">
                    <c:choose>
                        <c:when test="${invoiceRef.paymentDate.time == null}">
                            -
                        </c:when>
                        <c:otherwise>
                            <fmt:formatDate pattern="MM/dd/yyyy" type="date" value="${invoiceRef.paymentDate.time}"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td align="left" width="30%"></td>
            </tr>
            <tr>
                <td align="left" width="30%">Amount:</td>
                <td align="right"><c:out value="${invoiceRef.balance}"/></td>
                <td align="left" width="30%"></td>
            </tr>
            <tr>
                <td align="left" width="30%">Revenue Month:</td>
                <td align="right"></td>
                <td align="left" width="30%"></td>
            </tr>
            </table>
            <table>
                <tr>
                    <td align="left" width="30%">
                        <label for="reason" class="required control-label">Reason:</label>
                    </td>
                    <td align="right" width="30%">
                        <form:select path="reason" class="wm-select" id="reason">
                            <form:option value="" label="" />
                            <form:options items="${reasons}" itemLabel="label" itemValue="value"/>
                        </form:select>
                    </td>
                    <td align="left" width="30%" ><form:errors path="reason" cssStyle="color: #ff0000;"/></td>
                </tr>
                <tr>
                    <td align="left" valign="top" width="30%">
                        <label for="reason" class="required control-label">Note:</label>
                    </td>
                    <td align="right">
                        <form:textarea rows="5" cols="150" path="note" id="note" class="wm-textarea" />
                    </td>
                    <td align="left" width="30%" ><form:errors path="note" cssStyle="color: #ff0000;"/></td>
                </tr>
                <tr>
                    <td align="left" width="30%"> </td>
                    <td align="right">
                        <a class="button" href="/admin/accounting/workmarket_invoices">Cancel</a>
                        <button class="button -primary" id="submit">Submit</button>
                    </td>
                    <td align="left" width="30%"></td>
                </tr>
                <tr>
                    <td align="left" width="30%"> </td>
                    <td align="right">
                    </td>
                    <td align="left" width="30%"></td>
                </tr>
        </table>
        </form:form>
    </div>
</wm:admin>


