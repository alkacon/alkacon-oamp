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
<!-- start: pagination -->
<c:if test="${cmt.config.list > 0 && cmt.countComments > cmt.config.list}" >
<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_pagination.jsp:15fbc99e-1847-11dd-88ef-111d34530985)" %>
</c:if>
<!-- end: pagination -->
<div id="comments_page_0" >
	<cms:include file="%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_page.jsp:34fca4a6-1da3-11dd-be62-111d34530985)">
		<cms:param name="cmturi" value="${param.cmturi}" />
		<cms:param name="cmtpage" value="0" />
	</cms:include>
</div>
