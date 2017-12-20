<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>

<fmt:message key="global.my_preferences" var="global_my_preferences"/>
<wm:app
        pagetitle="${global_my_preferences}"
        bodyclass="accountSettings"
        webpackScript="settings"
>

    <script>
        var config = {
            mode: 'language'
        };
    </script>

    <div class="row_wide_sidebar_left">

        <div class="sidebar">
            <c:set var="selected_navigation_link" value="/settings/manage/labels" scope="request"/>
            <jsp:include page="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
        </div>

        <div class="content">
            <div class="inner-container">
                <div class="page-header clear">
                    <h3><fmt:message key="global.my_preferences"/></h3>
                </div>
                <sf:form action="/settings/manage/language" method="POST" modelAttribute="languageForm" id="language_preferences" cssClass="form-horizontal left">
                    <wm-csrf:csrfToken />

                    <div class="control-group">
                        <label for="locale" class="control-label">Language</label>
                        <div class="controls">
                            <sf:select path="locale" id="locale" cssErrorClass="fieldError">
                                <sf:options items="${supportedLocalesList}" itemValue="code" itemLabel="language"/>
                            </sf:select>
                            <span class="help-block">
                                    The WorkMarket platform will appear in the language selected here.
                                </span>
                        </div>
                    </div>

                    <hr/>

                    <div class="control-group">
                        <label for="format" class="control-label">Formats</label>
                        <div class="controls">
                            <sf:select path="format" id="format" cssErrorClass="fieldError">
                                <sf:options items="${supportedFormatsList}" itemValue="code" itemLabel="country"/>
                            </sf:select>
                            <span class="help-block">
                                    Formats for date, time, distance, and currency will be set according to your selected locale.
                                </span>
                        </div>
                    </div>

                    <hr/>

                    <div class="wm-action-container">
                        <button type="submit" class="button">Save Changes</button>
                    </div>
                </sf:form>
            </div>
        </div>
    </div>

</wm:app>
