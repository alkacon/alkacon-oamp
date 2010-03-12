<%@ page import="com.alkacon.opencms.comments.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response);
	pageContext.setAttribute("alkaconCmt", alkaconCmt);
%>
<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}" />
<fmt:setBundle basename="${alkaconCmt.resourceBundle}" />
<!-- start: page -->
<c:choose>
<c:when test="${alkaconCmt.userCanManage}" >
	<c:forEach var="comment" items="${alkaconCmt.comments}" >
	   <!-- start: manager comment -->
	   <%@include file="%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_manager.jsp:dfeb4d86-1846-11dd-88ef-111d34530985)" %>
	   <!-- end: manager comment -->
	</c:forEach>
	<script>
		function doAction(actionId, entryId) {
		    if ((actionId != 'delete') || confirm('<fmt:message key="comment.manager.delete.conf" />')) {
				$('body').css("cursor", "wait");
				var page = ($("div.cmtPaginationBox span.current").not(".next").not(".prev").html()*1)-1;
				$.post(
					'<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/elements/comment_actions.jsp:b043d3d1-1dc9-11dd-b28b-111d34530985)</cms:link>', 
					{ cmtaction: actionId, cmtentry: entryId, cmturi: '${param.cmturi}' }, 							
					function() { reloadComments('${alkaconCmt.state}', page); }
				);
			}
		}
	</script>
</c:when>
<c:otherwise>
	<c:forEach var="comment" items="${alkaconCmt.comments}" >
	   <!-- start: comment -->
	   <%@include file="%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_view.jsp:0e39b41a-1847-11dd-88ef-111d34530985)" %>
	   <!-- end: comment -->
	</c:forEach>
</c:otherwise>
</c:choose>
<!-- end: page -->
