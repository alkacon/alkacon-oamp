<%@ page taglibs="c,cms,fn,fmt" %>
<!-- start: page -->
<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="${param.resourceBundle}">
	<jsp:useBean id="alkaconReplies" class="com.alkacon.opencms.v8.comments.CmsRepliesAccessBean" />
	<c:forEach var="comment" items="${alkaconReplies.replies[param.entryId]}" >
		<!-- start: comment -->
		<c:choose>
		<c:when test="${param.userCanManage == 'true'}">
			<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_manager.jsp?cmtallowreplies=false:564331dc-15df-11e1-aeb4-9b778fa0dc42)" %>
		</c:when>
		<c:otherwise>
			<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_view.jsp?cmtallowreplies=false:564f18cb-15df-11e1-aeb4-9b778fa0dc42)" %>
		</c:otherwise>
		</c:choose>
		<!-- end: comment -->
	</c:forEach>
</cms:bundle>
<!-- end: page -->