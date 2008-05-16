<%@ page import="com.alkacon.opencms.comments.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%
	CmsCommentsAccess cmt = new CmsCommentsAccess(pageContext, request, response);
	pageContext.setAttribute("cmt", cmt);
%>
<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}" />
<fmt:setBundle basename="com.alkacon.opencms.comments.frontend" />
<!-- start: page -->
<c:choose>
<c:when test="${cmt.userCanManage}" >
	<c:forEach var="comment" items="${cmt.comments}" >
	   <!-- start: manager comment -->
	   <%@include file="%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_manager.jsp:dfeb4d86-1846-11dd-88ef-111d34530985)" %>
	   <!-- end: manager comment -->
	</c:forEach>
	<script>
	$("a.action").each(
		function(intIndex) {
			var getClickHandler = function(actionId) {
				return function(evt) { 
					$('body').css("cursor", "wait");
					$.post(
						'<cms:link>%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_actions.jsp:b043d3d1-1dc9-11dd-b28b-111d34530985)</cms:link>', 
						{ cmtaction: actionId, cmturi: '${param.cmturi}' }, 
						reloadComments);
					if (evt.stopPropagation) {
						evt.stopPropagation();
					} else {
						evt.cancelBubble = true;
					}
					return false;
				};
			};
			$(this).bind("click", getClickHandler($(this).attr("id")));
		}
	);	
	</script>
</c:when>
<c:otherwise>
	<c:forEach var="comment" items="${cmt.comments}" >
	   <!-- start: comment -->
	   <%@include file="%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_view.jsp:0e39b41a-1847-11dd-88ef-111d34530985)" %>
	   <!-- end: comment -->
	</c:forEach>
</c:otherwise>
</c:choose>
<!-- end: page -->
