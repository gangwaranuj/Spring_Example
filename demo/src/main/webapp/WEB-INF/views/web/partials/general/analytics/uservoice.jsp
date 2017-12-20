<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
    <c:when test="${currentUser.isBuyer()}">
        <c:set var="forumId" value="597961" />
        <c:set var="accountType" value="Employer" />
    </c:when>
    <c:otherwise>
        <c:set var="forumId" value="598312" />
        <c:set var="accountType" value="Worker" />
    </c:otherwise>
</c:choose>

<%-- Begin UserVoice Embed Code
<script type="text/javascript" id="uservoice">
    // Include the UserVoice JavaScript SDK (only needed once on a page)
    UserVoice=window.UserVoice||[];(function(){var uv=document.createElement('script');uv.type='text/javascript';uv.async=true;uv.src='//widget.uservoice.com/30LOoAr8OOL6sFVkymqA.js';var s=document.getElementsByTagName('script')[0];s.parentNode.insertBefore(uv,s)})();

    //
    // UserVoice Javascript SDK developer documentation:
    // https://www.uservoice.com/o/javascript-sdk
    //

    // Set colors
    UserVoice.push(['set', {
        accent_color: '#FF6600',
        trigger_color: 'white',
        trigger_background_color: '#3E91E0',
        forum_id: '${forumId}',
        smartvote_enabled: true,
        post_suggestion_enabled: true,
        contact_enabled: false,
        strings: {
            post_suggestion_body: 'How can we improve your WorkMarket experience?',
            suggestion_title_placeholder: 'Summarize your suggestion (be specific)'
        }
    }]);


    // Identify the user and pass traits
    UserVoice.push(['identify', {
        email:      '${currentUser.email}',
        name:       '${currentUser.fullName}',
        id:         '${currentUser.userNumber}',
        type:       '${accountType}',
        account: {
            id: '${currentUser.companyNumber}',
            name: '${currentUser.companyName}',
        }
    }]);

    // Add default trigger to the bottom-right corner of the window:
    UserVoice.push(['addTrigger', {mode: 'smartvote', trigger_position: 'bottom-right' }]);

    // Autoprompt for Satisfaction and SmartVote (only displayed under certain conditions)
    UserVoice.push(['autoprompt', {}]);
</script>
<%-- End UserVoice Embed Code --%>
