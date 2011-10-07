<%@ page import="com.alkacon.opencms.comments.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"));
	pageContext.setAttribute("alkaconCmt", alkaconCmt);
%>
<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}" />
<fmt:setBundle basename="${alkaconCmt.resourceBundle}" />
<!-- start: pagination -->
<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_pagination.jsp:15fbc99e-1847-11dd-88ef-111d34530985)" %>
<!-- end: pagination -->
<div id="comments_page_${param.cmtpage}" >
	<cms:include file="%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_page.jsp:34fca4a6-1da3-11dd-be62-111d34530985)">
		<cms:param name="cmturi" value="${param.cmturi}" />
		<cms:param name="cmtminimized" value="${param.cmtminimized}" />
	    <cms:param name="cmtlist" value="${param.cmtlist}" />
	    <cms:param name="cmtsecurity" value="${param.cmtsecurity}" />
		<cms:param name="configUri" value="${param.configUri}" />
		<cms:param name="cmtpage" value="${param.cmtpage}" />
	</cms:include>
</div>
